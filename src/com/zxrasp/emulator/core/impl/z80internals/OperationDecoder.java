package com.zxrasp.emulator.core.impl.z80internals;

import java.util.HashMap;
import java.util.Map;

public class OperationDecoder {

    private static final Map<Integer, RegisterNames> r8 = new HashMap<Integer, RegisterNames>();
    private static final Map<Integer, RegisterNames> r16a = new HashMap<Integer, RegisterNames>();
    private static final Map<Integer, RegisterNames> r16b = new HashMap<Integer, RegisterNames>();

    private static int totalTicks;

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


    public static long decodeAndExecute(Context context) throws UnknownOperationException{
        int pc = context.get(RegisterNames.PC);
        int opcode = context.getSystemBus().readByteFromMemory(pc);

        System.out.println("PC: " + pc);

        if (pc == Short.MAX_VALUE * 2 + 1) {
            System.out.println("Total ticks = " + totalTicks);
            System.exit(2);
        }

        long result;

        if (isExtendedOpcode(opcode)) {
            result = resolveExtendedOperation(context, opcode, context.getSystemBus().readByteFromMemory(pc + 1));
        } else {
            result = decodeOneByteOperation(context, opcode);
        }

        totalTicks += result;

        return result;
    }

    private static boolean isExtendedOpcode(int opcode) {
        return opcode == 0xCB || opcode == 0xDD || opcode == 0xED || opcode == 0xFD;
    }

    private static long decodeOneByteOperation(Context context, int opcode) throws UnknownOperationException {
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
                                return OperationExecutor.nop(context);
                            case 1:
                                return OperationExecutor.exx_af_af(context);
                            case 2:
                                return OperationExecutor.djnz(context);
                            case 3:
                                return OperationExecutor.jr(context);
                            default:
                                return OperationExecutor.jr_cc(context, Flags.values()[y - 4]);
                        }
                    case 1:
                        switch(q){
                            case 0:
                                return OperationExecutor.ld_16(context, r16a.get(p));
                            case 1:
                                return OperationExecutor.add_hl(context, r16a.get(p));
                    }
                    case 2:
                        switch (q) {
                            case 0:
                                switch (p) {
                                    case 0:
                                        return OperationExecutor.ld_m_bc(context);
                                    case 1:
                                        return OperationExecutor.ld_m_de(context);
                                    case 2:
                                        return OperationExecutor.ld_m_hl(context);
                                    case 3:
                                        return OperationExecutor.ld_m_a(context);
                                }
                            case 1:
                                switch (p) {
                                    case 0:
                                        return OperationExecutor.ld_a_m_bc(context);
                                    case 1:
                                        return OperationExecutor.ld_a_m_de(context);
                                    case 2:
                                        return OperationExecutor.ld_hl_m(context);
                                    case 3:
                                        return OperationExecutor.ld_a_m(context);
                                }
                        }
                    case 3:
                        switch (q) {
                            case 0:
                                return OperationExecutor.inc16(context, r16a.get(p));
                            case 1:
                                return OperationExecutor.dec16(context, r16a.get(p));
                        }
                    case 4:
                        return OperationExecutor.inc8(context, r8.get(y));
                    case 5:
                        return OperationExecutor.dec8(context, r8.get(y));
                    case 6:
                        return OperationExecutor.ld_8(context, r8.get(y));
                    case 7:
                        switch (y) {
                            case 0:
                                return OperationExecutor.rlca(context);
                            case 1:
                                return OperationExecutor.rrca(context);
                            case 2:
                                return OperationExecutor.rla(context);
                            case 3:
                                return OperationExecutor.rra(context);
                            case 4:
                                return OperationExecutor.daa(context);
                            case 5:
                                return OperationExecutor.cpl(context);
                            case 6:
                                return OperationExecutor.scf(context);
                            case 7:
                                return OperationExecutor.ccf(context);
                        }
                }
            case 1:
                return OperationExecutor.ld_r8_r8(context, r8.get(y), r8.get(z));
            case 2:
                return OperationExecutor.performALUOperation(context, ALUOperations.values()[y], r8.get(z));
            case 3:
                switch (z) {
                    case 0:
                        return OperationExecutor.ret_cc(context, Flags.values()[y]);
                    case 1:
                        switch (q) {
                            case 0:
                                return OperationExecutor.pop(context, r16b.get(p));
                            case 1:
                                switch (p) {
                                    case 0:
                                        return OperationExecutor.ret(context);
                                    case 1:
                                        return OperationExecutor.exx(context);
                                    case 2:
                                        return OperationExecutor.jp_hl(context);
                                    case 3:
                                        return OperationExecutor.ld_sp_hl(context);
                                }
                        }
                    case 2:
                        return OperationExecutor.jp_cc(context, Flags.values()[y]);
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                    case 7:
                }
        }

        throw  new UnknownOperationException();
    }

    private static long resolveExtendedOperation(Context context, int prefix, int opcode) throws UnknownOperationException {
        throw new UnknownOperationException();
    }
}
