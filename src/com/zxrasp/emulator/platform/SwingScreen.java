package com.zxrasp.emulator.platform;

import com.zxrasp.emulator.core.Screen;

import javax.swing.*;
import java.awt.*;
import java.awt.image.MemoryImageSource;

public class SwingScreen extends JFrame implements Screen {

    private int[] screenBuffer;
    private MemoryImageSource mis;
    private Image image;

    public SwingScreen(String title) throws HeadlessException {
        super(title);

        screenBuffer = new int[448 * 312];
        mis = new MemoryImageSource(448, 312, screenBuffer, 0, 448);
        mis.setAnimated(true);
        image = createImage(mis);

        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    @Override
    public void paint(Graphics g) {
        g.drawImage(image, 176, 144, this);
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
