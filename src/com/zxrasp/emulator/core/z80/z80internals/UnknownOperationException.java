package com.zxrasp.emulator.core.z80.z80internals;

import com.zxrasp.emulator.core.EmulationException;

public class UnknownOperationException extends EmulationException {
    private Context context;

    public UnknownOperationException(String message, Context context) {
        super(message);
        this.context = context;
    }

    @Override
    public String getMessage() {
        return super.getMessage() + "; " + context.toString();
    }
}
