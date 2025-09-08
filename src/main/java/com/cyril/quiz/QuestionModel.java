package com.cyril.quiz;

import java.util.List;

public class QuestionModel {
    private String question;
    private List<String> options;
    private int answerIndex;

    // Add this constructor
    public QuestionModel(String question, List<String> options, int answerIndex) {
        this.question = question;
        this.options = options;
        this.answerIndex = answerIndex;
    }

    // --- Getters ---
    public String getQuestion() {
        return question;
    }

    public List<String> getOptions() {
        return options;
    }

    public int getAnswerIndex() {
        return answerIndex;
    }
}