package com.zxrasp.emulator.core.z80.z80internals;

public enum Conditions {
    NZ {
        @Override
        public Flags flagToCheck() {
            return Flags.Z;
        }

        @Override
        public boolean expectedValue() {
            return false;
        }
    },

    Z {
        @Override
        public Flags flagToCheck() {
            return Flags.Z;
        }

        @Override
        public boolean expectedValue() {
            return true;
        }
    },

    NC {
        @Override
        public Flags flagToCheck() {
            return Flags.C;
        }

        @Override
        public boolean expectedValue() {
            return false;
        }
    },

    C {
        @Override
        public Flags flagToCheck() {
            return Flags.C;
        }

        @Override
        public boolean expectedValue() {
            return true;
        }
    },

    PO {
        @Override
        public Flags flagToCheck() {
            return Flags.PV;
        }

        @Override
        public boolean expectedValue() {
            return false;
        }
    },

    PE {
        @Override
        public Flags flagToCheck() {
            return Flags.PV;
        }

        @Override
        public boolean expectedValue() {
            return true;
        }
    },

    P {
        @Override
        public Flags flagToCheck() {
            return Flags.S;
        }

        @Override
        public boolean expectedValue() {
            return false;
        }
    },

    M {
        @Override
        public Flags flagToCheck() {
            return Flags.S;
        }

        @Override
        public boolean expectedValue() {
            return true;
        }
    };

    public abstract Flags flagToCheck();
    public abstract boolean expectedValue();
}
