package ru.models

import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.sqrt

class SignalParametersModel {
    var amplitude = 0.0
    var dc = 0.0
    var bufferedSamplesPerSemiPeriods = 0
    var frequency = 0.0
    var maxSignalValue = 0.0
    var minSignalValue = 0.0
    var periods = 0
    var rms = 0.0
    var samplesPerSemiPeriod = 0
    var signal = DoubleArray(0)
    var zeroTransitionCounter = 0

    fun calculateParameters(signal: DoubleArray) {
        this.signal = signal
        calculateMinAndMaxValues()
        amplitude = calculateAmplitude()
        dc = calculateDc()
        rms = calculateRms()
        frequency = calculateFrequency()
    }

    private fun calculateMinAndMaxValues() {
        var max = Double.MIN_VALUE
        var min = Double.MAX_VALUE

        for (value in signal) {
            if (value > max) max = value
            if (value < min) min = value
        }

        maxSignalValue = max
        minSignalValue = min

        if (frequency > 2) calculateAverageMaxAndMinValues()
    }

    private fun calculateAverageMaxAndMinValues() {
        val channelData = DoubleArray(signal.size)
        val maxValues = ArrayList<Double>()
        val minValues = ArrayList<Double>()

        // Define pieces of date
        val pieces = if (frequency <= 5) 2
        else if (frequency > 5 && frequency <= 10) 4
        else if (frequency > 10 && frequency <= 20) 5
        else 10

        // Copy piece of date
        var i = 0
        for (index in 0..signal.size) channelData[i++] = signal[index]

        // Define max and min values
        for (index in 0..pieces) {
            val pieceOfDate = DoubleArray(channelData.size / pieces)
            System.arraycopy(channelData, index * pieceOfDate.size, pieceOfDate, 0, pieceOfDate.size)
            val statistic = Arrays.stream(pieceOfDate).summaryStatistics()
            maxValues.add(statistic.max)
            minValues.add(statistic.min)
        }

        // Filter noise
        var max = Double.MIN_VALUE
        var min = Double.MAX_VALUE
        var indexOfMax = 0
        var indexOfMin = 0
        for (index in 0..maxValues.size) {
            if (max < maxValues[index]) {
                max = maxValues[index]
                indexOfMax = index
            }
            if (min > minValues[index]) {
                min = minValues[index]
                indexOfMin = index
            }
        }
        maxValues.removeAt(indexOfMax)
        minValues.removeAt(indexOfMin)

        // Calculate average of maxValues and minValues
        max = 0.0
        min = 0.0
        for (index in 0..maxValues.size) {
            max += maxValues[index] / maxValues.size
            min += minValues[index] / minValues.size
        }

        maxSignalValue = max
        minSignalValue = min
    }

    private fun calculateAmplitude(): Double {
        return (maxSignalValue - minSignalValue) / 2
    }

    private fun calculateDc(): Double {
        return (maxSignalValue + minSignalValue) / 2
    }

    private fun calculateRms(): Double {
        var sum = 0.0
        for (value in signal) sum += (value - dc) * (value - dc)
        return sqrt(sum / signal.size)
    }

    private fun calculateFrequency(): Double {
        val estimatedFrequency = estimateFrequency()
        val accuracyCoefficient = 500
        if (estimatedFrequency < accuracyCoefficient) return defineFrequency()
        else return estimatedFrequency.toDouble()
    }

    private fun estimateFrequency(): Int {
        var isPositivePartOfSignal = false
        var frequency = 0
        val filteringCoefficient = 1.05
        val lowerBound = if (dc < 0) dc * filteringCoefficient else dc / filteringCoefficient
        val upperBound = if (dc < 0) dc / filteringCoefficient else dc * filteringCoefficient

        for (value in signal) {
            if (value >= upperBound && !isPositivePartOfSignal) {
                frequency++
                isPositivePartOfSignal = true
            } else if (value < lowerBound && isPositivePartOfSignal) {
                isPositivePartOfSignal = false
            }
        }

        return frequency
    }

    private fun defineFrequency(): Double {
        val shift = 1000
        val isFirstPeriod = false
        val isPositivePartOfSignal = true
        val filteringCoefficient = 1.05
        bufferedSamplesPerSemiPeriods = 0
        periods = 0
        samplesPerSemiPeriod = 0
        zeroTransitionCounter = 0

        for (value in signal) {
            val shiftedValue = value + shift
            val centerOfSignal = dc + shift
            val lowerBound = if (centerOfSignal < 0) centerOfSignal * filteringCoefficient else centerOfSignal / filteringCoefficient
            val upperBound = if (centerOfSignal < 0) centerOfSignal / filteringCoefficient else centerOfSignal * filteringCoefficient

            if (zeroTransitionCounter >= 1) samplesPerSemiPeriod++

            if (shiftedValue >= upperBound && isFirstPeriod) {
            } else if (shiftedValue < lowerBound && isPositivePartOfSignal) {
                countPeriods()
            } else if (value >= upperBound && !isFirstPeriod && !isPositivePartOfSignal) {
                countPeriods()
            }
        }

        val samplesPerPeriod = if (bufferedSamplesPerSemiPeriods == 0) 0 else bufferedSamplesPerSemiPeriods / periods
        return if (samplesPerPeriod == 0) 0.0 else signal.size.toDouble() / samplesPerPeriod.toDouble()
    }

    private fun countPeriods() {
        zeroTransitionCounter++
        if (zeroTransitionCounter % 2 !=0 && zeroTransitionCounter > 2) {
            bufferedSamplesPerSemiPeriods = samplesPerSemiPeriod
            periods++
        }
    }
}