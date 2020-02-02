package com.zxrasp.emulator.core.z80.z80internals;

public class Z80EmulationException extends RuntimeException {

    public Z80EmulationException() {
    }

    public Z80EmulationException(String message) {
        super(message);
    }
}
