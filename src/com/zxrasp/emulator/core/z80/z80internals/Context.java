package com.zxrasp.emulator.core.z80.z80internals;

public interface Context {

    void reset();

    boolean isHalted();

    void setHalt(boolean value);

    void set(RegisterNames register, int value);

    int get(RegisterNames register);

    int incrementAndGet(RegisterNames register);

    int decrementAndGet(RegisterNames register);

    boolean get(Flags flag);

    void set(Flags flags, boolean value);

}
