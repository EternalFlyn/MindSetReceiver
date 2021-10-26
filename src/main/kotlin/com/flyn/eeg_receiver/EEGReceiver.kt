package com.flyn.eeg_receiver

import com.flyn.eeg_receiver.data.DataReceiver
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

    override fun stop() {
        super.stop()
        if (DataReceiver.isConnect) DataReceiver.disconnect()
    }

}

fun main() {
    Application.launch(EEGReceiver::class.java)
}

internal fun getLoader(location: String): FXMLLoader {
    return FXMLLoader(EEGReceiver::class.java.getResource(location))
}