package com.zxrasp.emulator.core.z80.z80internals;

public class InvalidOperationException extends Z80EmulationException {

    public InvalidOperationException(int opcode) {
        super(String.format("Invalid opcode: %x", opcode));
    }
}
