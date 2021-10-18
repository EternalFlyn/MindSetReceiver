package com.flyn.eeg_receiver.view

import com.fazecast.jSerialComm.SerialPort
import com.flyn.eeg_receiver.data.DataReceiver
import com.flyn.eeg_receiver.event.RawWaveEvent
import com.flyn.eeg_receiver.getLoader
import com.flyn.eeg_receiver.listener.RawWaveListener
import javafx.application.Platform
import javafx.collections.FXCollections
import javafx.fxml.FXML
import javafx.scene.Scene
import javafx.scene.chart.LineChart
import javafx.scene.chart.NumberAxis
import javafx.scene.chart.XYChart
import javafx.scene.control.Button
import javafx.scene.control.ChoiceBox
import javafx.scene.control.Tooltip
import javafx.stage.FileChooser
import javafx.util.StringConverter

class Viewer: RawWaveListener {

    companion object {

        private const val location = "view/Viewer.fxml"
        val scene = Scene(getLoader(location).load())

    }

    private var isPause = false
    private var comPort = ""
    private val rawData = XYChart.Series<Long, Int>()
    private val tempData = mutableListOf<XYChart.Data<Long, Int>>()

    @FXML
    private lateinit var rawDataChart: LineChart<Long, Int>
    @FXML
    private lateinit var xAxis: NumberAxis
    @FXML
    private lateinit var comPortSelect: ChoiceBox<String>
    @FXML
    private lateinit var connectButton: Button
    @FXML
    private lateinit var controlButton: Button

    @FXML
    fun initialize() {
        DataReceiver.addListener(this)
        rawDataChart.data.add(rawData)
        xAxis.lowerBound = DataReceiver.startTime.toDouble()
        xAxis.upperBound = DataReceiver.startTime.toDouble()
        xAxis.tickLabelFormatter = object: StringConverter<Number>() {

            override fun toString(`object`: Number): String {
                val currentSec = (`object`.toLong() - DataReceiver.startTime) / 1000
                return "$currentSec s"
            }

            override fun fromString(string: String): Number {
                return 0
            }

        }
        comPortSelect.tooltip = Tooltip("Select the COM Port")
        comPortSelect.items = FXCollections.observableArrayList<String>().apply {
            addAll(SerialPort.getCommPorts().map { port -> port.systemPortName })
        }
        comPortSelect.selectionModel.selectedIndexProperty().addListener { _, _, newValue ->
            comPort = comPortSelect.items[newValue.toInt()]
        }
    }

    fun connectDevice() {
        if (DataReceiver.isConnect) {
            connectButton.text = "Connect"
            DataReceiver.disconnect()
        }
        else {
            connectButton.text = "Disconnect"
            DataReceiver.connect(comPort)
            rawData.data.clear()
            xAxis.lowerBound = DataReceiver.startTime.toDouble()
            xAxis.upperBound = DataReceiver.startTime.toDouble()
        }
    }

    fun chartControl() {
        if (!DataReceiver.isConnect) return
        if (isPause) {
            controlButton.text = "pause"
            rawData.data.clear()
            rawData.data.addAll(tempData)
        }
        else {
            controlButton.text = "resume"
            tempData.clear()
            tempData.addAll(rawData.data)
        }
        isPause = !isPause
    }

    fun saveFile() {
        val fileChooser = FileChooser().apply {
            extensionFilters.add(FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv"))
        }
        val file = fileChooser.showSaveDialog(scene.window)
        file?.run {
            writeText("")
            rawData.data.forEach {
                appendText("${it.xValue - DataReceiver.startTime}, ${it.yValue}\n")
            }
        }
    }

    override fun onRawWaveEvent(event: RawWaveEvent) {
        if (isPause) {
            with(tempData) {
                if(size > 2560) removeAt(0)
                add(XYChart.Data(event.time, event.value))
            }
        }
        else {
            Platform.runLater {
                with(rawData.data) {
                    if(size > 2560) removeAt(0)
                    add(XYChart.Data(event.time, event.value))
                    xAxis.lowerBound = this[0].xValue.toDouble()
                    xAxis.upperBound = this[size - 1].xValue.toDouble()
                }
            }
        }
    }


}