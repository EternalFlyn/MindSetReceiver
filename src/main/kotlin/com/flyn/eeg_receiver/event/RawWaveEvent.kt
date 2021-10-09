package com.flyn.eeg_receiver.event

class RawWaveEvent(val time: Long, val value: Int): DeviceEvent {
}