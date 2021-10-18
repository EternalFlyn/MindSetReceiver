package com.flyn.eeg_receiver.data

import com.fazecast.jSerialComm.SerialPort
import com.fazecast.jSerialComm.SerialPortDataListener
import com.fazecast.jSerialComm.SerialPortEvent
import com.flyn.eeg_receiver.event.*
import com.flyn.eeg_receiver.listener.*
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.CopyOnWriteArrayList

object DataReceiver {

    private const val POOR_SIGNAL = 0X02
    private const val ATTENTION = 0X04
    private const val MEDITATION = 0X05
    private const val STRENGTH = 0X16
    private const val RAW_WAVE = 0X80
    private const val EEG_POWER = 0X83

    private val dataStorage = ConcurrentLinkedQueue<Byte>()
    private val eventList = ConcurrentLinkedQueue<DeviceEvent>()
    private val listenerList = CopyOnWriteArrayList<DeviceListener>()
    private lateinit var serialPort: SerialPort

    var isConnect = false
        private set
    var startTime: Long = -1
        private set

    fun connect(comPort: String, echo: Boolean = false) {
        if (isConnect) return
        isConnect = true
        Decoder.dataStorage = dataStorage
        serialPort = SerialPort.getCommPort(comPort)
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
                    println("${Date(System.currentTimeMillis())} - Length: ${bytes.size}")
                    bytes.forEach { print("%02x ".format(it)) }
                    println()
                }
                dataStorage.addAll(bytes.toList())
            }

        })
        println("Device connect")
        startTime = System.currentTimeMillis()
        Thread { dataDecoder() }.start()
        Thread { eventExecutor() }.start()
    }

    fun disconnect() {
        if (!isConnect) return
        isConnect = false
        while(Decoder.isDecoding) Thread.sleep(10)
        serialPort.closePort()
        println("Device disconnect")
    }

    fun addListener(listener: DeviceListener) {
        listenerList.add(listener)
    }

    private fun dataDecoder() {
        while (isConnect) {
            if (dataStorage.size > 0) {
                val time = System.currentTimeMillis()
                Decoder.dataDecode().forEach {
                    payloadDecoder(time, it.code, it.value)
                }
            }
            else {
                Thread.sleep(1)
            }
        }
    }

    private fun payloadDecoder(time: Long, code: Int, values: List<Byte>) {
        when (code) {
            POOR_SIGNAL -> eventList.add(PoorSignalEvent(time, values[0].toUByte().toInt()))
            ATTENTION -> eventList.add(AttentionEvent(time, values[0].toInt()))
            MEDITATION -> eventList.add(MeditationEvent(time, values[0].toInt()))
            STRENGTH -> eventList.add(StrengthEvent(time, values[0].toUByte().toInt()))
            RAW_WAVE -> {
                val value = ByteBuffer.wrap(values.slice(0..1).toByteArray()).order(ByteOrder.BIG_ENDIAN)
                eventList.add(RawWaveEvent(time, value.short.toInt()))
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
                eventList.add(EEGPowerEvent(time, wave[0], wave[1], wave[2], wave[3], wave[4], wave[5], wave[6], wave[7]))
            }
            else -> {
                print("unknown code event: %02x with value:".format(code))
                values.forEach { print("%02x ".format(it)) }
                println()
            }
        }
    }

    private fun eventExecutor() {
        while (isConnect) {
            if (eventList.size > 0) {
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
            }
            else {
                Thread.sleep(1)
            }
        }
    }

}