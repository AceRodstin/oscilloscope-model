package ru.Models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.DoubleSummaryStatistics;
import java.util.List;

public class SignalParametersModel {
    private double amplitude;
    private double dc;
    private int bufferedSamplesPerSemiPeriods;
    private double frequency;
    private double maxSignalValue;
    private int minSamples = 20;
    private double minSignalValue;
    private int periods;
    private double rms;
    private int samplesPerSemiPeriods;
    private double[] signal;
    private int zeroTransitionCounter;

    public void calculateParameters(double[] signal) {
        this.signal = signal;
        calculateMinAndMaxValues();
        calculateParameters();
    }

    private void calculateMinAndMaxValues() {
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;

        for (int i = 0; i < signal.length; i++) {
            if (signal[i] > max) {
                max = signal[i];
            }

            if (signal[i] < min) {
                min = signal[i];
            }
        }

        minSignalValue = min;
        maxSignalValue = max;

        if (frequency > 2) {
            calculateAverageMinAndMaxValues();
        }
    }

    private void calculateAverageMinAndMaxValues() {
        double[] channelData = new double[signal.length];
        List<Double> maxs = new ArrayList<>();
        List<Double> mins = new ArrayList<>();
        int pieces;

        if (frequency < 5) {
            pieces = 2;
        } else if (frequency > 5 && frequency < 10) {
            pieces = 4;
        } else if (frequency > 10 && frequency < 20) {
            pieces = 5;
        } else {
            pieces = 10;
        }

        for (int i = 0, j = 0; i < signal.length; i++) {
            channelData[j++] = signal[i];
        }

        for (int pieceIndex = 0; pieceIndex < pieces; pieceIndex++) {
            double[] pieceOfDate = new double[channelData.length / pieces];
            System.arraycopy(channelData, pieceIndex * pieceOfDate.length, pieceOfDate, 0, pieceOfDate.length);
            DoubleSummaryStatistics statistics = Arrays.stream(pieceOfDate).summaryStatistics();
            maxs.add(statistics.getMax());
            mins.add(statistics.getMin());
        }

        double max = Double.MIN_VALUE;
        double min = Double.MAX_VALUE;
        int indexOfMax = 0;
        int indexOfMin = 0;
        for (int i = 0; i < maxs.size(); i++) {
            if (max < maxs.get(i)) {
                max = maxs.get(i);
                indexOfMax = i;
            }
            if (min > mins.get(i)) {
                min = mins.get(i);
                indexOfMin = i;
            }
        }

        maxs.remove(indexOfMax);
        mins.remove(indexOfMin);
        max = min = 0;

        for (int i = 0; i < maxs.size(); i++) {
            max += maxs.get(i) / maxs.size();
            min += mins.get(i) / mins.size();
        }

        minSignalValue = min;
        maxSignalValue = max;
    }

    private void calculateParameters() {
        amplitude = calculateAmplitude();
        dc = calculateDC();
        rms = calculateRms();
        frequency = calculateFrequency();
    }

    private double calculateAmplitude() {
        return (maxSignalValue - minSignalValue) / 2;
    }

    private double calculateDC() {
        return (maxSignalValue + minSignalValue) / 2;
    }

    private double calculateRms() {
        double summ = 0;
        for (int i = 0; i < signal.length; i++) {
            summ += (signal[i] - dc) * (signal[i] - dc);
        }
        return Math.sqrt(summ / signal.length);
    }

    private double calculateFrequency() {
        double estimatedFrequency = estimateFrequency();
        double accuracyCoefficient = 500;

        if (estimatedFrequency < accuracyCoefficient) {
            return defineFrequency();
        } else {
            return estimatedFrequency;
        }

    }

    private double estimateFrequency() {
        boolean positivePartOfSignal = false;
        double frequency = 0;
        double filteringCoefficient = 1.05;

        for (int i = 0; i < signal.length; i++) {
            if (amplitude + dc >= 0) {
                if (dc >= 0) {
                    if (signal[i] >= dc * filteringCoefficient && !positivePartOfSignal) {
                        frequency++;
                        positivePartOfSignal = true;
                    } else if (signal[i] < dc / filteringCoefficient && positivePartOfSignal) {
                        positivePartOfSignal = false;
                    }
                } else {
                    if (signal[i] >= dc / filteringCoefficient && !positivePartOfSignal) {
                        frequency++;
                        positivePartOfSignal = true;
                    } else if (signal[i] < dc * filteringCoefficient && positivePartOfSignal) {
                        positivePartOfSignal = false;
                    }
                }
            } else if (amplitude + dc < 0 && dc < 0) {
                if (signal[i] < dc * filteringCoefficient && !positivePartOfSignal) {
                    frequency++;
                    positivePartOfSignal = true;
                } else if (signal[i] >= dc / filteringCoefficient && positivePartOfSignal) {
                    positivePartOfSignal = false;
                }
            }
        }

        return frequency;
    }

    private double defineFrequency() {
        int shift = 1_000;
        double firstValue = signal[0] + shift;
        boolean firstPeriod = true;
        boolean positivePartOfSignal = !(firstValue > (dc + shift));
        bufferedSamplesPerSemiPeriods = periods = samplesPerSemiPeriods = zeroTransitionCounter = 0;

        for (int index = 0; index < signal.length; index++) {
            double value = signal[index] + shift;
            double centerOfSignal = dc + shift;

            countSamples();

            if (firstValue >= centerOfSignal) {
                if (value >= centerOfSignal && firstPeriod && (index >= minSamples)) {
                    positivePartOfSignal = true;
                } else if ((value < centerOfSignal && positivePartOfSignal)) {
                    countPeriods();
                    positivePartOfSignal = false;
                    firstPeriod = false;
                } else if (value >= centerOfSignal && !firstPeriod && !positivePartOfSignal && samplesPerSemiPeriods > minSamples) {
                    countPeriods();
                    positivePartOfSignal = true;
                }
            }

            if (firstValue < centerOfSignal) {
                if (value < centerOfSignal && firstPeriod && (index >= minSamples)) {
                    positivePartOfSignal = false;
                } else if ((value > centerOfSignal && !positivePartOfSignal)) {
                    countPeriods();
                    positivePartOfSignal = true;
                    firstPeriod = false;
                } else if (value < centerOfSignal && !firstPeriod && positivePartOfSignal && samplesPerSemiPeriods > minSamples) {
                    countPeriods();
                    positivePartOfSignal = false;
                }
            }
        }

        double samplesPerPeriod = bufferedSamplesPerSemiPeriods == 0 ? 0 : bufferedSamplesPerSemiPeriods / periods;
        double signalFrequency = (samplesPerPeriod == 0 ? 0 : ((double) signal.length / samplesPerPeriod));

        return signalFrequency;
    }

    private void countSamples() {
        if (zeroTransitionCounter >= 1) {
            samplesPerSemiPeriods++;
        }
    }

    private void countPeriods() {
        zeroTransitionCounter++;
        if (zeroTransitionCounter % 2 != 0 && zeroTransitionCounter > 2) {
            bufferedSamplesPerSemiPeriods = samplesPerSemiPeriods;
            periods++;
        }
    }

    public double getAmplitude() {
        return amplitude;
    }

    public double getDc() {
        return dc;
    }

    public double getFrequency() {
        return frequency;
    }

    public double getRms() {
        return rms;
    }
}
