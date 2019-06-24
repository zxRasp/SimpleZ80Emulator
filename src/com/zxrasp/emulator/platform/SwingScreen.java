package com.zxrasp.emulator.platform;

import com.zxrasp.emulator.core.Screen;
import com.zxrasp.emulator.core.spectrum.SpectrumScreenMetrics;

import javax.swing.*;
import java.awt.*;
import java.awt.image.MemoryImageSource;

public class SwingScreen extends JFrame implements Screen, SpectrumScreenMetrics {

    private int[] screenBuffer;
    private MemoryImageSource mis;
    private Image image;

    private int screenX, screenY;

    public SwingScreen(String title, Dimension windowSize) throws HeadlessException {
        super(title);

        screenBuffer = new int[SCAN_LINES * PIXEL_PER_LINE];
        mis = new MemoryImageSource(PIXEL_PER_LINE, SCAN_LINES, screenBuffer, 0, PIXEL_PER_LINE);
        mis.setAnimated(true);
        image = createImage(mis);

        screenX = (windowSize.width - PIXEL_PER_LINE) / 2;
        screenY = (windowSize.height - SCAN_LINES) / 2;

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
