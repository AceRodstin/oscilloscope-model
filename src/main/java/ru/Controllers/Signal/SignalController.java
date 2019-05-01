package ru.Controllers.Signal;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TextField;
import ru.Controllers.MainController;
import ru.Models.SignalModel;

public class SignalController {
    private MainController mainController;
    private String noiseType;
    private SignalModel signalModel;
    private String signalType;

    public SignalController(MainController mainController) {
        this.mainController = mainController;
        signalModel = new SignalModel();
    }

    public void initialize() {
        initTextFields();
        initComboBoxes();
    }

    private void initTextFields() {
        addDigitFiltersToTextFields();
    }

    private void addDigitFiltersToTextFields() {
        setDigitFilter(mainController.getAmplitudeTextField());
        setDigitFilter(mainController.getDcTextField());
        setDigitFilter(mainController.getFrequencyTextField());
        setDigitFilter(mainController.getPhaseTextField());
    }

    private void setDigitFilter(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            textField.setText(newValue.replaceAll("[^-\\d(\\.|,)]", ""));
            if (!newValue.matches("^-?[\\d]+(\\.|,)\\d+|^-?[\\d]+(\\.|,)|^-?[\\d]+|-|$")) {
                textField.setText(oldValue);
            }
        });
    }

    private void initComboBoxes() {
        setSignalTypes();
        setNoiseTypes();
    }

    private void setSignalTypes() {
        ObservableList<String> types = FXCollections.observableArrayList();

        types.add(SignalTypes.SINE.getTypeName());
        types.add(SignalTypes.PULSE.getTypeName());
        types.add(SignalTypes.TRIANGLES.getTypeName());
        types.add(SignalTypes.SAW.getTypeName());
        types.add(SignalTypes.NOISE.getTypeName());

        mainController.getSignalTypeComboBox().setItems(types);
        mainController.getSignalTypeComboBox().getSelectionModel().select(0);
    }

    private void setNoiseTypes() {
        ObservableList<String> types = FXCollections.observableArrayList();

        types.add(NoiseTypes.NONE.getTypeName());
        types.add(NoiseTypes.LOW.getTypeName());
        types.add(NoiseTypes.MIDDLE.getTypeName());
        types.add(NoiseTypes.HIGH.getTypeName());

        mainController.getNoiseTypesComboBox().setItems(types);
        mainController.getNoiseTypesComboBox().getSelectionModel().select(0);
    }

    public boolean checkEmptyTextFields() {
        return mainController.getAmplitudeTextField().getText().isEmpty() ||
                mainController.getDcTextField().getText().isEmpty() ||
                mainController.getFrequencyTextField().getText().isEmpty() ||
                mainController.getPhaseTextField().getText().isEmpty();
    }

    public void generateSignal() {
        parseSignalParameters();
        signalModel.setNoiseType(noiseType);
        signalModel.generateSignal(signalType);
    }

    public void parseSignalParameters() {
        signalModel.setAmplitude(Double.parseDouble(mainController.getAmplitudeTextField().getText()));
        signalModel.setDc(Double.parseDouble(mainController.getDcTextField().getText()));
        signalModel.setFrequency(Double.parseDouble(mainController.getFrequencyTextField().getText()));
        signalModel.setPhase(Double.parseDouble(mainController.getPhaseTextField().getText()));

        signalType = mainController.getSignalTypeComboBox().getSelectionModel().getSelectedItem();
        noiseType = mainController.getNoiseTypesComboBox().getSelectionModel().getSelectedItem();
    }

    public SignalModel getSignalModel() {
        return signalModel;
    }
}
