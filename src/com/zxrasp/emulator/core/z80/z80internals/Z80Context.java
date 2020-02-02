package com.zxrasp.emulator.core.z80.z80internals;

import com.zxrasp.emulator.core.EmulationException;

public class Z80Context implements Context {

    private int af;
    private int bc;
    private int de;
    private int hl;
    private int pc;
    private int sp;
    private int ix;
    private int iy;

    private boolean halted;

    public Z80Context() {
       reset();
    }

    @Override
    public void reset() {
        af = bc = de = hl = 0;
        pc = sp = ix = iy = 0;
        halted = false;
    }

    @Override
    public boolean isHalted() {
        return halted;
    }

    @Override
    public void setHalt(boolean value) {
        halted = value;
    }

    @Override
    public void set(RegisterNames register, int value) {
        switch (register) {
            case A:
                af = setHi8bit(af, value);
                break;
            case F:
                af = setLo8bit(af, value);
                break;
            case B:
                bc = setHi8bit(bc, value);
                break;
            case C:
                bc = setLo8bit(bc, value);
                break;
            case D:
                de = setHi8bit(de, value);
                break;
            case E:
                de = setLo8bit(de, value);
                break;
            case H:
                hl = setHi8bit(hl, value);
                break;
            case L:
                hl = setLo8bit(hl, value);
            case AF:
                af = getLo16bit(value);
                break;
            case BC:
                bc = getLo16bit(value);
                break;
            case DE:
                de = getLo16bit(value);
                break;
            case HL:
                hl = getLo16bit(value);
                break;
            case PC:
                pc = getLo16bit(value);
                break;
            case SP:
                sp = getLo16bit(value);
                break;
            case IX:
                ix = getLo16bit(value);
                break;
            case IY:
                iy = getLo16bit(value);
                break;
        }
    }

    private int setLo8bit(int oldValue, int newValue) {
        return (getLo8bit(newValue)) | (oldValue & 0xFF00);
    }

    private int setHi8bit(int oldValue, int newValue) {
        return (getLo8bit(newValue) << 8) | (getLo8bit(oldValue));
    }

    @Override
    public int get(RegisterNames register) {
        switch (register) {
            case A:
                return getHi8bit(af);
            case F:
                return getLo8bit(af);
            case B:
                return  getHi8bit(bc);
            case C:
                return getLo8bit(bc);
            case D:
                return getHi8bit(de);
            case E:
                return getLo8bit(de);
            case H:
                return getHi8bit(hl);
            case L:
                return getLo8bit(hl);
            case AF:
                return af;
            case BC:
                return bc;
            case DE:
                return de;
            case HL:
                return hl;
            case PC:
                return pc;
            case SP:
                return sp;
            case IX:
                return ix;
            case IY:
                return iy;
            default:
                throw new EmulationException("Unknown register: " + register);
        }
    }

    @Override
    public int incrementAndGet(RegisterNames register) {
        set(register, get(register) + 1);
        return get(register);
    }

    @Override
    public int decrementAndGet(RegisterNames register) {
        set(register, get(register) - 1);
        return get(register);
    }

    @Override
    public boolean get(Flags flag) {
        return (get(RegisterNames.F) & flag.getMask()) == 1;
    }

    @Override
    public void set(Flags flag, boolean value) {
        if (value)
            set(RegisterNames.F, get(RegisterNames.F) | flag.getMask());
        else
            set(RegisterNames.F, get(RegisterNames.F) & ~flag.getMask());
    }

    private int getLo16bit(int value) {
        return value & 0xFFFF;
    }

    private int getHi8bit(int value) {
        return (value >> 8) & 0xFF;
    }

    private int getLo8bit(int value) {
        return value & 0xFF;
    }

    @Override
    public String toString() {
        return String.format("[AF = %X | BC = %X | DE = %X | HL = %X | PC = %X | SP = %X | IX = %X | IY = %X]",
                af, bc, de, hl, pc, sp, ix, iy);
    }
}
