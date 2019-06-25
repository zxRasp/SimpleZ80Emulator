package com.zxrasp.emulator.core.z80.z80internals;

import static com.zxrasp.emulator.core.z80.z80internals.BlockOperations.*;

public class BlockOperationsDecoder {

    private static BlockOperations operations[][] = {
            { LDI,	CPI,	INI,	OUTI },
            { LDD,	CPD,	IND,	OUTD },
            { LDIR,	CPIR,	INIR,	OTIR },
            { LDDR,	CPDR,	INDR,	OTDR }
    };

    public static BlockOperations decodeBlockOperation (int a, int b) {
        return operations[a - 4] [b];
    }
}
