package com.zxrasp.emulator.core.impl;

import com.zxrasp.emulator.core.SystemBus;

public class SimpleULA implements SystemBus {

    @Override
    public void writeByteToMemory(int address, int data) {
        System.out.println("Write byte <" + (data & 0xFF) + "> to address [" + address + "]");
    }

    @Override
    public int readByteFromMemory(int address) {
        return 0x01;
    }

    @Override
    public void writeWordToMemory(int address, int data) {
        System.out.println("Write word <" + (data & 0xFFFF) + "> to address [" + address + "]");
    }

    @Override
    public int readWordFromMemory(int address) {
        return 0;
    }

    @Override
    public void writeByteToPort(int address, int data) {
        System.out.println("Write byte <" + data + "> to port address [" + address + "]");
    }

    @Override
    public int readByteFromPort(int address) {
        return 0;
    }
}
