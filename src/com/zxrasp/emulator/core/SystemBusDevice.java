package com.zxrasp.emulator.core;

public interface SystemBusDevice extends Memory, IODevice {

    void writeWordToMemory(int address, int data);

    int readWordFromMemory(int address);
}
