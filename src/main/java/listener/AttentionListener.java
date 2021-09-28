package listener;

import event.AttentionEvent;

public interface AttentionListener extends DeviceListener {

    void onAttentionEvent(AttentionEvent event);

}
