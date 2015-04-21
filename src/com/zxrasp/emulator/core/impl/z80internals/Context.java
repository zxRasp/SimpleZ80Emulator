package com.zxrasp.emulator.core.impl.z80internals;

import com.zxrasp.emulator.core.SystemBus;

public interface Context {

    void reset();

    boolean isHalted();

    void setHalt(boolean value);

    SystemBus getSystemBus();

    void set(RegisterNames register, int value);

    int get(RegisterNames register);

    int incrementAndGet(RegisterNames register);

    int decrementAndGet(RegisterNames register);

    boolean get(Flags flag);

    void set(Flags flags, boolean value);

}
