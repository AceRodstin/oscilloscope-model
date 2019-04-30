package ru.Models;

import javafx.scene.chart.XYChart;
import org.vitrivr.cineast.core.util.dsp.fft.FFT;
import org.vitrivr.cineast.core.util.dsp.fft.windows.HanningWindow;

import java.util.*;

public class SignalModel {
    private double amplitude;
    private FFT fft = new FFT();
    ;
    private double frequency;
    private XYChart.Series<Number, Number> graphSeries = new XYChart.Series<>();
    private HanningWindow hanningWindow = new HanningWindow();
    private List<XYChart.Data<Number, Number>> intermediateList = new ArrayList<>();
    private double phase;
    private Random random = new Random();
    private final int SAMPLES = 7680;
    private double[] signal = new double[SAMPLES];

    public void generateSignal() {
        double channelPhase = Math.toRadians(phase);

        for (int i = 0; i < SAMPLES; i++) {
            signal[i] = amplitude * Math.sin(2 * Math.PI * frequency * i / SAMPLES + channelPhase);
        }
    }

    public void fillIntermediateList(boolean isFFT) {
        if (isFFT) {
            fft.forward(signal, SAMPLES, hanningWindow);
            for (int i = 0; i < fft.getMagnitudeSpectrum().size(); i++) {
                double frequency = fft.getMagnitudeSpectrum().getFrequency(i);
                double magnitude = fft.getMagnitudeSpectrum().getValue(i);
                intermediateList.add(new XYChart.Data<>(frequency, magnitude));
            }
        } else {
            for (int i = 0; i < signal.length; i++) {
                intermediateList.add(new XYChart.Data<>((double) i / SAMPLES, signal[i]));
            }
        }
    }

    public int getRarefactionCoefficient() {
        int rarefactionCoefficient;

        if (frequency < 10) {
            rarefactionCoefficient = 10;
        } else if (frequency < 50) {
            rarefactionCoefficient = 2;
        } else {
            rarefactionCoefficient = 1;
        }

        return rarefactionCoefficient;
    }

    public XYChart.Series<Number, Number> getGraphSeries() {
        return graphSeries;
    }

    public List<XYChart.Data<Number, Number>> getIntermediateList() {
        return intermediateList;
    }

    public void setAmplitude(double amplitude) {
        this.amplitude = amplitude;
    }

    public double getFrequency() {
        return frequency;
    }

    public void setFrequency(double frequency) {
        this.frequency = frequency;
    }

    public void setPhase(double phase) {
        this.phase = phase;
    }
}
