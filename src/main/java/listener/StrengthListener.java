package listener;

import event.StrengthEvent;

public interface StrengthListener extends DeviceListener {

    void onStrengthEvent(StrengthEvent event);

}
