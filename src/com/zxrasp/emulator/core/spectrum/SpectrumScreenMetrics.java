package com.zxrasp.emulator.core.spectrum;

public interface SpectrumScreenMetrics {

    int HORIZONTAL_BORDER = 64;
    int VERTICAL_BORDER = 48;

    int PIXEL_PER_LINE =    HORIZONTAL_BORDER + 256 + HORIZONTAL_BORDER;
    int SCAN_LINES =        VERTICAL_BORDER + 192 + VERTICAL_BORDER;

}
