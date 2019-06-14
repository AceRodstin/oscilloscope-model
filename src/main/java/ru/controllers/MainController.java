package ru.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.control.*;
import org.controlsfx.control.StatusBar;
import ru.controllers.graph.GraphController;
import ru.controllers.graph.GraphTypes;
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
    private ComboBox<String> graphTypesComboBox;
    @FXML
    private Label graphTypesLabel;
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
    private Label receivedDcLabel;
    @FXML
    private TextField receivedDcTextField;
    @FXML
    private Label receivedFrequencyLabel;
    @FXML
    private TextField receivedFrequencyTextField;
    @FXML
    private Label receivedRmsLabel;
    @FXML
    private TextField receivedRmsTextField;
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
            parseSignalParameters();
        } else {
            Platform.runLater(() -> statusBarLine.setStatus("Перед генерацией необходимо указать параметры сигнала",
                    false));
        }
    }

    private void parseSignalParameters() {
        if (!graphTypesComboBox.getSelectionModel().getSelectedItem().equals(GraphTypes.REGULATOR.getTypeName())) {
            signalController.parseSignalParameters();
        }
    }

    private void setStatusBar() {
        if (buttonPressedCounter % 2 != 0 && signalParametersSet) {
            Platform.runLater(() -> statusBarLine.setStatusOfProgress("Генерация сигнала"));
        } else if (buttonPressedCounter % 2 == 0 && signalParametersSet) {
            Platform.runLater(() -> {
                statusBarLine.clearStatusBar();
                statusBarLine.toggleProgressIndicator(true);
                statusBarLine.setStatus("Генерация сигнала остановлена", true);
            });
        }
    }

    private void checkGenerationState() {
        if (buttonPressedCounter % 2 == 0 && signalParametersSet) {
            controllerManager.setFinished(true);
            showSignal.interrupt();
            graphController.clearSeries();
            generateButton.setText("Генерировать");
            toggleUiElementsState(false);
        } else if (signalParametersSet) {
            controllerManager.setFinished(false);
            generateButton.setText("Остановить генерацию");
            toggleUiElementsState(true);
        }
    }

    private void toggleUiElementsState(boolean isDisable) {
        graphController.toggleUiElementsState(isDisable);
        signalController.toggleUiElementsState(isDisable);
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

    public Label getDecimalFormatLabel() {
        return decimalFormatLabel;
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

    public Label getFilterTypesLabel() {
        return filterTypesLabel;
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

    public ComboBox<String> getGraphTypesComboBox() {
        return graphTypesComboBox;
    }

    public Label getGraphTypesLabel() {
        return graphTypesLabel;
    }

    public ComboBox<String> getHorizontalScalesComboBox() {
        return horizontalScalesComboBox;
    }

    public Label getHorizontalScalesLabel() {
        return horizontalScalesLabel;
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

    public Label getReceivedAmplitudeLabel() {
        return receivedAmplitudeLabel;
    }

    public TextField getReceivedAmplitudeTextField() {
        return receivedAmplitudeTextField;
    }

    public Label getReceivedDcLabel() {
        return receivedDcLabel;
    }

    public TextField getReceivedDcTextField() {
        return receivedDcTextField;
    }

    public Label getReceivedFrequencyLabel() {
        return receivedFrequencyLabel;
    }

    public TextField getReceivedFrequencyTextField() {
        return receivedFrequencyTextField;
    }

    public Label getReceivedRmsLabel() {
        return receivedRmsLabel;
    }

    public TextField getReceivedRmsTextField() {
        return receivedRmsTextField;
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

    public ComboBox<String> getSignalTypeComboBox() {
        return signalTypeComboBox;
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

    public Label getVerticalScalesLabel() {
        return verticalScalesLabel;
    }

    @Override
    public void setControllerManager(ControllerManager controllerManager) {
        this.controllerManager = controllerManager;
    }
}
