package com.zxrasp.emulator.core.dummy;

import com.zxrasp.emulator.core.VideoController;

public class DummyVideoController implements VideoController {

    public DummyVideoController() {
    }

    @Override
    public void drawFrame(long ticks) {
        // nothing to do
    }
}
