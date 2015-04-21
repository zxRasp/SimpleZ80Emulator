package com.zxrasp.emulator.core;

import com.zxrasp.emulator.core.impl.z80internals.UnknownOperationException;

public interface CPU {

    long clock() throws UnknownOperationException;

    void reset();

    boolean isHalted();

    void interrupt(boolean masked);
}
