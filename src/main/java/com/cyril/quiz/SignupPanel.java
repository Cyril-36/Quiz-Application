package com.cyril.quiz;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class SignupPanel extends JPanel {
    public final JTextField userField = new JTextField(15);
    public final JPasswordField passField = new JPasswordField(15);
    public final JPasswordField confirmPassField = new JPasswordField(15);
    public final JButton signupButton = new JButton("Sign Up");
    public final JButton backToLoginButton = new JButton("Back to Login");

    public SignupPanel() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // --- Title ---
        JLabel titleLabel = new JLabel("Create an Account");
        titleLabel.setFont(new Font("Segoe UI Light", Font.PLAIN, 36));
        gbc.insets = new Insets(15, 15, 15, 15);
        add(titleLabel, gbc);

        // --- Username Field ---
        gbc.gridy++;
        gbc.insets = new Insets(10, 15, 10, 15);
        userField.setPreferredSize(new Dimension(250, 45));
        userField.putClientProperty("JTextField.placeholderText", "Choose a username");
        userField.getAccessibleContext().setAccessibleName("Username");
        TitledBorder userBorder = BorderFactory.createTitledBorder("Username");
        userBorder.setTitleColor(Color.decode("#2864DC"));
        userField.setBorder(userBorder);
        add(userField, gbc);

        // --- Password Field ---
        gbc.gridy++;
        passField.setPreferredSize(new Dimension(250, 45));
        passField.putClientProperty("JTextField.placeholderText", "Create a password");
        passField.getAccessibleContext().setAccessibleName("Password");
        TitledBorder passBorder = BorderFactory.createTitledBorder("Password");
        passBorder.setTitleColor(Color.decode("#2864DC"));
        passField.setBorder(passBorder);
        add(passField, gbc);

        // --- Confirm Password Field ---
        gbc.gridy++;
        confirmPassField.setPreferredSize(new Dimension(250, 45));
        confirmPassField.putClientProperty("JTextField.placeholderText", "Confirm your password");
        confirmPassField.getAccessibleContext().setAccessibleName("Confirm Password");
        TitledBorder confirmPassBorder = BorderFactory.createTitledBorder("Confirm Password");
        confirmPassBorder.setTitleColor(Color.decode("#2864DC"));
        confirmPassField.setBorder(confirmPassBorder);
        add(confirmPassField, gbc);

        // --- Signup Button ---
        gbc.gridy++;
        gbc.insets = new Insets(15, 15, 15, 15);
        signupButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        signupButton.putClientProperty("JButton.buttonType", "roundRect");
        signupButton.setToolTipText("Create account (Enter)");
        signupButton.setMnemonic('U');
        signupButton.setPreferredSize(new Dimension(250, 45));
        signupButton.setBackground(Color.BLACK);
        signupButton.setForeground(Color.WHITE);
        add(signupButton, gbc);

        // --- Back to Login Link ---
        gbc.gridy++;
        gbc.insets = new Insets(0, 15, 15, 15);
        backToLoginButton.setBorderPainted(false);
        backToLoginButton.setOpaque(false);
        backToLoginButton.setContentAreaFilled(false);
        backToLoginButton.setForeground(Color.decode("#2864DC"));
        backToLoginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backToLoginButton.setToolTipText("Back to login (Esc)");
        add(backToLoginButton, gbc);

        // Key bindings: Enter -> signup, Esc -> back to login
        getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("ENTER"), "doSignup");
        getActionMap().put("doSignup", new AbstractAction() {
            @Override public void actionPerformed(java.awt.event.ActionEvent e) { signupButton.doClick(); }
        });
        getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("ESCAPE"), "goBack");
        getActionMap().put("goBack", new AbstractAction() {
            @Override public void actionPerformed(java.awt.event.ActionEvent e) { backToLoginButton.doClick(); }
        });
    }
}
