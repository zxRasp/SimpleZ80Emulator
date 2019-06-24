package com.zxrasp.emulator.core.dummy;

import com.zxrasp.emulator.core.Screen;
import com.zxrasp.emulator.core.VideoController;

import java.util.Random;

public class DummyVideoController implements VideoController {

    private Screen screen;
    private Random rnd;

    public DummyVideoController(Screen screen) {
        this.screen = screen;
        this.rnd = new Random();
    }

    @Override
    public void tick(long ticks) {
        int[] screenBuffer = screen.getScreenBuffer();

        for (int i = 0; i < screenBuffer.length; i++) {
            int red = rnd.nextInt() % 255;
            int green = rnd.nextInt() % 255;
            int blue = rnd.nextInt() % 255;

            screenBuffer[i] = (0xFF << 24) | (red << 16) | (green << 8) | blue;
        }

        screen.updateScreen();

    }
}
