package listener;

import event.MeditationEvent;

public interface MeditationListener extends DeviceListener {

    void onMeditationEvent(MeditationEvent event);

}
