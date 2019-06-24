package com.zxrasp.emulator.core.spectrum;

import com.zxrasp.emulator.core.*;
import com.zxrasp.emulator.core.memory.RAMPage;
import com.zxrasp.emulator.core.memory.ROMLoadingException;
import com.zxrasp.emulator.core.memory.ROMPage;
import com.zxrasp.emulator.core.z80.Z80;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Random;

public class SpectrumBus extends AbstractSystem {

    private static final int DEFAULT_PAGE_SIZE = 16384;

    private static final int CLOCKS_PER_FRAME = 69000;
    private static final int FRAME_TIME = 1000 / 50;

    private Memory[] pages;

    private int portFE;
    private CPU cpu;
    private VideoController videoController;

    public SpectrumBus(Screen screen) {
        cpu = new Z80(this);
        videoController = new SpectrumVideoController(this, screen);

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
    public void run() {
        while (true) {
            long start = System.currentTimeMillis();

            long clock = 0;
            while (clock < CLOCKS_PER_FRAME) {
                clock += cpu.clock();
            }

            videoController.drawFrame(0);

            cpu.interrupt(true);

            long actualFrameTime = System.currentTimeMillis() - start;

            if (actualFrameTime < FRAME_TIME) {
                try {
                    Thread.sleep(FRAME_TIME - actualFrameTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
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
}
