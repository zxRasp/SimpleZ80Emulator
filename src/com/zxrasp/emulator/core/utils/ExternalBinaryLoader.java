package com.zxrasp.emulator.core.utils;

import java.io.FileInputStream;
import java.io.IOException;

public class ExternalBinaryLoader {

    public static byte[] loadBinary(String fileName) {
        byte[] result = new byte[0];

        try {
            new FileInputStream(fileName).read(result);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }
}
