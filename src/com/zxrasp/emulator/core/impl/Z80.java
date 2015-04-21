package com.zxrasp.emulator.core.impl;

import com.zxrasp.emulator.core.CPU;
import com.zxrasp.emulator.core.SystemBus;
import com.zxrasp.emulator.core.impl.z80internals.*;

public class Z80 implements CPU {

    private Context context;

    public Z80(SystemBus bus) {
        context = new Z80Context(bus);
    }

    @Override
    public long clock() throws UnknownOperationException {
        return OperationDecoder.decodeAndExecute(context);
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
