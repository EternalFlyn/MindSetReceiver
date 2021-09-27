package event

class EEGPowerEvent(val delta: Int,
                    val theta: Int,
                    val low_alpha: Int,
                    val high_alpha: Int,
                    val low_beta: Int,
                    val high_beta: Int,
                    val low_gamma: Int,
                    val mid_gamma: Int
                    ): DeviceEvent {
}