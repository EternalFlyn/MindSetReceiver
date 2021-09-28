package listener;

import event.RawWaveEvent;

public interface RawWaveListener extends DeviceListener {

    void onRawWaveEvent(RawWaveEvent event);

}
