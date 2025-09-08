package com.cyril.quiz;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class AppConfig {
    private static final String CONFIG_FILE = "config.properties";
    private static AppConfig instance;
    private final Properties properties;
    
    private AppConfig() {
        properties = new Properties();
        loadConfig();
    }
    
    public static AppConfig getInstance() {
        if (instance == null) {
            instance = new AppConfig();
        }
        return instance;
    }
    
    private void loadConfig() {
        try (FileInputStream fis = new FileInputStream(CONFIG_FILE)) {
            properties.load(fis);
        } catch (IOException e) {
            // Use default values if config file not found
            setDefaults();
        }
    }
    
    private void setDefaults() {
        properties.setProperty("quiz.timer.duration", "15");
        properties.setProperty("quiz.questions.count", "10");
        properties.setProperty("files.users", "users.json");
        properties.setProperty("files.history", "history.json");
        properties.setProperty("sounds.correct", "sounds/correct.wav");
        properties.setProperty("sounds.wrong", "sounds/wrong.wav");
        properties.setProperty("password.min.length", "3");
        properties.setProperty("ui.window.width", "800");
        properties.setProperty("ui.window.height", "600");
        properties.setProperty("http.connect.timeout.seconds", "5");
        properties.setProperty("http.request.timeout.seconds", "10");
        properties.setProperty("ui.loading.fetchQuestions", "Fetching questions...");
        properties.setProperty("ui.loading.history", "Loading history...");
        properties.setProperty("ui.loading.leaderboard", "Loading leaderboard...");
    }
    
    public int getTimerDuration() {
        return Integer.parseInt(properties.getProperty("quiz.timer.duration", "15"));
    }
    
    public int getQuestionsCount() {
        return Integer.parseInt(properties.getProperty("quiz.questions.count", "10"));
    }
    
    public String getUsersFile() {
        return properties.getProperty("files.users", "users.json");
    }
    
    public String getHistoryFile() {
        return properties.getProperty("files.history", "history.json");
    }
    
    public String getCorrectSoundFile() {
        return properties.getProperty("sounds.correct", "sounds/correct.wav");
    }
    
    public String getWrongSoundFile() {
        return properties.getProperty("sounds.wrong", "sounds/wrong.wav");
    }
    
    public int getPasswordMinLength() {
        return Integer.parseInt(properties.getProperty("password.min.length", "3"));
    }
    
    public int getWindowWidth() {
        return Integer.parseInt(properties.getProperty("ui.window.width", "800"));
    }
    
    public int getWindowHeight() {
        return Integer.parseInt(properties.getProperty("ui.window.height", "600"));
    }

    public int getHttpConnectTimeoutSeconds() {
        return Integer.parseInt(properties.getProperty("http.connect.timeout.seconds", "5"));
    }

    public int getHttpRequestTimeoutSeconds() {
        return Integer.parseInt(properties.getProperty("http.request.timeout.seconds", "10"));
    }

    public String getLoadingMessageFetchQuestions() {
        return properties.getProperty("ui.loading.fetchQuestions", "Fetching questions...");
    }

    public String getLoadingMessageHistory() {
        return properties.getProperty("ui.loading.history", "Loading history...");
    }

    public String getLoadingMessageLeaderboard() {
        return properties.getProperty("ui.loading.leaderboard", "Loading leaderboard...");
    }
}
