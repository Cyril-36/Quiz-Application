package com.cyril.quiz;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class HistoryPanel extends JPanel {
    public final JButton backButton = new JButton("Back to Home");
    private final DefaultTableModel tableModel;

    public HistoryPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Table setup
        String[] columnNames = {"Date", "Topic", "Score"};
        tableModel = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        add(scrollPane, BorderLayout.CENTER);

        // Footer with back button
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footerPanel.add(backButton);
        add(footerPanel, BorderLayout.SOUTH);
    }

    public void setHistoryData(List<HistoryEntry> history) {
        // Clear old data
        tableModel.setRowCount(0);
        // Add new data
        for (HistoryEntry entry : history) {
            Object[] row = {entry.getDate(), entry.getTopic(), entry.getScore()};
            tableModel.addRow(row);
        }
    }
}