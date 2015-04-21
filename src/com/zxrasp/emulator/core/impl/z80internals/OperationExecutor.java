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

    public static long ld_16(Context context, RegisterNames registerName) {
        int pc = context.get(RegisterNames.PC);
        int word = context.getSystemBus().readWordFromMemory(pc + 1);
        context.set(registerName, word);
        context.set(RegisterNames.PC, pc + 3);
        return 10;
    }

    public static long add_hl(Context context, RegisterNames registerName) {
        int pc = context.get(RegisterNames.PC);
        int rvalue = context.get(registerName);
        int mvalue = context.getSystemBus().readWordFromMemory(pc);
        context.set(registerName, rvalue + mvalue);
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

    public static long inc16(Context context, RegisterNames registerName) {
        context.incrementAndGet(registerName);
        context.incrementAndGet(RegisterNames.PC);
        return 6;
    }

    public static long dec16(Context context, RegisterNames registerName) {
        context.decrementAndGet(registerName);
        context.incrementAndGet(RegisterNames.PC);
        return 6;
    }

    public static long inc8(Context context, RegisterNames registerName) {
        if (registerName == RegisterNames.HL) { // special case
            int address = context.get(registerName);
            int value = context.getSystemBus().readByteFromMemory(address);
            context.getSystemBus().writeByteToMemory(address, value + 1);
            context.incrementAndGet(RegisterNames.PC);
            return 11;
        }

        context.incrementAndGet(registerName);
        context.incrementAndGet(RegisterNames.PC);
        return 4;
    }

    public static long dec8(Context context, RegisterNames registerName) {
        if (registerName == RegisterNames.HL) { // special case
            int address = context.get(registerName);
            int value = context.getSystemBus().readByteFromMemory(address);
            context.getSystemBus().writeByteToMemory(address, value - 1);
            context.incrementAndGet(RegisterNames.PC);
            return 11;
        }

        context.decrementAndGet(registerName);
        context.incrementAndGet(RegisterNames.PC);
        return 4;
    }
}
