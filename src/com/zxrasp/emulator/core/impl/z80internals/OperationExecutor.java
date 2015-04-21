package com.zxrasp.emulator.core.impl.z80internals;

public class OperationExecutor {

    public static final int OPERATION_DECODE_COST = 4;

    private static final int COMPARE_TIME = 4;
    private static final int JUMP_TIME = 5;

    public static long nop(Context context) {
        context.incrementAndGet(RegisterNames.PC);
        return OPERATION_DECODE_COST;
    }

    public static long exx_af_af(Context context) {
        int tmp = context.get(RegisterNames.AF_);
        context.set(RegisterNames.AF_, context.get(RegisterNames.AF));
        context.set(RegisterNames.AF, tmp);
        context.incrementAndGet(RegisterNames.PC);
        return OPERATION_DECODE_COST;
    }

    public static long djnz(Context context) {
        int b = context.decrementAndGet(RegisterNames.B);
        int pc = context.get(RegisterNames.PC);

        if (b != 0) {
            int offset = context.getSystemBus().readByteFromMemory(pc + 1);
            context.set(RegisterNames.PC, pc + offset);
            return OPERATION_DECODE_COST + COMPARE_TIME + JUMP_TIME;
        }

        context.set(RegisterNames.PC, pc + 2);
        return OPERATION_DECODE_COST + COMPARE_TIME;
    }

    public static long jr(Context context) {
        int pc = context.get(RegisterNames.PC);
        int offset = context.getSystemBus().readByteFromMemory(pc);
        context.set(RegisterNames.PC, pc + offset);
        return OPERATION_DECODE_COST + JUMP_TIME + 3; // ??
    }

    public static long jr_cc(Context context, Flags flagToCheck) {
        boolean cc = context.get(flagToCheck);
        int pc = context.get(RegisterNames.PC);

        if (cc) {
            int offset = context.getSystemBus().readByteFromMemory(pc + 1);
            context.set(RegisterNames.PC, pc + offset);
            return 12;
        }

        context.set(RegisterNames.PC, pc + 2);
        return 7;
    }

    public static long ld_16(Context context, RegisterNames register) {
        int pc = context.get(RegisterNames.PC);
        int word = context.getSystemBus().readWordFromMemory(pc + 1);
        context.set(register, word);
        context.set(RegisterNames.PC, pc + 3);
        return 10;
    }

    public static long add_hl(Context context, RegisterNames register) {
        int pc = context.get(RegisterNames.PC);
        int rvalue = context.get(register);
        int mvalue = context.getSystemBus().readWordFromMemory(pc);
        context.set(register, rvalue + mvalue);
        // todo: set flags
        context.set(RegisterNames.PC, pc + 3);
        return 11;
    }

    public static long ld_m_bc(Context context) {
        int bc = context.get(RegisterNames.BC);
        int a = context.get(RegisterNames.A);
        context.getSystemBus().writeByteToMemory(bc, a);
        context.incrementAndGet(RegisterNames.PC);
        return 7;
    }

    public static long ld_m_de(Context context) {
        int de = context.get(RegisterNames.DE);
        int a = context.get(RegisterNames.A);
        context.getSystemBus().writeByteToMemory(de, a);
        context.incrementAndGet(RegisterNames.PC);
        return 7;
    }

    public static long ld_m_hl(Context context) {
        int pc = context.get(RegisterNames.PC);
        int address = context.getSystemBus().readWordFromMemory(pc + 1);
        context.getSystemBus().writeWordToMemory(address, context.get(RegisterNames.HL));
        context.set(RegisterNames.PC, pc + 3);
        return 20;
    }

    public static long ld_m_a(Context context) {
        int pc = context.get(RegisterNames.PC);
        int address = context.getSystemBus().readWordFromMemory(pc + 1);
        context.getSystemBus().writeByteToMemory(address, context.get(RegisterNames.A));
        context.set(RegisterNames.PC, pc + 3);
        return 13;
    }

    public static long ld_a_m_bc(Context context) {
        int bc = context.get(RegisterNames.BC);
        int value = context.getSystemBus().readByteFromMemory(bc);
        context.set(RegisterNames.A, value);
        context.incrementAndGet(RegisterNames.PC);
        return 7;
    }

    public static long ld_a_m_de(Context context) {
        int de = context.get(RegisterNames.DE);
        int value = context.getSystemBus().readByteFromMemory(de);
        context.set(RegisterNames.A, value);
        context.incrementAndGet(RegisterNames.PC);
        return 7;
    }

    public static long ld_hl_m(Context context) {
        int pc = context.get(RegisterNames.PC);
        int address = context.getSystemBus().readWordFromMemory(pc + 1);
        int value = context.getSystemBus().readWordFromMemory(address);
        context.set(RegisterNames.HL, value);
        context.set(RegisterNames.PC, pc + 3);
        return 20;
    }

    public static long ld_a_m(Context context) {
        int pc = context.get(RegisterNames.PC);
        int address = context.getSystemBus().readWordFromMemory(pc + 1);
        int value = context.getSystemBus().readByteFromMemory(address);
        context.set(RegisterNames.A, value);
        context.set(RegisterNames.PC, pc + 3);
        return 13;
    }

    public static long inc16(Context context, RegisterNames register) {
        context.incrementAndGet(register);
        context.incrementAndGet(RegisterNames.PC);
        return 6;
    }

    public static long dec16(Context context, RegisterNames register) {
        context.decrementAndGet(register);
        context.incrementAndGet(RegisterNames.PC);
        return 6;
    }

    public static long inc8(Context context, RegisterNames register) {
        if (register == RegisterNames.HL) { // special case
            int address = context.get(register);
            int value = context.getSystemBus().readByteFromMemory(address);
            context.getSystemBus().writeByteToMemory(address, value + 1);
            context.incrementAndGet(RegisterNames.PC);
            return 11;
        }

        context.incrementAndGet(register);
        context.incrementAndGet(RegisterNames.PC);
        return 4;
    }

    public static long dec8(Context context, RegisterNames register) {
        if (register == RegisterNames.HL) { // special case
            int address = context.get(register);
            int value = context.getSystemBus().readByteFromMemory(address);
            context.getSystemBus().writeByteToMemory(address, value - 1);
            context.incrementAndGet(RegisterNames.PC);
            return 11;
        }

        context.decrementAndGet(register);
        context.incrementAndGet(RegisterNames.PC);
        return 4;
    }

    public static long ld_8(Context context, RegisterNames register) {
        int pc = context.get(RegisterNames.PC);
        int value = context.getSystemBus().readByteFromMemory(pc + 1);
        context.set(register, value);
        context.set(RegisterNames.PC, pc + 2);
        return 7;
    }

    public static long rlca(Context context) {
        // todo
        return 4;
    }

    public static long rrca(Context context) {
        // todo
        return 0;
    }

    public static long rla(Context context) {
        // todo
        return 0;
    }

    public static long rra(Context context) {
        // todo
        return 0;
    }

    public static long daa(Context context) {
        // todo
        return 0;
    }

    public static long cpl(Context context) {
        // todo
        return 0;
    }

    public static long scf(Context context) {
        // todo
        return 0;
    }

    public static long ccf(Context context) {
        // todo
        return 0;
    }

    public static long ld_r8_r8(Context context, RegisterNames dst, RegisterNames src) {
        if (dst == RegisterNames.HL && src == RegisterNames.HL) { // special case - HALT
            context.setHalt(true);
            return 4;
        }

        context.set(dst, context.get(src));
        context.incrementAndGet(RegisterNames.PC);
        return 4;
    }

    public static long performALUOperation(Context context, ALUOperations operation, RegisterNames register) {
        int value;
        int result;
        if (register == RegisterNames.HL) {
            value = context.getSystemBus().readByteFromMemory(context.get(RegisterNames.HL));
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

        context.incrementAndGet(RegisterNames.PC);
        return result;
    }

    public static long ret_cc(Context context, Flags flagToCheck) {
        boolean cc = context.get(flagToCheck);

        if (cc) {
            int sp = context.get(RegisterNames.SP);
            int jumpAddress = context.getSystemBus().readWordFromMemory(sp);
            context.set(RegisterNames.PC, jumpAddress);
            context.set(RegisterNames.SP, sp + 2);
            return 11;
        }

        context.incrementAndGet(RegisterNames.PC);
        return 5;
    }

    public static long pop(Context context, RegisterNames register) {
        int sp = context.get(RegisterNames.SP);
        context.set(register, context.getSystemBus().readWordFromMemory(sp));
        context.set(RegisterNames.SP, sp + 2);
        context.incrementAndGet(RegisterNames.PC);
        return 10;
    }

    public static long ret(Context context) {
        int sp = context.get(RegisterNames.SP);
        int jumpAddress = context.getSystemBus().readWordFromMemory(sp);
        context.set(RegisterNames.PC, jumpAddress);
        context.set(RegisterNames.SP, sp + 2);
        return 10;
    }
}
