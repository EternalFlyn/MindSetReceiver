package listener;

import event.PoorSignalEvent;

public interface PoorSignalListener extends DeviceListener {

    void onPoorSignalEvent(PoorSignalEvent event);

}
