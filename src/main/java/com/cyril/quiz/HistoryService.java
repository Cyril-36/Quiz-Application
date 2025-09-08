package com.cyril.quiz;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HistoryService implements IHistoryService {
    private static final Logger logger = LoggerFactory.getLogger(HistoryService.class);
    private final AppConfig config = AppConfig.getInstance();
    private final String HISTORY_FILE = config.getHistoryFile();
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public List<HistoryEntry> readHistory() {
        try (FileReader reader = new FileReader(HISTORY_FILE)) {
            Type listType = new TypeToken<ArrayList<HistoryEntry>>() {}.getType();
            List<HistoryEntry> history = gson.fromJson(reader, listType);
            return history != null ? history : new ArrayList<>();
        } catch (IOException e) {
            logger.info("History file not found, starting with empty history: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    public void addEntry(HistoryEntry newEntry) {
        List<HistoryEntry> history = readHistory();
        history.add(0, newEntry); // Add new entry to the top of the list
        try (FileWriter writer = new FileWriter(HISTORY_FILE)) {
            gson.toJson(history, writer);
        } catch (IOException e) {
            logger.error("Failed to save history file: {}", e.getMessage(), e);
        }
    }
    // In HistoryService.java, add this new method:
    public List<HistoryEntry> getLeaderboard() {
        List<HistoryEntry> history = readHistory();
        // Sort the list by score in descending order
        history.sort((e1, e2) -> Integer.compare(e2.getScore(), e1.getScore()));
        return history;
    }
}