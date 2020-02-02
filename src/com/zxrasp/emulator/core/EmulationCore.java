package com.zxrasp.emulator.core;

import com.zxrasp.emulator.core.spectrum.Spectrum48K;
import com.zxrasp.emulator.core.test.TestMachine;
import com.zxrasp.emulator.platform.SwingScreen;

import java.awt.*;

public class EmulationCore {

    private SystemBoard systemBoard;
    private boolean isDebugMode;

    public EmulationCore(String[] args) {
        if (args.length == 0) {
            systemBoard = new TestMachine();
        } else if (args[0].equalsIgnoreCase("zx")) {
            Screen screen = new SwingScreen("ZX 48K", new Dimension(800, 600));
            systemBoard = new Spectrum48K(screen, "48.rom");
        } else {
            throw new EmulationConfigurationException("Undefined config: " + args[0]);
        }

        isDebugMode = false;
    }

    public void doEmulation() {
        systemBoard.reset();

        while (true) {
            systemBoard.clock();

            if (isDebugMode) {
                System.out.println(systemBoard.getCPU().getContext());
            }
        }
    }
}
