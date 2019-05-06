package ru.Controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.control.*;
import org.controlsfx.control.StatusBar;
import ru.Controllers.Graph.GraphController;
import ru.Controllers.Signal.SignalController;
import ru.Utils.BaseController;
import ru.Utils.ControllerManager;
import ru.Utils.StatusBarLine;

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
    private ComboBox<String> signalTypeComboBox;
    @FXML
    private Label signalTypeLabel;
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
    private Thread showSignal = new Thread();
    private SignalController signalController = new SignalController(this);
    private boolean signalParametersSet;
    private StatusBarLine statusBarLine = new StatusBarLine();

    public void initialize() {
        graphController.initialize();
        signalController.initialize();
    }

    public void handleGenerate() {
        checkSignalParameters();
        setStatusBar();
        show();
        checkGenerationState();
    }

    private void checkSignalParameters() {
        signalParametersSet = !signalController.checkEmptyTextFields();

        if (!signalController.checkEmptyTextFields()) {
            buttonPressedCounter++;
        } else {
            Platform.runLater(() -> statusBarLine.setStatus("Перед генерацией необходимо указать параметры сигнала",
                    statusBar, checkIcon, warningIcon));
        }
    }

    private void setStatusBar() {
        if (buttonPressedCounter % 2 != 0 && signalParametersSet) {
            toggleProgressIndicatorState(false);
            Platform.runLater(() -> statusBarLine.setStatus("Генерация сигнала", statusBar));
        } else if (buttonPressedCounter % 2 == 0 && signalParametersSet) {
            toggleProgressIndicatorState(true);
            statusBarLine.setStatusOk(true);
            Platform.runLater(() -> statusBarLine.setStatus("Генерация сигнала остановлена", statusBar,
                    checkIcon, warningIcon));
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
            generateButton.setText("Генерировать");
            toggleUiElementsState(false);
        } else if (signalParametersSet) {
            controllerManager.setFinished(false);
            generateButton.setText("Остановить генерацию");
            toggleUiElementsState(true);
        }
    }

    private void toggleUiElementsState(boolean isDisable) {
        verticalScalesLabel.setDisable(!isDisable);
        verticalScalesComboBox.setDisable(!isDisable);
        horizontalScalesLabel.setDisable(!isDisable);
        horizontalScalesComboBox.setDisable(!isDisable);
        graphTypeLabel.setDisable(!isDisable);
        graphTypeComboBox.setDisable(!isDisable);
        filterTypesLabel.setDisable(!isDisable);
        filterTypesComboBox.setDisable(!isDisable);

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
    }

    public void show() {
        showSignal = new Thread(() -> {
            while (!controllerManager.isFinished()) {
                if (signalParametersSet) {
                    signalController.generateSignal();
                    signalController.showSignalParameters();
                    graphController.clearGraph();
                    graphController.showSignal();
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

    public Label getDcLabel() {
        return dcLabel;
    }

    public TextField getDcTextField() {
        return dcTextField;
    }

    public Label getFrequencyLabel() {
        return frequencyLabel;
    }

    public TextField getFrequencyTextField() {
        return frequencyTextField;
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

    public TextField getReceivedDCTextField() {
        return receivedDCTextField;
    }

    public TextField getReceivedFrequencyTextField() {
        return receivedFrequencyTextField;
    }

    public TextField getReceivedRMSTextField() {
        return receivedRMSTextField;
    }

    public ComboBox<String> getSignalTypeComboBox() {
        return signalTypeComboBox;
    }

    public Label getSignalTypeLabel() {
        return signalTypeLabel;
    }

    public ControllerManager getControllerManager() {
        return controllerManager;
    }

    public ComboBox<String> getFilterTypesComboBox() {
        return filterTypesComboBox;
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

    public Thread getShowSignalThread() {
        return showSignal;
    }

    public SignalController getSignalController() {
        return signalController;
    }

    public ComboBox<String> getVerticalScalesComboBox() {
        return verticalScalesComboBox;
    }

    @Override
    public void setControllerManager(ControllerManager controllerManager) {
        this.controllerManager = controllerManager;
    }
}
