package ru.Models;

import javafx.scene.chart.XYChart;

import java.util.ArrayList;
import java.util.List;

public class SignalModel {
    private double amplitude;
    private double frequency;
    private XYChart.Series<Number, Number> graphSeries = new XYChart.Series<>();
    private List<XYChart.Data<Number, Number>> intermediateList = new ArrayList<>();
    private double phase;


    public void generateSignal() {
        double channelPhase = Math.toRadians(phase);

        int SAMPLES = 7680;
        for (int i = 0; i < SAMPLES; i++) {
            intermediateList.add(new XYChart.Data<>((double) i / SAMPLES,
                    amplitude * Math.sin(2 * Math.PI * frequency * i / SAMPLES + channelPhase)));
        }
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
