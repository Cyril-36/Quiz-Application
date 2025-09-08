package com.cyril.quiz;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.text.StringEscapeUtils;

import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class APIClient implements IAPIClient {
    private static final Logger logger = LoggerFactory.getLogger(APIClient.class);
    private final AppConfig config = AppConfig.getInstance();
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(AppConfig.getInstance().getHttpConnectTimeoutSeconds()))
            .build();
    private final Gson gson = new GsonBuilder().create();

    public List<String> getTopics() {
        List<String> topics = Arrays.asList("Computers", "Gadgets", "General Knowledge");
        logger.debug("Returning {} topics", topics.size());
        return topics;
    }

    // Add "throws ApiException" to the method signature here
    public List<QuestionModel> getQuestions(String topic) throws ApiException {
        int categoryId = 18; // Default to "Science: Computers"
        if (topic.equals("Gadgets")) {
            categoryId = 30;
        } else if (topic.equals("General Knowledge")) {
            categoryId = 9;
        }

        String apiUrl = "https://opentdb.com/api.php?amount=10&category=" + categoryId + "&type=multiple";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .timeout(Duration.ofSeconds(config.getHttpRequestTimeoutSeconds()))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                logger.warn("Non-OK HTTP status {} while fetching questions for topic {}", response.statusCode(), topic);
                throw new ApiException("Non-OK HTTP status: " + response.statusCode(), null);
            }
            TriviaAPIResponse apiResponse = gson.fromJson(response.body(), TriviaAPIResponse.class);

            if (apiResponse != null && apiResponse.results != null && !apiResponse.results.isEmpty()) {
                logger.info("Successfully fetched {} questions for topic: {}", apiResponse.results.size(), topic);
                return mapToQuestionModels(apiResponse.results);
            } else {
                logger.warn("API response was null or empty for topic: {}", topic);
                throw new ApiException("Failed to parse API response or no questions available.", null);
            }
        } catch (IOException e) {
            logger.error("Network error while fetching questions: {}", e.getMessage(), e);
            throw new ApiException("Network error while fetching questions.", e);
        } catch (InterruptedException e) {
            logger.error("Request interrupted while fetching questions: {}", e.getMessage(), e);
            Thread.currentThread().interrupt();
            throw new ApiException("Request interrupted while fetching questions.", e);
        } catch (Exception e) {
            logger.error("Unexpected error while fetching questions: {}", e.getMessage(), e);
            throw new ApiException("Unexpected error while fetching questions.", e);
        }
    }

    private List<QuestionModel> mapToQuestionModels(List<TriviaQuestion> triviaQuestions) {
        List<QuestionModel> questionModels = new ArrayList<>();
        for (TriviaQuestion tq : triviaQuestions) {
            List<String> options = new ArrayList<>(tq.incorrect_answers);
            options.add(tq.correct_answer);
            Collections.shuffle(options);
            int answerIndex = options.indexOf(tq.correct_answer);
            String decodedQuestion = StringEscapeUtils.unescapeHtml4(tq.question);
            List<String> decodedOptions = new ArrayList<>();
            for (String option : options) {
                decodedOptions.add(StringEscapeUtils.unescapeHtml4(option));
            }
            QuestionModel qm = new QuestionModel(decodedQuestion, decodedOptions, answerIndex);
            questionModels.add(qm);
        }
        return questionModels;
    }
}
