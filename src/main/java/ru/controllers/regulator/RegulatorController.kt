package ru.controllers.regulator

import javafx.application.Platform
import javafx.collections.FXCollections
import javafx.scene.control.ComboBox
import javafx.scene.control.TextField
import ru.controllers.MainController
import ru.controllers.signal.SignalTypes
import ru.models.RegulatorModel
import ru.utils.Utils


class RegulatorController(val mainController: MainController) {
    var isRegulatorEnabled = false
    private val regulatorModel = RegulatorModel()

    fun toggleRegulator(isEnable: Boolean) {
        isRegulatorEnabled = isEnable
        listenTextFields()
        listen(mainController.signalTypeComboBox)
        toggleUiElements()
        correctControlledParameter()
    }

    private fun listenTextFields() {
        listen(mainController.amplitudeTextField) { value -> regulatorModel.pCoefficient = value }
        listen(mainController.frequencyTextField) { value -> regulatorModel.iCoefficient = value }
        listen(mainController.phaseTextField) { value -> regulatorModel.dCoefficient = value }
        listen(mainController.dcTextField) { value -> regulatorModel.setNeededValueOfControlledParameter(value) }
    }

    private fun listen(textField: TextField, option: (Double) -> Unit) {
        textField.textProperty().addListener { _, _, newValue ->
            if (isRegulatorEnabled && textField.text.isNotEmpty() && textField.text != "-") {
                option(newValue.toDouble())
            }
        }
    }

    private fun listen(comboBox: ComboBox<String>) {
        comboBox.valueProperty().addListener { _ ->
            if (isRegulatorEnabled) {
                when (comboBox.selectionModel.selectedItem) {
                    ControlledParameters.AMPLITUDE.parameterName -> regulatorModel.selectedParameter = ControlledParameters.AMPLITUDE
                    ControlledParameters.DC.parameterName -> regulatorModel.selectedParameter = ControlledParameters.DC
                    ControlledParameters.FREQUENCY.parameterName -> regulatorModel.selectedParameter = ControlledParameters.FREQUENCY
                }
                regulatorModel.setNeededValueOfControlledParameter(parseNeededValueOfControlledParameter(mainController.dcTextField))
            }
        }
    }

    private fun parseNeededValueOfControlledParameter(textField: TextField): Double {
        return if (textField.text.isNotEmpty() && textField.text != "-") textField.text.toDouble() else 0.0
    }

    private fun toggleUiElements() {
        if (isRegulatorEnabled) {
            Platform.runLater {
                mainController.signalSettingsLabel.text = "Настройки регулятора:"
                mainController.amplitudeLabel.text = "П - коэффициент:"
                mainController.amplitudeTextField.text = regulatorModel.pCoefficient.toString()
                mainController.frequencyLabel.text = "И - коэффициент:"
                mainController.frequencyTextField.text = regulatorModel.iCoefficient.toString()
                mainController.phaseLabel.text = "Д - коэффициент:"
                mainController.phaseTextField.text = regulatorModel.dCoefficient.toString()
                mainController.dcLabel.text = "Норма:"
                mainController.dcTextField.text = regulatorModel.getNeededValueOfControlledParameter().toString()
                mainController.signalTypeLabel.text = "Регулировать по:"
                setControlledParameters()
            }
        } else {
            Platform.runLater {
                mainController.signalSettingsLabel.text = "Настройки генерируемого сигнала:"
                mainController.amplitudeLabel.text = "Амплитуда, В:"
                mainController.amplitudeTextField.text = mainController.signalController.amplitude.toString()
                mainController.frequencyLabel.text = "Частота, Гц:"
                mainController.frequencyTextField.text = mainController.signalController.frequency.toString()
                mainController.phaseLabel.text = "Фаза, °:"
                mainController.phaseTextField.text = mainController.signalController.phase.toString()
                mainController.dcLabel.text = "Статика, В:"
                mainController.dcTextField.text = mainController.signalController.dc.toString()
                mainController.signalTypeLabel.text = "Тип сигнала:"
                setSignalTypes()
            }
        }

        Platform.runLater {
            mainController.amplitudeLabel.isDisable = !isRegulatorEnabled
            mainController.amplitudeTextField.isDisable = !isRegulatorEnabled
            mainController.frequencyLabel.isDisable = !isRegulatorEnabled
            mainController.frequencyTextField.isDisable = !isRegulatorEnabled
            mainController.phaseLabel.isDisable = !isRegulatorEnabled
            mainController.phaseTextField.isDisable = !isRegulatorEnabled
            mainController.noiseLabel.isDisable = !isRegulatorEnabled
            mainController.noiseTypesComboBox.isDisable = !isRegulatorEnabled
            mainController.dcLabel.isDisable = !isRegulatorEnabled
            mainController.dcTextField.isDisable = !isRegulatorEnabled
            mainController.signalTypeLabel.isDisable = !isRegulatorEnabled
            mainController.signalTypeComboBox.isDisable = !isRegulatorEnabled
        }
    }

    private fun setControlledParameters() {
        val parameters = FXCollections.observableArrayList<String>(ControlledParameters.AMPLITUDE.parameterName,
                ControlledParameters.DC.parameterName, ControlledParameters.FREQUENCY.parameterName)
        mainController.signalTypeComboBox.items = parameters

        when (regulatorModel.selectedParameter) {
            ControlledParameters.AMPLITUDE -> mainController.signalTypeComboBox.selectionModel.select(ControlledParameters.AMPLITUDE.parameterName)
            ControlledParameters.DC -> mainController.signalTypeComboBox.selectionModel.select(ControlledParameters.DC.parameterName)
            ControlledParameters.FREQUENCY -> mainController.signalTypeComboBox.selectionModel.select(ControlledParameters.FREQUENCY.parameterName)
        }
    }

    private fun correctControlledParameter() {
        if (isRegulatorEnabled) {
            Thread {
                while (isRegulatorEnabled && !mainController.controllerManager.isFinished) {
                    when (regulatorModel.selectedParameter) {
                        ControlledParameters.AMPLITUDE -> mainController.signalController.amplitude += regulatorModel.getCorrection()
                        ControlledParameters.DC -> mainController.signalController.dc += regulatorModel.getCorrection()
                        ControlledParameters.FREQUENCY -> mainController.signalController.frequency += regulatorModel.getCorrection()
                    }
                    Utils.sleep(1000)
                }
            }.start()
        }
    }

    private fun setSignalTypes() {
        val types = FXCollections.observableArrayList<String>(SignalTypes.SINE.typeName,
                SignalTypes.PULSE.typeName, SignalTypes.TRIANGLES.typeName, SignalTypes.SAW.typeName,
                SignalTypes.NOISE.typeName)
        mainController.signalTypeComboBox.items = types
        mainController.signalTypeComboBox.selectionModel.select(mainController.signalController.signalType)
    }

    fun setResponseAmplitude(value: Double) {
        regulatorModel.responseAmplitude = value
    }

    fun setResponseDc(value: Double) {
        regulatorModel.responseDc = value
    }

    fun setResponseFrequency(value: Double) {
        regulatorModel.responseFrequency = value
    }
}