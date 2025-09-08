package com.cyril.quiz;

import java.util.List;

public interface IHistoryService {
    List<HistoryEntry> readHistory();
    void addEntry(HistoryEntry entry);
    List<HistoryEntry> getLeaderboard();
}