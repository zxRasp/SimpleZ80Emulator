package com.zxrasp.emulator.core;

public abstract class SystemBoard implements SystemBusDevice {

    public abstract CPU getCPU();

    public abstract void reset();

    @Override
    public void writeByteToPort(int address, int data) {
        // nothing to do
    }

    @Override
    public int readByteFromPort(int address) {
        return 0xFF;
    }

    @Override
    public void writeWordToMemory(int address, int data) {
        writeByteToMemory(address, data & 0xFF);
        writeByteToMemory(address + 1, data >> 8);
    }

    @Override
    public int readWordFromMemory(int address) {
        return (readByteFromMemory(address + 1) << 8) | readByteFromMemory(address);
    }
}
