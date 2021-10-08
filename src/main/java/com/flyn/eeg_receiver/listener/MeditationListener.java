package com.flyn.eeg_receiver.listener;

import com.flyn.eeg_receiver.event.MeditationEvent;

public interface MeditationListener extends DeviceListener {

    void onMeditationEvent(MeditationEvent event);

}
