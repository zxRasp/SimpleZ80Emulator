package com.zxrasp.emulator.core.memory;

import com.zxrasp.emulator.core.Memory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

public class ROMPage implements Memory {

    private final byte[] memory;

    public ROMPage(int size) {
        this.memory = new byte[size];
        Arrays.fill(memory, (byte) 0xFF);
    }

    public ROMPage(FileInputStream inputStream, int size) {
        this(size);

        try {
            inputStream.read(memory);
        } catch (IOException e) {
            throw new ROMLoadingException();
        }
    }

    @Override
    public void writeByteToMemory(int address, int data) {
        // nothing to do
    }

    @Override
    public int readByteFromMemory(int address) {
        return memory[address] & 0xFF;
    }

}
