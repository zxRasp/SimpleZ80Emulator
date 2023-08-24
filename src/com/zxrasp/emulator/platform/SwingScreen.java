package com.zxrasp.emulator.platform;

import com.zxrasp.emulator.core.Screen;
import com.zxrasp.emulator.core.spectrum.SpectrumScreenMetrics;

import javax.swing.*;
import java.awt.*;
import java.awt.image.MemoryImageSource;

public class SwingScreen extends JFrame implements Screen, SpectrumScreenMetrics {

    private final int[] screenBuffer;
    private final MemoryImageSource mis;
    private final Image image;

    public SwingScreen(String title) throws HeadlessException {
        super(title);

        screenBuffer = new int[SCAN_LINES * PIXEL_PER_LINE];
        mis = new MemoryImageSource(PIXEL_PER_LINE, SCAN_LINES, screenBuffer, 0, PIXEL_PER_LINE);
        mis.setAnimated(true);
        image = createImage(mis);

        setSize(PIXEL_PER_LINE * 2, SCAN_LINES * 2);
        setMinimumSize(new Dimension(PIXEL_PER_LINE, SCAN_LINES));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    @Override
    public void paint(Graphics g) {
        g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
    }

    @Override
    public int[] getScreenBuffer() {
        return screenBuffer;
    }

    @Override
    public void updateScreen() {
        mis.newPixels();
    }
}
