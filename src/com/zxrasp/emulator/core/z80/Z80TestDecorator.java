package com.zxrasp.emulator.core.z80;

import com.zxrasp.emulator.core.EmulationException;
import com.zxrasp.emulator.core.SystemBusDevice;
import com.zxrasp.emulator.core.z80.z80internals.RegisterNames;
import com.zxrasp.emulator.core.z80.z80internals.UnknownOperationException;

public class Z80TestDecorator extends Z80 {

    public Z80TestDecorator(SystemBusDevice bus) {
        super(bus);
        context.set(RegisterNames.PC, 0x0100);
        context.set(RegisterNames.SP, 0xFF00);
    }

    @Override
    public long clock() throws UnknownOperationException {
        if (context.get(RegisterNames.PC) == 0x5) {
            handleSystemRequest();
        }

        return super.clock();
    }

    private void handleSystemRequest() {
        int syscall = context.get(RegisterNames.C);
        switch (syscall) {
            case 2:
                printChar();
                break;
            case 9:
                printString();
                break;
            default:
                throw new EmulationException(String.format("Unexpected system call #%d", syscall));
        }
    }

    private void printString() {
        int strPtr = context.get(RegisterNames.DE);

        char c;

        while((c = (char) bus.readByteFromMemory(strPtr++)) != '$') {
            System.out.print(c);
        }
    }

    private void printChar() {
        char c = (char) context.get(RegisterNames.E);
        System.out.print(c);
    }
}
