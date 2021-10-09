package com.flyn.eeg_receiver.view

import com.flyn.eeg_receiver.data.DataReceiver
import com.flyn.eeg_receiver.event.RawWaveEvent
import com.flyn.eeg_receiver.getLoader
import com.flyn.eeg_receiver.listener.RawWaveListener
import javafx.application.Platform
import javafx.fxml.FXML
import javafx.scene.Scene
import javafx.scene.chart.LineChart
import javafx.scene.chart.NumberAxis
import javafx.scene.chart.XYChart
import javafx.util.StringConverter

class Viewer: RawWaveListener {

    companion object {

        private const val location = "view/Viewer.fxml"
        val scene = Scene(getLoader(location).load())

    }

    private val startTime: Long = System.currentTimeMillis()
    private val rawData = XYChart.Series<Long, Int>()

    @FXML
    private lateinit var rawDataChart: LineChart<Long, Int>
    @FXML
    private lateinit var xAxis: NumberAxis

    @FXML
    fun initialize() {
        DataReceiver.addListener(this)
        rawDataChart.data.add(rawData)
        xAxis.lowerBound = startTime.toDouble()
        xAxis.upperBound = startTime.toDouble()
        xAxis.tickLabelFormatter = object: StringConverter<Number>() {

            override fun toString(`object`: Number): String {
                val currentSec = (`object`.toLong() - startTime) / 1000
                return "$currentSec s"
            }

            override fun fromString(string: String): Number {
                return 0
            }

        }
    }

    override fun onRawWaveEvent(event: RawWaveEvent) {
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