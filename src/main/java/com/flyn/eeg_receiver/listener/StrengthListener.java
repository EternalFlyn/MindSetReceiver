package com.flyn.eeg_receiver.listener;

import com.flyn.eeg_receiver.event.StrengthEvent;

public interface StrengthListener extends DeviceListener {

    void onStrengthEvent(StrengthEvent event);

}
