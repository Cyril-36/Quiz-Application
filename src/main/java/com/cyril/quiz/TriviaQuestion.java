package com.cyril.quiz;

import java.util.List;

// This class matches the structure of a single question from the API
public class TriviaQuestion {
    String category;
    String type;
    String difficulty;
    String question;
    String correct_answer;
    List<String> incorrect_answers;
}