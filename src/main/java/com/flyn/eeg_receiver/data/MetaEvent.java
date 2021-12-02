package com.flyn.eeg_receiver.data;

import java.util.List;

public class MetaEvent {

    private final int code;
    private final List<Byte> value;

    MetaEvent(int code, List<Byte> value) {
        this.code = code;
        this.value = value;
    }

    public int getCode() {
        return code;
    }

    public List<Byte> getValue() {
        return value;
    }

}
