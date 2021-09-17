import thinkgear.ThinkGear

internal const val RECEIVE_DATA_TIME = 5000

fun main() {
    /* Print driver version and get a connetion ID */
    val version = ThinkGear.GetDriverVersion()
    println("ThinkGear DLL version: $version")
    val connectionID = ThinkGear.GetNewConnectionId()
    if(connectionID < 0) {
        System.err.println("Error: Can not get connection ID!")
        return
    }

    /* Connect device */
    val comPortName = "\\\\.\\COM4"
    if(ThinkGear.Connect(connectionID, comPortName, ThinkGear.BAUD_57600, ThinkGear.STREAM_PACKETS) < 0) {
        System.err.println("Error: Can not connect to device!")
        return
    }

    /* Keep reading packets in setting time */
    val startTime = System.currentTimeMillis()
    while(System.currentTimeMillis() - startTime < RECEIVE_DATA_TIME) {
        var packetsRead: Int
        do {
            packetsRead = ThinkGear.ReadPackets(connectionID, 1)
            if(packetsRead == 1 && ThinkGear.GetValueStatus(connectionID, ThinkGear.DATA_RAW) != 0) {
                val currTime = System.currentTimeMillis()
                val data = ThinkGear.GetValue(connectionID, ThinkGear.DATA_RAW)
                println("Raw data - $currTime: $data")
            }
        } while (packetsRead > 0)
    }

    /* Disconnect */
    ThinkGear.FreeConnection(connectionID)
}