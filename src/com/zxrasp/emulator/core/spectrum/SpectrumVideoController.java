package com.zxrasp.emulator.core.spectrum;

import com.zxrasp.emulator.core.Screen;
import com.zxrasp.emulator.core.SystemBusDevice;
import com.zxrasp.emulator.core.VideoController;

public class SpectrumVideoController implements VideoController, SpectrumScreenMetrics {

    private static final int PIXELS_IN_VERTICAL_BORDER = SpectrumScreenMetrics.PIXEL_PER_LINE * SpectrumScreenMetrics.VERTICAL_BORDER;

    private static final int VIDEO_MEMORY_START = 16 * 1024;
    private static final int VIDEO_MEMORY_SIZE =  6 * 1024;
    private static final int ATR_MEMORY_START =   VIDEO_MEMORY_START + VIDEO_MEMORY_SIZE;
    private static final int BORDER_COLOR_PORT = 0xFE;

    // colors
    private static final int BRIGHT_COLOR = 255;
    private static final int NORMAL_COLOR = 205;

    private final SystemBusDevice bus;
    private final Screen screen;

    private int frameCount = 0;
    private boolean isInverse = false;


    public SpectrumVideoController(SystemBusDevice bus, Screen screen) {
        this.bus = bus;
        this.screen = screen;
    }

    @Override
    public void drawFrame(long ticks) {

        ++frameCount;

        if (frameCount > 16) {
            isInverse = !isInverse;
            frameCount = 0;
        }

        int[] buffer = screen.getScreenBuffer();

        // draw upper border
        int borderColor = getBorderColor(bus.readByteFromPort(BORDER_COLOR_PORT));
        int scr = 0;
        for(; scr < PIXELS_IN_VERTICAL_BORDER; scr++) {
            buffer[scr] = borderColor;
        }

        // draws paper area (192 lines)
        for (int t = 0; t < 3; t++) {
            for (int s = 0; s < 8; s++) {
                for (int c = 0; c < 8; c++) {

                    // left border
                    for (int i = 0; i < HORIZONTAL_BORDER; i++) {
                        buffer[scr++] = borderColor;
                    }

                    // middle
                    for (int h = 0; h < 32; h++) {
                        int pixPtr = VIDEO_MEMORY_START | (t << 11) | (c << 8) | (s << 5) | h;
                        int atrPtr = ATR_MEMORY_START | (t << 8) | (pixPtr & 0xFF) ;

                        int pixel = bus.readByteFromMemory(pixPtr);
                        int atr = bus.readByteFromMemory(atrPtr);

                        buffer[scr++] = calculateScreenColor(pixel, atr, 0x80);
                        buffer[scr++] = calculateScreenColor(pixel, atr, 0x40);
                        buffer[scr++] = calculateScreenColor(pixel, atr, 0x20);
                        buffer[scr++] = calculateScreenColor(pixel, atr, 0x10);
                        buffer[scr++] = calculateScreenColor(pixel, atr, 0x8);
                        buffer[scr++] = calculateScreenColor(pixel, atr, 0x4);
                        buffer[scr++] = calculateScreenColor(pixel, atr, 0x2);
                        buffer[scr++] = calculateScreenColor(pixel, atr, 0x1);
                    }

                    // right border
                    for (int i = 0; i < HORIZONTAL_BORDER; i++) {
                        buffer[scr++] = borderColor;
                    }
                }
            }
        }

        // draw bottom border
        for (; scr < buffer.length; scr++) {
            buffer[scr] = borderColor;
        }

        screen.updateScreen();
    }

    private int calculateScreenColor(int pixel, int atr, int mask) {
        boolean isInk = ((pixel & mask) != 0);
        boolean isFlash = ((atr & 0x80) != 0);

        if (isInk) {
            return (isFlash && isInverse) ? getPaperColor(atr) : getInkColor(atr);
        } else {
            return (isFlash && isInverse) ? getInkColor(atr) : getPaperColor(atr);
        }
    }

    private int getInkColor(int atr) {
        boolean isBright = ((atr & 0x40) != 0);
        int g = ((atr & 0x4) == 0 ? 0 : isBright ? BRIGHT_COLOR : NORMAL_COLOR);
        int r = ((atr & 0x2) == 0 ? 0 : isBright ? BRIGHT_COLOR : NORMAL_COLOR);
        int b = ((atr & 0x1) == 0 ? 0 : isBright ? BRIGHT_COLOR : NORMAL_COLOR);

        return (0xFF << 24) | (r << 16) | (g << 8) | b;
    }

    private int getPaperColor(int atr) {
        boolean isBright = ((atr & 0x40) != 0);
        int g = ((atr & 0x20) == 0 ? 0 : isBright ? BRIGHT_COLOR : NORMAL_COLOR);
        int r = ((atr & 0x10) == 0 ? 0 : isBright ? BRIGHT_COLOR : NORMAL_COLOR);
        int b = ((atr & 0x8)  == 0 ? 0 : isBright ? BRIGHT_COLOR : NORMAL_COLOR);

        return (0xFF << 24) | (r << 16) | (g << 8) | b;
    }

    private int getBorderColor(int portValue) {
        int g = ((portValue & 0x4) == 0 ? 0 : NORMAL_COLOR);
        int r = ((portValue & 0x2) == 0 ? 0 : NORMAL_COLOR);
        int b = ((portValue & 0x1) == 0 ? 0 : NORMAL_COLOR);

        return (0xFF << 24) | (r << 16) | (g << 8) | b;
    }
}
