package com.zxrasp.emulator.core;

import com.zxrasp.emulator.core.z80.z80internals.Context;
import com.zxrasp.emulator.core.z80.z80internals.UnknownOperationException;

public interface CPU {

    long clock() throws UnknownOperationException;

    void reset();

    boolean isHalted();

    void interrupt(boolean masked);

    Context getContext();
}
