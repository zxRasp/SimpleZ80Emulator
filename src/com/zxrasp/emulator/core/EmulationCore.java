package com.zxrasp.emulator.core;

import com.zxrasp.emulator.core.dummy.DummyCPU;
import com.zxrasp.emulator.core.spectrum.SpectrumBus;
import com.zxrasp.emulator.core.spectrum.SpectrumVideoController;
import com.zxrasp.emulator.core.z80.Z80;

public class EmulationCore {

    public static final int CLOCKS_PER_FRAME = 69000;


    private CPU cpu;
    private SystemBus systemBus;
    private VideoController videoController;

    public void init(Screen screen) {
        systemBus = new SpectrumBus();
        cpu =  new Z80(systemBus);
        videoController = new SpectrumVideoController(systemBus, screen);
    }

    public void doEmulation(long frameTime) {
        while (true) {
            long start = System.currentTimeMillis();

            long clock = 0;
            while (clock < CLOCKS_PER_FRAME) {
                clock += cpu.clock();
            }

            videoController.drawFrame(0);

            long actualFrameTime = System.currentTimeMillis() - start;

            if (actualFrameTime < frameTime) {
                try {
                    Thread.sleep(frameTime - actualFrameTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
