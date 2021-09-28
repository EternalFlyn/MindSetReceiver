package listener;

import event.EEGPowerEvent;

public interface EEGPowerListener extends DeviceListener {

    void onEEGPowerEvent(EEGPowerEvent event);

}
