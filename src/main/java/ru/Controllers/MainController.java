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
    private TextField amplitudeTextField;
    @FXML
    private TextField frequencyTextField;
    @FXML
    private Button generateButton;
    @FXML
    private LineChart<Number, Number> graph;
    @FXML
    private ComboBox<String> horizontalScalesComboBox;
    @FXML
    private TextField phaseTextField;
    @FXML
    private StatusBar statusBar;
    @FXML
    private ComboBox<String> verticalScalesComboBox;

    private int buttonPressedCounter;
    private ControllerManager controllerManager;
    private GraphModel graphModel = new GraphModel();
    private boolean signalParametersSetted;
    private SignalModel signalModel = new SignalModel();
    private StatusBarLine statusBarLine = new StatusBarLine();

    public void initialize() {
        initializeGraph();
        addVerticalScaleValues();
        addHorizontalScaleValues();
        setDefaultScales();
        listenScalesComboBox(verticalScalesComboBox);
        listenScalesComboBox(horizontalScalesComboBox);
        addDigitFiltersToTextFields();
    }

    private void initializeGraph() {
        graph.getData().add(signalModel.getGraphSeries());
    }

    private void addVerticalScaleValues() {
        ObservableList<String> scaleValues = FXCollections.observableArrayList();

        scaleValues.add("1 мВ/дел");
        scaleValues.add("10 мВ/дел");
        scaleValues.add("100 мВ/дел");
        scaleValues.add("1 В/дел");
        scaleValues.add("10 В/дел");
        scaleValues.add("100 В/дел");

        verticalScalesComboBox.setItems(scaleValues);
    }

    private void addHorizontalScaleValues() {
        ObservableList<String> scaleValues = FXCollections.observableArrayList();

        scaleValues.add("1 мс/дел");
        scaleValues.add("10 мс/дел");
        scaleValues.add("100 мс/дел");

        horizontalScalesComboBox.setItems(scaleValues);
    }

    private void setDefaultScales() {
        verticalScalesComboBox.getSelectionModel().select(3);
        graphModel.parseScale(verticalScalesComboBox.getSelectionModel().getSelectedItem());
        graphModel.calculateBounds();
        setScale((NumberAxis) graph.getYAxis());

        horizontalScalesComboBox.getSelectionModel().select(2);
        graphModel.parseScale(horizontalScalesComboBox.getSelectionModel().getSelectedItem());
        graphModel.calculateBounds();
        setScale((NumberAxis) graph.getXAxis());
    }

    private void setScale(NumberAxis axis) {
        axis.setLowerBound(graphModel.getLowerBound());
        axis.setTickUnit(graphModel.getTickUnit());
        axis.setUpperBound(graphModel.getUpperBound());
    }

    private void addDigitFiltersToTextFields() {
        setDigitFilter(amplitudeTextField);
        setDigitFilter(frequencyTextField);
        setDigitFilter(phaseTextField);
    }

    private void listenScalesComboBox(ComboBox<String> comboBox) {
        comboBox.valueProperty().addListener(observable -> {
            if (!comboBox.getSelectionModel().isEmpty()) {
                graphModel.parseScale(comboBox.getSelectionModel().getSelectedItem());
                graphModel.calculateBounds();

                if (comboBox == verticalScalesComboBox) {
                    setScale((NumberAxis) graph.getYAxis());
                } else {
                    setScale((NumberAxis) graph.getXAxis());
                }
            }
        });
    }

    private void setDigitFilter(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            textField.setText(newValue.replaceAll("[^-\\d(\\.|,)]", ""));
            if (!newValue.matches("^-?[\\d]+(\\.|,)\\d+|^-?[\\d]+(\\.|,)|^-?[\\d]+|-|$")) {
                textField.setText(oldValue);
            }
        });
    }

    public void handleGenerate() {
        show();
        buttonPressedCounter++;
        checkGenerationState();
    }

    private void show() {
        new Thread(() -> {
            while (!controllerManager.isFinished()) {
                checkEmptyTextFields();
                if (signalParametersSetted) {
                    clearGraph();
                    parseSignalParameters();
                    signalModel.generateSignal();
                    showSignal();
                }
            }
        }).start();
    }

    private void checkGenerationState() {
        if (buttonPressedCounter % 2 == 0) {
            controllerManager.setFinished(true);
            generateButton.setText("Генерировать");
        } else {
            controllerManager.setFinished(false);
            generateButton.setText("Остановить генерацию");
        }
    }

    private void checkEmptyTextFields() {
        if (amplitudeTextField.getText().isEmpty() ||
                frequencyTextField.getText().isEmpty() ||
                phaseTextField.getText().isEmpty()) {
            statusBarLine.setStatus("Перед генерацией необходимо указать параметры сигнала", statusBar);
            signalParametersSetted = false;
        } else {
            signalParametersSetted = true;
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

    public void showSignal() {
        List<XYChart.Data<Number, Number>> intermediateList = signalModel.getIntermediateList();
        XYChart.Series<Number, Number> graphSeries = signalModel.getGraphSeries();

        graphModel.parseScale(horizontalScalesComboBox.getSelectionModel().getSelectedItem());
        graphModel.calculateBounds();
        int scale;

        if (signalModel.getFrequency() < 10) {
            scale = 10;
        } else if (signalModel.getFrequency() < 50) {
            scale = 2;
        } else {
            scale = 1;
        }

        int index;
        for (index = 0; index < intermediateList.size(); index += scale) {
            XYChart.Data point = intermediateList.get(index);
            Runnable addPoint = () -> graphSeries.getData().add(point);

            if ((double) point.getXValue() < graphModel.getUpperBound()) {
                Platform.runLater(addPoint);
                Utils.sleep(1);
                index += scale;
            }

            if (index == intermediateList.size() - scale) {
                XYChart.Data lastPoint = new XYChart.Data(graphModel.getUpperBound(), intermediateList.get(0).getYValue());
                Platform.runLater(() -> graphSeries.getData().add(lastPoint));
                Utils.sleep(100);
            } else if ((double) point.getXValue() >= graphModel.getUpperBound()){
                Platform.runLater(addPoint);
                Utils.sleep(100);
                break;
            }
        }
    }

    @Override
    public void setControllerManager(ControllerManager controllerManager) {
        this.controllerManager = controllerManager;
    }
}
