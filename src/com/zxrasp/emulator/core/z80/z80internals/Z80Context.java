package com.zxrasp.emulator.core.z80.z80internals;

import com.zxrasp.emulator.core.EmulationException;

import static com.zxrasp.emulator.core.z80.z80internals.InterruptMode.IM_0;

public class Z80Context implements Context {

    public enum HLRegisterMode {
        HL, IX, IY
    }

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

    private HLRegisterMode hlRegisterMode;
    private boolean halted;

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
        hlRegisterMode = HLRegisterMode.HL;
        interruptMode = IM_0;
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
                break;
            case I:
                ir = setHi8bit(ir ,value);
                break;
            case R:
                ir = setLo8bit(ir, value);
                break;
            case AF:
                af = getLo16bit(value);
                break;
            case AF_:
                af_ = getLo16bit(value);
                break;
            case BC:
                bc = getLo16bit(value);
                break;
            case BC_:
                bc_ = getLo16bit(value);
                break;
            case DE:
                de = getLo16bit(value);
                break;
            case DE_:
                de_ = getLo16bit(value);
                break;
            case HL:
                setHL(value);
                break;
            case HL_:
                hl_ = getLo16bit(value);
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
            default:
                throw new EmulationException("Unknown register: " + register);
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
                return getHi8bit(getHL());
            case L:
                return getLo8bit(getHL());
            case I:
                return getHi8bit(ir);
            case R:
                return getLo8bit(ir);
            case AF:
                return af;
            case AF_:
                return af_;
            case BC:
                return bc;
            case BC_:
                return bc_;
            case DE:
                return de;
            case DE_:
                return de_;
            case HL:
                return getHL();
            case HL_:
                return hl_;
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

    private int getHL() {
       switch (hlRegisterMode) {
           case HL:
               return hl;
           case IX:
               return ix;
           case IY:
               return iy;
           default:
               throw new Z80EmulationException();
       }
    }

    private void setHL(int value) {
        switch (hlRegisterMode) {
            case HL:
                hl = getLo16bit(value);
                break;
            case IX:
                ix = getLo16bit(value);
                break;
            case IY:
                iy = getLo16bit(value);
                break;
            default:
                throw new Z80EmulationException();
        }
    }

    public HLRegisterMode getHlRegisterMode() {
        return hlRegisterMode;
    }

    public void enableInterrupt() {
        iff1 = iff2 = true;
    }

    void disableInterrupt() {
        iff1 = iff2 = false;
    }

    public void swap(RegisterNames r1, RegisterNames r2) {
        int tmp = get(r1);
        set(r1, get(r2));
        set(r2, tmp);
    }

    public void setIM(InterruptMode mode) {
        this.interruptMode = mode;
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
        return (get(RegisterNames.F) & flag.getMask()) != 0;
    }

    @Override
    public void set(Flags flag, boolean value) {
        if (value)
            set(RegisterNames.F, get(RegisterNames.F) | flag.getMask());
        else
            set(RegisterNames.F, get(RegisterNames.F) & ~flag.getMask());
    }

    public void setHLRegisterMode(HLRegisterMode hlRegisterMode) {
        this.hlRegisterMode = hlRegisterMode;
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
        return String.format("[AF = %04X | BC = %04X | DE = %04X | HL = %04X | PC = %04X | SP = %04X | IX = %04X | IY = %04X | IR = %04X]\n%s",
                af, bc, de, hl, pc, sp, ix, iy, ir, debugFlags());
    }

    private String debugFlags() {
        return String.format("[S = %b | Z = %b | H = %b | PV = %b | N = %b | C = %b]",
                get(Flags.S), get(Flags.Z), get(Flags.H), get(Flags.PV), get(Flags.N), get(Flags.C));
    }
}
