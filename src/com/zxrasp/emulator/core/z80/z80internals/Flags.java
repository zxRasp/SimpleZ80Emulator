package com.zxrasp.emulator.core.z80.z80internals;

public enum Flags {
    C {
        @Override
        public int getMask() {
            return 0x1;
        }
    },

    N {
        @Override
        public int getMask() {
            return 0x2;
        }
    },

    PV {
        @Override
        public int getMask() {
            return 0x4;
        }
    },

    F3 {
        @Override
        public int getMask() {
            return 0x8;
        }
    },

    H {
        @Override
        public int getMask() {
            return 0x10;
        }
    },

    F5 {
        @Override
        public int getMask() {
            return 0x20;
        }
    },

    Z {
        @Override
        public int getMask() {
            return 0x40;
        }
    },

    S {
        @Override
        public int getMask() {
            return 0x80;
        }
    };

    public abstract int getMask();
}
