package com.zxrasp.emulator.core.z80.z80internals;

import com.zxrasp.emulator.core.EmulationException;
import com.zxrasp.emulator.core.SystemBusDevice;

import static com.zxrasp.emulator.core.z80.z80internals.Register16.*;
import static com.zxrasp.emulator.core.z80.z80internals.Register8.*;
import static com.zxrasp.emulator.core.z80.z80internals.RegisterSpecial.I;
import static com.zxrasp.emulator.core.z80.z80internals.RegisterSpecial.PC;

public class OperationExecutor {

    private final Z80Context context;
    private final SystemBusDevice bus;

    public OperationExecutor(Z80Context context, SystemBusDevice bus) {
        this.context = context;
        this.bus = bus;
    }

    public long nop() {
        // nothing to do
        return 4;
    }

    public long exx_af_af() {
        context.swap(AF);
        return 4;
    }

    public long djnz() {
        int b = context.decrementAndGet(B);
        int pc = context.get(PC);

        if (b != 0) {
            byte offset = (byte) bus.readByteFromMemory(pc);
            context.set(PC, pc + 1 + offset);
            return 13;
        }

        context.set(PC, pc + 1);
        return 8;
    }

    public long jr() {
        int pc = context.get(PC);
        byte offset = (byte) bus.readByteFromMemory(pc);
        context.set(PC, pc + 1 + offset);
        return 12;
    }

    public long jr_cc(Conditions condition) {
        boolean cc = checkCondition(condition);
        int pc = context.get(PC);

        if (cc) {
            byte offset = (byte) bus.readByteFromMemory(pc);
            context.set(PC, pc + 1 + offset);
            return 12;
        }

        context.set(PC, pc + 1);
        return 7;
    }

    private boolean checkCondition(Conditions condition) {
        return context.get(condition.flagToCheck()) == condition.expectedValue();
    }

    public long ld_16(Register16 register) {
        int pc = context.get(PC);
        int word = bus.readWordFromMemory(pc);
        RegisterSpecial addressRegister = context.getCurrentAddressRegister();
        context.set(PC, pc + 2);

        if (register == HL && addressRegister != null) {
            context.set(addressRegister, word);
            return 14;
        } else {
            context.set(register, word);
            return 10;
        }
    }

    public long add_to_hl(Register16 register) {
        int hl = context.get(HL);
        int rvalue = context.get(register);
        context.set(HL, hl + rvalue);
        // todo: set flags
        return 11;
    }

    public long ld_m_bc() {
        int bc = context.get(BC);
        int a = context.get(A);
        bus.writeByteToMemory(bc, a);
        return 7;
    }

    public long ld_m_de() {
        int de = context.get(DE);
        int a = context.get(A);
        bus.writeByteToMemory(de, a);
        return 7;
    }

    public long ld_m_hl() {
        int pc = context.get(PC);
        int address = bus.readWordFromMemory(pc);
        bus.writeWordToMemory(address, context.get(HL));
        context.set(PC, pc + 2);
        return 20;
    }

    public long ld_m_a() {
        int pc = context.get(PC);
        int address = bus.readWordFromMemory(pc);
        bus.writeByteToMemory(address, context.get(A));
        context.set(PC, pc + 2);
        return 13;
    }

    public long ld_a_m_bc() {
        int bc = context.get(BC);
        int value = bus.readByteFromMemory(bc);
        context.set(A, value);
        return 7;
    }

    public long ld_a_m_de() {
        int de = context.get(DE);
        int value = bus.readByteFromMemory(de);
        context.set(A, value);
        return 7;
    }

    public long ld_hl_m() {
        int pc = context.get(PC);
        int address = bus.readWordFromMemory(pc);
        int value = bus.readWordFromMemory(address);
        RegisterSpecial dest = context.getCurrentAddressRegister();

        if (dest == null) {
            context.set(HL, value);
        } else {
            context.set(dest, value);
        }

        context.set(PC, pc + 2);
        return 20;
    }

    public long ld_a_m() {
        int pc = context.get(PC);
        int address = bus.readWordFromMemory(pc);
        int value = bus.readByteFromMemory(address);
        context.set(A, value);
        context.set(PC, pc + 2);
        return 13;
    }

    public long inc16(Register16 register) {
        context.set(register, context.get(register) + 1);
        return 6;
    }

    public long dec16(Register16 register) {
        context.set(register, context.get(register) - 1);
        return 6;
    }

    public long inc8(Register8 register) {
        byte value = (byte) context.get(register);
        setALUFlags(++value);
        context.set(register, value);
        return 4;
    }

    public long inc8_hl() {
        byte value = (byte) readByteFromMemoryHL();
        setALUFlags(++value);
        writeByteToMemoryHL(value);
        return 11;
    }

    public long dec8(Register8 register) {
        byte value = (byte) context.get(register);
        setALUFlags(--value);
        context.set(register, value);
        return 4;
    }

    public long dec8_hl() {
        byte value = (byte) readByteFromMemoryHL();
        setALUFlags(--value);
        writeByteToMemoryHL(value);
        return 11;
    }

    public long ld_8(Register8 register) {
        int pc = context.get(PC);
        int value = bus.readByteFromMemory(pc);
        context.set(register, value);
        context.set(PC, pc + 1);
        return 7;
    }

    public long ld_8_hl() {
        int pc = context.get(PC);
        int value = bus.readByteFromMemory(pc);
        writeByteToMemoryHL(value);
        context.set(PC, pc + 1);
        return 10;
    }

    public long rlca() {
        int a = context.get(A);
        int c = a & 0x80;
        a <<= 1;
        a |= c;
        context.set(A, a);
        // todo: set c to CARRY flag
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

    public long halt() {
        context.setHalt(true);
        return 4;
    }

    public long ld_r8_r8(Register8 dst, Register8 src) {
        context.set(dst, context.get(src));
        return 4;
    }

    public long performALUOperation(ALUOperations operation, Register8 register){
        byte value = (byte) context.get(register);
        performALUOperation(operation, value);
        return 4;
    }

    public long performALUOperation(ALUOperations operation) {
        int pc = context.get(PC);
        byte value = (byte) bus.readByteFromMemory(pc + 1);

        performALUOperation(operation, value);
        context.set(PC, pc + 2);

        return 7;
    }

    private void performALUOperation(ALUOperations operation, byte value) {
        byte acc = (byte) context.get(A);
        byte result;

        switch (operation) {
            case ADD_A:
                result = (byte) (acc + value);
                setALUFlags(result);
                context.set(A, result);
                break;
            case ADC_A:
                result = (byte) (acc + value + (context.get(Flags.C) ? 1 : 0));
                setALUFlags(result);
                context.set(A, result);
                break;
            case SUB:
                result = (byte) (acc - value);
                setALUFlags(result);
                context.set(A, result);
                break;
            case SUBC_A:
                int raw_result = acc - value - (context.get(Flags.C) ? 1 : 0);
                result = (byte) raw_result;
                setALUFlags(result);
                context.set(A, result);
                break;
            case AND:
                result = (byte) (acc & value);
                setALUFlags(result);
                context.set(Flags.C, false);
                context.set(Flags.N, false);
                context.set(Flags.H, true);
                // todo: set parity
                context.set(A, result);
                break;
            case XOR:
                result = (byte) (acc ^ value);
                setALUFlags(result);
                context.set(A, result);
                break;
            case OR:
                result = (byte) (acc | value);
                setALUFlags(result);
                context.set(A, result);
                break;
            case CP:
                setALUFlags((byte) (acc - value));
        }
    }

    private void setALUFlags(byte operationResult) {
        context.set(Flags.Z, operationResult == 0);
        context.set(Flags.S, operationResult < 0);
    }

    public long ret_cc(Conditions condition) {
        boolean cc = checkCondition(condition);

        if (cc) {
            return ret() + 1;
        }

        return 5;
    }

    public long push(Register16 register) {
        int sp = context.get(SP) - 2;
        bus.writeWordToMemory(sp, context.get(register));
        context.set(SP, sp);
        return 11;
    }

    public long pop(Register16 register) {
        int sp = context.get(SP);
        context.set(register, bus.readWordFromMemory(sp));
        context.set(SP, sp + 2);
        return 10;
    }

    public long call_nn() {
        int pc = context.get(PC);
        int address = bus.readWordFromMemory(pc);
        int sp = context.get(SP) - 2;
        bus.writeWordToMemory(sp, pc + 2);
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
        context.swap(BC);
        context.swap(DE);
        context.swap(HL);
        return 4;
    }

    public long jp_hl() {
        context.set(PC, context.get(HL));
        return 4;
    }

    public long ld_sp_hl() {
        context.set(SP, context.get(HL));
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
        bus.writeWordToMemory(context.decrementAndGet(SP), context.get(PC));
        context.decrementAndGet(SP);
        context.set(PC, n);
        return 11;
    }

    public long jp_nn() {
        context.set(PC, bus.readWordFromMemory(context.get(PC)));
        return 10;
    }

    public long ex_de_hl() {
        int de = context.get(DE);
        int hl = context.get(HL);
        context.set(DE, hl);
        context.set(HL, de);
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
        int address = bus.readByteFromMemory(pc);
        bus.writeByteToPort(address, context.get(A));
        context.set(PC, pc + 1);
        return 11;
    }

    public long ld_a_i() {
        context.set(A, context.get(I));
        return 9;
    }

    public long ld_i_a() {
        context.set(I, context.get(A));
        return 9;
    }

    public long di() {
        context.disableInterrupt();
        return 4;
    }

    public long ei() {
        context.enableInterrupt();
        return 4;
    }

    public long sbc_hl_rp(Register16 register) {
        int carry = context.get(Flags.C) ? 1 : 0;
        context.set(HL, context.get(HL) - context.get(register) - carry);
        return 15;
    }

    public long adc_hl_rp(Register16 register) {
        int carry = context.get(Flags.C) ? 1 : 0;
        context.set(HL, context.get(HL) + context.get(register) + carry);
        return 15;
    }

    public long ld_nn_rp(Register16 register) {
        int pc = context.get(PC);
        int address = bus.readWordFromMemory(pc);
        bus.writeWordToMemory(address, context.get(register));
        context.set(PC, pc + 2);
        return 20;
    }

    public long ld_rp_nn(Register16 register) {
        int pc = context.get(PC);
        int address = bus.readWordFromMemory(pc);
        int value = bus.readByteFromMemory(address);
        context.set(register, value);
        context.set(PC, pc + 2);
        return 20;
    }

    public long setIM(InterruptMode mode) {
        context.setIM(mode);
        return 8;
    }

    public long testBit(int bit, Register8 register) {
        throw new EmulationException("Not implemented yet");
    }

    public long resetBit(int bit, Register8 register) {
        throw new EmulationException("Not implemented yet");
    }

    public long setBit(int bit, Register8 register) {
        int value = context.get(register);
        value |= (1 << bit);
        context.set(register, value);
        return 8;
    }

    public long setBit_hl(int bit){
        int value = readByteFromMemoryHL();
        writeByteToMemoryHL(value | (1 << bit));
        return 15;
    }

    public long extendedSetBit(int bit, byte offset) {
        RegisterSpecial dst = context.getCurrentAddressRegister();
        int address;

        if (dst == null) {
            address = context.get(HL) + offset;
        } else {
            address = context.get(dst) + offset;
        }

        int value = bus.readByteFromMemory(address);
        bus.writeByteToMemory(address, value | (1 << bit));
        return 23;
    }

    public long extendedResetBit(int bit, byte offset) {
        RegisterSpecial dst = context.getCurrentAddressRegister();
        int address;

        if (dst == null) {
            address = context.get(HL) + offset;
        } else {
            address = context.get(dst) + offset;
        }

        int value = bus.readByteFromMemory(address);
        bus.writeByteToMemory(address, value & ~(1 << bit));
        return 23;
    }

    public long extendedTestBit(int bit, byte offset) {
        RegisterSpecial dst = context.getCurrentAddressRegister();
        int address;

        if (dst == null) {
            address = context.get(HL) + offset;
        } else {
            address = context.get(dst) + offset;
        }

        int value = bus.readByteFromMemory(address);
        int result = (value >>> bit) & 1;
        context.set(Flags.N, false);
        context.set(Flags.H, true);
        context.set(Flags.Z, result == 0);
        return 20;
    }

    private int readByteFromMemoryHL() {
        int address = context.get(HL);
        return bus.readByteFromMemory(address);
    }

    private void writeByteToMemoryHL(int value) {
        int address = context.get(HL);
        bus.writeByteToMemory(address, value);
    }

    public long ld_hl_r8(Register8 src) {
        RegisterSpecial addressRegister = context.getCurrentAddressRegister();

        if (addressRegister == null) {
            int address = context.get(HL);
            bus.writeByteToMemory(address, context.get(src));
            return 7;
        } else {
            byte displacement = (byte) bus.readByteFromMemory(context.getAndIncrement(PC));
            int address = context.get(addressRegister) + displacement;
            bus.writeByteToMemory(address, context.get(src));
            return 19;
        }
    }

    public long ld_r8_hl(Register8 dst) {
        RegisterSpecial addressRegister = context.getCurrentAddressRegister();

        if (addressRegister == null) {
            int address = context.get(HL);
            context.set(dst, bus.readByteFromMemory(address));
            return 7;
        } else {
            byte displacement = (byte) bus.readByteFromMemory(context.getAndIncrement(PC));
            int address = context.get(addressRegister) + displacement;
            context.set(dst, bus.readByteFromMemory(address));
            return 19;
        }
    }
}
