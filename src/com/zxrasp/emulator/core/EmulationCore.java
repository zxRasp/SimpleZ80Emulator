package com.zxrasp.emulator.core;

import com.zxrasp.emulator.core.spectrum.Spectrum48K;
import com.zxrasp.emulator.core.test.TestMachine;
import com.zxrasp.emulator.platform.SwingScreen;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class EmulationCore {

    private SystemBoard systemBoard;
    private boolean isDebugMode;

    public EmulationCore(String[] args) {
        configureEmulator(args);
    }

    public void doEmulation() {
        systemBoard.reset();

        while (true) {
            systemBoard.clock();

            if (isDebugMode) {
                System.out.println(systemBoard.getCPU());
            }
        }
    }

    private void configureEmulator(String[] args) {
        Set<String> config = new HashSet<>(args.length);
        Collections.addAll(config, args);

        if (config.contains("debug")) {
            isDebugMode = true;
        }

        String romName;

        if (config.contains("48")) {
            romName = "48.rom";
        } else {
            romName = "test.rom";
        }

        if (config.contains("zx")) {
            Screen screen = new SwingScreen(romName);
            systemBoard = new Spectrum48K(screen, romName);
        } else {
            systemBoard = new TestMachine();
        }

    }
}
