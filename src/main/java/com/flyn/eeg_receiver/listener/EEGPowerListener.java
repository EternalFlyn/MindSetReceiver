package com.flyn.eeg_receiver.listener;

import com.flyn.eeg_receiver.event.EEGPowerEvent;

public interface EEGPowerListener extends DeviceListener {

    void onEEGPowerEvent(EEGPowerEvent event);

}
