package com.flyn.eeg_receiver.listener;

import com.flyn.eeg_receiver.event.PoorSignalEvent;

public interface PoorSignalListener extends DeviceListener {

    void onPoorSignalEvent(PoorSignalEvent event);

}
