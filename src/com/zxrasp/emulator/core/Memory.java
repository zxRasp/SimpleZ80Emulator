package com.zxrasp.emulator.core;

public interface Memory {
    void writeByteToMemory(int address, int data);

    int readByteFromMemory(int address);
}
