package com.zxrasp.emulator.core.z80.z80internals;

import com.zxrasp.emulator.core.EmulationException;

public class UnknownOperationException extends EmulationException {
    public UnknownOperationException(String message) {
        super(message);
    }
}
