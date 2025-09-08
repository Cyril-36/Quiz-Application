package com.cyril.quiz;

import javax.swing.*;
import java.awt.*;

public class ShadowPanel extends JPanel {
    public ShadowPanel(LayoutManager layout) {
        super(layout);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        int shadowSize = 8;
        int cornerArc = 15; // Should match your component arc

        // Draw shadow
        g2d.setColor(new Color(0, 0, 0, 50)); // Shadow color
        g2d.fillRoundRect(shadowSize, shadowSize, getWidth() - shadowSize, getHeight() - shadowSize, cornerArc, cornerArc);

        // Draw the actual background on top
        g2d.setColor(getBackground());
        g2d.fillRoundRect(0, 0, getWidth() - shadowSize, getHeight() - shadowSize, cornerArc, cornerArc);

        g2d.dispose();
    }
}