package com.zxrasp.emulator.core.z80.z80internals;

import com.zxrasp.emulator.core.EmulationException;

import static com.zxrasp.emulator.core.z80.z80internals.InterruptMode.IM_0;

public class Z80Context implements Context {

    private int af;
    private int bc;
    private int de;
    private int hl;
    private int af_;
    private int bc_;
    private int de_;
    private int hl_;
    private int pc;
    private int sp;
    private int ix;
    private int iy;
    private int ir;
    private boolean iff1, iff2;
    private InterruptMode interruptMode;
    private boolean halted;
    private RegisterSpecial currentAddressRegister;

    public Z80Context() {
       reset();
    }

    @Override
    public void reset() {
        af = bc = de = hl = 0;
        af_ = bc_ = de_ = hl_ = 0;
        pc = sp = ix = iy = 0;
        ir = 0;
        iff1 = iff2 = false;
        halted = false;
        interruptMode = IM_0;
        currentAddressRegister = null;
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
    public void set(Register8 register, int value) {
        switch (register) {
            case A:
                af = setHi8bit(af, value);
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
                break;
            default:
                throw new EmulationException("Unknown register: " + register);
        }
    }

    @Override
    public void set(Register16 register, int value) {
        switch (register) {
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
            case SP:
                sp = getLo16bit(value);
                break;
            default:
                throw new EmulationException("Unknown register: " + register);
        }
    }

    @Override
    public int get(Register16 register) {
        switch (register) {
            case AF:
                return getLo16bit(af);
            case BC:
                return getLo16bit(bc);
            case DE:
                return getLo16bit(de);
            case HL:
                return getLo16bit(hl);
            case SP:
                return getLo16bit(sp);
            default:
                throw new EmulationException("Unknown register: " + register);
        }
    }

    @Override
    public void set(RegisterSpecial register, int value) {
        switch (register) {
            case PC:
                pc = getLo16bit(value);
                break;
            case IX:
                ix = getLo16bit(value);
                break;
            case IY:
                iy = getLo16bit(value);
            case I:
                ir = setHi8bit(ir, getLo8bit(value));
                break;
            default:
                throw new EmulationException("Unknown register: " + register);
        }
    }

    @Override
    public int get(RegisterSpecial register) {
        switch (register) {
            case PC:
                return getLo16bit(pc);
            case IX:
                return getLo16bit(ix);
            case IY:
                return getLo16bit(iy);
            case I:
                return getHi8bit(ir);
        }
        throw new EmulationException("Unknown register: " + register);
    }

    @Override
    public int get(Register8 register) {
        switch (register) {
            case A:
                return getHi8bit(af);
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
            default:
                throw new EmulationException("Unknown register: " + register);
        }
    }

    @Override
    public void enableInterrupt() {
        iff1 = iff2 = true;
    }

    @Override
    public void disableInterrupt() {
        iff1 = iff2 = false;
    }

    @Override
    public void swap(Register16 register) {
        int tmp = get(register);
        set(register, getAlternativeRegister(register));
        setAlternativeRegister(register, tmp);
    }

    @Override
    public void setIM(InterruptMode mode) {
        this.interruptMode = mode;
    }

    @Override
    public int decrementAndGet(Register8 register) {
        set(register, get(register) - 1);
        return get(register);
    }

    @Override
    public int decrementAndGet(Register16 register) {
        set(register, get(register) - 1);
        return get(register);
    }

    @Override
    public int decrementAndGet(RegisterSpecial register) {
        set(register, get(register) - 1);
        return get(register);
    }

    @Override
    public boolean get(Flags flag) {
        return (getLo8bit(af) & flag.getMask()) != 0;
    }

    @Override
    public void set(Flags flag, boolean value) {
        int oldValue = af;

        if (value)
            af = setLo8bit(oldValue, oldValue | flag.getMask());
        else
            af = setLo8bit(oldValue, oldValue & ~flag.getMask());
    }

    @Override
    public void incrementR() {
        int r = getLo8bit(ir);
        ir = setLo8bit(ir,r + 1);
    }

    @Override
    public int getAndIncrement(RegisterSpecial register) {
        int result = get(register);
        set(register, result + 1);
        return result;
    }

    public RegisterSpecial getCurrentAddressRegister() {
        return currentAddressRegister;
    }

    public void setCurrentAddressRegister(RegisterSpecial currentAddressRegister) {
        this.currentAddressRegister = currentAddressRegister;
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

    private int setLo8bit(int oldValue, int newValue) {
        return (getLo8bit(newValue)) | (oldValue & 0xFF00);
    }

    private int setHi8bit(int oldValue, int newValue) {
        return (getLo8bit(newValue) << 8) | (getLo8bit(oldValue));
    }

    private int getAlternativeRegister(Register16 register) {
        switch (register) {
            case AF:
                return getLo16bit(af_);
            case BC:
                return getLo16bit(bc_);
            case DE:
                return getLo16bit(de_);
            case HL:
                return getLo16bit(hl_);
            default:
                throw new EmulationException("Unknown register: " + register);
        }
    }

    private void setAlternativeRegister(Register16 register, int value) {
        switch (register) {
            case AF:
                af_ = getLo16bit(value);
                break;
            case BC:
                bc_ = getLo16bit(value);
                break;
            case DE:
                de_ = getLo16bit(value);
                break;
            case HL:
                hl_ = getLo16bit(value);
                break;
            default:
                throw new EmulationException("Unknown register: " + register);
        }
    }

    @Override
    public String toString() {
        return String.format("[AF = %04X | BC = %04X | DE = %04X | HL = %04X | PC = %04X | SP = %04X | IX = %04X | IY = %04X | IR = %04X]\n%s",
                af, bc, de, hl, pc, sp, ix, iy, ir, debugFlags());
    }

    private String debugFlags() {
        return String.format("[S = %b | Z = %b | H = %b | PV = %b | N = %b | C = %b]",
                get(Flags.S), get(Flags.Z), get(Flags.H), get(Flags.PV), get(Flags.N), get(Flags.C));
    }
}
