package com.cyril.quiz;

import javax.swing.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QuizController {
    private static final Logger logger = LoggerFactory.getLogger(QuizController.class);
    private final AppConfig config = AppConfig.getInstance();
    
    // Services
    private final IAuthenticationService authService;
    private final IAPIClient apiClient;
    private final IHistoryService historyService;
    private final ThemeService themeService;

    // UI Components
    private final MainFrame mainFrame;
    private final LoginPanel loginPanel;
    private final SignupPanel signupPanel;
    private final HomeScreenPanel homeScreenPanel;
    private final QuestionPanel questionPanel;
    private final ResultPanel resultPanel;
    private final HistoryPanel historyPanel;
    private final LeaderboardPanel leaderboardPanel;

    // Quiz State
    private List<QuestionModel> questions;
    private int currentQuestionIndex;
    private int score;
    private String currentTopic;
    private String currentUsername;
    private Timer timer;
    private int timeLeft;
    private List<Integer> userAnswers;
    private boolean answerHintShown;

    public QuizController(IAuthenticationService authService, IAPIClient apiClient, IHistoryService historyService, ThemeService themeService, MainFrame mainFrame, LoginPanel loginPanel, SignupPanel signupPanel, HomeScreenPanel homeScreenPanel, QuestionPanel questionPanel, ResultPanel resultPanel, HistoryPanel historyPanel, LeaderboardPanel leaderboardPanel) {
        this.authService = authService;
        this.apiClient = apiClient;
        this.historyService = historyService;
        this.themeService = themeService;
        this.mainFrame = mainFrame;
        this.loginPanel = loginPanel;
        this.signupPanel = signupPanel;
        this.homeScreenPanel = homeScreenPanel;
        this.questionPanel = questionPanel;
        this.resultPanel = resultPanel;
        this.historyPanel = historyPanel;
        this.leaderboardPanel = leaderboardPanel;
        this.timer = new Timer(1000, e -> updateTimer());
        attachListeners();
    }

    private void attachListeners() {
        loginPanel.loginButton.addActionListener(e -> handleLogin());
        loginPanel.signupButton.addActionListener(e -> mainFrame.showScreen("SIGNUP"));
        signupPanel.signupButton.addActionListener(e -> handleSignup());
        signupPanel.backToLoginButton.addActionListener(e -> mainFrame.showScreen("LOGIN"));
        homeScreenPanel.startQuizButton.addActionListener(e -> startQuiz());
        homeScreenPanel.historyButton.addActionListener(e -> showHistory());
        homeScreenPanel.toggleThemeButton.addActionListener(e -> themeService.toggleTheme(mainFrame));
        questionPanel.previousButton.addActionListener(e -> handlePreviousQuestion());
        questionPanel.nextButton.addActionListener(e -> handleNextQuestion());
        resultPanel.retryButton.addActionListener(e -> startQuiz());
        resultPanel.homeButton.addActionListener(e -> showHomeScreen());
        resultPanel.leaderboardButton.addActionListener(e -> showLeaderboard());
        historyPanel.backButton.addActionListener(e -> showHomeScreen());
        leaderboardPanel.backButton.addActionListener(e -> showHomeScreen());
    }

    private void handleNextQuestion() {
        timer.stop();
        userAnswers.set(currentQuestionIndex, questionPanel.getSelectedOption());

        // Check the answer and play a sound
        if (questionPanel.getSelectedOption() == questions.get(currentQuestionIndex).getAnswerIndex()) {
            playSound(config.getCorrectSoundFile()); // Play correct sound
        } else {
            playSound(config.getWrongSoundFile()); // Play wrong sound
        }

        if (currentQuestionIndex < questions.size() - 1) {
            currentQuestionIndex++;
            loadQuestion();
        } else {
            calculateAndShowResults();
        }
    }

    private void calculateAndShowResults() {
        score = 0;
        for (int i = 0; i < questions.size(); i++) {
            if (userAnswers.get(i) == questions.get(i).getAnswerIndex()) {
                score++;
            }
        }
        animateAndShowResults();
    }

    private void animateAndShowResults() {
        resultPanel.setScore(0, questions.size());
        mainFrame.showScreen("RESULTS");

        Timer animationTimer = new Timer(50, null);
        animationTimer.addActionListener(e -> {
            String[] parts = resultPanel.scoreLabel.getText().split(" ");
            int currentDisplayScore = Integer.parseInt(parts[2]);
            if (currentDisplayScore < score) {
                resultPanel.setScore(currentDisplayScore + 1, questions.size());
            } else {
                animationTimer.stop();
            }
        });
        animationTimer.start();

        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date());
        HistoryEntry entry = new HistoryEntry(date, currentTopic, score, this.currentUsername);
        
        // Save history entry in background
        SwingUtilities.invokeLater(() -> {
            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() {
                    historyService.addEntry(entry);
                    return null;
                }
                
                @Override
                protected void done() {
                    logger.debug("History entry saved successfully");
                }
            }.execute();
        });
    }

    private void playSound(String soundFilePath) {
        try {
            java.net.URL url = this.getClass().getResource("/" + soundFilePath);
            if (url != null) {
                javax.sound.sampled.AudioInputStream audioStream = javax.sound.sampled.AudioSystem.getAudioInputStream(url);
                javax.sound.sampled.Clip clip = javax.sound.sampled.AudioSystem.getClip();
                clip.open(audioStream);
                clip.start();
            } else {
                logger.warn("Sound file not found: {}", soundFilePath);
            }
        } catch (javax.sound.sampled.UnsupportedAudioFileException e) {
            logger.error("Unsupported audio format for sound file {}: {}", soundFilePath, e.getMessage(), e);
        } catch (javax.sound.sampled.LineUnavailableException e) {
            logger.error("Audio line unavailable for sound file {}: {}", soundFilePath, e.getMessage(), e);
        } catch (java.io.IOException e) {
            logger.error("IO error reading sound file {}: {}", soundFilePath, e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Unexpected error playing sound file {}: {}", soundFilePath, e.getMessage(), e);
        }
    }

    // --- All other methods remain the same ---
    private void startQuiz() {
        currentTopic = (String) homeScreenPanel.topicComboBox.getSelectedItem();
        if (currentTopic == null || currentTopic.trim().isEmpty()) {
            logger.warn("No topic selected for quiz");
            return;
        }

        final String originalStartText = homeScreenPanel.startQuizButton.getText();
        homeScreenPanel.startQuizButton.setEnabled(false);
        homeScreenPanel.startLoadingAnimation();
        mainFrame.showLoading(config.getLoadingMessageFetchQuestions());

        new SwingWorker<List<QuestionModel>, Void>() {
            @Override
            protected List<QuestionModel> doInBackground() {
                try {
                    return apiClient.getQuestions(currentTopic);
                } catch (ApiException e) {
                    logger.warn("Falling back to local questions due to API error: {}", e.getMessage());
                    return loadFallbackQuestions();
                }
            }

            @Override
            protected void done() {
                try {
                    questions = get();
                } catch (Exception e) {
                    logger.error("Failed to load questions: {}", e.getMessage(), e);
                    questions = loadFallbackQuestions();
                }

                currentQuestionIndex = 0;
                score = 0;
                userAnswers = new ArrayList<>(Collections.nCopies(questions.size(), -1));
                if (questions != null && !questions.isEmpty()) {
                    loadQuestion();
                    mainFrame.showScreen("QUIZ");
                } else {
                    JOptionPane.showMessageDialog(mainFrame, "No questions available.", "Error", JOptionPane.ERROR_MESSAGE);
                }

                homeScreenPanel.startQuizButton.setEnabled(true);
                homeScreenPanel.stopLoadingAnimation(originalStartText);
                mainFrame.hideLoading();
            }
        }.execute();
    }
    private void loadQuestion() {
        if (questions == null || questions.isEmpty() || currentQuestionIndex < 0 || currentQuestionIndex >= questions.size()) {
            logger.error("Invalid question state: questions={}, currentIndex={}", questions != null ? questions.size() : "null", currentQuestionIndex);
            return;
        }
        
        questionPanel.progressLabel.setText("Question " + (currentQuestionIndex + 1) + " of " + questions.size());
        int selectedAnswer = userAnswers.get(currentQuestionIndex);
        questionPanel.displayQuestion(questions.get(currentQuestionIndex), selectedAnswer);
        questionPanel.previousButton.setEnabled(currentQuestionIndex > 0);
        questionPanel.nextButton.setText(currentQuestionIndex == questions.size() - 1 ? "Finish" : "Next");
        if (!answerHintShown && currentQuestionIndex == 0) {
            questionPanel.showHintTemporarily(3000);
            answerHintShown = true;
        } else {
            questionPanel.setHintVisible(false);
        }
        resetTimer();
    }
    private void handlePreviousQuestion() {
        timer.stop();
        userAnswers.set(currentQuestionIndex, questionPanel.getSelectedOption());
        if (currentQuestionIndex > 0) {
            currentQuestionIndex--;
            loadQuestion();
        }
    }
    private void showHistory() {
        SwingUtilities.invokeLater(() -> {
            mainFrame.showLoading(config.getLoadingMessageHistory());
            final String original = homeScreenPanel.historyButton.getText();
            homeScreenPanel.historyButton.setEnabled(false);
            homeScreenPanel.startButtonLoading(homeScreenPanel.historyButton, "Loading");
            new SwingWorker<List<HistoryEntry>, Void>() {
                @Override
                protected List<HistoryEntry> doInBackground() {
                    return historyService.readHistory();
                }
                
                @Override
                protected void done() {
                    try {
                        List<HistoryEntry> history = get();
                        historyPanel.setHistoryData(history);
                        mainFrame.showScreen("HISTORY");
                    } catch (Exception e) {
                        logger.error("Failed to load history: {}", e.getMessage(), e);
                        JOptionPane.showMessageDialog(mainFrame, "Failed to load history.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                    mainFrame.hideLoading();
                    homeScreenPanel.stopButtonLoading(homeScreenPanel.historyButton, original);
                    homeScreenPanel.historyButton.setEnabled(true);
                }
            }.execute();
        });
    }
    private void showLeaderboard() {
        SwingUtilities.invokeLater(() -> {
            mainFrame.showLoading(config.getLoadingMessageLeaderboard());
            final String original = resultPanel.leaderboardButton.getText();
            resultPanel.leaderboardButton.setEnabled(false);
            resultPanel.startButtonLoading(resultPanel.leaderboardButton, "Loading");
            new SwingWorker<List<HistoryEntry>, Void>() {
                @Override
                protected List<HistoryEntry> doInBackground() {
                    return historyService.getLeaderboard();
                }
                
                @Override
                protected void done() {
                    try {
                        List<HistoryEntry> leaderboard = get();
                        leaderboardPanel.setLeaderboardData(leaderboard);
                        mainFrame.showScreen("LEADERBOARD");
                    } catch (Exception e) {
                        logger.error("Failed to load leaderboard: {}", e.getMessage(), e);
                        JOptionPane.showMessageDialog(mainFrame, "Failed to load leaderboard.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                    mainFrame.hideLoading();
                    resultPanel.stopButtonLoading(resultPanel.leaderboardButton, original);
                    resultPanel.leaderboardButton.setEnabled(true);
                }
            }.execute();
        });
    }
    private List<QuestionModel> loadFallbackQuestions() {
        List<QuestionModel> fallback = new ArrayList<>();
        fallback.add(new QuestionModel("What is a fallback question?", List.of("A", "B", "C", "D"), 0));
        return fallback;
    }
    private void resetTimer() {
        timeLeft = config.getTimerDuration();
        questionPanel.timerLabel.setText("Time: " + timeLeft + "s");
        timer.restart();
    }
    private void updateTimer() {
        timeLeft--;
        questionPanel.timerLabel.setText("Time: " + timeLeft + "s");
        if (timeLeft <= 0) {
            handleNextQuestion();
        }
    }
    private void handleLogin() {
        String username = loginPanel.userField.getText();
        char[] pw = loginPanel.passField.getPassword();
        String password = new String(pw);
        
        if (username == null || username.trim().isEmpty()) {
            loginPanel.userField.putClientProperty("JComponent.outline", "error");
            JOptionPane.showMessageDialog(mainFrame, "Please enter a username.", "Login Error", JOptionPane.ERROR_MESSAGE);
            SwingUtilities.invokeLater(() -> loginPanel.userField.putClientProperty("JComponent.outline", null));
            return;
        }
        
        if (password == null || password.isEmpty()) {
            loginPanel.passField.putClientProperty("JComponent.outline", "error");
            JOptionPane.showMessageDialog(mainFrame, "Please enter a password.", "Login Error", JOptionPane.ERROR_MESSAGE);
            SwingUtilities.invokeLater(() -> loginPanel.passField.putClientProperty("JComponent.outline", null));
            return;
        }
        
        if (authService.login(username, password)) {
            this.currentUsername = username.trim();
            showHomeScreen();
        } else {
            JOptionPane.showMessageDialog(mainFrame, "Invalid username or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
        Arrays.fill(pw, '\0');
    }
    private void handleSignup() {
        String username = signupPanel.userField.getText();
        char[] pw1 = signupPanel.passField.getPassword();
        char[] pw2 = signupPanel.confirmPassField.getPassword();
        String password = new String(pw1);
        String confirmPassword = new String(pw2);
        if (username.isEmpty() || password.isEmpty()) {
            if (username.isEmpty()) signupPanel.userField.putClientProperty("JComponent.outline", "error");
            if (password.isEmpty()) signupPanel.passField.putClientProperty("JComponent.outline", "error");
            JOptionPane.showMessageDialog(mainFrame, "Username and password cannot be empty.", "Signup Error", JOptionPane.ERROR_MESSAGE);
            SwingUtilities.invokeLater(() -> {
                signupPanel.userField.putClientProperty("JComponent.outline", null);
                signupPanel.passField.putClientProperty("JComponent.outline", null);
            });
            return;
        }
        if (!password.equals(confirmPassword)) {
            signupPanel.confirmPassField.putClientProperty("JComponent.outline", "error");
            JOptionPane.showMessageDialog(mainFrame, "Passwords do not match.", "Signup Error", JOptionPane.ERROR_MESSAGE);
            SwingUtilities.invokeLater(() -> signupPanel.confirmPassField.putClientProperty("JComponent.outline", null));
            return;
        }
        if (authService.signup(username, password)) {
            JOptionPane.showMessageDialog(mainFrame, "Signup successful! Please log in.", "Success", JOptionPane.INFORMATION_MESSAGE);
            mainFrame.showScreen("LOGIN");
        } else {
            JOptionPane.showMessageDialog(mainFrame, "Username already exists.", "Signup Failed", JOptionPane.ERROR_MESSAGE);
        }
        Arrays.fill(pw1, '\0');
        Arrays.fill(pw2, '\0');
    }
    private void showHomeScreen() {
        List<String> topics = apiClient.getTopics();
        homeScreenPanel.topicComboBox.removeAllItems();
        
        if (topics != null && !topics.isEmpty()) {
            for (String topic : topics) {
                if (topic != null && !topic.trim().isEmpty()) {
                    homeScreenPanel.topicComboBox.addItem(topic);
                }
            }
        } else {
            logger.warn("No topics available from API client");
        }
        
        mainFrame.showScreen("HOME");
    }
}
