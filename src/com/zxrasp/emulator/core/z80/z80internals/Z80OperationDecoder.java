package com.zxrasp.emulator.core.z80.z80internals;

import com.zxrasp.emulator.core.DebugAware;
import com.zxrasp.emulator.core.SystemBusDevice;

import java.util.HashMap;
import java.util.Map;

import static com.zxrasp.emulator.core.z80.z80internals.InterruptMode.*;
import static com.zxrasp.emulator.core.z80.z80internals.RegisterSpecial.PC;

public class Z80OperationDecoder implements DebugAware {

    private static final Map<Integer, Register8> r8 = new HashMap<>();
    private static final Map<Integer, Register16> r16a = new HashMap<>();
    private static final Map<Integer, Register16> r16b = new HashMap<>();
    private static final Map<Integer, InterruptMode> im = new HashMap<>();

    private final OperationExecutor executor;

    static {
        r8.put(0, Register8.B);
        r8.put(1, Register8.C);
        r8.put(2, Register8.D);
        r8.put(3, Register8.E);
        r8.put(4, Register8.H);
        r8.put(5, Register8.L);
        r8.put(7, Register8.A);

        r16a.put(0, Register16.BC);
        r16a.put(1, Register16.DE);
        r16a.put(2, Register16.HL);
        r16a.put(3, Register16.SP);

        r16b.put(0, Register16.BC);
        r16b.put(1, Register16.DE);
        r16b.put(2, Register16.HL);
        r16b.put(3, Register16.AF);

        im.put(0, IM_0);
        im.put(1, IM_0); // 0/1 ???
        im.put(2, IM_1);
        im.put(3, IM_2);
        im.put(4, IM_0);
        im.put(5, IM_0); // 0/1 ???
        im.put(6, IM_1);
        im.put(7, IM_2);
    }

    private final Z80Context context;
    private final SystemBusDevice bus;
    private int prefix;
    private int opcode;


    public Z80OperationDecoder(Z80Context context, SystemBusDevice bus) {
        this.context = context;
        this.bus = bus;
        this.executor = new OperationExecutor(context, bus);
    }

    public long decodeAndExecute() throws UnknownOperationException{
        fetchOpcode();
        long result;

        if (isExtendedOpcode()) {
            prefix = opcode;
            fetchOpcode();
            result = resolveExtendedOperation();
        } else {
            result = decodeOneByteOperation();
        }

        context.incrementR();
        return result;
    }

    private void fetchOpcode() {
        int pc = context.getAndIncrement(PC);
        opcode = bus.readByteFromMemory(pc);
    }

    private boolean isExtendedOpcode() {
        return opcode == 0xCB || opcode == 0xDD || opcode == 0xED || opcode == 0xFD;
    }

    private long decodeOneByteOperation() throws UnknownOperationException {
        int x = (opcode >> 6) & 3;
        int y = (opcode >> 3) & 7;
        int z = opcode & 7;
        int p = y >> 1;
        int q = y & 1;

        switch (x) {
            case 0:
                switch(z) {
                    case 0:
                        switch(y) {
                            case 0:
                                return executor.nop();
                            case 1:
                                return executor.exx_af_af();
                            case 2:
                                return executor.djnz();
                            case 3:
                                return executor.jr();
                            default:
                                return executor.jr_cc(Conditions.values()[y - 4]);
                        }
                    case 1:
                        switch(q){
                            case 0:
                                return executor.ld_16(r16a.get(p));
                            case 1:
                                return executor.add_to_hl(r16a.get(p));
                    }
                    case 2:
                        switch (q) {
                            case 0:
                                switch (p) {
                                    case 0:
                                        return executor.ld_m_bc();
                                    case 1:
                                        return executor.ld_m_de();
                                    case 2:
                                        return executor.ld_m_hl();
                                    case 3:
                                        return executor.ld_m_a();
                                }
                            case 1:
                                switch (p) {
                                    case 0:
                                        return executor.ld_a_m_bc();
                                    case 1:
                                        return executor.ld_a_m_de();
                                    case 2:
                                        return executor.ld_hl_m();
                                    case 3:
                                        return executor.ld_a_m();
                                }
                        }
                    case 3:
                        switch (q) {
                            case 0:
                                return executor.inc16(r16a.get(p));
                            case 1:
                                return executor.dec16(r16a.get(p));
                        }
                    case 4:
                        return (y == 6) ? executor.inc8_hl() : executor.inc8(r8.get(y));
                    case 5:
                        return (y == 6) ? executor.dec8_hl() : executor.dec8(r8.get(y));
                    case 6:
                        return (y == 6) ? executor.ld_8_hl() : executor.ld_8(r8.get(y));
                    case 7:
                        switch (y) {
                            case 0:
                                return executor.rlca();
                            case 1:
                                return executor.rrca();
                            case 2:
                                return executor.rla();
                            case 3:
                                return executor.rra();
                            case 4:
                                return executor.daa();
                            case 5:
                                return executor.cpl();
                            case 6:
                                return executor.scf();
                            case 7:
                                return executor.ccf();
                        }
                }
            case 1:
                if (y == 6 && z == 6) {
                    return executor.halt();
                }
                if (y == 6) {
                    return executor.ld_hl_r8(r8.get(z));
                }
                if (z == 6) {
                    return executor.ld_r8_hl(r8.get(y));
                }
                return executor.ld_r8_r8(r8.get(y), r8.get(z));
            case 2:
                return z == 6 ? executor.performALUOperationHL(ALUOperations.values()[y]) : executor.performALUOperationR8(ALUOperations.values()[y], r8.get(z));
            case 3:
                switch (z) {
                    case 0:
                        return executor.ret_cc(Conditions.values()[y]);
                    case 1:
                        switch (q) {
                            case 0:
                                return executor.pop(r16b.get(p));
                            case 1:
                                switch (p) {
                                    case 0:
                                        return executor.ret();
                                    case 1:
                                        return executor.exx();
                                    case 2:
                                        return executor.jp_hl();
                                    case 3:
                                        return executor.ld_sp_hl();
                                }
                        }
                    case 2:
                        return executor.jp_cc(Conditions.values()[y]);
                    case 3:
                        switch (y) {
                            case 0:
                                return executor.jp_nn();
                            case 2:
                                return executor.out_n_a();
                            case 4:
                                return executor.ex_sp_hl();
                            case 5:
                                return executor.ex_de_hl();
                            case 6:
                                return executor.di();
                            case 7:
                                return executor.ei();
                            default:
                                throw  new UnknownOperationException(String.format("Unknown opcode: %x", opcode), context);
                        }
                    case 4:
                        return executor.call_cc_nn(Conditions.values()[y]);
                    case 5:
                        switch(q) {
                            case 0:
                                return executor.push(r16b.get(p));
                            case 1:
                                return executor.call_nn();
                        }
                    case 6:
                        return executor.performALUOperationNN(ALUOperations.values()[y]);
                    case 7:
                        return executor.rst(y << 3);
                }
        }

        throw  new UnknownOperationException(String.format("Unknown opcode: %x", opcode), context);
    }

    private long resolveExtendedOperation() throws UnknownOperationException {
        switch (prefix) {
            case 0xCB:
                return decodeCBOperation();
            case 0xED:
                return decodeEDOperation();
            case 0xDD:
                return decodeIXOperation();
            case 0xFD:
                return decodeIYOperation();
        }

        throw new UnknownOperationException(String.format("Unknown opcode: %x %x", prefix, opcode), context);
    }

    private long decodeCBOperation() {
        int x = (opcode >> 6) & 3;
        int y = (opcode >> 3) & 7;
        int z = opcode & 7;

        switch (x) {
            case 1:
                return (z == 6) ? executor.testBit_hl(y) : executor.testBit(y, r8.get(z));
            case 2:
                return (z == 6) ? executor.resetBit_hl(y) : executor.resetBit(y, r8.get(z));
            case 3:
                return (z == 6) ? executor.setBit_hl(y) : executor.setBit(y, r8.get(z));
            default:
                throw new UnknownOperationException(String.format("Unknown opcode: %x", opcode), context);

        }
    }

    private long decodeEDOperation() {
        int x = (opcode >> 6) & 3;
        int y = (opcode >> 3) & 7;
        int z = opcode & 7;
        int p = y >> 1;
        int q = y & 1;

        switch (x) {
            case 0:
            case 3:
                throw new InvalidOperationException(opcode);
            case 1:
                switch(z) {
                    case 2:
                        switch (q) {
                            case 0:
                                return executor.sbc_hl_rp(r16a.get(p));
                            case 1:
                                return executor.adc_hl_rp(r16a.get(p));
                        }
                    case 3:
                        switch (q) {
                            case 0:
                                return executor.ld_nn_rp(r16a.get(p));
                            case 1:
                                return executor.ld_rp_nn(r16a.get(p));
                        }
                    case 4:
                        return executor.neg();
                    case 6:
                        return executor.setIM(im.get(y));
                    case 7:
                        switch (y) {
                            case 0:
                                return executor.ld_i_a();
                            case 2:
                                return executor.ld_a_i();
                            default:
                                throw new UnknownOperationException(String.format("Unknown opcode: %X", opcode), context);
                        }
                    default:
                        throw new UnknownOperationException(String.format("Unknown opcode: %X", opcode), context);
                }
            case 2:
                if (y >= 4 && z <= 3) {
                    return executor.blockOperation(BlockOperationsDecoder.decodeBlockOperation(y, z));
                }

                throw new InvalidOperationException(opcode);
        }

        throw new UnknownOperationException(String.format("Unknown opcode: %X", opcode), context);
    }

    private long decodeIXOperation() {
        if (opcode == 0xDD || opcode == 0xED || opcode == 0xFD) {
            // ignore this prefix
            return 4;
        }

        if (opcode == 0xCB) {
            throw new UnknownOperationException(String.format("Unknown opcode: %X", opcode), context);
        }

        long result = 0;
        context.setCurrentAddressRegister(RegisterSpecial.IX);

        if (opcode == 0xCB) {
            byte offset = (byte) bus.readByteFromMemory(context.getAndIncrement(PC));
            fetchOpcode();
            result = decodeExtendedCBOperation(offset);
        } else {
            result = decodeOneByteOperation();
        }

        context.setCurrentAddressRegister(null);

        return result;
    }

    private long decodeIYOperation() {
        if (opcode == 0xDD || opcode == 0xED || opcode == 0xFD) {
            // ignore this prefix
            return 4;
        }

        long result = 0;
        context.setCurrentAddressRegister(RegisterSpecial.IY);

        if (opcode == 0xCB) {
            byte offset = (byte) bus.readByteFromMemory(context.getAndIncrement(PC));
            fetchOpcode();
            result = decodeExtendedCBOperation(offset);
        } else {
            result = decodeOneByteOperation();
        }

        context.setCurrentAddressRegister(null);

        return result;
    }

    private long decodeExtendedCBOperation(byte offset) {
        int x = (opcode >> 6) & 3;
        int y = (opcode >> 3) & 7;
        int z = opcode & 7;


        switch (x) {
            case 1:
                return executor.extendedTestBit(y, offset);
            case 2:
                if (z == 6) {
                    return executor.extendedResetBit(y, offset);
                } else {
                    throw new UnknownOperationException(String.format("Unknown opcode: %X", opcode), context);
                }
            case 3:
                if (z == 6) {
                    return executor.extendedSetBit(y, offset);
                } else {
                    throw new UnknownOperationException(String.format("Unknown opcode: %X", opcode), context);
                }
            default:
                throw new UnknownOperationException(String.format("Unknown opcode: %X", opcode), context);
        }
    }

    @Override
    public int getCurrentOpcode() {
        return opcode;
    }
}
