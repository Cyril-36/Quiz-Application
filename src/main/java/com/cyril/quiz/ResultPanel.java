package com.cyril.quiz;

import javax.swing.*;
import java.awt.*;

public class ResultPanel extends JPanel {
    public final JLabel scoreLabel = new JLabel();
    public final JButton retryButton = new JButton("Retry Quiz");
    public final JButton homeButton = new JButton("Back to Home");
    public final JButton leaderboardButton = new JButton("Leaderboard"); // Added button
    private final java.util.Map<JButton, javax.swing.Timer> buttonLoadingTimers = new java.util.HashMap<>();

    public ResultPanel() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;

        JLabel titleLabel = new JLabel("Quiz Completed!");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        gbc.gridy = 0;
        add(titleLabel, gbc);

        scoreLabel.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridy = 1;
        add(scoreLabel, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.add(retryButton);
        buttonPanel.add(homeButton);
        buttonPanel.add(leaderboardButton); // Added button
        gbc.gridy = 3;
        add(buttonPanel, gbc);
    }

    public void setScore(int score, int totalQuestions) {
        scoreLabel.setText("Your Score: " + score + " / " + totalQuestions);
    }

    public void startButtonLoading(JButton button, String baseText) {
        stopButtonLoading(button, null);
        button.setText(baseText);
        javax.swing.Timer t = new javax.swing.Timer(400, new java.awt.event.ActionListener() {
            int dots = 0;
            @Override public void actionPerformed(java.awt.event.ActionEvent e) {
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
        javax.swing.Timer t = buttonLoadingTimers.remove(button);
        if (t != null) t.stop();
        if (restoreText != null) button.setText(restoreText);
    }
}
