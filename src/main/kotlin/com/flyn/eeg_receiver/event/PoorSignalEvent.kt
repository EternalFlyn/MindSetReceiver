package com.flyn.eeg_receiver.event

class PoorSignalEvent(val time: Long, val quality: Int): DeviceEvent {
}