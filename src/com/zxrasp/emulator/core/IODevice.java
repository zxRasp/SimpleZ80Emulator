package com.zxrasp.emulator.core;

public interface IODevice {

    void writeByteToPort(int address, int data);

    int readByteFromPort(int address);

    void clock();
}
