package com.zxrasp.emulator.core.test;

import com.zxrasp.emulator.core.SystemBoard;
import com.zxrasp.emulator.core.CPU;
import com.zxrasp.emulator.core.memory.RAMPage;
import com.zxrasp.emulator.core.memory.ROMLoadingException;
import com.zxrasp.emulator.core.z80.Z80TestDecorator;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class TestMachine extends SystemBoard {

    private static final int PAGE_SIZE = 64 * 1024;

    private CPU cpu;
    private RAMPage page;

    public TestMachine() {
        cpu = new Z80TestDecorator(this);
        try {
            page = new RAMPage(new FileInputStream("zexdoc.com"), PAGE_SIZE, 0x100);
        } catch (FileNotFoundException e) {
            throw new ROMLoadingException();
        }
    }

    @Override
    public CPU getCPU() {
        return cpu;
    }

    @Override
    public void reset() {
        cpu.reset();
    }

    @Override
    public void writeByteToMemory(int address, int data) {
        page.writeByteToMemory(address, data);
    }

    @Override
    public int readByteFromMemory(int address) {
        if (address == 0x5) {
            return 0xC9;
        }
        return page.readByteFromMemory(address);
    }

    @Override
    public void clock() {
        if (cpu.isHalted()) {
            return;
        }

        cpu.clock();
    }
}
