package com.flyn.eeg_receiver.listener;

import com.flyn.eeg_receiver.event.RawWaveEvent;

public interface RawWaveListener extends DeviceListener {

    void onRawWaveEvent(RawWaveEvent event);

}
