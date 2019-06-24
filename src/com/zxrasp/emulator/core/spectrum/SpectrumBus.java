package com.zxrasp.emulator.core.spectrum;

import com.zxrasp.emulator.core.SystemBus;

import java.util.Random;

public class SpectrumBus implements SystemBus {

    private Random rnd = new Random();

    @Override
    public void writeByteToPort(int address, int data) {
        // todo
    }

    @Override
    public int readByteFromPort(int address) {
        return 0xFF; // todo
    }

    @Override
    public void writeByteToMemory(int address, int data) {

    }

    @Override
    public int readByteFromMemory(int address) {
        return 0;
    }

    @Override
    public void writeWordToMemory(int address, int data) {

    }

    @Override
    public int readWordFromMemory(int address) {
        return 0;
    }
}
