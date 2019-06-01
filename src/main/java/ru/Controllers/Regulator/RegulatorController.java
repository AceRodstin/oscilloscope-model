package ru.Controllers.Regulator;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import ru.Controllers.MainController;
import ru.Controllers.Signal.SignalTypes;
import ru.Models.RegulatorModel;
import ru.Models.SignalModel;
import ru.Utils.Utils;

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
    private boolean regulatorEnabled;
    private TextField responseAmplitudeTextField;
    private TextField responseDcTextField;
    private TextField responseFrequencyTextField;
    private TextField responseRmsTextField;
    private Label signalSettingsLabel;
    private Label signalTypeLabel;
    private ComboBox<String> signalTypeComboBox;

    private RegulatorModel regulatorModel = new RegulatorModel();
    private MainController mainController;

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
        responseAmplitudeTextField = mainController.getReceivedAmplitudeTextField();
        responseDcTextField = mainController.getReceivedDCTextField();
        responseFrequencyTextField = mainController.getReceivedFrequencyTextField();
        responseRmsTextField = mainController.getReceivedRmsTextField();
        signalSettingsLabel = mainController.getSignalSettingsLabel();
        signalTypeLabel = mainController.getSignalTypeLabel();
        signalTypeComboBox = mainController.getSignalTypeComboBox();
        this.mainController = mainController;
    }

    public void toggleRegulator(boolean isEnable) {
        regulatorEnabled = isEnable;
        listenTextFields();
        listenNeededParameterComboBox();
        toggleNeededParameter();

        if (isEnable) {
            Platform.runLater(() -> {
                signalSettingsLabel.setText("Настройки регулятора:");
                amplitudeLabel.setText("П - коэффициент:");
                amplitudeTextField.setText(String.valueOf(regulatorModel.getpCoefficient()));
                frequencyLabel.setText("И - коэффициент:");
                frequencyTextField.setText(String.valueOf(regulatorModel.getiCoefficient()));
                phaseLabel.setText("Д - коэффициент:");
                phaseTextField.setText(String.valueOf(regulatorModel.getdCoefficient()));
            });
        } else {
            Platform.runLater(() -> {
                signalSettingsLabel.setText("Настройки генерируемого сигнала:");
                amplitudeLabel.setText("Амплитуда, В:");
                amplitudeTextField.setText(String.valueOf(mainController.getSignalController().getSignalModel().getAmplitude()));
                frequencyLabel.setText("Частота, Гц:");
                frequencyTextField.setText(String.valueOf(mainController.getSignalController().getSignalModel().getFrequency()));
                phaseLabel.setText("Фаза, °:");
                phaseTextField.setText(String.valueOf(mainController.getSignalController().getSignalModel().getPhase()));
            });
        }

        Platform.runLater(() -> {
            amplitudeLabel.setDisable(!isEnable);
            amplitudeTextField.setDisable(!isEnable);
            frequencyLabel.setDisable(!isEnable);
            frequencyTextField.setDisable(!isEnable);
            phaseLabel.setDisable(!isEnable);
            phaseTextField.setDisable(!isEnable);
            noiseLabel.setVisible(!isEnable);
            noiseTypesComboBox.setVisible(!isEnable);
        });
    }

    private void listenTextFields() {
        if (regulatorEnabled) {
            amplitudeTextField.textProperty().addListener((observable, oldValue, newValue) -> {
                if (regulatorEnabled) {
                    regulatorModel.setpCoefficient(Double.parseDouble(newValue));
                }
            });

            frequencyTextField.textProperty().addListener((observable, oldValue, newValue) -> {
                if (regulatorEnabled) {
                    regulatorModel.setiCoefficient(Double.parseDouble(newValue));
                }
            });

            phaseTextField.textProperty().addListener((observable, oldValue, newValue) -> {
                if (regulatorEnabled) {
                    regulatorModel.setdCoefficient(Double.parseDouble(newValue));
                }
            });

            dcTextField.textProperty().addListener((observable, oldValue, newValue) -> {
                if (regulatorEnabled) {
                    regulatorModel.setNeededParameter(Double.parseDouble(newValue));
                }
            });
        }
    }

    private void listenNeededParameterComboBox() {
        signalTypeComboBox.valueProperty().addListener(observable -> {
            if (regulatorEnabled) {
                regulatorModel.setSelectedParameter(signalTypeComboBox.getSelectionModel().getSelectedItem());
            }
        });
    }

    private void toggleNeededParameter() {
        if (regulatorEnabled) {
            String selectedParameter = regulatorModel.getSelectedParameter();

            Platform.runLater(() -> {
                dcLabel.setText("Норма:");
                dcTextField.setText(String.valueOf(regulatorModel.getNeededParameter()));
                signalTypeLabel.setText("Регулировать по:");
                setNeededParameters();
                signalTypeComboBox.getSelectionModel().select(selectedParameter);
            });
        } else {
            String selectedType = mainController.getSignalController().getSignalType();

            Platform.runLater(() -> {
                dcLabel.setText("Статика, В:");
                dcTextField.setText(String.valueOf(mainController.getSignalController().getSignalModel().getDc()));
                signalTypeLabel.setText("Тип сигнала:");
                setSignalTypes();
                signalTypeComboBox.getSelectionModel().select(selectedType);
            });
        }

        Platform.runLater(() -> {
            dcLabel.setDisable(!regulatorEnabled);
            dcTextField.setDisable(!regulatorEnabled);
            signalTypeLabel.setDisable(!regulatorEnabled);
            signalTypeComboBox.setDisable(!regulatorEnabled);
        });
    }

    private void setNeededParameters() {
        ObservableList<String> parameters = FXCollections.observableArrayList();

        parameters.add(NeededParameters.AMPLITUDE.getParameterName());
        parameters.add(NeededParameters.DC.getParameterName());
        parameters.add(NeededParameters.FREQUENCY.getParameterName());
        parameters.add(NeededParameters.RMS.getParameterName());

        signalTypeComboBox.setItems(parameters);
    }

    private void setSignalTypes() {
        ObservableList<String> types = FXCollections.observableArrayList();

        types.add(SignalTypes.SINE.getTypeName());
        types.add(SignalTypes.PULSE.getTypeName());
        types.add(SignalTypes.TRIANGLES.getTypeName());
        types.add(SignalTypes.SAW.getTypeName());
        types.add(SignalTypes.NOISE.getTypeName());

        mainController.getSignalTypeComboBox().setItems(types);
    }


    public RegulatorModel getRegulatorModel() {
        return regulatorModel;
    }
}
