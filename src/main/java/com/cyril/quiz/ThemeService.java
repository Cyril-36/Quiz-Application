package com.cyril.quiz;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;
import java.awt.Color;
import java.awt.Font;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThemeService {
    private static final Logger logger = LoggerFactory.getLogger(ThemeService.class);
    private static final String SETTINGS_FILE = "settings.properties";
    private enum ThemeMode { LIGHT, DARK, CUSTOM }
    private ThemeMode mode = ThemeMode.LIGHT;
    private Color accent = Color.decode("#2864DC");

    public void initializeTheme() {
        // Base UI props
        UIManager.put("defaultFont", new Font("Segoe UI", Font.PLAIN, 15));
        UIManager.put("Component.focusWidth", 1);
        UIManager.put("Component.innerFocusWidth", 0);
        UIManager.put("Component.arc", 12);
        UIManager.put("Button.arc", 999);

        // Load saved preferences
        loadThemePreference();
        applyTheme();
    }

    public void toggleTheme(JFrame mainFrame) {
        // Cycle through LIGHT → DARK → CUSTOM → LIGHT
        switch (mode) {
            case LIGHT: mode = ThemeMode.DARK; break;
            case DARK: mode = ThemeMode.CUSTOM; break;
            default: mode = ThemeMode.LIGHT; break;
        }
        applyTheme();
        SwingUtilities.updateComponentTreeUI(mainFrame);
        saveThemePreference();
    }

    private void applyTheme() {
        try {
            if (mode == ThemeMode.DARK) {
                UIManager.setLookAndFeel(new FlatDarkLaf());
            } else {
                UIManager.setLookAndFeel(new FlatLightLaf());
            }
            
            // Apply accent color and proper button styling
            UIManager.put("Component.focusColor", accent);
            UIManager.put("TabbedPane.selectedBackground", accent);
            
            if (mode == ThemeMode.CUSTOM) {
                // Custom theme - keep buttons black with no hover effects
                UIManager.put("Button.background", Color.BLACK);
                UIManager.put("Button.hoverBackground", Color.BLACK);
                UIManager.put("Button.pressedBackground", Color.BLACK);
                UIManager.put("Button.focusedBackground", Color.BLACK);
                UIManager.put("Button.foreground", Color.WHITE);
            } else {
                // Reset to default for light/dark themes
                UIManager.put("Button.background", null);
                UIManager.put("Button.hoverBackground", null);
                UIManager.put("Button.pressedBackground", null);
                UIManager.put("Button.focusedBackground", null);
                UIManager.put("Button.foreground", null);
            }
        } catch (UnsupportedLookAndFeelException e) {
            logger.error("Failed to set Look and Feel: {}", e.getMessage(), e);
        }
    }

    private void saveThemePreference() {
        Properties props = new Properties();
        props.setProperty("theme", mode.name().toLowerCase());
        props.setProperty("accent", String.format("#%06X", (0xFFFFFF & accent.getRGB())));
        try (FileOutputStream out = new FileOutputStream(SETTINGS_FILE)) {
            props.store(out, "Application Settings");
        } catch (IOException e) {
            logger.warn("Failed to save theme settings: {}", e.getMessage());
        }
    }

    private void loadThemePreference() {
        Properties props = new Properties();
        try (FileInputStream in = new FileInputStream(SETTINGS_FILE)) {
            props.load(in);
            String theme = props.getProperty("theme", "light").toUpperCase();
            try {
                mode = ThemeMode.valueOf(theme);
            } catch (IllegalArgumentException ex) {
                mode = ThemeMode.LIGHT;
            }
            String accentHex = props.getProperty("accent", "#2864DC");
            try {
                accent = Color.decode(accentHex);
            } catch (Exception ex) {
                accent = Color.decode("#2864DC");
            }
        } catch (IOException e) {
            // Defaults already set
            mode = ThemeMode.LIGHT;
            accent = Color.decode("#2864DC");
        }
    }
}
