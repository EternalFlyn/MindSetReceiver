package com.flyn.eeg_receiver.data;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

class Decoder {

    record MetaEvent(int code, List<Byte> value) {}

    static boolean isDecoding = false;
    static ConcurrentLinkedQueue<Byte> dataStorage;

    private enum  DecoderState { GET_CODE, GET_LENGTH, GET_VALUE }

    @NotNull
    static ArrayList<MetaEvent> dataDecode() {
        isDecoding = true;
        ArrayList<MetaEvent> result = new ArrayList<>();
        if(dataStorage == null) return result;
        if ((getData() & 0xFF) != 0xAA) return result;
        if ((getData() & 0xFF) != 0xAA) return result;
        int payloadLength = getData() & 0xFF;
        ArrayList<Byte> payload = new ArrayList<>();
        byte checkSum = 0;
        for(int i = 0; i < payloadLength; i++) {
            byte b = getData();
            checkSum += b & 0xFF;
            payload.add(b);
        }
        if ((~checkSum & 0xFF) != (getData() & 0xFF)) return result;
        DecoderState state = DecoderState.GET_CODE;
        int dataLength = 1, code = 0;
        ArrayList<Byte> value = new ArrayList<>();
        for(Byte it : payload) {
            switch (state) {
                case GET_CODE -> {
                    code = it & 0xFF;
                    value = new ArrayList<>();
                    if ((code & 0x80) > 0) state = DecoderState.GET_LENGTH;
                    else state = DecoderState.GET_VALUE;
                }
                case GET_LENGTH -> {
                    dataLength = it & 0xFF;
                    state = DecoderState.GET_VALUE;
                }
                case GET_VALUE -> {
                    dataLength--;
                    value.add(it);
                    if(dataLength <= 0) {
                        state = DecoderState.GET_CODE;
                        result.add(new MetaEvent(code, value));
                    }
                }
            }
        }
        isDecoding = false;
        return result;
    }

    private static byte getData() {
        while(dataStorage.size() <= 0) {
            try {
                Thread.sleep(0, 100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return dataStorage.poll();
    }

    private Decoder() {}

}
