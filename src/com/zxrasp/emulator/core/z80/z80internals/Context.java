package com.zxrasp.emulator.core.z80.z80internals;

public interface Context {

    void reset();

    boolean isHalted();

    void setHalt(boolean value);

    void set(Register8 register, int value);

    int get(Register8 register);

    void set(Register16 register, int value);

    int get(Register16 register);

    void set(RegisterSpecial register, int value);

    int get(RegisterSpecial register);

    void enableInterrupt();

    void disableInterrupt();

    void swap(Register16 register);

    void setIM(InterruptMode mode);

  //  int incrementAndGet(RegisterSpecial register);

    int decrementAndGet(Register8 register);

    int decrementAndGet(Register16 register);

    int decrementAndGet(RegisterSpecial register);

    boolean get(Flags flag);

    void set(Flags flags, boolean value);

    void incrementR();

    int getAndIncrement(RegisterSpecial register);

    RegisterSpecial getCurrentAddressRegister();

    void setCurrentAddressRegister(RegisterSpecial currentAddressRegister);
}
