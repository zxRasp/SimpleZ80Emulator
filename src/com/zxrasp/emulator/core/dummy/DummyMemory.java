package com.zxrasp.emulator.core.dummy;

import com.zxrasp.emulator.core.Memory;

public class DummyMemory implements Memory {

    @Override
    public void writeByteToMemory(int address, int data) {
        // nothing to do
    }

    @Override
    public int readByteFromMemory(int address) {
        return 0xFF;
    }
}
