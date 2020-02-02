package com.zxrasp.emulator;

import com.zxrasp.emulator.core.EmulationCore;

public class Main {

    public static final String WINDOW_TITLE = "Simple Z80 Emulator";

    public static void main(String[] args) {
       EmulationCore core = new EmulationCore(args);
       core.doEmulation();
    }
}
