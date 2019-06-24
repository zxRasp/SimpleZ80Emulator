package com.zxrasp.emulator.core.spectrum;

import com.zxrasp.emulator.core.Screen;
import com.zxrasp.emulator.core.SystemBus;
import com.zxrasp.emulator.core.VideoController;

public class SpectrumVideoController implements VideoController {

    public static final int VIDEO_MEMORY_START = 16 * 1024;
    public static final int VIDEO_MEMORY_SIZE =  6 * 1024;
    public static final int ATR_MEMORY_START =   VIDEO_MEMORY_START + VIDEO_MEMORY_SIZE;
    public static final int ATR_MEMORY_SIZE = 32 * 24;
    public static final int BORDER_COLOR_PORT = 0xFE;

    private SystemBus bus;
    private Screen screen;

    public SpectrumVideoController(SystemBus bus, Screen screen) {
        this.bus = bus;
        this.screen = screen;
    }

    @Override
    public void tick(long ticks) {
        int[] buffer = screen.getScreenBuffer();

        // draw upper border (56 lines)
        int borderColor = getBorderColor(bus.readByteFromPort(BORDER_COLOR_PORT));
        int scr = 0;
        for(; scr < 56 * 448; scr++) {
            buffer[scr] = borderColor;
        }

        // draws paper area (192 lines)
        int pixPtr = VIDEO_MEMORY_START;
        int atrPtr = ATR_MEMORY_START;

        for (int j = 0; j < 193; j++) {
            // left border (48 pix)
            for (int i = 0; i < 49; i++) {
                buffer[++scr] = borderColor;
            }

            // middle (256 pix)
            for (int i = 0; i < 33; i++) {
                int pixel = bus.readByteFromMemory(pixPtr++);
                int atr = bus.readByteFromMemory(atrPtr++);
                buffer[++scr] = calculateScreenColor(pixel, atr, 0x1);
                buffer[++scr] = calculateScreenColor(pixel, atr, 0x2);
                buffer[++scr] = calculateScreenColor(pixel, atr, 0x4);
                buffer[++scr] = calculateScreenColor(pixel, atr, 0x8);
                buffer[++scr] = calculateScreenColor(pixel, atr, 0x10);
                buffer[++scr] = calculateScreenColor(pixel, atr, 0x20);
                buffer[++scr] = calculateScreenColor(pixel, atr, 0x40);
                buffer[++scr] = calculateScreenColor(pixel, atr, 0x80);
            }

            // right border (48 pix)
            for (int i = 0; i < 49; i++) {
                buffer[++scr] = borderColor;
            }
        }

        // draw bottom border (56 lines)
        for (; scr < buffer.length; scr++) {
            buffer[scr] = borderColor;
        }

        screen.updateScreen();
    }

    private int calculateScreenColor(int pixel, int atr, int mask) {
        boolean isPaper = ((pixel & mask) == 0);

        if (isPaper) {
            return getPaperColor(atr);
        } else {
            return getInkColor(atr);
        }
    }

    private int getInkColor(int atr) {
        boolean isBright = ((atr & 0x40) != 0);
        int r = ((atr & 0x20) == 0 ? 0 : isBright ? 255 : 128);
        int g = ((atr & 0x10) == 0 ? 0 : isBright ? 255 : 128);
        int b = ((atr & 0x8) == 0 ? 0 : isBright ? 255 : 128);

        return (0xFF << 24) | (r << 16) | (g << 8) | b;
    }

    private int getPaperColor(int atr) {
        boolean isBright = ((atr & 0x40) != 0);
        int r = ((atr & 0x4) == 0 ? 0 : isBright ? 255 : 128);
        int g = ((atr & 0x2) == 0 ? 0 : isBright ? 255 : 128);
        int b = ((atr & 0x1) == 0 ? 0 : isBright ? 255 : 128);

        return (0xFF << 24) | (r << 16) | (g << 8) | b;
    }

    private int getBorderColor(int portValue) {
        int r = ((portValue & 0x4) == 0 ? 0 : 128);
        int g = ((portValue & 0x2) == 0 ? 0 : 128);
        int b = ((portValue & 0x1) == 0 ? 0 : 128);

        return (0xFF << 24) | (r << 16) | (g << 8) | b;
    }
}
