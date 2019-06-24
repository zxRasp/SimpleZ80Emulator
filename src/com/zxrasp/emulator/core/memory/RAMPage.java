package com.zxrasp.emulator.core.memory;

import com.zxrasp.emulator.core.Memory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Random;

public class RAMPage implements Memory {

    private final byte[] memory;

    public RAMPage(int size) {
        this.memory = new byte[size];
    }

    public RAMPage(int size, Random rnd) {
       this(size);

        for (int i = 0; i < memory.length; i++) {
            memory[i] = (byte) (rnd.nextInt() % 255);
        }
    }

    public RAMPage(FileInputStream inputStream, int size, int offset) {
        this(size);

        try {
            inputStream.read(memory, offset, memory.length - offset);
        } catch (IOException e) {
            throw new ROMLoadingException();
        }
    }

    @Override
    public void writeByteToMemory(int address, int data) {
        memory[address & 0xFFFF] = (byte) (data & 0xFF);
    }

    @Override
    public int readByteFromMemory(int address) {
        return memory[address & 0xFFFF] & 0xFF;
    }
}
