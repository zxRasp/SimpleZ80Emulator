package com.zxrasp.emulator.core;

import com.zxrasp.emulator.core.test.TestMachine;

public class EmulationCore {

    private SystemBus systemBus;

    public void init(Screen screen) {
        systemBus = new TestMachine();
    }

    public void doEmulation() {
        systemBus.run();
    }
}
