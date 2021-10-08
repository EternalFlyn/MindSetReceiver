package com.flyn.eeg_receiver.view

import com.flyn.eeg_receiver.getLoader
import javafx.fxml.FXML
import javafx.scene.Scene
import javafx.scene.chart.LineChart
import javafx.scene.chart.XYChart

class Viewer {

    companion object {

        private const val location = "view/Viewer.fxml"
        val scene = Scene(getLoader(location).load())

    }

    private val rawData = XYChart.Series<Int, Int>()

    @FXML
    private lateinit var rawDataChart: LineChart<Int, Int>

    @FXML
    fun initialize() {
        rawData.data.add(XYChart.Data(100, 100))
        rawDataChart.data.add(rawData)
    }

}