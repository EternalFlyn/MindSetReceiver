package com.flyn.eeg_receiver.event

class EEGPowerEvent(val time: Long,
                    val delta: Int,
                    val theta: Int,
                    val lowAlpha: Int,
                    val highAlpha: Int,
                    val lowBeta: Int,
                    val highBeta: Int,
                    val lowGamma: Int,
                    val midGamma: Int
                    ): DeviceEvent {
}