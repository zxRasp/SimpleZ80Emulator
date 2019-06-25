package com.zxrasp.emulator.core.z80.z80internals;

import com.zxrasp.emulator.core.EmulationException;

public class InvalidOperationException extends EmulationException {

    public InvalidOperationException(int opcode) {
        super(String.format("Invalid opcode: %x", opcode));
    }
}
