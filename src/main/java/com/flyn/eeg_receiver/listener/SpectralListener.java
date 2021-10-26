package com.flyn.eeg_receiver.listener;

import com.flyn.eeg_receiver.event.SpectralEvent;

public interface SpectralListener extends DeviceListener {

    void onSpectralEvent(SpectralEvent event);

}
