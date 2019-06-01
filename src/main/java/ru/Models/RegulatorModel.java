package ru.Models;

import ru.Controllers.Regulator.NeededParameters;

public class RegulatorModel {
    private double bufferedError;
    private double bufferedIValue;
    private double dCoefficient;
    private double dValue;
    private double error;
    private double iCoefficient;
    private double iValue;
    private double neededAmplitude;
    private double neededDc;
    private double neededFrequency;
    private double neededRms;
    private double pCoefficient;
    private double pValue;
    private double responseAmplitude;
    private double responseDc;
    private double responseFrequency;
    private double responseRms;
    private String selectedParameter = NeededParameters.AMPLITUDE.getParameterName();

    public double getdCoefficient() {
        return dCoefficient;
    }

    public double getiCoefficient() {
        return iCoefficient;
    }

    public double getpCoefficient() {
        return pCoefficient;
    }

    public double getNeededParameter() {
        if (selectedParameter.equals(NeededParameters.AMPLITUDE.getParameterName())) {
            return neededAmplitude;
        } else if (selectedParameter.equals(NeededParameters.DC.getParameterName())) {
            return neededDc;
        } else if (selectedParameter.equals(NeededParameters.FREQUENCY.getParameterName())) {
            return neededFrequency;
        } else {
            return neededRms;
        }
    }

    public String getSelectedParameter() {
        return selectedParameter;
    }

    public void setdCoefficient(double dCoefficient) {
        this.dCoefficient = dCoefficient;
    }

    public void setiCoefficient(double iCoefficient) {
        this.iCoefficient = iCoefficient;
    }

    public void setpCoefficient(double pCoefficient) {
        this.pCoefficient = pCoefficient;
    }

    public void setNeededParameter(double value) {
        if (selectedParameter.equals(NeededParameters.AMPLITUDE.getParameterName())) {
            neededAmplitude = value;
        } else if (selectedParameter.equals(NeededParameters.DC.getParameterName())) {
            neededDc = value;
        } else if (selectedParameter.equals(NeededParameters.FREQUENCY.getParameterName())) {
            neededFrequency = value;
        } else {
            neededRms = value;
        }
    }

    public void setResponseAmplitude(double responseAmplitude) {
        this.responseAmplitude = responseAmplitude;
    }

    public void setResponseDc(double responseDc) {
        this.responseDc = responseDc;
    }

    public void setResponseFrequency(double responseFrequency) {
        this.responseFrequency = responseFrequency;
    }

    public void setResponseRms(double responseRms) {
        this.responseRms = responseRms;
    }

    public void setSelectedParameter(String selectedParameter) {
        this.selectedParameter = selectedParameter;
    }
}
