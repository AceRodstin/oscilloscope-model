package ru.Controllers.Regulator;

import javafx.application.Platform;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import ru.Controllers.MainController;
import ru.Models.RegulatorModel;
import ru.Models.SignalModel;

public class RegulatorController {
    private Label amplitudeLabel;
    private TextField amplitudeTextField;
    private Label dcLabel;
    private TextField dcTextField;
    private Label frequencyLabel;
    private Label noiseLabel;
    private ComboBox<String> noiseTypesComboBox;
    private TextField frequencyTextField;
    private Label phaseLabel;
    private TextField phaseTextField;
    private Label signalSettingsLabel;
    private Label signalTypeLabel;
    private ComboBox<String> signalTypeComboBox;

    private RegulatorModel regulatorModel = new RegulatorModel();
    private SignalModel signalModel;

    public void initialize(MainController mainController) {
        amplitudeLabel = mainController.getAmplitudeLabel();
        amplitudeTextField = mainController.getAmplitudeTextField();
        dcLabel = mainController.getDcLabel();
        dcTextField = mainController.getDcTextField();
        frequencyLabel = mainController.getFrequencyLabel();
        frequencyTextField = mainController.getFrequencyTextField();
        noiseLabel = mainController.getNoiseLabel();
        noiseTypesComboBox = mainController.getNoiseTypesComboBox();
        phaseLabel = mainController.getPhaseLabel();
        phaseTextField = mainController.getPhaseTextField();
        signalSettingsLabel = mainController.getSignalSettingsLabel();
        signalTypeLabel = mainController.getSignalTypeLabel();
        signalTypeComboBox = mainController.getSignalTypeComboBox();
        signalModel = mainController.getSignalController().getSignalModel();
    }

    public void toggleRegulator(boolean isEnable) {
        if (isEnable) {
            Platform.runLater(() -> {
                signalSettingsLabel.setText("Настройки регулятора:");
                amplitudeLabel.setText("П - коэффициент:");
                amplitudeTextField.setText("0");
                frequencyLabel.setText("И - коэффициент:");
                frequencyTextField.setText("0");
                phaseLabel.setText("Д - коэффициент:");
                phaseTextField.setText("0");
            });
        } else {
            Platform.runLater(() -> {
                signalSettingsLabel.setText("Настройки генерируемого сигнала:");
                amplitudeLabel.setText("Амплитуда, В:");
                amplitudeTextField.setText(String.valueOf(signalModel.getAmplitude()));
                frequencyLabel.setText("Частота, Гц:");
                frequencyTextField.setText(String.valueOf(signalModel.getFrequency()));
                phaseLabel.setText("Фаза, °:");
                phaseTextField.setText(String.valueOf(signalModel.getPhase()));
            });
        }

        amplitudeLabel.setDisable(!isEnable);
        amplitudeTextField.setDisable(!isEnable);
        frequencyLabel.setDisable(!isEnable);
        frequencyTextField.setDisable(!isEnable);
        phaseLabel.setDisable(!isEnable);
        phaseTextField.setDisable(!isEnable);
        dcLabel.setVisible(!isEnable);
        dcTextField.setVisible(!isEnable);
        signalTypeLabel.setVisible(!isEnable);
        signalTypeComboBox.setVisible(!isEnable);
        noiseLabel.setVisible(!isEnable);
        noiseTypesComboBox.setVisible(!isEnable);
    }
}
