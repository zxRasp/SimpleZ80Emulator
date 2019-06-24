package com.zxrasp.emulator.core.dummy;

import com.zxrasp.emulator.core.CPU;
import com.zxrasp.emulator.core.z80.z80internals.UnknownOperationException;

public class DummyCPU implements CPU {

    @Override
    public long clock() throws UnknownOperationException {
        return 4;
    }

    @Override
    public void reset() {
        // nothing to do
    }

    @Override
    public boolean isHalted() {
        return false;
    }

    @Override
    public void interrupt(boolean masked) {
        // nothing to do
    }
}
