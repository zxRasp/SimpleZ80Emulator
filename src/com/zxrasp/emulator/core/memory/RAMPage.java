package com.zxrasp.emulator.core.memory;

import com.zxrasp.emulator.core.Memory;

public class RAMPage implements Memory {

    private final byte[] memory;

    public RAMPage(int size) {
        this.memory = new byte[size];
    }

    public RAMPage(final byte[] dump) {
        this.memory = new byte[dump.length];
        System.arraycopy(dump, 0, memory, 0, memory.length);
    }

    @Override
    public void writeByteToMemory(int address, int data) {
        memory[address] = (byte) (data & 0xFF);
    }

    @Override
    public int readByteFromMemory(int address) {
        return memory[address];
    }

    @Override
    public void writeWordToMemory(int address, int data) {
        memory[address] = (byte) (data & 0xFF);
        memory[address + 1] = (byte) ((data >> 8) & 0xFF);
    }

    @Override
    public int readWordFromMemory(int address) {
        return (memory[address + 1] << 8) | memory[address];
    }
}
