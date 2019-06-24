package com.zxrasp.emulator.core.memory;

import com.zxrasp.emulator.core.Memory;

import java.util.Random;

public class RAMPage implements Memory {

    private final byte[] memory;

    public RAMPage(int size) {
        this.memory = new byte[size];
    }

    public RAMPage(final byte[] dump) {
        this.memory = new byte[dump.length];
        System.arraycopy(dump, 0, memory, 0, memory.length);
    }

    public RAMPage(int size, Random rnd) {
       memory = new byte[size];

       for (int i = 0; i < memory.length; i++) {
           memory[i] = (byte) (rnd.nextInt() % 255);
       }
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
