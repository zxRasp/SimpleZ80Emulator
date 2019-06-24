package com.zxrasp.emulator.core;

public interface SystemBus extends Memory {

    void writeByteToPort(int address, int data);

    int readByteFromPort(int address);

    void writeWordToMemory(int address, int data);

    int readWordFromMemory(int address);
}
