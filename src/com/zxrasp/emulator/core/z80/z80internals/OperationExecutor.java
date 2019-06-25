package com.zxrasp.emulator.core.z80.z80internals;

import com.zxrasp.emulator.core.SystemBus;

import static com.zxrasp.emulator.core.z80.z80internals.RegisterNames.*;

public class OperationExecutor {

    public static final int OPERATION_DECODE_COST = 4;

    private static final int COMPARE_TIME = 4;
    private static final int JUMP_TIME = 5;

    private final Context context;
    private final SystemBus bus;

    public OperationExecutor(Context context, SystemBus bus) {
        this.context = context;
        this.bus = bus;
    }

    public long nop() {
        context.incrementAndGet(PC);
        return OPERATION_DECODE_COST;
    }

    public long exx_af_af() {
        int tmp = context.get(RegisterNames.AF_);
        context.set(RegisterNames.AF_, context.get(RegisterNames.AF));
        context.set(RegisterNames.AF, tmp);
        context.incrementAndGet(PC);
        return OPERATION_DECODE_COST;
    }

    public long djnz() {
        int b = context.decrementAndGet(RegisterNames.B);
        int pc = context.get(PC);

        if (b != 0) {
            int offset = bus.readByteFromMemory(pc + 1);
            context.set(PC, pc + offset);
            return OPERATION_DECODE_COST + COMPARE_TIME + JUMP_TIME;
        }

        context.set(PC, pc + 2);
        return OPERATION_DECODE_COST + COMPARE_TIME;
    }

    public long jr() {
        int pc = context.get(PC);
        int offset = bus.readByteFromMemory(pc);
        context.set(PC, pc + offset);
        return OPERATION_DECODE_COST + JUMP_TIME + 3; // ??
    }

    public long jr_cc(Flags flagToCheck) {
        boolean cc = context.get(flagToCheck);
        int pc = context.get(PC);

        if (cc) {
            int offset = bus.readByteFromMemory(pc + 1);
            context.set(PC, pc + offset);
            return 12;
        }

        context.set(PC, pc + 2);
        return 7;
    }

    public long ld_16(RegisterNames register) {
        int pc = context.get(PC);
        int word = bus.readWordFromMemory(pc + 1);
        context.set(register, word);
        context.set(PC, pc + 3);
        return 10;
    }

    public long add_hl(RegisterNames register) {
        int pc = context.get(PC);
        int rvalue = context.get(register);
        int mvalue = bus.readWordFromMemory(pc);
        context.set(register, rvalue + mvalue);
        // todo: set flags
        context.set(PC, pc + 3);
        return 11;
    }

    public long ld_m_bc() {
        int bc = context.get(RegisterNames.BC);
        int a = context.get(RegisterNames.A);
        bus.writeByteToMemory(bc, a);
        context.incrementAndGet(PC);
        return 7;
    }

    public long ld_m_de() {
        int de = context.get(RegisterNames.DE);
        int a = context.get(RegisterNames.A);
        bus.writeByteToMemory(de, a);
        context.incrementAndGet(PC);
        return 7;
    }

    public long ld_m_hl() {
        int pc = context.get(PC);
        int address = bus.readWordFromMemory(pc + 1);
        bus.writeWordToMemory(address, context.get(RegisterNames.HL));
        context.set(PC, pc + 3);
        return 20;
    }

    public long ld_m_a() {
        int pc = context.get(PC);
        int address = bus.readWordFromMemory(pc + 1);
        bus.writeByteToMemory(address, context.get(RegisterNames.A));
        context.set(PC, pc + 3);
        return 13;
    }

    public long ld_a_m_bc() {
        int bc = context.get(RegisterNames.BC);
        int value = bus.readByteFromMemory(bc);
        context.set(RegisterNames.A, value);
        context.incrementAndGet(PC);
        return 7;
    }

    public long ld_a_m_de() {
        int de = context.get(RegisterNames.DE);
        int value = bus.readByteFromMemory(de);
        context.set(RegisterNames.A, value);
        context.incrementAndGet(PC);
        return 7;
    }

    public long ld_hl_m() {
        int pc = context.get(PC);
        int address = bus.readWordFromMemory(pc + 1);
        int value = bus.readWordFromMemory(address);
        context.set(RegisterNames.HL, value);
        context.set(PC, pc + 3);
        return 20;
    }

    public long ld_a_m() {
        int pc = context.get(PC);
        int address = bus.readWordFromMemory(pc + 1);
        int value = bus.readByteFromMemory(address);
        context.set(RegisterNames.A, value);
        context.set(PC, pc + 3);
        return 13;
    }

    public long inc16(RegisterNames register) {
        context.incrementAndGet(register);
        context.incrementAndGet(PC);
        return 6;
    }

    public long dec16(RegisterNames register) {
        context.decrementAndGet(register);
        context.incrementAndGet(PC);
        return 6;
    }

    public long inc8(RegisterNames register) {
        if (register == RegisterNames.HL) { // special case
            int address = context.get(register);
            int value = bus.readByteFromMemory(address);
            bus.writeByteToMemory(address, value + 1);
            context.incrementAndGet(PC);
            return 11;
        }

        context.incrementAndGet(register);
        context.incrementAndGet(PC);
        return 4;
    }

    public long dec8(RegisterNames register) {
        if (register == RegisterNames.HL) { // special case
            int address = context.get(register);
            int value = bus.readByteFromMemory(address);
            bus.writeByteToMemory(address, value - 1);
            context.incrementAndGet(PC);
            return 11;
        }

        context.decrementAndGet(register);
        context.incrementAndGet(PC);
        return 4;
    }

    public long ld_8(RegisterNames register) {
        int pc = context.get(PC);
        int value = bus.readByteFromMemory(pc + 1);
        context.set(register, value);
        context.set(PC, pc + 2);
        return 7;
    }

    public long rlca() {
        // todo
        return 4;
    }

    public long rrca() {
        // todo
        return 0;
    }

    public long rla() {
        // todo
        return 0;
    }

    public long rra() {
        // todo
        return 0;
    }

    public long daa() {
        // todo
        return 0;
    }

    public static long cpl() {
        // todo
        return 0;
    }

    public long scf() {
        // todo
        return 0;
    }

    public long ccf() {
        // todo
        return 0;
    }

    public long ld_r8_r8(RegisterNames dst, RegisterNames src) {
        if (dst == RegisterNames.HL && src == RegisterNames.HL) { // special case - HALT
            context.setHalt(true);
            return 4;
        }

        context.set(dst, context.get(src));
        context.incrementAndGet(PC);
        return 4;
    }

    public long performALUOperation(ALUOperations operation, RegisterNames register) {
        int value;
        int result;
        if (register == RegisterNames.HL) {
            value = bus.readByteFromMemory(context.get(RegisterNames.HL));
            result = 7;
        } else {
            value = context.get(register);
            result = 4;
        }

        int acc = context.get(RegisterNames.A);

        switch (operation) {
            case ADD_A:
                context.set(RegisterNames.A, acc + value);
                break;
            case ADC_A:
                context.set(RegisterNames.A, acc + value + (context.get(Flags.C) ? 1 : 0));
                break;
            case SUB:
                context.set(RegisterNames.A, acc - value);
                break;
            case SUBC_A:
                context.set(RegisterNames.A, acc - value - (context.get(Flags.C) ? 1 : 0));
                break;
            case AND:
                context.set(RegisterNames.A, acc & value);
                break;
            case XOR:
                context.set(RegisterNames.A, acc ^ value);
                break;
            case OR:
                context.set(RegisterNames.A, acc | value);
                break;
            case CP:
                // todo
                break;
        }

        context.incrementAndGet(PC);
        return result;
    }

    public long ret_cc(Flags flagToCheck) {
        boolean cc = context.get(flagToCheck);

        if (cc) {
            return ret() + 1;
        }

        context.incrementAndGet(PC);
        return 5;
    }

    public long push(RegisterNames register) {
        int sp = context.get(SP) - 2;
        bus.writeWordToMemory(sp, context.get(register));
        context.set(SP, sp);
        context.incrementAndGet(PC);
        return 11;
    }

    public long pop(RegisterNames register) {
        int sp = context.get(SP);
        context.set(register, bus.readWordFromMemory(sp));
        context.set(SP, sp + 2);
        context.incrementAndGet(PC);
        return 10;
    }

    public long call_nn() {
        int pc = context.get(PC);
        int address = bus.readWordFromMemory(pc + 1);
        int sp = context.get(SP) - 2;
        bus.writeWordToMemory(sp, pc + 3);
        context.set(SP, sp);
        context.set(PC, address);
        return 17;
    }

    public long ret() {
        int sp = context.get(SP);
        int jumpAddress = bus.readWordFromMemory(sp);
        context.set(PC, jumpAddress);
        context.set(SP, sp + 2);
        return 10;
    }

    public long exx() {
        // todo
        context.incrementAndGet(PC);
        return 4;
    }

    public long jp_hl() {
        context.set(PC, context.get(RegisterNames.HL));
        return 4;
    }

    public long ld_sp_hl() {
        context.set(SP, context.get(RegisterNames.HL));
        context.incrementAndGet(PC);
        return 6;
    }

    public long jp_cc(Flags flags) {
        boolean cc = context.get(flags);
        int pc = context.get(PC);

        if (cc) {
            int jumpAddress = bus.readWordFromMemory(pc + 1);
            context.set(PC, jumpAddress);
            return 10;
        }

        context.set(PC, pc + 3);
        return 10;
    }

    public long rst(int n) {
        bus.writeWordToMemory(context.decrementAndGet(SP), context.get(PC) + 1);
        context.decrementAndGet(SP);
        context.set(PC, n);
        return 11;
    }

    public long jp_nn() {
        context.set(PC, bus.readWordFromMemory(context.get(PC) + 1));
        return 10;
    }

    public long ex_de_hl() {
        int de = context.get(DE);
        int hl = context.get(HL);
        context.set(DE, hl);
        context.set(HL, de);
        context.incrementAndGet(PC);
        return 4;
    }
}
