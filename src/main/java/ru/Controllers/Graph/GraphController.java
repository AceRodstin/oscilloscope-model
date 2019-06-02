package ru.Controllers.Graph;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import ru.Controllers.MainController;
import ru.Models.GraphModel;
import ru.Utils.Utils;

import java.util.List;

public class GraphController {
    private GraphModel graphModel;
    private MainController mainController;

    public GraphController(MainController mainController) {
        this.mainController = mainController;
        graphModel = new GraphModel();
    }

    public void initialize() {
        initGraph();
        initComboBoxes();
    }

    public void initGraph() {
        LineChart<Number, Number> graph = mainController.getGraph();
        XYChart.Series<Number, Number> series = mainController.getSignalController().getSignalModel().getGraphSeries();

        graph.getData().add(series);
    }

    private void initComboBoxes() {
        setVerticalScales();
        setHorizontalSignalScales();
        setDefaultScales();
        listenScalesComboBox(mainController.getVerticalScalesComboBox());
        listenScalesComboBox(mainController.getHorizontalScalesComboBox());
        setGraphTypes();
        listenGraphTypes();
        initFilters();
        setFilterTypes();
        listenFilterTypes();
        setDecimalFormats();
        listenDecimalFormats();
    }

    private void setVerticalScales() {
        ObservableList<String> scales = FXCollections.observableArrayList();

        scales.add("1 мВ/дел");
        scales.add("10 мВ/дел");
        scales.add("100 мВ/дел");
        scales.add("1 В/дел");
        scales.add("10 В/дел");
        scales.add("100 В/дел");
        scales.add("500 В/дел");
        scales.add("1000 В/дел");

        mainController.getVerticalScalesComboBox().setItems(scales);
        mainController.getVerticalScalesComboBox().getSelectionModel().select(3);
    }

    private void setHorizontalSignalScales() {
        ObservableList<String> scales = FXCollections.observableArrayList();

        scales.add("1 мс/дел");
        scales.add("10 мс/дел");
        scales.add("100 мс/дел");

        mainController.getHorizontalScalesComboBox().setItems(scales);
        mainController.getHorizontalScalesComboBox().getSelectionModel().select(2);
    }

    private void setDefaultScales() {
        graphModel.parseScale(mainController.getVerticalScalesComboBox().getSelectionModel().getSelectedItem());
        graphModel.calculateBounds();
        setScale((NumberAxis) mainController.getGraph().getYAxis());

        graphModel.parseScale(mainController.getHorizontalScalesComboBox().getSelectionModel().getSelectedItem());
        graphModel.calculateBounds();
        setScale((NumberAxis) mainController.getGraph().getXAxis());
    }

    private void setScale(NumberAxis axis) {
        axis.setLowerBound(graphModel.getLowerBound());
        axis.setTickUnit(graphModel.getTickUnit());
        axis.setUpperBound(graphModel.getUpperBound());
    }

    private void listenScalesComboBox(ComboBox<String> comboBox) {
        comboBox.valueProperty().addListener(observable -> {
            if (!comboBox.getSelectionModel().isEmpty()) {
                graphModel.parseScale(comboBox.getSelectionModel().getSelectedItem());
                graphModel.calculateBounds();

                if (comboBox == mainController.getVerticalScalesComboBox()) {
                    setScale((NumberAxis) mainController.getGraph().getYAxis());
                } else {
                    setScale((NumberAxis) mainController.getGraph().getXAxis());
                }
            }
        });
    }

    private void setGraphTypes() {
        ObservableList<String> scaleValues = FXCollections.observableArrayList();

        scaleValues.add(GraphTypes.SIGNAL.getTypeName());
        scaleValues.add(GraphTypes.SPECTRUM.getTypeName());
        scaleValues.add(GraphTypes.REGULATOR.getTypeName());

        mainController.getGraphTypeComboBox().setItems(scaleValues);
        mainController.getGraphTypeComboBox().getSelectionModel().select(0);
    }

    private void listenGraphTypes() {
        mainController.getGraphTypeComboBox().valueProperty().addListener(observable -> {
            String selectedType = mainController.getGraphTypeComboBox().getSelectionModel().getSelectedItem();

            if (selectedType.equals(GraphTypes.SIGNAL.getTypeName())) {
                restartShowDataThread();
                setHorizontalSignalScales();
                setSignalTitles();
            } else if (selectedType.equals(GraphTypes.SPECTRUM.getTypeName())) {
                restartShowDataThread();
                setHorizontalSpectrumScales();
                setSpectrumTitles();
            } else {
                setHorizontalSignalScales();
                setSignalTitles();
            }

            mainController.getRegulatorController().toggleRegulator(selectedType.equals(GraphTypes.REGULATOR.getTypeName()));
        });
    }

    private void setHorizontalSpectrumScales() {
        ObservableList<String> scales = FXCollections.observableArrayList();

        scales.add("1 Гц/дел");
        scales.add("2 Гц/дел");
        scales.add("5 Гц/дел");
        scales.add("10 Гц/дел");
        scales.add("20 Гц/дел");
        scales.add("50 Гц/дел");
        scales.add("100 Гц/дел");

        mainController.getHorizontalScalesComboBox().setItems(scales);
        mainController.getHorizontalScalesComboBox().getSelectionModel().select(2);
    }

    private void restartShowDataThread() {
        mainController.getStatusBarLine().setStatusOfProgress("Обработка графика");

        new Thread(() -> {
            mainController.getControllerManager().setFinished(true);
            mainController.getShowSignalThread().interrupt();
            clearGraph();
            Utils.sleep(1000);
            mainController.getControllerManager().setFinished(false);
            mainController.show();

            mainController.getStatusBarLine().clearStatusBar();
            mainController.getStatusBarLine().toggleProgressIndicator(false);
        }).start();
    }

    private void setSpectrumTitles() {
        mainController.getGraph().setTitle("Спектр сигнала");
        mainController.getGraph().getYAxis().setLabel("Амплитуда, В");
        mainController.getGraph().getXAxis().setLabel("Частота, Гц");
    }

    private void setSignalTitles() {
        mainController.getGraph().setTitle("График сигнала");
        mainController.getGraph().getYAxis().setLabel("Напряжение, В");
        mainController.getGraph().getXAxis().setLabel("Время, с");
    }

    private void initFilters() {
        toggleFiltersUiElements(false);
    }

    private void toggleFiltersUiElements(boolean isEnable) {
        mainController.getFilterLabel().setVisible(isEnable);
        mainController.getFilterTextField().setVisible(isEnable);
    }

    private void setFilterTypes() {
        ObservableList<String> types = FXCollections.observableArrayList();

        types.add(FilterTypes.NONE.getTypeName());
        types.add(FilterTypes.IIR.getTypeName());

        mainController.getFilterTypesComboBox().setItems(types);
        mainController.getFilterTypesComboBox().getSelectionModel().select(0);
    }

    private void listenFilterTypes() {
        ComboBox<String> filterTypes = mainController.getFilterTypesComboBox();

        filterTypes.valueProperty().addListener(observable -> {
            if (filterTypes.getSelectionModel().getSelectedItem().equals(FilterTypes.NONE.getTypeName())) {
                toggleFilter(false);
            } else if (filterTypes.getSelectionModel().getSelectedItem().equals(FilterTypes.IIR.getTypeName())) {
                toggleFilter(true);
            }
        });
    }

    public void toggleFilter(boolean isEnable) {
        if (isEnable) {
            mainController.getSignalController().getSignalModel().setFilterOn(true);
            mainController.getSignalController().generateSignal();
            toggleFiltersUiElements(true);
            listenFilterTextField();
        } else {
            mainController.getSignalController().getSignalModel().setFilterOn(false);
            toggleFiltersUiElements(false);
        }
    }

    private void listenFilterTextField() {
        TextField textField = mainController.getFilterTextField();

        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            int cutoffFrequency = 50; // default value

            if (!textField.getText().isEmpty() && textField.getText() != "-") {
                cutoffFrequency = (int) Double.parseDouble(newValue);
            }

            mainController.getSignalController().getSignalModel().setFilter(2, cutoffFrequency);
        });
    }

    private void setDecimalFormats() {
        if (mainController.getDecimalFormatComboBox().getItems().isEmpty()) {
            ObservableList<String> strings = FXCollections.observableArrayList();
            for (int i = 1; i <= Utils.getDecimalScaleLimit(); i++) {
                strings.add(String.format("%d", i));
            }
            mainController.getDecimalFormatComboBox().getItems().addAll(strings);
            mainController.getDecimalFormatComboBox().getSelectionModel().select(1);
        }
    }

    private void listenDecimalFormats() {
        mainController.getDecimalFormatComboBox().valueProperty().addListener(observable -> {
            restartShowDataThread();
        });
    }

    public void showSignal() {
        String graphType = mainController.getGraphTypeComboBox().getSelectionModel().getSelectedItem();
        boolean isFFT = graphType.equals(GraphTypes.SPECTRUM.getTypeName());
        List<XYChart.Data<Number, Number>> intermediateList = mainController.getSignalController().getSignalModel().getIntermediateList();
        XYChart.Series<Number, Number> graphSeries = mainController.getSignalController().getSignalModel().getGraphSeries();


        mainController.getSignalController().getSignalModel().fillIntermediateList(isFFT);
        graphModel.parseScale(mainController.getHorizontalScalesComboBox().getSelectionModel().getSelectedItem());
        graphModel.calculateBounds();

        if (isFFT) {
            Platform.runLater(() -> graphSeries.getData().add(new XYChart.Data<>(0, 0)));
        }

        for (XYChart.Data<Number, Number> point : intermediateList) {
            if (mainController.getControllerManager().isFinished()) {
                break;
            }

            Runnable addPoint = () -> {
                if (!graphSeries.getData().contains(point)) {
                    graphSeries.getData().add(point);
                }
            };

            if (isFFT) {
                XYChart.Data<Number, Number> previousPoint = new XYChart.Data<>((double) point.getXValue() / 1.001, 0);
                XYChart.Data<Number, Number> nextPoint = new XYChart.Data<>((double) point.getXValue() * 1.001, 0);

                if (!graphSeries.getData().contains(previousPoint)) {
                    Platform.runLater(() -> graphSeries.getData().add(previousPoint));
                }

                if (!graphSeries.getData().contains(nextPoint)) {
                    Platform.runLater(() -> graphSeries.getData().add(nextPoint));
                }
            }

            if ((double) point.getXValue() < graphModel.getUpperBound()) {
                Platform.runLater(addPoint);
                Utils.sleep(1);
            }

            if ((double) point.getXValue() >= graphModel.getUpperBound()) {
                Platform.runLater(addPoint);
                Utils.sleep(1);
                break;
            }
        }
    }

    public void clearGraph() {
        Platform.runLater(() -> {
            mainController.getGraph().getData().get(0).getData().clear();
            Utils.sleep(50);
        });
    }
}
