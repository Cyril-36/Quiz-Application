package com.cyril.quiz;

import javax.swing.SwingUtilities;

public class QuizApplication {
    public static void main(String[] args) {
        // Initialize theme service first
        ThemeService themeService = new ThemeService();
        themeService.initializeTheme();

        SwingUtilities.invokeLater(() -> {
            // --- Services ---
            AuthenticationService authService = new AuthenticationService();
            APIClient apiClient = new APIClient();
            HistoryService historyService = new HistoryService();

            // --- UI Components ---
            MainFrame mainFrame = new MainFrame();
            LoginPanel loginPanel = new LoginPanel();
            SignupPanel signupPanel = new SignupPanel();
            HomeScreenPanel homeScreen = new HomeScreenPanel();
            QuestionPanel questionPanel = new QuestionPanel();
            ResultPanel resultPanel = new ResultPanel();
            HistoryPanel historyPanel = new HistoryPanel();
            LeaderboardPanel leaderboardPanel = new LeaderboardPanel(); // New panel

            // --- Add all panels to the main frame ---
            mainFrame.addScreen(loginPanel, "LOGIN");
            mainFrame.addScreen(signupPanel, "SIGNUP");
            mainFrame.addScreen(homeScreen, "HOME");
            mainFrame.addScreen(questionPanel, "QUIZ");
            mainFrame.addScreen(resultPanel, "RESULTS");
            mainFrame.addScreen(historyPanel, "HISTORY");
            mainFrame.addScreen(leaderboardPanel, "LEADERBOARD"); // New screen

            // --- Create Controller to link everything ---
            new QuizController(authService, apiClient, historyService, themeService, mainFrame, loginPanel, signupPanel, homeScreen, questionPanel, resultPanel, historyPanel, leaderboardPanel);

            // --- Show the first screen ---
            mainFrame.showScreen("LOGIN");
            mainFrame.setVisible(true);
        });
    }
}