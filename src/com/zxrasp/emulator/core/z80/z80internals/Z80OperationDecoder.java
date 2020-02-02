package com.zxrasp.emulator.core.z80.z80internals;

import com.zxrasp.emulator.core.SystemBusDevice;

import java.util.HashMap;
import java.util.Map;

import static com.zxrasp.emulator.core.z80.z80internals.InterruptMode.*;
import static com.zxrasp.emulator.core.z80.z80internals.RegisterNames.PC;
import static com.zxrasp.emulator.core.z80.z80internals.RegisterNames.R;
import static com.zxrasp.emulator.core.z80.z80internals.Z80Context.HLRegisterMode.*;

public class Z80OperationDecoder {

    private static final Map<Integer, RegisterNames> r8 = new HashMap<>();
    private static final Map<Integer, RegisterNames> r16a = new HashMap<>();
    private static final Map<Integer, RegisterNames> r16b = new HashMap<>();
    private static final Map<Integer, InterruptMode> im = new HashMap<>();

    private final OperationExecutor executor;

    static {
        r8.put(0, RegisterNames.B);
        r8.put(1, RegisterNames.C);
        r8.put(2, RegisterNames.D);
        r8.put(3, RegisterNames.E);
        r8.put(4, RegisterNames.H);
        r8.put(5, RegisterNames.L);
        r8.put(6, RegisterNames.HL); // special case, means (HL)!
        r8.put(7, RegisterNames.A);

        r16a.put(0, RegisterNames.BC);
        r16a.put(1, RegisterNames.DE);
        r16a.put(2, RegisterNames.HL);
        r16a.put(3, RegisterNames.SP);

        r16b.put(0, RegisterNames.BC);
        r16b.put(1, RegisterNames.DE);
        r16b.put(2, RegisterNames.HL);
        r16b.put(3, RegisterNames.AF);

        im.put(0, IM_0);
        im.put(1, IM_0); // 0/1 ???
        im.put(2, IM_1);
        im.put(3, IM_2);
        im.put(4, IM_0);
        im.put(5, IM_0); // 0/1 ???
        im.put(6, IM_1);
        im.put(7, IM_2);
    }

    private Z80Context context;
    private SystemBusDevice bus;


    public Z80OperationDecoder(Z80Context context, SystemBusDevice bus) {
        this.context = context;
        this.bus = bus;
        this.executor = new OperationExecutor(context, bus);
    }

    public long decodeAndExecute() throws UnknownOperationException{
        int pc = context.get(PC);
        int opcode = bus.readByteFromMemory(pc);

        //System.out.printf("PC: %X, opcode: %X\n", pc, opcode);

        long result;

        if (isExtendedOpcode(opcode)) {
            result = resolveExtendedOperation(opcode, bus.readByteFromMemory(context.incrementAndGet(PC)));
        } else {
            result = decodeOneByteOperation(opcode);
        }

        context.incrementAndGet(R);
        return result;
    }

    private boolean isExtendedOpcode(int opcode) {
        return opcode == 0xCB || opcode == 0xDD || opcode == 0xED || opcode == 0xFD;
    }

    private long decodeOneByteOperation(int opcode) throws UnknownOperationException {
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
                        return executor.inc8(r8.get(y));
                    case 5:
                        return executor.dec8(r8.get(y));
                    case 6:
                        return executor.ld_8(r8.get(y));
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
                return executor.ld_r8_r8(r8.get(y), r8.get(z));
            case 2:
                return executor.performALUOperation(ALUOperations.values()[y], r8.get(z));
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
                        throw  new UnknownOperationException(String.format("Unknown opcode: %x", opcode), context);
                    case 5:
                        switch(q) {
                            case 0:
                                return executor.push(r16b.get(p));
                            case 1:
                                return executor.call_nn();
                        }
                    case 6:
                        return executor.performALUOperation(ALUOperations.values()[y]);
                    case 7:
                        return executor.rst(y << 3);
                }
        }

        throw  new UnknownOperationException(String.format("Unknown opcode: %x", opcode), context);
    }

    private long resolveExtendedOperation(int prefix, int opcode) throws UnknownOperationException {
        switch (prefix) {
            case 0xCB:
                return decodeCBOperation(opcode);
            case 0xED:
                return decodeEDOperation(opcode);
            case 0xDD:
            case 0xFD:
                return decodeIXIYOperation(prefix, opcode);
        }

        throw new UnknownOperationException(String.format("Unknown opcode: %x %x", prefix, opcode), context);
    }

    private long decodeCBOperation(int opcode) {
        int x = (opcode >> 6) & 3;
        int y = (opcode >> 3) & 7;
        int z = opcode & 7;

        switch (x) {
            case 1:
                return executor.testBit(y, r8.get(z));
            case 2:
                return executor.resetBit(y, r8.get(z));
            case 3:
                return executor.setBit(y, r8.get(z));
            default:
                throw new UnknownOperationException(String.format("Unknown opcode: %x", opcode), context);

        }
    }

    private long decodeEDOperation(int opcode) {
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

    private long decodeIXIYOperation(int prefix, int opcode) {
        if (opcode == 0xDD || opcode == 0xED || opcode == 0xFD) {
            // ignore this prefix
            context.incrementAndGet(PC);
            return 4;
        }

        if (opcode == 0xCB) {
            context.setHLRegisterMode(prefix == 0xDD ? IX : IY);
            int offset = bus.readByteFromMemory(context.incrementAndGet(PC));
            long result = decodeExtendedCBOperation(offset, bus.readByteFromMemory(context.incrementAndGet(PC)));
            context.setHLRegisterMode(HL);
            return result;
        }

        context.setHLRegisterMode(prefix == 0xDD ? IX : IY);
        long result = decodeOneByteOperation(opcode);
        context.setHLRegisterMode(HL);
        return result;
    }

    private long decodeExtendedCBOperation(int offset, int opcode) {
        int x = (opcode >> 6) & 3;
        int y = (opcode >> 3) & 7;
        int z = opcode & 7;


        switch (x) {
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
}
