package com.zxrasp.emulator.core.z80.z80internals;

import com.zxrasp.emulator.core.EmulationException;
import com.zxrasp.emulator.core.SystemBusDevice;

import static com.zxrasp.emulator.core.z80.z80internals.RegisterNames.*;

public class OperationExecutor {

    public static final int OPERATION_DECODE_COST = 4;

    private static final int COMPARE_TIME = 4;
    private static final int JUMP_TIME = 5;

    private final Z80Context context;
    private final SystemBusDevice bus;

    public OperationExecutor(Z80Context context, SystemBusDevice bus) {
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
            byte offset = (byte) bus.readByteFromMemory(pc + 1);
            context.set(PC, pc + offset);
            return OPERATION_DECODE_COST + COMPARE_TIME + JUMP_TIME;
        }

        context.set(PC, pc + 2);
        return OPERATION_DECODE_COST + COMPARE_TIME;
    }

    public long jr() {
        int pc = context.get(PC);
        byte offset = (byte) bus.readByteFromMemory(pc);
        context.set(PC, pc + offset);
        return 12;
    }

    public long jr_cc(Conditions condition) {
        boolean cc = checkCondition(condition);
        int pc = context.get(PC);

        if (cc) {
            byte offset = (byte) bus.readByteFromMemory(pc + 1);
            context.set(PC, (pc + 2) + offset);
            return 12;
        }

        context.set(PC, pc + 2);
        return 7;
    }

    private boolean checkCondition(Conditions condition) {
        return context.get(condition.flagToCheck()) == condition.expectedValue();
    }

    public long ld_16(RegisterNames register) {
        int pc = context.get(PC);
        int word = bus.readWordFromMemory(pc + 1);
        context.set(register, word);
        context.set(PC, pc + 3);
        return 10;
    }

    public long add_to_hl(RegisterNames register) {
        int pc = context.get(PC);
        int hl = context.get(HL);
        int rvalue = context.get(register);
        context.set(HL, hl + rvalue);
        // todo: set flags
        context.set(PC, pc + 1);
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
        bus.writeWordToMemory(address, context.get(HL));
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
        context.set(HL, value);
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
        if (register == HL) { // special case
            int value = readByteFromMemoryHL();
            writeByteToMemoryHL(value + 1);
            context.incrementAndGet(PC);
            return 11;
        }

        context.incrementAndGet(register);
        context.incrementAndGet(PC);
        return 4;
    }

    public long dec8(RegisterNames register) {
        if (register == HL) { // special case
            int value = readByteFromMemoryHL();
            writeByteToMemoryHL(value - 1);
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

        if (register == HL) { // special case
            writeByteToMemoryHL(value);
            context.set(PC, pc + 2);
            return 10;
        } else {
            context.set(register, value);
            context.set(PC, pc + 2);
            return 7;
        }
    }

    public long rlca() {
        int a = context.get(A);
        int c = a & 0x80;
        a <<= 1;
        a |= c;
        context.set(A, a);

        // todo: set c to CARRY flag

        context.incrementAndGet(PC);
        return 4;
    }

    public long rrca() {
        // todo
        throw new EmulationException("Not implemented yet");
    }

    public long rla() {
        // todo
        throw new EmulationException("Not implemented yet");
    }

    public long rra() {
        // todo
        throw new EmulationException("Not implemented yet");
    }

    public long daa() {
        // todo
        throw new EmulationException("Not implemented yet");
    }

    public long cpl() {
        // todo
        throw new EmulationException("Not implemented yet");
    }

    public long scf() {
        // todo
        throw new EmulationException("Not implemented yet");
    }

    public long ccf() {
        // todo
        throw new EmulationException("Not implemented yet");
    }

    public long ld_r8_r8(RegisterNames dst, RegisterNames src) {
        if (dst == HL && src == HL) { // special case - HALT
            context.setHalt(true);
            return 4;
        }

        context.set(dst, context.get(src));
        context.incrementAndGet(PC);
        return 4;
    }

    public long performALUOperation(ALUOperations operation, RegisterNames register){
        int value;
        int result;

        if (register == HL) {
            value = bus.readByteFromMemory(context.get(HL));
            result = 7;
        } else {
            value = context.get(register);
            result = 4;
        }

        performALUOperation(operation, value);
        context.incrementAndGet(PC);

        return result;
    }

    public long performALUOperation(ALUOperations operation) {
        int pc = context.get(PC);
        int value = bus.readByteFromMemory(pc + 1);

        performALUOperation(operation, value);
        context.set(PC, pc + 2);

        return 7;
    }

    private void performALUOperation(ALUOperations operation, int value) {
        int acc = context.get(RegisterNames.A);
        int result;

        switch (operation) {
            case ADD_A:
                result = acc + value;
                setALUFlags(result);
                context.set(RegisterNames.A, result);
                break;
            case ADC_A:
                result = acc + value + (context.get(Flags.C) ? 1 : 0);
                setALUFlags(result);
                context.set(RegisterNames.A, result);
                break;
            case SUB:
                result = acc - value;
                setALUFlags(result);
                context.set(RegisterNames.A, result);
                break;
            case SUBC_A:
                result = acc - value - (context.get(Flags.C) ? 1 : 0);
                setALUFlags(result);
                context.set(RegisterNames.A, result);
                break;
            case AND:
                result = acc & value;
                setALUFlags(result);
                context.set(RegisterNames.A, result);
                break;
            case XOR:
                result = acc ^ value;
                setALUFlags(result);
                context.set(RegisterNames.A, result);
                break;
            case OR:
                result = acc | value;
                setALUFlags(result);
                context.set(RegisterNames.A, result);
                break;
            case CP:
                setALUFlags(acc - value);
        }
    }

    private void setALUFlags(int operationResult) {
        context.set(Flags.Z, operationResult == 0);

    }

    public long ret_cc(Conditions condition) {
        boolean cc = checkCondition(condition);

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
        context.swap(BC, BC_);
        context.swap(DE, DE_);
        context.swap(HL, HL_);
        context.incrementAndGet(PC);
        return 4;
    }

    public long jp_hl() {
        context.set(PC, context.get(HL));
        return 4;
    }

    public long ld_sp_hl() {
        context.set(SP, context.get(HL));
        context.incrementAndGet(PC);
        return 6;
    }

    public long jp_cc(Conditions condition) {
        boolean cc = checkCondition(condition);
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

    public long blockOperation(BlockOperations operation) {
        long result;

        switch (operation){
            case LDI:
                result = ldi();
                break;
            case LDIR:
                result = ldir();
                break;
            case LDD:
                result = ldd();
                break;
            case LDDR:
                result = lddr();
                break;
            default:
                throw new EmulationException("Not implemented yet");
        }

        context.incrementAndGet(PC);
        return result;
    }

    private long ldi() {
        int de = context.get(DE);
        int hl = context.get(HL);

        int val = bus.readByteFromMemory(hl);
        bus.writeByteToMemory(de, val);

        context.set(DE, de + 1);
        context.set(HL, hl + 1);
        context.decrementAndGet(BC);

        // todo: set flags
        return 16;
    }

    private long ldir() {
        int result = 5;
        do {
            result += ldi();
        } while (context.get(BC) != 0);

        return result;
    }

    private long ldd() {
        int hl = context.get(HL);
        int de = context.get(DE);

        int val = bus.readByteFromMemory(hl);
        bus.writeByteToMemory(de, val);

        context.set(HL, hl - 1);
        context.set(DE, de - 1);
        context.decrementAndGet(BC);
        return 16;
    }

    private long lddr() {
        int result = 5;
        do {
            result += ldd();
        } while (context.get(BC) != 0);

        return result;
    }

    public long out_n_a() {
        int pc = context.get(PC);
        int address = bus.readByteFromMemory(pc + 1);
        bus.writeByteToPort(address, context.get(A));
        context.set(PC, pc + 2);
        return 11;
    }

    public long ld_a_i() {
        context.set(A, context.get(I));
        context.incrementAndGet(PC);
        return 9;
    }

    public long ld_i_a() {
        context.set(I, context.get(A));
        context.incrementAndGet(PC);
        return 9;
    }

    public long di() {
        context.disableInterrupt();
        context.incrementAndGet(PC);
        return 4;
    }

    public long ei() {
        context.enableInterrupt();
        context.incrementAndGet(PC);
        return 4;
    }

    public long sbc_hl_rp(RegisterNames register) {
        int carry = context.get(Flags.C) ? 1 : 0;
        context.set(HL, context.get(HL) - context.get(register) - carry);
        return 15;
    }

    public long adc_hl_rp(RegisterNames register) {
        int carry = context.get(Flags.C) ? 1 : 0;
        context.set(HL, context.get(HL) + context.get(register) + carry);
        return 15;
    }

    public long ld_nn_rp(RegisterNames register) {
        int pc = context.get(PC);
        int address = bus.readWordFromMemory(pc + 1);
        bus.writeWordToMemory(address, context.get(register));
        context.set(PC, pc + 3);
        return 20;
    }

    public long ld_rp_nn(RegisterNames register) {
        int pc = context.get(PC);
        int address = bus.readWordFromMemory(pc + 1);
        int value = bus.readByteFromMemory(address);
        context.set(register, value);
        context.set(PC, pc + 3);
        return 20;
    }

    public long setIM(InterruptMode mode) {
        context.setIM(mode);
        return 8;
    }

    public long testBit(int bit, RegisterNames register) {
        throw new EmulationException("Not implemented yet");
    }

    public long resetBit(int bit, RegisterNames register) {
        throw new EmulationException("Not implemented yet");
    }

    public long setBit(int bit, RegisterNames register) {
        if (register == HL) { // special case
            int value = readByteFromMemoryHL();
            writeByteToMemoryHL(value | (1 << bit));
            return 15;
        }

        int value = context.get(register);
        value |= (1 << bit);
        context.set(register, value);
        return 8;
    }

    public long extendedSetBit(int bit, int offset) {
        int address = context.get(HL) + offset;
        int value = bus.readByteFromMemory(address);
        bus.writeByteToMemory(address, value | (1 << bit));
        return 23;
    }

    private int readByteFromMemoryHL() {
        byte offset = 0;
        int address = context.get(HL);

        switch (context.getHlRegisterMode()) {
            case IX:
            case IY:
                offset = (byte) bus.readByteFromMemory(context.incrementAndGet(PC));
                break;
        }

        return bus.readByteFromMemory(address + offset);
    }

    private void writeByteToMemoryHL(int value) {
        byte offset = 0;
        int address = context.get(HL);

        switch (context.getHlRegisterMode()) {
            case IX:
            case IY:
                offset = (byte) bus.readByteFromMemory(context.incrementAndGet(PC));
                break;
        }

        bus.writeByteToMemory(address + offset, value);
    }
}
