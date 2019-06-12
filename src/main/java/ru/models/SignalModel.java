package ru.models;

import javafx.scene.chart.XYChart;
import org.vitrivr.cineast.core.util.dsp.fft.FFT;
import org.vitrivr.cineast.core.util.dsp.fft.windows.HanningWindow;
import ru.controllers.signal.NoiseTypes;
import ru.controllers.signal.SignalTypes;
import ru.utils.Utils;
import uk.me.berndporr.iirj.Butterworth;

import java.util.*;

public class SignalModel {
    private double amplitude;
    private Butterworth iir = new Butterworth();
    private double dc;
    private FFT fft = new FFT();
    private boolean filterOn;
    private double frequency;
    private XYChart.Series<Number, Number> graphSeries = new XYChart.Series<>();
    private HanningWindow hanningWindow = new HanningWindow();
    private List<XYChart.Data<Number, Number>> intermediateList = new ArrayList<>();
    private double noiseCoefficient;
    private String noiseType;
    private double phase;
    private Random random = new Random();
    private int samples;
    private SignalParametersModel signalParametersModel = new SignalParametersModel();
    private double[] signal;


    public void generateSignal(String signalType) {
        defineSamplesRate();
        signal = new double[samples + 1];

        if (signalType.equals(SignalTypes.SINE.getTypeName())) {
            generateSin();
        } else if (signalType.equals(SignalTypes.PULSE.getTypeName())) {
            generatePulse();
        } else if (signalType.equals(SignalTypes.TRIANGLES.getTypeName())) {
            generateTriangles();
        } else if (signalType.equals(SignalTypes.SAW.getTypeName())) {
            generateSaw();
        } else if (signalType.equals(SignalTypes.NOISE.getTypeName())) {
            generateNoise();
        }

        filterSignal();
    }

    private void defineSamplesRate() {
        if (frequency <= 10) {
            samples = 500;
        } else if (frequency > 10 && frequency <= 50) {
            samples = 1500;
        } else if (frequency > 50 && frequency <= 100) {
            samples = 3000;
        } else if (frequency > 100 && frequency <= 200) {
            samples = 6000;
        } else if (frequency > 200 && frequency <= 500) {
            samples = 8000;
        } else if (frequency > 500) {
            samples = 10000;
        }
    }

    private void generateSin() {
        for (int i = 0; i < samples + 1; i++) {
            setNoise();
            double channelPhase = Math.toRadians(phase);
            signal[i] = dc + (amplitude + noiseCoefficient) * Math.sin(2 * Math.PI * frequency * i / samples + channelPhase);
            signal[i] = iir.filter(signal[i]);
        }

    }

    private void generatePulse() {
        for (int i = 0; i < samples; i++) {
            setNoise();
            double channelPhase = Math.toRadians(phase);
            signal[i] = dc + (amplitude + noiseCoefficient) * Math.signum(Math.sin(2 * Math.PI * frequency * i / samples + channelPhase));
        }
    }

    private void generateTriangles() {
        for (int i = 0; i < samples; i++) {
            setNoise();
            double channelPhase = Math.toRadians(phase);
            signal[i] = dc + (2 * (amplitude + noiseCoefficient)) / Math.PI * Math.asin(Math.sin(2 * Math.PI * frequency * i / samples
                    + channelPhase));
        }
    }

    private void generateSaw() {
        for (int i = 0; i < samples; i++) {
            setNoise();
            double channelPhase = Math.toRadians(phase);
            signal[i] = dc + (-2 * (amplitude + noiseCoefficient)) / Math.PI * Math.atan(1.0 / Math.tan((double) i / samples * Math.PI * frequency
                    + channelPhase));
        }
    }

    private void generateNoise() {
        for (int i = 0; i < samples; i++) {
            setNoise();
            double channelPhase = Math.toRadians(phase);
            signal[i] = dc + (amplitude + noiseCoefficient) * random.nextDouble() * Math.sin(2 * Math.PI * (frequency + random.nextDouble()) *
                    i / samples + channelPhase);
        }
    }

    private void setNoise() {
        if (noiseType.equals(NoiseTypes.LOW.getTypeName())) {
            noiseCoefficient = amplitude * 0.15 * random.nextDouble();
        } else if (noiseType.equals(NoiseTypes.MIDDLE.getTypeName())) {
            noiseCoefficient = amplitude * 0.25 * random.nextDouble();
            dc += 0.05 * random.nextDouble();
            dc -= 0.05 * random.nextDouble();
        } else if (noiseType.equals(NoiseTypes.HIGH.getTypeName())) {
            noiseCoefficient = amplitude * (0.05 * random.nextDouble() + random.nextDouble());
            phase += 5 * random.nextDouble();
            phase -= 5 * random.nextDouble();
            dc += 0.1 * random.nextDouble();
            dc -= 0.1 * random.nextDouble();
        } else {
            noiseCoefficient = 0;
        }
    }

    private void filterSignal() {
        for (int i = 0; i < signal.length; i++) {
            if (filterOn) {
                signal[i] = iir.filter(signal[i]);
            }
        }
    }

    public void fillIntermediateList(boolean isFFT) {
        intermediateList.clear();

        if (isFFT) {
            fft.forward(signal, signal.length, hanningWindow);
            for (int i = 0; i < fft.getMagnitudeSpectrum().size(); i++) {
                double frequency = fft.getMagnitudeSpectrum().getFrequency(i);
                double magnitude = fft.getMagnitudeSpectrum().getValue(i);
                intermediateList.add(new XYChart.Data<>(frequency, magnitude));
            }
        } else {
            for (int i = 0; i < signal.length; i++) {
                intermediateList.add(new XYChart.Data<>((double) i / samples, signal[i]));
            }
        }
    }

    public void calculateSignalParameters() {
        signalParametersModel.calculateParameters(signal);
    }

    public XYChart.Series<Number, Number> getGraphSeries() {
        return graphSeries;
    }

    public List<XYChart.Data<Number, Number>> getIntermediateList() {
        return intermediateList;
    }

    public double getAmplitude() {
        return Utils.roundValue(amplitude, 100000);
    }

    public double getDc() {
        return Utils.roundValue(dc, 100000);
    }

    public double getFrequency() {
        return Utils.roundValue(frequency, 100000);
    }

    public double getPhase() {
        return Utils.roundValue(phase, 100000);
    }

    public double getReceivedAmplitude() {
        return signalParametersModel.getAmplitude();
    }

    public double getReceivedDc() {
        return signalParametersModel.getDc();
    }

    public double getReceivedFrequency() {
        return signalParametersModel.getFrequency();
    }

    public double getRms() {
        return signalParametersModel.getRms();
    }

    public void setAmplitude(double amplitude) {
        this.amplitude = amplitude;
    }

    public void setDc(double dc) {
        this.dc = dc;
    }

    public void setFilter(int order, int cutoffFrequency) {
        iir.lowPass(order, signal.length, cutoffFrequency);
    }

    public void setFilterOn(boolean filterOn) {
        this.filterOn = filterOn;
    }

    public void setFrequency(double frequency) {
        this.frequency = frequency;
    }

    public void setNoiseType(String noiseType) {
        this.noiseType = noiseType;
    }

    public void setPhase(double phase) {
        this.phase = phase;
    }
}
