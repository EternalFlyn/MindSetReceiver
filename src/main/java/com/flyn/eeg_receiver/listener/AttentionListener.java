package com.flyn.eeg_receiver.listener;

import com.flyn.eeg_receiver.event.AttentionEvent;

public interface AttentionListener extends DeviceListener {

    void onAttentionEvent(AttentionEvent event);

}
