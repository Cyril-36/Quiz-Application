package com.cyril.quiz;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class QuestionPanel extends JPanel {
    public final JLabel progressLabel = new JLabel("Q: 1/10", SwingConstants.LEFT);
    private final JLabel questionLabel = new JLabel("Question will be here.", SwingConstants.CENTER);
    public final JLabel timerLabel = new JLabel("Time: 15s", SwingConstants.RIGHT);
    public final JButton previousButton = new JButton("Previous");
    public final JButton nextButton = new JButton("Next");
    private final ButtonGroup optionsGroup = new ButtonGroup();
    public final JRadioButton[] radioButtons = new JRadioButton[4];
    private final JLabel hintLabel = new JLabel("Tip: Press 1–4 to select an answer", SwingConstants.CENTER);
    private javax.swing.Timer hintTimer;

    public QuestionPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // --- Header Panel ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        progressLabel.setFont(new Font("Arial", Font.BOLD, 14));
        timerLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        headerPanel.add(progressLabel, BorderLayout.WEST);
        headerPanel.add(timerLabel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // --- Question Label (Center) ---
        questionLabel.setFont(new Font("Arial", Font.BOLD, 16));
        add(questionLabel, BorderLayout.CENTER);

        // --- Options Panel (moved to a separate panel for better layout) ---
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(questionLabel, BorderLayout.NORTH);

        JPanel optionsPanel = new JPanel(new GridLayout(4, 1, 5, 5));
        optionsPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        for (int i = 0; i < 4; i++) {
            radioButtons[i] = new JRadioButton();
            radioButtons[i].setFont(new Font("Arial", Font.PLAIN, 14));
            optionsGroup.add(radioButtons[i]);
            optionsPanel.add(radioButtons[i]);
            // Show 1-4 access keys in tooltips
            radioButtons[i].setToolTipText("Press " + (i + 1) + " to select");
        }
        centerPanel.add(optionsPanel, BorderLayout.CENTER);
        hintLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        hintLabel.setForeground(new Color(120, 120, 120));
        hintLabel.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 0));
        hintLabel.setVisible(false);
        centerPanel.add(hintLabel, BorderLayout.SOUTH);
        add(centerPanel, BorderLayout.CENTER);


        // --- Footer with Buttons ---
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.add(previousButton);
        footerPanel.add(nextButton);
        add(footerPanel, BorderLayout.SOUTH);

        // Key bindings: 1-4 select options, Enter -> Next, Backspace -> Previous
        InputMap im = getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        ActionMap am = getActionMap();
        for (int i = 0; i < 4; i++) {
            final int index = i;
            im.put(KeyStroke.getKeyStroke(String.valueOf(i + 1)), "opt" + i);
            am.put("opt" + i, new AbstractAction() {
                @Override public void actionPerformed(java.awt.event.ActionEvent e) {
                    radioButtons[index].setSelected(true);
                }
            });
        }
        im.put(KeyStroke.getKeyStroke("ENTER"), "goNext");
        am.put("goNext", new AbstractAction() {
            @Override public void actionPerformed(java.awt.event.ActionEvent e) { nextButton.doClick(); }
        });
        im.put(KeyStroke.getKeyStroke("BACK_SPACE"), "goPrev");
        am.put("goPrev", new AbstractAction() {
            @Override public void actionPerformed(java.awt.event.ActionEvent e) { previousButton.doClick(); }
        });
    }

    public void setHintVisible(boolean visible) {
        hintLabel.setVisible(visible);
    }

    public void showHintTemporarily(int millis) {
        setHintVisible(true);
        if (hintTimer != null && hintTimer.isRunning()) {
            hintTimer.stop();
        }
        hintTimer = new javax.swing.Timer(millis, e -> {
            setHintVisible(false);
        });
        hintTimer.setRepeats(false);
        hintTimer.start();
    }

    public void displayQuestion(QuestionModel q, int selectedAnswer) {
        questionLabel.setText("<html><div style='text-align: center;'>" + q.getQuestion() + "</div></html>");
        List<String> options = q.getOptions();
        optionsGroup.clearSelection();

        for (int i = 0; i < radioButtons.length; i++) {
            radioButtons[i].setText(options.get(i));
            if (i == selectedAnswer) {
                radioButtons[i].setSelected(true);
            }
        }
    }

    public int getSelectedOption() {
        for (int i = 0; i < radioButtons.length; i++) {
            if (radioButtons[i].isSelected()) return i;
        }
        return -1; // Nothing selected
    }
} // <-- This is the closing brace that was likely missing.
