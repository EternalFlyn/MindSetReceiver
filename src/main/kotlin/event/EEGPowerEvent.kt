package event

class EEGPowerEvent(val delta: Int,
                    val theta: Int,
                    val lowAlpha: Int,
                    val highAlpha: Int,
                    val lowBeta: Int,
                    val highBeta: Int,
                    val lowGamma: Int,
                    val midGamma: Int
                    ): DeviceEvent {
}