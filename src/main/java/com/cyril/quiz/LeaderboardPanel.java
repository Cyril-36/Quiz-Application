package com.cyril.quiz;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class LeaderboardPanel extends JPanel {
    public final JButton backButton = new JButton("Back to Home");
    private final DefaultTableModel tableModel;

    public LeaderboardPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Leaderboard", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        String[] columnNames = {"Rank", "Username", "Topic", "Score"};
        tableModel = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footerPanel.add(backButton);
        add(footerPanel, BorderLayout.SOUTH);
    }

    public void setLeaderboardData(List<HistoryEntry> leaderboard) {
        tableModel.setRowCount(0);
        int rank = 1;
        for (HistoryEntry entry : leaderboard) {
            Object[] row = {rank++, entry.getUsername(), entry.getTopic(), entry.getScore()};
            tableModel.addRow(row);
        }
    }
}