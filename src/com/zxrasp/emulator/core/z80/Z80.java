package com.zxrasp.emulator.core.z80;

import static com.zxrasp.emulator.core.z80.z80internals.RegisterSpecial.*;

import com.zxrasp.emulator.core.CPU;
import com.zxrasp.emulator.core.DebugAware;
import com.zxrasp.emulator.core.SystemBusDevice;
import com.zxrasp.emulator.core.z80.z80internals.*;

public class Z80 implements CPU, DebugAware {

    protected Z80Context context;
    protected SystemBusDevice bus;

    private Z80OperationDecoder decoder;

    public Z80(SystemBusDevice bus) {
        this.bus = bus;
        this.context = new Z80Context();
        this.decoder = new Z80OperationDecoder(context, bus);
    }

    @Override
    public long clock() throws UnknownOperationException {
        return decoder.decodeAndExecute();
    }

    @Override
    public void reset() {
        context.reset();
    }

    @Override
    public boolean isHalted() {
        return context.isHalted();
    }

    @Override
    public void interrupt(boolean masked) {
        // todo
    }

    @Override
    public int getCurrentOpcode() {
        return decoder.getCurrentOpcode();
    }

    @Override
    public String toString() {
        return String.format("Current opcode: %02X\n%s", getCurrentOpcode(), context);
    }
}
