package data

import com.fazecast.jSerialComm.SerialPort
import com.fazecast.jSerialComm.SerialPortDataListener
import com.fazecast.jSerialComm.SerialPortEvent
import event.*
import listener.*
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.*
import java.util.concurrent.locks.ReentrantLock

object DataReceiver {

    private const val POOR_SIGNAL = 0X02
    private const val ATTENTION = 0X04
    private const val MEDITATION = 0X05
    private const val STRENGTH = 0X16
    private const val RAW_WAVE = 0X80
    private const val EEG_POWER = 0X83

    private var isConnect = false
    private val dataStorage = LinkedList<Byte>()
    private val eventList = LinkedList<DeviceEvent>()
    private val listenerList = mutableListOf<DeviceListener>()
    private val dataLock = ReentrantLock()
    private val eventLock = ReentrantLock()
    private val serialPort = SerialPort.getCommPort("COM6")

    fun connect(echo: Boolean = false) {
        if (isConnect) return
        serialPort.openPort()
        serialPort.addDataListener(object : SerialPortDataListener {

            override fun getListeningEvents(): Int {
                return SerialPort.LISTENING_EVENT_DATA_AVAILABLE
            }

            override fun serialEvent(event: SerialPortEvent?) {
                if (event?.eventType != SerialPort.LISTENING_EVENT_DATA_AVAILABLE) return
                val bytes = ByteArray(serialPort.bytesAvailable())
                serialPort.readBytes(bytes, bytes.size.toLong())
                if(echo) {
                    println("${System.currentTimeMillis()} - Length: ${bytes.size}")
                    bytes.forEach { print("%02x ".format(it)) }
                    println()
                }
                dataLock.lock()
                try {
                    dataStorage.addAll(bytes.toList())
                } finally {
                    dataLock.unlock()
                }
            }

        })
        isConnect = true
        println("Device connect")
        Thread { dataDecoder() }.start()
        Thread { eventExecutor() }.start()
    }

    fun disconnect() {
        isConnect = false
        serialPort.closePort()
        println("Device disconnect")
    }

    fun addListener(listener: DeviceListener) {
        listenerList.add(listener)
    }

    private fun dataDecoder() {
        while (isConnect) {
            if (dataStorage.size > 0) {
                dataLock.lock()
                try {
                    Decoder.dataDecode(dataStorage).forEach {
                        payloadDecoder(it.code, it.value)
                    }
                } finally {
                    dataLock.unlock()
                }
            }
            else {
                Thread.sleep(10)
            }
        }
    }

    private fun payloadDecoder(code: Int, values: List<Byte>) {
        eventLock.lock()
        try {
            when (code) {
                POOR_SIGNAL -> eventList.add(PoorSignalEvent(values[0].toUByte().toInt()))
                ATTENTION -> eventList.add(AttentionEvent(values[0].toInt()))
                MEDITATION -> eventList.add(MeditationEvent(values[0].toInt()))
                STRENGTH -> eventList.add(StrengthEvent(values[0].toUByte().toInt()))
                RAW_WAVE -> {
                    val value = ByteBuffer.wrap(values.slice(0..1).toByteArray()).order(ByteOrder.BIG_ENDIAN)
                    eventList.add(RawWaveEvent(value.short.toInt()))
                }
                EEG_POWER -> {
                    val wave = values.withIndex().groupBy {
                        it.index / 3
                    }.map {
                        var value = 0
                        it.value.forEach { dataWithIndex ->
                            value = value shl 8 or dataWithIndex.value.toInt()
                        }
                        value
                    }
                    eventList.add(EEGPowerEvent(wave[0], wave[1], wave[2], wave[3], wave[4], wave[5], wave[6], wave[7]))
                }
                else -> {
                    print("unknown code event: %02x with value:".format(code))
                    values.forEach { print("%02x ".format(it)) }
                    println()
                }
            }
        } finally {
            eventLock.unlock()
        }
    }

    private fun eventExecutor() {
        while (isConnect) {
            if (eventList.size > 0) {
                eventLock.lock()
                try {
                    when (val event = eventList.poll()) {
                        is PoorSignalEvent -> {
                            listenerList.filterIsInstance<PoorSignalListener>().forEach { it.onPoorSignalEvent(event) }
                        }
                        is AttentionEvent -> {
                            listenerList.filterIsInstance<AttentionListener>().forEach { it.onAttentionEvent(event) }
                        }
                        is MeditationEvent -> {
                            listenerList.filterIsInstance<MeditationListener>().forEach { it.onMeditationEvent(event) }
                        }
                        is StrengthEvent -> {
                            listenerList.filterIsInstance<StrengthListener>().forEach { it.onStrengthEvent(event) }
                        }
                        is RawWaveEvent -> {
                            listenerList.filterIsInstance<RawWaveListener>().forEach { it.onRawWaveEvent(event) }
                        }
                        is EEGPowerEvent -> {
                            listenerList.filterIsInstance<EEGPowerListener>().forEach { it.onEEGPowerEvent(event) }
                        }
                    }
                } finally {
                    eventLock.unlock()
                }
            }
            else {
                Thread.sleep(10)
            }
        }
    }

}