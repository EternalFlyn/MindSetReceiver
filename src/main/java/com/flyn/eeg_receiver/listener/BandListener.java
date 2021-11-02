package com.flyn.eeg_receiver.listener;

import com.flyn.eeg_receiver.event.BandEvent;

public interface BandListener extends DeviceListener {

    void onSpectralEvent(BandEvent event);

}
