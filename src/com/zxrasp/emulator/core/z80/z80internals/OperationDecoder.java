package com.zxrasp.emulator.core.z80.z80internals;

import com.zxrasp.emulator.core.SystemBus;

import java.util.HashMap;
import java.util.Map;

public class OperationDecoder {

    private static final Map<Integer, RegisterNames> r8 = new HashMap<>();
    private static final Map<Integer, RegisterNames> r16a = new HashMap<>();
    private static final Map<Integer, RegisterNames> r16b = new HashMap<>();

    private static int totalTicks;

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
    }

    private Context context;
    private SystemBus bus;


    public OperationDecoder(Context context, SystemBus bus) {
        this.context = context;
        this.bus = bus;
        this.executor = new OperationExecutor(context, bus);
    }

    public long decodeAndExecute() throws UnknownOperationException{
        int pc = context.get(RegisterNames.PC);
        int opcode = bus.readByteFromMemory(pc);

        System.out.printf("PC: %X, opcode: %X\n", pc, opcode);

        long result;

        if (isExtendedOpcode(opcode)) {
            result = resolveExtendedOperation(opcode, bus.readByteFromMemory(context.incrementAndGet(RegisterNames.PC)));
        } else {
            result = decodeOneByteOperation(opcode);
        }

        totalTicks += result;

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
                            case 5:
                                return executor.ex_de_hl();
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
            case 0xED:
                return decodeEDOperation(opcode);
        }

        throw new UnknownOperationException(String.format("Unknown opcode: %x %x", prefix, opcode), context);
    }

    private long decodeEDOperation(int opcode) {
        int x = (opcode >> 6) & 3;
        int y = (opcode >> 3) & 7;
        int z = opcode & 7;

        switch (x) {
            case 0:
            case 3:
                throw new InvalidOperationException(opcode);
            case 2:
                if (y >= 4 && z <= 3) {
                    return executor.blockOperation(BlockOperationsDecoder.decodeBlockOperation(y, z));
                }

                throw new InvalidOperationException(opcode);
        }

        throw new UnknownOperationException(String.format("Unknown opcode: %x %x", opcode), context);
    }
}
