package com.zxrasp.emulator.core.z80.z80internals;

public class UnknownOperationException extends Z80EmulationException {
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
