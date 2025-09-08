package com.cyril.quiz;

import java.util.List;

public interface IAPIClient {
    List<String> getTopics();
    List<QuestionModel> getQuestions(String topic) throws ApiException;
}