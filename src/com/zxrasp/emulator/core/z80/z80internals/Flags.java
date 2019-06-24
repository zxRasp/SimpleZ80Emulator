package com.zxrasp.emulator.core.z80.z80internals;

public enum Flags {
    NZ {
        @Override
        public int getMask() {
            return 0;
        }
    },
    Z {
        @Override
        public int getMask() {
            return 0;
        }
    },
    NC {
        @Override
        public int getMask() {
            return 0;
        }
    },
    C {
        @Override
        public int getMask() {
            return 0;
        }
    },
    PO {
        @Override
        public int getMask() {
            return 0;
        }
    },

    PE {
        @Override
        public int getMask() {
            return 0;
        }
    },
    P {
        @Override
        public int getMask() {
            return 0;
        }
    },
    M {
        @Override
        public int getMask() {
            return 0;
        }
    };

    public abstract int getMask();

}
