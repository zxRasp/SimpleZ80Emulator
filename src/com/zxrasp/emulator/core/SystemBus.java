package com.zxrasp.emulator.core;

public interface SystemBus extends Memory {

    void writeByteToPort(int address, int data);

    int readByteFromPort(int address);
}
