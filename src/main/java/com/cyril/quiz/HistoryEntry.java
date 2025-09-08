package com.cyril.quiz;

public class HistoryEntry {
    private String date;
    private String topic;
    private int score;
    private String username; // New field

    // Updated constructor
    public HistoryEntry(String date, String topic, int score, String username) {
        this.date = date;
        this.topic = topic;
        this.score = score;
        this.username = username;
    }

    // Getters
    public String getDate() { return date; }
    public String getTopic() { return topic; }
    public int getScore() { return score; }
    public String getUsername() { return username; } // New getter
}