package com.flyn.eeg_receiver

import com.flyn.eeg_receiver.view.Viewer
import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.stage.Stage

class EEGReceiver: Application() {

    override fun start(primaryStage: Stage) {
        primaryStage.apply {
            scene = Viewer.scene
            title = "test"
            show()
        }

    }

}

internal const val RECEIVE_DATA_TIME = 5000L

fun main() {
//            DataReceiver.addListener(ExampleListener)
//            DataReceiver.connect(true)
//            Thread.sleep(RECEIVE_DATA_TIME)
//            DataReceiver.disconnect()
    Application.launch(EEGReceiver::class.java)
}

internal fun getLoader(location: String): FXMLLoader {
    return FXMLLoader(EEGReceiver::class.java.getResource(location))
}