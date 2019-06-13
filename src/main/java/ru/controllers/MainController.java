package ru.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import org.controlsfx.control.StatusBar;
import ru.controllers.graph.GraphController;
import ru.controllers.regulator.RegulatorController;
import ru.controllers.signal.SignalController;
import ru.utils.BaseController;
import ru.utils.ControllerManager;
import ru.utils.StatusBarLine;
import ru.utils.Utils;

public class MainController implements BaseController {
    @FXML
    private Label amplitudeLabel;
    @FXML
    private TextField amplitudeTextField;
    @FXML
    private Label checkIcon;
    @FXML
    private Label dcLabel;
    @FXML
    private TextField dcTextField;
    @FXML
    private ComboBox<String> decimalFormatComboBox;
    @FXML
    private Label decimalFormatLabel;
    @FXML
    private Label filterLabel;
    @FXML
    private TextField filterTextField;
    @FXML
    private ComboBox<String> filterTypesComboBox;
    @FXML
    private Label filterTypesLabel;
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
    private Label graphTypeLabel;
    @FXML
    private ComboBox<String> horizontalScalesComboBox;
    @FXML
    private Label horizontalScalesLabel;
    @FXML
    private Label noiseLabel;
    @FXML
    private ComboBox<String> noiseTypesComboBox;
    @FXML
    private Label phaseLabel;
    @FXML
    private TextField phaseTextField;
    @FXML
    private ProgressIndicator progressIndicator;
    @FXML
    private Label receivedAmplitudeLabel;
    @FXML
    private TextField receivedAmplitudeTextField;
    @FXML
    private Label receivedDCLabel;
    @FXML
    private TextField receivedDCTextField;
    @FXML
    private Label receivedFrequencyLabel;
    @FXML
    private TextField receivedFrequencyTextField;
    @FXML
    private Label receivedRMSLabel;
    @FXML
    private TextField receivedRMSTextField;
    @FXML
    private Label signalSettingsLabel;
    @FXML
    private Label signalTypeLabel;
    @FXML
    private ComboBox<String> signalTypeComboBox;
    @FXML
    private StatusBar statusBar;
    @FXML
    private ComboBox<String> verticalScalesComboBox;
    @FXML
    private Label verticalScalesLabel;
    @FXML
    private Label warningIcon;

    private int buttonPressedCounter;
    private ControllerManager controllerManager;
    private GraphController graphController = new GraphController(this);
    private RegulatorController regulatorController = new RegulatorController(this);
    private Thread showSignal = new Thread();
    private SignalController signalController = new SignalController(this);
    private boolean signalParametersSet;
    private StatusBarLine statusBarLine;

    @FXML
    public void initialize() {
        graphController.initialize();
        signalController.initialize();
        statusBarLine = new StatusBarLine(checkIcon, progressIndicator, statusBar, warningIcon);
    }

    public void handleGenerate() {
        checkEmptyFields();
        setStatusBar();
        show();
        checkGenerationState();
    }

    private void checkEmptyFields() {
        signalParametersSet = !signalController.checkEmptyFields();

        if (!signalController.checkEmptyFields()) {
            buttonPressedCounter++;
        } else {
            Platform.runLater(() -> statusBarLine.setStatus("Перед генерацией необходимо указать параметры сигнала",
                    false));
        }
    }

    private void setStatusBar() {
        if (buttonPressedCounter % 2 != 0 && signalParametersSet) {
            Platform.runLater(() -> statusBarLine.setStatusOfProgress("Генерация сигнала"));
        } else if (buttonPressedCounter % 2 == 0 && signalParametersSet) {
            Platform.runLater(() -> {
                statusBarLine.clearStatusBar();
                statusBarLine.setStatus("Генерация сигнала остановлена", true);
            });
        }
    }

    private void toggleProgressIndicatorState(boolean isHidden) {
        if (isHidden) {
            progressIndicator.setStyle("-fx-opacity: 0;");
        } else {
            progressIndicator.setStyle("-fx-opacity: 1;");
        }
    }

    private void checkGenerationState() {
        if (buttonPressedCounter % 2 == 0 && signalParametersSet) {
            controllerManager.setFinished(true);
            showSignal.interrupt();
            graphController.clearSeries();
            setDefaultState();
            generateButton.setText("Генерировать");
            toggleUiElementsState(false);
        } else if (signalParametersSet) {
            controllerManager.setFinished(false);
            generateButton.setText("Остановить генерацию");
            toggleUiElementsState(true);
        }
    }

    private void setDefaultState() {
        Platform.runLater(() -> {
            receivedAmplitudeTextField.setText("0.0");
            receivedDCTextField.setText("0.0");
            receivedFrequencyTextField.setText("0.0");
            receivedRMSTextField.setText("0.0");

            toggleProgressIndicatorState(true);
            toggleUiElementsState(false);
            graphController.toggleFilter(false);
            verticalScalesComboBox.getSelectionModel().select(3);
            horizontalScalesComboBox.getSelectionModel().select(2);
            decimalFormatComboBox.getSelectionModel().select(1);
            graphTypeComboBox.getSelectionModel().select(0);
            filterTypesComboBox.getSelectionModel().select(0);
            filterTextField.setText("0");

            amplitudeTextField.setText("4");
            frequencyTextField.setText("10");
            phaseTextField.setText("0");
            dcTextField.setText("0");
            signalTypeComboBox.getSelectionModel().select(0);
            noiseTypesComboBox.getSelectionModel().select(0);
        });
    }

    private void toggleUiElementsState(boolean isDisable) {
        Platform.runLater(() -> {
            verticalScalesLabel.setDisable(!isDisable);
            verticalScalesComboBox.setDisable(!isDisable);
            horizontalScalesLabel.setDisable(!isDisable);
            horizontalScalesComboBox.setDisable(!isDisable);
            graphTypeLabel.setDisable(!isDisable);
            graphTypeComboBox.setDisable(!isDisable);
            filterTypesLabel.setDisable(!isDisable);
            filterTypesComboBox.setDisable(!isDisable);
            decimalFormatComboBox.setDisable(!isDisable);
            decimalFormatLabel.setDisable(!isDisable);

            amplitudeLabel.setDisable(isDisable);
            amplitudeTextField.setDisable(isDisable);
            dcLabel.setDisable(isDisable);
            dcTextField.setDisable(isDisable);
            frequencyLabel.setDisable(isDisable);
            frequencyTextField.setDisable(isDisable);
            noiseLabel.setDisable(isDisable);
            noiseTypesComboBox.setDisable(isDisable);
            phaseLabel.setDisable(isDisable);
            phaseTextField.setDisable(isDisable);
            signalTypeComboBox.setDisable(isDisable);
            signalTypeLabel.setDisable(isDisable);

            receivedAmplitudeLabel.setDisable(!isDisable);
            receivedAmplitudeTextField.setDisable(!isDisable);
            receivedDCLabel.setDisable(!isDisable);
            receivedDCTextField.setDisable(!isDisable);
            receivedFrequencyLabel.setDisable(!isDisable);
            receivedFrequencyTextField.setDisable(!isDisable);
            receivedRMSLabel.setDisable(!isDisable);
            receivedRMSTextField.setDisable(!isDisable);
        });
    }

    public void show() {
        showSignal = new Thread(() -> {
            while (!controllerManager.isFinished()) {
                if (signalParametersSet) {
                    signalController.generateSignal();
                    signalController.showSignalParameters();
                    graphController.clearSeries();
                    graphController.showSignal();
                    Utils.sleep(500);
                }
            }
        });

        showSignal.start();
    }

    public Label getAmplitudeLabel() {
        return amplitudeLabel;
    }

    public TextField getAmplitudeTextField() {
        return amplitudeTextField;
    }

    public ControllerManager getControllerManager() {
        return controllerManager;
    }

    public Label getDcLabel() {
        return dcLabel;
    }

    public TextField getDcTextField() {
        return dcTextField;
    }

    public ComboBox<String> getDecimalFormatComboBox() {
        return decimalFormatComboBox;
    }

    public Label getFilterLabel() {
        return filterLabel;
    }

    public TextField getFilterTextField() {
        return filterTextField;
    }

    public ComboBox<String> getFilterTypesComboBox() {
        return filterTypesComboBox;
    }

    public Label getFrequencyLabel() {
        return frequencyLabel;
    }

    public TextField getFrequencyTextField() {
        return frequencyTextField;
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

    public Label getNoiseLabel() {
        return noiseLabel;
    }

    public ComboBox<String> getNoiseTypesComboBox() {
        return noiseTypesComboBox;
    }

    public Label getPhaseLabel() {
        return phaseLabel;
    }

    public TextField getPhaseTextField() {
        return phaseTextField;
    }

    public TextField getReceivedAmplitudeTextField() {
        return receivedAmplitudeTextField;
    }

    public TextField getReceivedDcTextField() {
        return receivedDCTextField;
    }

    public TextField getReceivedFrequencyTextField() {
        return receivedFrequencyTextField;
    }

    public TextField getRmsTextField() {
        return receivedRMSTextField;
    }

    public RegulatorController getRegulatorController() {
        return regulatorController;
    }

    public Thread getShowSignalThread() {
        return showSignal;
    }

    public SignalController getSignalController() {
        return signalController;
    }

    public Label getSignalSettingsLabel() {
        return signalSettingsLabel;
    }

    public Label getSignalTypeLabel() {
        return signalTypeLabel;
    }

    public ComboBox<String> getSignalTypesComboBox() {
        return signalTypeComboBox;
    }

    public StatusBarLine getStatusBarLine() {
        return statusBarLine;
    }

    public ComboBox<String> getVerticalScalesComboBox() {
        return verticalScalesComboBox;
    }

    @Override
    public void setControllerManager(ControllerManager controllerManager) {
        this.controllerManager = controllerManager;
    }
}
