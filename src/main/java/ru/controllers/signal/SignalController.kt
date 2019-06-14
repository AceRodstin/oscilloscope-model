package ru.controllers.signal

import javafx.application.Platform
import javafx.collections.FXCollections
import javafx.scene.Node
import javafx.scene.chart.XYChart
import javafx.scene.control.Control
import javafx.scene.control.TextField
import ru.controllers.MainController
import ru.controllers.graph.GraphTypes
import ru.models.SignalModel
import ru.utils.Utils


class SignalController(private val mainController: MainController) {
    var noiseType = NoiseTypes.NONE
    private val signalModel = SignalModel()
    private var signalSettings = listOf<Control>()
    var signalType = SignalTypes.SINE

    fun initialize() {
        fillSignalSettings()
        initializeTextFields()
        initializeSettings()
    }

    private fun fillSignalSettings() {
        signalSettings = listOf(
                mainController.amplitudeLabel,
                mainController.amplitudeTextField,
                mainController.dcLabel,
                mainController.dcTextField,
                mainController.frequencyLabel,
                mainController.frequencyTextField,
                mainController.noiseLabel,
                mainController.noiseTypesComboBox,
                mainController.phaseLabel,
                mainController.phaseTextField,
                mainController.signalTypeComboBox,
                mainController.signalTypeLabel
        )
    }

    private fun initializeTextFields() {
        setDigitFilter(mainController.amplitudeTextField)
        setDigitFilter(mainController.dcTextField)
        setDigitFilter(mainController.frequencyTextField)
        setDigitFilter(mainController.phaseTextField)
        setDigitFilter(mainController.filterTextField)
    }

    private fun setDigitFilter(textField: TextField) {
        textField.textProperty().addListener { _, oldValue, newValue ->
            textField.text = newValue.replace("[^-\\d(.|,)]".toRegex(), "")
            if (!newValue.matches("^-?[\\d]+([.,])\\d+|^-?[\\d]+([.,])|^-?[\\d]+|-|$".toRegex())) {
                textField.text = oldValue
            }
        }
    }

    private fun initializeSettings() {
        setSignalTypes()
        setNoiseTypes()
    }

    private fun setSignalTypes() {
        val types = FXCollections.observableArrayList<String>(SignalTypes.SINE.typeName, SignalTypes.PULSE.typeName,
                SignalTypes.TRIANGLES.typeName, SignalTypes.SAW.typeName, SignalTypes.NOISE.typeName)
        mainController.signalTypesComboBox.items = types
        mainController.signalTypesComboBox.selectionModel.select(SignalTypes.SINE.typeName)
    }

    private fun setNoiseTypes() {
        val types = FXCollections.observableArrayList<String>(NoiseTypes.NONE.typeName, NoiseTypes.LOW.typeName,
                NoiseTypes.MIDDLE.typeName, NoiseTypes.HIGH.typeName)
        mainController.noiseTypesComboBox.items = types
        mainController.noiseTypesComboBox.selectionModel.select(NoiseTypes.NONE.typeName)
    }

    fun checkEmptyFields(): Boolean {
        return mainController.amplitudeTextField.text.isEmpty() ||
                mainController.dcTextField.text.isEmpty() ||
                mainController.frequencyTextField.text.isEmpty() ||
                mainController.phaseTextField.text.isEmpty()
    }

    fun generateSignal() {
        signalModel.generateSignal(signalType, noiseType)
    }

    fun parseSignalParameters() {
        signalModel.amplitude = mainController.amplitudeTextField.text.toDouble()
        signalModel.dc = mainController.dcTextField.text.toDouble()
        signalModel.frequency = mainController.frequencyTextField.text.toDouble()
        signalModel.phase = mainController.phaseTextField.text.toDouble()

        when (mainController.signalTypesComboBox.selectionModel.selectedItem) {
            SignalTypes.SINE.typeName -> signalType = SignalTypes.SINE
            SignalTypes.PULSE.typeName -> signalType = SignalTypes.PULSE
            SignalTypes.TRIANGLES.typeName -> signalType = SignalTypes.TRIANGLES
            SignalTypes.SAW.typeName -> signalType = SignalTypes.SAW
            SignalTypes.NOISE.typeName -> signalType = SignalTypes.NOISE
        }

        when (mainController.noiseTypesComboBox.selectionModel.selectedItem) {
            NoiseTypes.NONE.typeName -> noiseType = NoiseTypes.NONE
            NoiseTypes.LOW.typeName -> noiseType = NoiseTypes.LOW
            NoiseTypes.MIDDLE.typeName -> noiseType = NoiseTypes.MIDDLE
            NoiseTypes.HIGH.typeName -> noiseType = NoiseTypes.HIGH
        }
    }

    fun showSignalParameters() {
        signalModel.calculateSignalParameters()
        val decimalFormat = getDecimalFormat()
        val amplitude = Utils.roundValue(signalModel.getReceivedAmplitude(), decimalFormat)
        val dc = Utils.roundValue(signalModel.getReceivedDc(), decimalFormat)
        val frequency = Utils.roundValue(signalModel.getReceivedFrequency(), decimalFormat)
        val rms = Utils.roundValue(signalModel.getRms(), decimalFormat)

        mainController.regulatorController.setResponseAmplitude(amplitude)
        mainController.regulatorController.setResponseDc(dc)
        mainController.regulatorController.setResponseFrequency(frequency)

        Platform.runLater {
            mainController.receivedAmplitudeTextField.text = Utils.convertFromExponentialFormat(amplitude, decimalFormat)
            mainController.receivedDcTextField.text = Utils.convertFromExponentialFormat(dc, decimalFormat)
            mainController.receivedFrequencyTextField.text = Utils.convertFromExponentialFormat(frequency, decimalFormat)
            mainController.receivedRmsTextField.text = Utils.convertFromExponentialFormat(rms, decimalFormat)
        }
    }

    fun toggleUiElementsState(isDisable: Boolean) {
        Platform.runLater {
            if (mainController.graphTypesComboBox.selectionModel.selectedItem != GraphTypes.REGULATOR.typeName) {
                for (uiElement in signalSettings) uiElement.isDisable = isDisable
            } else {
                for (uiElement in signalSettings) uiElement.isDisable = !isDisable
            }

            mainController.receivedAmplitudeLabel.isDisable = !isDisable
            mainController.receivedAmplitudeTextField.isDisable = !isDisable
            mainController.receivedDcLabel.isDisable = !isDisable
            mainController.receivedDcTextField.isDisable = !isDisable
            mainController.receivedFrequencyLabel.isDisable = !isDisable
            mainController.receivedFrequencyTextField.isDisable = !isDisable
            mainController.receivedRmsLabel.isDisable = !isDisable
            mainController.receivedRmsTextField.isDisable = !isDisable
        }
    }

    private fun getDecimalFormat(): Int {
        return Math.pow(10.0, (mainController.decimalFormatComboBox.selectionModel.selectedIndex + 1).toDouble()).toInt()
    }

    fun toggleFilter(isEnable: Boolean) {
        signalModel.isFilterEnable = isEnable
    }

    fun fillIntermediateList(isFFT: Boolean) {
        signalModel.fillIntermediateList(isFFT)
    }

    fun getAmplitude(): Double {
        return signalModel.amplitude
    }

    fun getDc(): Double {
        return signalModel.dc
    }

    fun getFrequency(): Double {
        return signalModel.frequency
    }

    fun getIntermediateList(): List<XYChart.Data<Number, Number>> {
        return signalModel.intermediateList
    }

    fun getPhase(): Double {
        return signalModel.phase
    }

    fun getSeries(): XYChart.Series<Number, Number> {
        return signalModel.graphSeries
    }

    fun setAmplitude(value: Double) {
        signalModel.amplitude = value
    }

    fun setDc(value: Double) {
        signalModel.dc = value
    }

    fun setFilter(filterOrder: Int, cutoffFrequency: Double) {
        signalModel.setFilter(filterOrder, cutoffFrequency)
    }

    fun setFrequency(value: Double) {
        signalModel.frequency = value
    }
}