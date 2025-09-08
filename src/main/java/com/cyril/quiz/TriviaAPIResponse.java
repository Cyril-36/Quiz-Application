package com.cyril.quiz;

import java.util.List;

// This class matches the top-level structure of the API response
public class TriviaAPIResponse {
    int response_code;
    List<TriviaQuestion> results;
}