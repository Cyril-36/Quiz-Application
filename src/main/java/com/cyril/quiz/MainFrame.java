package com.cyril.quiz;

import javax.swing.*;
import java.awt.*;

/**
 * The main window of the application.
 * It uses a CardLayout to switch between different screens (panels).
 */
public class MainFrame extends JFrame {
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel mainPanel = new JPanel(cardLayout);
    private final LoadingGlassPane loading = new LoadingGlassPane();

    public MainFrame() {
        AppConfig config = AppConfig.getInstance();
        setTitle("Java Swing Quiz Application");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(config.getWindowWidth(), config.getWindowHeight());
        setLocationRelativeTo(null); // Center the window

        add(mainPanel);
        setGlassPane(loading);
    }

    /**
     * Adds a screen (panel) to the main card layout.
     * @param panel The panel to add.
     * @param name The name to identify the panel.
     */
    public void addScreen(JPanel panel, String name) {
        mainPanel.add(panel, name);
    }

    /**
     * Shows a specific screen by its name.
     * @param name The name of the panel to show.
     */
    public void showScreen(String name) {
        cardLayout.show(mainPanel, name);
    }

    public void showLoading(String message) {
        loading.setMessage(message != null ? message : "Loading...");
        loading.setVisible(true);
    }

    public void hideLoading() {
        loading.setVisible(false);
    }
}
