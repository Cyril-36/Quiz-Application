package com.cyril.quiz;

import javax.swing.*;
import java.awt.*;

public class LoadingGlassPane extends JComponent {
    private final JPanel panel;
    private final JLabel messageLabel;
    private final JProgressBar progressBar;

    public LoadingGlassPane() {
        setLayout(new GridBagLayout());
        setOpaque(false);

        panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(16, 22, 16, 22));
        panel.setBackground(new Color(0, 0, 0, 170));
        panel.setForeground(Color.WHITE);

        messageLabel = new JLabel("Loading...", SwingConstants.CENTER);
        messageLabel.setForeground(Color.WHITE);
        messageLabel.setFont(messageLabel.getFont().deriveFont(Font.BOLD, 14f));

        progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);

        panel.add(messageLabel, BorderLayout.NORTH);
        panel.add(progressBar, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        add(panel, gbc);

        // Consume all mouse/key events while visible
        enableEvents(AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK | AWTEvent.KEY_EVENT_MASK);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(new Color(0, 0, 0, 100));
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.dispose();
    }

    public void setMessage(String message) {
        messageLabel.setText(message);
    }
}

