package com.cyril.quiz;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

public class HomeScreenPanel extends JPanel {
    public final JComboBox<String> topicComboBox = new JComboBox<>();
    public final JButton startQuizButton = new JButton("Start Quiz");
    public final JButton historyButton = new JButton("View History");
    public final JButton toggleThemeButton = new JButton("Toggle Theme");
    private Timer loadingDotsTimer;
    private String baseLoadingText = "Loading";
    private final Map<JButton, Timer> buttonLoadingTimers = new HashMap<>();
    private final Map<JButton, String> buttonBaseText = new HashMap<>();

    public HomeScreenPanel() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;

        // --- Title ---
        gbc.gridy = 0;
        JLabel titleLabel = new JLabel("<html>Welcome to <font color='#2864DC'>InQuiz</font>!</html>");
        titleLabel.setFont(new Font("Segoe UI Light", Font.BOLD, 32));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(titleLabel, gbc);

        // --- Topic Dropdown ---
        gbc.gridy = 1;
        topicComboBox.setBorder(BorderFactory.createTitledBorder("Choose a Topic"));
        topicComboBox.setPreferredSize(new Dimension(250, 50));
        add(topicComboBox, gbc);

        // --- Start Quiz Button (Primary Action) ---
        gbc.gridy = 2;
        startQuizButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        startQuizButton.setBackground(Color.BLACK);
        startQuizButton.setForeground(Color.WHITE);
        startQuizButton.setPreferredSize(new Dimension(250, 45));
        startQuizButton.putClientProperty("JButton.buttonType", "roundRect");
        startQuizButton.setToolTipText("Start quiz (Alt+Q)");
        startQuizButton.setMnemonic('Q');
        add(startQuizButton, gbc);

        // Add the hover animation for the primary button
        startQuizButton.addMouseListener(new java.awt.event.MouseAdapter() {
            // Store the original border to restore it later
            Border originalBorder = startQuizButton.getBorder();

            public void mouseEntered(java.awt.event.MouseEvent evt) {
                // On hover, create a new slightly thicker border to "lift" the button
                // This creates a subtle visual feedback effect
                startQuizButton.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Color.GRAY, 1),
                        BorderFactory.createEmptyBorder(4, 4, 4, 4)
                ));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                // When the mouse leaves, restore the original border
                startQuizButton.setBorder(originalBorder);
            }
        });


        // --- Secondary Buttons in a separate panel ---
        gbc.gridy = 3;
        JPanel secondaryButtonPanel = new JPanel(new GridLayout(1, 2, 10, 10));

        // Style secondary buttons to use the default theme look but remain rounded
        historyButton.putClientProperty("JButton.buttonType", "roundRect");
        historyButton.setToolTipText("View history (Alt+H)");
        historyButton.setMnemonic('H');
        toggleThemeButton.putClientProperty("JButton.buttonType", "roundRect");
        toggleThemeButton.setToolTipText("Toggle theme (Alt+T)");
        toggleThemeButton.setMnemonic('T');

        secondaryButtonPanel.add(historyButton);
        secondaryButtonPanel.add(toggleThemeButton);
        add(secondaryButtonPanel, gbc);
    }

    public void startLoadingAnimation() {
        stopLoadingAnimation(null); // ensure no duplicate timers
        startQuizButton.setText(baseLoadingText);
        loadingDotsTimer = new Timer(400, new ActionListener() {
            int dots = 0;
            @Override public void actionPerformed(ActionEvent e) {
                StringBuilder sb = new StringBuilder(baseLoadingText);
                for (int i = 0; i < dots; i++) sb.append('.');
                startQuizButton.setText(sb.toString());
                dots = (dots + 1) % 4;
            }
        });
        loadingDotsTimer.start();
    }

    public void stopLoadingAnimation(String restoreText) {
        if (loadingDotsTimer != null) {
            loadingDotsTimer.stop();
            loadingDotsTimer = null;
        }
        if (restoreText != null) {
            startQuizButton.setText(restoreText);
        }
    }

    public void startButtonLoading(JButton button, String baseText) {
        stopButtonLoading(button, null);
        buttonBaseText.put(button, baseText);
        button.setText(baseText);
        Timer t = new Timer(400, new ActionListener() {
            int dots = 0;
            @Override public void actionPerformed(ActionEvent e) {
                StringBuilder sb = new StringBuilder(baseText);
                for (int i = 0; i < dots; i++) sb.append('.');
                button.setText(sb.toString());
                dots = (dots + 1) % 4;
            }
        });
        buttonLoadingTimers.put(button, t);
        t.start();
    }

    public void stopButtonLoading(JButton button, String restoreText) {
        Timer t = buttonLoadingTimers.remove(button);
        if (t != null) t.stop();
        if (restoreText != null) button.setText(restoreText);
        buttonBaseText.remove(button);
    }
}
