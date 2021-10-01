import data.DataReceiver
import event.*
import listener.*

internal const val RECEIVE_DATA_TIME = 5000L

fun main() {
    DataReceiver.addListener(object : PoorSignalListener, AttentionListener, MeditationListener, StrengthListener, RawWaveListener, EEGPowerListener {

        override fun onRawWaveEvent(event: RawWaveEvent) {
            println("Receive Raw Wave Event, value: ${event.value}")
        }

        override fun onPoorSignalEvent(event: PoorSignalEvent) {
            println("Receive Poor Signal Event, value: ${event.quality}")
        }

        override fun onAttentionEvent(event: AttentionEvent) {
            println("Receive Attention Event, value: ${event.value}")
        }

        override fun onEEGPowerEvent(event: EEGPowerEvent) {
            print("Receive EEG Event,")
            print("low-alpha: ${event.lowAlpha},")
            print("high-alpha: ${event.highAlpha},")
            print("low-beta: ${event.lowBeta},")
            print("high-beta: ${event.highBeta},")
            print("low-gamma: ${event.lowGamma},")
            print("mid-gamma: ${event.midGamma},")
            print("delta: ${event.delta},")
            println("theta: ${event.theta}")
        }

        override fun onMeditationEvent(event: MeditationEvent) {
            println("Receive Meditation Event, value: ${event.value}")
        }

        override fun onStrengthEvent(event: StrengthEvent) {
            println("Receive Strength Event, value: ${event.value}")
        }

    })
    DataReceiver.connect(true)
    Thread.sleep(RECEIVE_DATA_TIME)
    DataReceiver.disconnect()
}