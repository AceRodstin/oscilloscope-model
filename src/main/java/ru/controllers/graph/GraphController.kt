package ru.controllers.graph

import javafx.application.Platform
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.chart.NumberAxis
import javafx.scene.chart.XYChart
import javafx.scene.control.ComboBox
import javafx.scene.control.TextField
import ru.controllers.MainController
import ru.models.GraphModel
import ru.utils.Utils

class GraphController(private val mainController: MainController) {
    private val graphModel = GraphModel()

    fun initialize() {
        initializeGraph()
        initializeGraphSettings()
        initializeFilters()
    }

    private fun initializeGraph() {
        mainController.graph.data.add(mainController.signalController.getSeries())
    }

    private fun initializeGraphSettings() {
        setHorizontalSignalScales()
        setVerticalSignalScales()
        setScaleToGraph()
        listen(mainController.verticalScalesComboBox)
        listen(mainController.horizontalScalesComboBox)
        setDecimalFormats()
        listenDecimalFormats()
        setGraphTypes()
        listenGraphTypes()
    }

    private fun setHorizontalSignalScales() {
        val horizontalScales = FXCollections.observableArrayList<String>("1 мс/дел", "10 мс/дел",
                "100 мс/дел")
        set(horizontalScales, mainController.horizontalScalesComboBox, "100 мс/дел")
    }

    private fun set(scales: ObservableList<String>, comboBox: ComboBox<String>, defaultScale: String) {
        comboBox.items = scales
        comboBox.selectionModel.select(defaultScale)
    }

    private fun setVerticalSignalScales() {
        val verticalScales = FXCollections.observableArrayList<String>("1 мВ/дел", "10 мВ/дел",
                "100 мВ/дел", "1 В/дел", "10 В/дел", "100 В/дел", "500 В/дел", "1000 В/дел")
        set(verticalScales, mainController.verticalScalesComboBox, "1 В/дел")
    }

    private fun setScaleToGraph() {
        graphModel.parse(mainController.horizontalScalesComboBox.selectionModel.selectedItem)
        setScale(mainController.graph.xAxis as NumberAxis)

        graphModel.parse(mainController.verticalScalesComboBox.selectionModel.selectedItem)
        setScale(mainController.graph.yAxis as NumberAxis)
    }

    private fun setScale(axis: NumberAxis) {
        axis.lowerBound = graphModel.lowerBound
        axis.tickUnit = graphModel.tickUnit
        axis.upperBound = graphModel.upperBound
    }

    private fun listen(comboBox: ComboBox<String>) {
        comboBox.valueProperty().addListener { _ ->
            if (comboBox.selectionModel.selectedItem != null) {
                graphModel.parse(comboBox.selectionModel.selectedItem)

                if (comboBox == mainController.verticalScalesComboBox) {
                    setScale(mainController.graph.yAxis as NumberAxis)
                } else {
                    setScale(mainController.graph.xAxis as NumberAxis)
                }
            }
        }
    }

    private fun setDecimalFormats() {
        val decimalFormats = FXCollections.observableArrayList<String>()
        for (value in 1 until Utils.getDecimalScaleLimit()) {
            decimalFormats.add(value.toString())
        }
        mainController.decimalFormatComboBox.items = decimalFormats
        mainController.decimalFormatComboBox.selectionModel.select("2")
    }

    private fun listenDecimalFormats() {
        mainController.decimalFormatComboBox.valueProperty().addListener { _ ->
            restartShowDataThread()
        }
    }

    private fun setGraphTypes() {
        val types = FXCollections.observableArrayList<String>(GraphTypes.SIGNAL.typeName, GraphTypes.SPECTRUM.typeName,
                GraphTypes.REGULATOR.typeName)
        mainController.graphTypesComboBox.items = types
        mainController.graphTypesComboBox.selectionModel.select(GraphTypes.SIGNAL.typeName)
    }

    private fun listenGraphTypes() {
        mainController.graphTypesComboBox.valueProperty().addListener { _ ->
            val selectedType = mainController.graphTypesComboBox.selectionModel.selectedItem
            when (selectedType) {
                GraphTypes.SIGNAL.typeName -> {
                    restartShowDataThread()
                    setHorizontalSignalScales()
                    setSignalTitles()
                }
                GraphTypes.SPECTRUM.typeName -> {
                    restartShowDataThread()
                    setHorizontalSpectrumScales()
                    setSpectrumTitles()
                }
                GraphTypes.REGULATOR.typeName -> {
                    setHorizontalSignalScales()
                    setSignalTitles()
                }
            }

            mainController.regulatorController.toggleRegulator(selectedType == GraphTypes.REGULATOR.typeName)
        }
    }

    private fun restartShowDataThread() {
        mainController.statusBarLine.setStatusOfProgress("Обработка графика")
        Thread {
            mainController.controllerManager.isFinished = true
            mainController.showSignalThread.interrupt()
            clearSeries()
            Utils.sleep(1000)
            mainController.controllerManager.isFinished = false
            mainController.show()
            mainController.statusBarLine.toggleProgressIndicator(true)
            mainController.statusBarLine.clearStatusBar()
        }.start()
    }

    fun clearSeries() {
        Platform.runLater {
            mainController.graph.data[0].data.clear()
            Utils.sleep(50)
        }
    }

    private fun setSignalTitles() {
        mainController.graph.title = "График сигнала"
        mainController.graph.yAxis.label = "Напряжение, В"
        mainController.graph.xAxis.label = "Время, с"
    }

    private fun setHorizontalSpectrumScales() {
        val horizontalScales = FXCollections.observableArrayList<String>("1 Гц/дел", "2 Гц/дел",
                "5 Гц/дел", "10 Гц/дел", "20 Гц/дел", "50 Гц/дел", "100 Гц/дел")
        set(horizontalScales, mainController.horizontalScalesComboBox, "5 Гц/дел")
    }

    private fun setSpectrumTitles() {
        mainController.graph.title = "Спектр сигнала"
        mainController.graph.yAxis.label = "Амплитуда, В"
        mainController.graph.xAxis.label = "Частота, Гц"
    }

    private fun initializeFilters() {
        toggleFiltersUiElements(false)
        setFiltersTypes()
        listenFilterTypes()
    }

    private fun toggleFiltersUiElements(isVisible: Boolean) {
        mainController.filterLabel.isVisible = isVisible
        mainController.filterTextField.isVisible = isVisible
    }

    private fun setFiltersTypes() {
        val types = FXCollections.observableArrayList<String>(FilterTypes.NONE.typeName, FilterTypes.IIR.typeName)
        mainController.filterTypesComboBox.items = types
        mainController.filterTypesComboBox.selectionModel.select(FilterTypes.NONE.typeName)
    }

    private fun listenFilterTypes() {
        mainController.filterTypesComboBox.valueProperty().addListener { _ ->
            when (mainController.filterTypesComboBox.selectionModel.selectedItem) {
                FilterTypes.NONE.typeName -> toggleFilter(false)
                FilterTypes.IIR.typeName -> toggleFilter(true)
            }
        }
    }

    fun toggleFilter(isEnable: Boolean) {
        mainController.signalController.toggleFilter(isEnable)
        toggleFiltersUiElements(isEnable)
        if (isEnable) {
            mainController.signalController.generateSignal()
            listen(mainController.filterTextField)
        }
    }

    private fun listen(textField: TextField) {
        textField.textProperty().addListener { _, _, newValue ->
            var cutoffFrequency = 50.0 // default value
            val filterOrder = 2 // default value

            if (textField.text.isNotEmpty() && textField.text != "-") {
                cutoffFrequency = newValue.toDouble()
            }

            mainController.signalController.setFilter(filterOrder, cutoffFrequency)
        }
    }

    fun showSignal() {
        val graphType = mainController.graphTypesComboBox.selectionModel.selectedItem
        val isFFT = graphType == GraphTypes.SPECTRUM.typeName
        val intermediateList = mainController.signalController.getIntermediateList()
        val graphSeries = mainController.signalController.getSeries()

        mainController.signalController.fillIntermediateList(isFFT)
        graphModel.parse(mainController.horizontalScalesComboBox.selectionModel.selectedItem)

        if (isFFT) {
            val firstPoint = XYChart.Data<Number, Number>(0, 0)
            Platform.runLater { graphSeries.data.add(firstPoint) }
        }

        // Cycle for printing the line
        for (point in intermediateList) {
            if (mainController.controllerManager.isFinished) break
            val addPoint = { if (!graphSeries.data.contains(point)) graphSeries.data.add(point) }

            if (isFFT) {
                val previousPoint = XYChart.Data<Number, Number>((point.xValue.toDouble() / 1.001), 0)
                val nextPoint = XYChart.Data<Number, Number>((point.xValue.toDouble() * 1.001), 0)
                add(previousPoint, graphSeries)
                Platform.runLater(addPoint)
                add(nextPoint, graphSeries)
            } else {
                Platform.runLater(addPoint)
                if (point.xValue.toDouble() >= graphModel.upperBound) break
            }

            Utils.sleep(1)
        }
    }

    private fun add(point: XYChart.Data<Number, Number>, series: XYChart.Series<Number, Number>) {
        if (!series.data.contains(point)) Platform.runLater { series.data.add(point) }
    }

    fun toggleUiElementsState(isDisable: Boolean) {
        Platform.runLater {
            mainController.verticalScalesLabel.setDisable(!isDisable)
            mainController.verticalScalesComboBox.setDisable(!isDisable)
            mainController.horizontalScalesLabel.setDisable(!isDisable)
            mainController.horizontalScalesComboBox.setDisable(!isDisable)
            mainController.decimalFormatComboBox.setDisable(!isDisable)
            mainController.decimalFormatLabel.setDisable(!isDisable)
            mainController.graphTypesLabel.setDisable(!isDisable)
            mainController.graphTypesComboBox.setDisable(!isDisable)
            mainController.filterTypesLabel.setDisable(!isDisable)
            mainController.filterTypesComboBox.setDisable(!isDisable)
            mainController.filterLabel.setDisable(!isDisable)
            mainController.filterTextField.setDisable(!isDisable)
        }
    }
}
