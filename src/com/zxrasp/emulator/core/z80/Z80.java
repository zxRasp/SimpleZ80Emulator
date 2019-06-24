package com.zxrasp.emulator.core.z80;

import com.zxrasp.emulator.core.CPU;
import com.zxrasp.emulator.core.SystemBus;
import com.zxrasp.emulator.core.z80.z80internals.*;

public class Z80 implements CPU {

    protected Context context;
    protected SystemBus bus;

    private OperationDecoder decoder;

    public Z80(SystemBus bus) {
        this.bus = bus;
        this.context = new Z80Context();
        this.decoder = new OperationDecoder(context, bus);
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
}
