package com.zxrasp.emulator.core;

import com.zxrasp.emulator.core.dummy.DummyVideoController;
import com.zxrasp.emulator.core.spectrum.SpectrumBus;
import com.zxrasp.emulator.core.z80.Z80;

public class EmulationCore {

    private CPU cpu;
    private SystemBus systemBus;
    private VideoController videoController;

    public void init(Screen screen) {
        systemBus = new SpectrumBus();
        cpu = new Z80(systemBus);
        videoController = new DummyVideoController(screen);
    }

    public void doEmulation(long frameTime) {
        while (true) {
            long t = cpu.clock();
            videoController.tick(t);

        }
    }
}
