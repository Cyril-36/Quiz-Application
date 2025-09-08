package com.cyril.quiz;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class LoginPanel extends JPanel {
    public final JTextField userField = new JTextField(15);
    public final JPasswordField passField = new JPasswordField(15);
    public final JButton loginButton = new JButton("Continue");
    public final JButton signupButton = new JButton("Sign up");

    public LoginPanel() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(10, 15, 10, 15); // Adjusted padding
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // --- Title ---
        JLabel titleLabel = new JLabel("Welcome back");
        titleLabel.setFont(new Font("Segoe UI Light", Font.PLAIN, 36));
        gbc.insets = new Insets(15, 15, 15, 15); // More padding for title
        add(titleLabel, gbc);

        // --- Username Field ---
        gbc.gridy++;
        gbc.insets = new Insets(10, 15, 10, 15); // Reset padding
        userField.setPreferredSize(new Dimension(250, 45));
        userField.putClientProperty("JTextField.placeholderText", "Enter username");
        userField.getAccessibleContext().setAccessibleName("Username");
        TitledBorder userBorder = BorderFactory.createTitledBorder("Username");
        userBorder.setTitleColor(Color.decode("#2864DC"));
        userField.setBorder(userBorder);
        add(userField, gbc);

        // --- Password Field ---
        gbc.gridy++;
        passField.setPreferredSize(new Dimension(250, 45));
        passField.putClientProperty("JTextField.placeholderText", "Enter password");
        passField.getAccessibleContext().setAccessibleName("Password");
        TitledBorder passBorder = BorderFactory.createTitledBorder("Password");
        passBorder.setTitleColor(Color.decode("#2864DC"));
        passField.setBorder(passBorder);
        add(passField, gbc);

        // --- Continue Button (Pill-shaped) ---
        gbc.gridy++;
        gbc.insets = new Insets(15, 15, 15, 15); // More padding for button
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        loginButton.putClientProperty("JButton.buttonType", "roundRect");
        loginButton.setToolTipText("Sign in (Enter)");
        loginButton.setMnemonic('C');
        loginButton.setPreferredSize(new Dimension(250, 45));
        loginButton.setBackground(Color.BLACK);
        loginButton.setForeground(Color.WHITE);
        add(loginButton, gbc);

        // --- Sign Up Link ---
        gbc.gridy++;
        gbc.insets = new Insets(0, 15, 15, 15); // Less padding for link
        JPanel signupPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        signupPanel.add(new JLabel("Don't have an account?"));

        signupButton.setBorderPainted(false);
        signupButton.setOpaque(false);
        signupButton.setContentAreaFilled(false);
        signupButton.setForeground(Color.decode("#2864DC"));
        signupButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        signupButton.setToolTipText("Create a new account (Alt+S)");
        signupButton.setMnemonic('S');
        signupPanel.add(signupButton);
        add(signupPanel, gbc);

        // Key bindings: Enter -> login, Esc -> focus username
        getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("ENTER"), "doLogin");
        getActionMap().put("doLogin", new AbstractAction() {
            @Override public void actionPerformed(java.awt.event.ActionEvent e) { loginButton.doClick(); }
        });
        getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("ESCAPE"), "focusUser");
        getActionMap().put("focusUser", new AbstractAction() {
            @Override public void actionPerformed(java.awt.event.ActionEvent e) { userField.requestFocusInWindow(); }
        });
    }
}
