package com.zxrasp.emulator.core;

public interface SystemBus {

    void writeByteToMemory(int address, int data);

    int readByteFromMemory(int address);

    void writeWordToMemory(int address, int data);

    int readWordFromMemory(int address);

    void writeByteToPort(int address, int data);

    int readByteFromPort(int address);
}
