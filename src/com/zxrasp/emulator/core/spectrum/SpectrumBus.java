package com.zxrasp.emulator.core.spectrum;

import com.zxrasp.emulator.core.Memory;
import com.zxrasp.emulator.core.SystemBus;
import com.zxrasp.emulator.core.memory.RAMPage;
import com.zxrasp.emulator.core.memory.ROMLoadingException;
import com.zxrasp.emulator.core.memory.ROMPage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Random;

public class SpectrumBus implements SystemBus {

    private static final int DEFAULT_PAGE_SIZE = 16384;

    private Memory[] pages;

    private int portFE;

    public SpectrumBus() {
        pages = new Memory[4];

        try {
            pages[0] = new ROMPage(new FileInputStream("test.rom"), DEFAULT_PAGE_SIZE);
        } catch (FileNotFoundException e) {
            throw new ROMLoadingException();
        }
        pages[1] = new RAMPage(DEFAULT_PAGE_SIZE, new Random());
        pages[2] = new RAMPage(DEFAULT_PAGE_SIZE, new Random());
        pages[3] = new RAMPage(DEFAULT_PAGE_SIZE, new Random());

        portFE = 0xFF;
    }

    @Override
    public void writeByteToPort(int address, int data) {
        if ((address & 0x1) == 0) {
            portFE = data & 0xFF;
        }
    }

    @Override
    public int readByteFromPort(int address) {
        if ((address & 0x1) == 0) {
            return portFE & 0xFF;
        }

        return 0xFF;
    }

    @Override
    public void writeByteToMemory(int address, int data) {
        int page = (address >> 14) & 0x3;
        pages[page].writeByteToMemory(address & 0x3FFF, data);
    }

    @Override
    public int readByteFromMemory(int address) {
        int page = (address >> 14) & 0x3;
        return pages[page].readByteFromMemory(address & 0x3FFF);
    }

    @Override
    public void writeWordToMemory(int address, int data) {
        writeByteToMemory(address, data & 0xFF);
        writeByteToMemory(address + 1, data >> 8);
    }

    @Override
    public int readWordFromMemory(int address) {
        return (readByteFromMemory(address) << 8) | readByteFromMemory(address + 1);
    }
}
