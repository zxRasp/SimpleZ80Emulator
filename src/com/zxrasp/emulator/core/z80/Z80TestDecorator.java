package com.zxrasp.emulator.core.z80;

import com.zxrasp.emulator.core.EmulationException;
import com.zxrasp.emulator.core.SystemBusDevice;
import com.zxrasp.emulator.core.z80.z80internals.UnknownOperationException;

import static com.zxrasp.emulator.core.z80.z80internals.Register16.DE;
import static com.zxrasp.emulator.core.z80.z80internals.Register16.SP;
import static com.zxrasp.emulator.core.z80.z80internals.Register8.C;
import static com.zxrasp.emulator.core.z80.z80internals.Register8.E;
import static com.zxrasp.emulator.core.z80.z80internals.RegisterSpecial.PC;

public class Z80TestDecorator extends Z80 {

    public Z80TestDecorator(SystemBusDevice bus) {
        super(bus);
    }

    @Override
    public void reset() {
        super.reset();
        context.set(PC, 0x0100);
        context.set(SP, 0xFF00);
    }

    @Override
    public long clock() throws UnknownOperationException {
        if (context.get(PC) == 0x5) {
            handleSystemRequest();
        }

        return super.clock();
    }

    private void handleSystemRequest() {
        int syscall = context.get(C);
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
        int strPtr = context.get(DE);

        char c;

        while((c = (char) bus.readByteFromMemory(strPtr++)) != '$') {
            System.out.print(c);
        }
    }

    private void printChar() {
        char c = (char) context.get(E);
        System.out.print(c);
    }
}
