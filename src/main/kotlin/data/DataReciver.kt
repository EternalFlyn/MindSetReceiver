package data

import com.fazecast.jSerialComm.SerialPort
import com.fazecast.jSerialComm.SerialPortDataListener
import com.fazecast.jSerialComm.SerialPortEvent
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.*
import java.util.concurrent.locks.ReentrantLock

class DataReciver {

    companion object {

        private const val POOR_SIGNAL = 0X02
        private const val ATTENTION = 0X04
        private const val MEDITATION = 0X05
        private const val STRENGTH = 0X16
        private const val RAW_WAVE = 0X80
        private const val EEG_POWER = 0X83

        private enum class PhaserState { GET_CODE, GET_LENGTH, GET_VALUE }

        private var isConnect = false
        private val dataStorage = LinkedList<Byte>()
        private val lock = ReentrantLock()
        private val serialPort = SerialPort.getCommPort("COM4")

        fun connect() {
            if(isConnect) return
            serialPort.openPort()
            serialPort.addDataListener(object: SerialPortDataListener {

                override fun getListeningEvents(): Int {
                    return SerialPort.LISTENING_EVENT_DATA_AVAILABLE
                }

                override fun serialEvent(event: SerialPortEvent?) {
                    if(event?.eventType != SerialPort.LISTENING_EVENT_DATA_AVAILABLE) return
                    val bytes = ByteArray(serialPort.bytesAvailable())
                    serialPort.readBytes(bytes, bytes.size.toLong())
                    println("${System.currentTimeMillis()} - Length: ${bytes.size}")
                    bytes.forEach { print("%02x ".format(it)) }
                    println()
                    lock.lock()
                    try {
                        dataStorage.addAll(bytes.toList())
                    } finally {
                        lock.unlock()
                    }
                }

            })
            isConnect = true

            runBlocking {
                launch {
                    dataDecoder()
                }
            }
        }

        fun disconnect() {
            isConnect = false
            serialPort.closePort()
        }

        private suspend fun dataDecoder() {
            while(isConnect) {
                if(dataStorage.size > 0) {
                    val payload = mutableListOf<Byte>()
                    lock.lock()
                    try {
                        // check sync
                        if(dataStorage.poll().toInt() != 0xAA) continue
                        if(dataStorage.poll().toInt() != 0xAA) continue
                        val payloadLength = dataStorage.poll()
                        // get payload data
                        var checkSUM = 0
                        for(i in 0 until payloadLength) {
                            val data = dataStorage.poll()
                            checkSUM += data
                            payload.add(data)
                        }
                        // check checkSUM
                        if(checkSUM.inv() and 0xFF != dataStorage.poll().toInt()) continue
                    } finally {
                        lock.unlock()
                    }
                    // decode payload
                    var state = PhaserState.GET_CODE
                    var dataLength = 1
                    var code = 0
                    var value = mutableListOf<Byte>()
                    payload.forEach {
                        when(state) {
                            PhaserState.GET_CODE -> {
                                if(code != 0) payloadDecoder(code, value)
                                code = it.toInt()
                                value = mutableListOf()
                                state =
                                    if(it >= 0xFF) PhaserState.GET_LENGTH
                                    else PhaserState.GET_VALUE
                            }
                            PhaserState.GET_LENGTH -> {
                                dataLength = it.toInt()
                                state = PhaserState.GET_VALUE
                            }
                            PhaserState.GET_VALUE -> {
                                if(dataLength <= 1) state = PhaserState.GET_CODE
                                dataLength--
                                value.add(it)
                            }
                        }
                    }
                }
                delay(10)
            }
        }

        private fun payloadDecoder(code: Int, value: MutableList<Byte>) {
            when(code) {
                POOR_SIGNAL -> TODO()
                ATTENTION -> TODO()
                MEDITATION -> TODO()
                STRENGTH -> TODO()
                RAW_WAVE -> TODO()
                EEG_POWER -> TODO()
                else -> TODO()
            }
        }

    }

}