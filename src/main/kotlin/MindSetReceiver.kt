import com.fazecast.jSerialComm.SerialPort
import com.fazecast.jSerialComm.SerialPortDataListener
import com.fazecast.jSerialComm.SerialPortEvent

internal const val RECEIVE_DATA_TIME = 5000L

fun main() {

    val serialPort = SerialPort.getCommPort("COM4")

    serialPort.openPort()

    serialPort.addDataListener(object: SerialPortDataListener {

        override fun getListeningEvents(): Int {
            return SerialPort.LISTENING_EVENT_DATA_AVAILABLE
        }

        override fun serialEvent(event: SerialPortEvent?) {
            if(event?.eventType != SerialPort.LISTENING_EVENT_DATA_AVAILABLE) return
            val bytes = ByteArray(serialPort.bytesAvailable())
            serialPort.readBytes(bytes, bytes.size.toLong())
            println("Length: ${bytes.size}")
            bytes.forEach { print("%02x ".format(it)) }
            println()
        }

    })

    Thread.sleep(RECEIVE_DATA_TIME)
    serialPort.closePort()

}