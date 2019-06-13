package ru.models

import javafx.scene.chart.XYChart
import org.vitrivr.cineast.core.util.dsp.fft.FFT
import org.vitrivr.cineast.core.util.dsp.fft.windows.HanningWindow
import ru.controllers.signal.NoiseTypes
import ru.controllers.signal.SignalTypes
import ru.utils.Utils
import uk.me.berndporr.iirj.Butterworth
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.*

class SignalModel {
    var amplitude: Double = 0.0
        get() = Utils.roundValue(field, 100_000)
    private val butterworth = Butterworth()
    var dc = 0.0
        get() = Utils.roundValue(field, 100_000)
    val fft = FFT()
    var frequency = 0.0
        get() = Utils.roundValue(field, 100_000)
    var graphSeries = XYChart.Series<Number, Number>()
    private val hanningWindow = HanningWindow()
    val intermediateList = ArrayList<XYChart.Data<Number, Number>>()
    var isFilterEnable = false
    private var noiseCoefficient = 0.0
    var phase = 0.0
        get() = Utils.roundValue(field, 100_000)
    val random = Random()
    var samples = 0
    private val signalParametersModel = SignalParametersModel()
    var signal = DoubleArray(samples)

    fun generateSignal(signalType: SignalTypes, noiseType: NoiseTypes) {
        defineSamplesRate()
        signal = DoubleArray(samples + 1)

        when (signalType) {
            SignalTypes.SINE -> generateSin(noiseType)
            SignalTypes.PULSE -> generatePulse(noiseType)
            SignalTypes.TRIANGLES -> generateTriangles(noiseType)
            SignalTypes.SAW -> generateSaw(noiseType)
            SignalTypes.NOISE -> generateNoise(noiseType)
        }
        filterSignal()
    }

    private fun defineSamplesRate() {
        if (frequency <= 10) samples = 500
        else if (frequency > 10 && frequency <= 50) samples = 1500
        else if (frequency > 50 && frequency <= 100) samples = 3000
        else if (frequency > 100 && frequency <= 200) samples = 6000
        else if (frequency > 200 && frequency <= 500) samples = 8000
        else if (frequency > 500) samples = 1000
    }

    private fun generateSin(noiseType: NoiseTypes) {
        for (index in 0 until (samples + 1)) {
            setNoise(noiseType)
            signal[index] = dc + (amplitude + noiseCoefficient) * sin(2 * PI * frequency *
                    (index.toDouble() / samples.toDouble()) + Math.toRadians(phase))

        }
    }

    private fun setNoise(noiseType: NoiseTypes) {
        when (noiseType) {
            NoiseTypes.NONE -> noiseCoefficient = 0.0
            NoiseTypes.LOW -> noiseCoefficient = amplitude * 0.15 * random.nextDouble()
            NoiseTypes.MIDDLE -> {
                noiseCoefficient = amplitude * 0.25 * random.nextDouble()
                dc += 0.05 * random.nextDouble()
                dc -= 0.05 * random.nextDouble()
            }
            NoiseTypes.HIGH -> {
                noiseCoefficient = amplitude * (0.05 * random.nextDouble() + random.nextDouble())
                phase += 5 * random.nextDouble()
                phase -= 5 * random.nextDouble()
                dc += 0.1 * random.nextDouble()
                dc -= 0.1 * random.nextDouble()
            }
        }
    }

    private fun generatePulse(noiseType: NoiseTypes) {
        for (index in 0 until (samples + 1)) {
            setNoise(noiseType)
            signal[index] = dc + (amplitude + noiseCoefficient) * Math.signum(sin(2 * PI * frequency *
                    (index.toDouble() / samples.toDouble()) + Math.toRadians(phase)))
        }
    }

    private fun generateTriangles(noiseType: NoiseTypes) {
        for (index in 0 until (samples + 1)) {
            setNoise(noiseType)
            signal[index] = dc + (2 * (amplitude + noiseCoefficient)) / PI * asin(sin(2 * PI * frequency *
                    index.toDouble() / samples.toDouble() + Math.toRadians(phase)))
        }
    }

    private fun generateSaw(noiseType: NoiseTypes) {
        for (index in 0 until (samples + 1)) {
            setNoise(noiseType)
            signal[index] = dc + (-2 * (amplitude + noiseCoefficient)) / PI * atan(1.0 / tan(index.toDouble() /
            samples.toDouble() * PI * frequency + Math.toRadians(phase)))
        }
    }

    private fun generateNoise(noiseType: NoiseTypes) {
        for (index in 0 until (samples + 1)) {
            setNoise(noiseType)
            signal[index] = dc + (amplitude + noiseCoefficient) * random.nextDouble() * sin(2 * PI * (frequency +
                    random.nextDouble()) * index.toDouble() / samples.toDouble() + Math.toRadians(phase))
        }
    }

    private fun filterSignal() {
        for (index in 0 until signal.size) {
            if (isFilterEnable) signal[index] = butterworth.filter(signal[index])
        }
    }

    fun fillIntermediateList(isFFT: Boolean) {
        intermediateList.clear()
        if (isFFT) {
            fft.forward(signal, signal.size.toFloat(), hanningWindow)
            for (index in 0 until fft.magnitudeSpectrum.size()) {
                val frequency = fft.magnitudeSpectrum.getFrequency(index)
                val magnitude = fft.magnitudeSpectrum.getValue(index)
                intermediateList.add(XYChart.Data(frequency, magnitude))
            }
        } else {
            for (index in 0 until signal.size) intermediateList.add(XYChart.Data(index.toDouble() / samples, signal[index]))
        }
    }

    fun calculateSignalParameters() { signalParametersModel.calculateParameters(signal) }

    fun getReceivedAmplitude(): Double {
        return signalParametersModel.amplitude
    }

    fun getReceivedDc(): Double {
        return signalParametersModel.dc
    }

    fun getReceivedFrequency(): Double {
        return signalParametersModel.frequency
    }

    fun getRms(): Double {
        return signalParametersModel.rms
    }

    fun setFilter(filterOrder: Int, cutoffFrequency: Double) { butterworth.lowPass(filterOrder, signal.size.toDouble(), cutoffFrequency) }
}