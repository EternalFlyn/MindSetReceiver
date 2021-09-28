package data;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Decoder {

    record MetaEvent(int code, List<Byte> value) {}

    private enum  DecoderState { GET_CODE, GET_LENGTH, GET_VALUE }

    @NotNull
    static ArrayList<MetaEvent> dataDecode(LinkedList<Byte> data) {
        ArrayList<MetaEvent> result = new ArrayList<>();
        if ((data.removeFirst() & 0xFF) != 0xAA) return result;
        if ((data.removeFirst() & 0xFF) != 0xAA) return result;
        int payloadLength = data.removeFirst() & 0xFF;
        ArrayList<Byte> payload = new ArrayList<>();
        byte checkSum = 0;
        for(int i = 0; i < payloadLength; i++) {
            byte b = data.removeFirst();
            checkSum += b & 0xFF;
            payload.add(b);
        }
        if ((~checkSum & 0xFF) != (data.removeFirst() & 0xFF)) return result;
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
        return result;
    }

    private Decoder() {}

}
