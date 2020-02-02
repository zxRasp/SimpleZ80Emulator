package com.zxrasp.emulator.core.z80.z80internals;

public enum RegisterNames {

    /* General purpose */

    /* com.zxrasp.emulator.Main set */

    // 8-bit
    A, F, B, C, D, E, H, L,

    //16-bit pairs
    AF, BC, DE, HL,

    /* Alternative set */

    // 8-bit
    A_, F_, B_, C_, D_, E_, H_, L_,

    //16-bit pairs
    AF_, BC_, DE_, HL_,

    /* Special */
    PC, SP, IX, IY, I, R
}
