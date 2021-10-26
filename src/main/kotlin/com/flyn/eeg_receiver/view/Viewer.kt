package com.flyn.eeg_receiver.view

import com.fazecast.jSerialComm.SerialPort
import com.flyn.eeg_receiver.data.DataReceiver
import com.flyn.eeg_receiver.event.SpectralEvent
import com.flyn.eeg_receiver.event.RawWaveEvent
import com.flyn.eeg_receiver.getLoader
import com.flyn.eeg_receiver.listener.SpectralListener
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
import java.util.concurrent.ConcurrentLinkedQueue

class Viewer: RawWaveListener, SpectralListener {

    companion object {

        private const val location = "view/Viewer.fxml"
        val scene = Scene(getLoader(location).load())

    }

    private var isPause = false
    private var comPort = ""
    private val rawData = XYChart.Series<Long, Int>()
    private val tempData = ConcurrentLinkedQueue<XYChart.Data<Long, Int>>()
    private val spectralEventList = ConcurrentLinkedQueue<SpectralEvent>()
    private val tempSpectralList = ConcurrentLinkedQueue<SpectralEvent>()

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
        comPortSelect.tooltip = Tooltip("Select the COM port")
        comPortSelect.items = FXCollections.observableArrayList<String>().apply {
            addAll(SerialPort.getCommPorts().map { port -> port.systemPortName })
        }
        comPortSelect.selectionModel.selectedIndexProperty().addListener { _, _, newValue ->
            comPort = comPortSelect.items[newValue.toInt()]
        }
    }

    fun connectDevice() {
        if (DataReceiver.isConnect) {
            isPause = false
            connectButton.text = "Connect"
            controlButton.text = "Pause"
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
            controlButton.text = "Pause"
            rawData.data.clear()
            rawData.data.addAll(tempData)
            spectralEventList.clear()
            spectralEventList.addAll(tempSpectralList)
        }
        else {
            controlButton.text = "Resume"
            tempData.clear()
            tempData.addAll(rawData.data)
            tempSpectralList.clear()
            tempSpectralList.addAll(spectralEventList)
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
            var isFirst = true
            rawData.data.forEach {
                if (isFirst) {
                    isFirst = false
                    appendText("${it.xValue - DataReceiver.startTime}, ${it.yValue}")
                    return@forEach
                }
                appendText("\n${it.xValue - DataReceiver.startTime}, ${it.yValue}")
            }
            spectralEventList.forEach {
                with(it) {
                    appendText("\n${time - DataReceiver.startTime}, $delta, $theta, $lowAlpha, $highAlpha, $lowBeta, $highBeta, $lowGamma, $midGamma")
                }
            }
        }
    }

    override fun onRawWaveEvent(event: RawWaveEvent) {
        if (isPause) {
            with(tempData) {
                offer(XYChart.Data(event.time, event.value))
                if (size > 2560) poll()
            }
            return
        }
        Platform.runLater {
            with(rawData.data) {
                add(XYChart.Data(event.time, event.value))
                if (size > 2560) removeAt(0)
                xAxis.lowerBound = this[0].xValue.toDouble()
                xAxis.upperBound = this[size - 1].xValue.toDouble()
            }
        }
    }

    override fun onSpectralEvent(event: SpectralEvent) {
        if (isPause) {
            tempSpectralList.offer(event)
            if (tempSpectralList.size > 5) tempSpectralList.poll()
            return
        }
        spectralEventList.offer(event)
        if (spectralEventList.size > 5) spectralEventList.poll()
    }

}