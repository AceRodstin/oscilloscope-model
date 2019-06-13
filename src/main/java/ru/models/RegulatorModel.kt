package ru.models

import ru.controllers.regulator.ControlledParameters

class RegulatorModel {
    private var bufferedError = 0.0
    private var bufferedIValue = 0.0
    private var calculationCount = 1
    var dCoefficient = 0.0
    private var dValue = 0.0
    var iCoefficient = 0.0
    private var neededAmplitude = 0.0
    private var neededDc = 0.0
    private var neededFrequency = 0.0
    var pCoefficient = 0.0
    var responseAmplitude = 0.0
    var responseDc = 0.0
    var responseFrequency = 0.0
    var selectedParameter = ControlledParameters.AMPLITUDE

    fun getNeededValueOfControlledParameter(): Double {
        return when (selectedParameter) {
            ControlledParameters.AMPLITUDE -> neededAmplitude
            ControlledParameters.DC -> neededDc
            ControlledParameters.FREQUENCY -> neededFrequency
        }
    }

    fun getCorrection(): Double {
        return when (selectedParameter) {
            ControlledParameters.AMPLITUDE -> correct(responseAmplitude, neededAmplitude)
            ControlledParameters.DC -> correct(responseDc, neededDc)
            ControlledParameters.FREQUENCY -> correct(responseFrequency, neededFrequency)
        }
    }

    private fun correct(response: Double, neededValue: Double): Double {
        val error = neededValue - response
        val pValue = pCoefficient * error
        var iValue = iCoefficient * error

        if (calculationCount % 2 == 0) {
            iValue = bufferedIValue + iCoefficient * error
            dValue = dCoefficient * (error - bufferedError)
            calculationCount = 1
        } else {
            bufferedError = error
            bufferedIValue = iValue
        }

        return pValue + iValue + dValue
    }

    fun setNeededValueOfControlledParameter(value: Double) {
        when (selectedParameter) {
            ControlledParameters.AMPLITUDE -> neededAmplitude = value
            ControlledParameters.DC -> neededDc = value
            ControlledParameters.FREQUENCY -> neededFrequency = value
        }
    }
}