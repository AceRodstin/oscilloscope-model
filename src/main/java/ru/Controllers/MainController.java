package ru.Controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.controlsfx.control.StatusBar;
import ru.Models.GraphModel;
import ru.Models.SignalModel;
import ru.Utils.BaseController;
import ru.Utils.ControllerManager;
import ru.Utils.StatusBarLine;
import ru.Utils.Utils;

import java.util.List;

public class MainController implements BaseController {
    @FXML
    private Label amplitudeLabel;
    @FXML
    private TextField amplitudeTextField;
    @FXML
    private ComboBox<String> filterComboBox;
    @FXML
    private Label frequencyLabel;
    @FXML
    private TextField frequencyTextField;
    @FXML
    private Button generateButton;
    @FXML
    private LineChart<Number, Number> graph;
    @FXML
    private ComboBox<String> graphTypeComboBox;
    @FXML
    private ComboBox<String> horizontalScalesComboBox;
    @FXML
    private Label phaseLabel;
    @FXML
    private TextField phaseTextField;
    @FXML
    private ComboBox<String> signalTypeComboBox;
    @FXML
    private Label signalTypeLabel;
    @FXML
    private StatusBar statusBar;
    @FXML
    private ComboBox<String> verticalScalesComboBox;

    private int buttonPressedCounter;
    private ControllerManager controllerManager;
    private GraphController graphController = new GraphController(this);
    private Thread showSignal;
    private boolean signalParametersSet;
    private SignalModel signalModel = new SignalModel();
    private StatusBarLine statusBarLine = new StatusBarLine();

    public void initialize() {
        initializeGraph();
        graphController.initComboBoxes();
        addDigitFiltersToTextFields();
        setSignalTypes();
    }

    private void initializeGraph() {
        graph.getData().add(signalModel.getGraphSeries());
    }


    private void addDigitFiltersToTextFields() {
        setDigitFilter(amplitudeTextField);
        setDigitFilter(frequencyTextField);
        setDigitFilter(phaseTextField);
    }


    private void setDigitFilter(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            textField.setText(newValue.replaceAll("[^-\\d(\\.|,)]", ""));
            if (!newValue.matches("^-?[\\d]+(\\.|,)\\d+|^-?[\\d]+(\\.|,)|^-?[\\d]+|-|$")) {
                textField.setText(oldValue);
            }
        });
    }

    private void setSignalTypes() {
        ObservableList<String> types = FXCollections.observableArrayList();

        types.add(SignalTypes.SINE.getTypeName());
        types.add(SignalTypes.PULSE.getTypeName());
        types.add(SignalTypes.TRIANGLES.getTypeName());
        types.add(SignalTypes.SAW.getTypeName());
        types.add(SignalTypes.NOISE.getTypeName());

        signalTypeComboBox.setItems(types);
        signalTypeComboBox.getSelectionModel().select(0);
    }

    public void handleGenerate() {
        show();
        buttonPressedCounter++;
        checkGenerationState();
    }

    private void toggleUiElementsState(boolean isDisable) {
        amplitudeLabel.setDisable(isDisable);
        amplitudeTextField.setDisable(isDisable);
        frequencyLabel.setDisable(isDisable);
        frequencyTextField.setDisable(isDisable);
        phaseLabel.setDisable(isDisable);
        phaseTextField.setDisable(isDisable);
        signalTypeComboBox.setDisable(isDisable);
        signalTypeLabel.setDisable(isDisable);
    }

    private void show() {
        showSignal = new Thread(() -> {
            while (!controllerManager.isFinished()) {
                checkEmptyTextFields();
                if (signalParametersSet) {
                    clearGraph();
                    parseSignalParameters();
                    signalModel.generateSignal(signalTypeComboBox.getSelectionModel().getSelectedItem());
                    graphController.showSignal();
                }
            }
        });

        showSignal.start();
    }

    private void checkGenerationState() {
        if (buttonPressedCounter % 2 == 0) {
            controllerManager.setFinished(true);
            showSignal.interrupt();
            generateButton.setText("Генерировать");
            toggleUiElementsState(false);
        } else {
            controllerManager.setFinished(false);
            generateButton.setText("Остановить генерацию");
            toggleUiElementsState(true);
        }
    }

    private void checkEmptyTextFields() {
        if (amplitudeTextField.getText().isEmpty() ||
                frequencyTextField.getText().isEmpty() ||
                phaseTextField.getText().isEmpty()) {
            Platform.runLater(() -> statusBarLine.setStatus("Перед генерацией необходимо указать параметры сигнала", statusBar));
            signalParametersSet = false;
        } else {
            signalParametersSet = true;
        }
    }

    private void clearGraph() {
        Platform.runLater(() -> {
            signalModel.getIntermediateList().clear();
            signalModel.getGraphSeries().getData().clear();
        });
        Utils.sleep(50);
    }

    private void parseSignalParameters() {
        signalModel.setAmplitude(Double.parseDouble(amplitudeTextField.getText()));
        signalModel.setFrequency(Double.parseDouble(frequencyTextField.getText()));
        signalModel.setPhase(Double.parseDouble(phaseTextField.getText()));
    }

    public void restartShowDataThread() {
        controllerManager.setFinished(true);
        showSignal.interrupt();
        Utils.sleep(100);
        controllerManager.setFinished(false);
        show();
    }

    public ControllerManager getControllerManager() {
        return controllerManager;
    }

    public ComboBox<String> getFilterComboBox() {
        return filterComboBox;
    }

    public LineChart<Number, Number> getGraph() {
        return graph;
    }

    public ComboBox<String> getGraphTypeComboBox() {
        return graphTypeComboBox;
    }

    public ComboBox<String> getHorizontalScalesComboBox() {
        return horizontalScalesComboBox;
    }

    public SignalModel getSignalModel() {
        return signalModel;
    }

    public ComboBox<String> getVerticalScalesComboBox() {
        return verticalScalesComboBox;
    }

    @Override
    public void setControllerManager(ControllerManager controllerManager) {
        this.controllerManager = controllerManager;
    }
}
