package com.vocabuilder;

import android.util.Log;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.json.JSONArray;
import org.json.JSONException;

/**
 * Class to fetch random English words for vocabulary building using external API
 */
public class RandomWordGenerator {
    private static final String TAG = "RandomWordGenerator";
    // API for random word generation
    private static final String RANDOM_WORD_API = "https://random-word-api.herokuapp.com/word";
    
    // Higher frequency of common English words that are useful for vocabulary building
    private static final String RANDOM_WORD_WITH_LENGTH = "https://random-word-api.herokuapp.com/word?length=";

    private final OkHttpClient client;
    private final Set<String> usedWords;

    public RandomWordGenerator() {
        client = new OkHttpClient();
        usedWords = new HashSet<>();
    }

    /**
     * Interface for receiving random word callback
     */
    public interface RandomWordCallback {
        void onWordGenerated(String word);
        void onFailure(String errorMessage);
    }

    /**
     * Get a random English word from external API
     * @param callback to handle the response
     */
    public void getRandomWord(RandomWordCallback callback) {
        // To get more interesting vocabulary words, we'll get words with 5-10 letters
        int minLength = 5;
        int maxLength = 10;
        int randomLength = minLength + (int)(Math.random() * ((maxLength - minLength) + 1));
        
        // Use the API to get a random word with a specific length
        String url = RANDOM_WORD_WITH_LENGTH + randomLength;
        
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Error fetching random word: " + e.getMessage());
                callback.onFailure("Error fetching random word: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    callback.onFailure("API error: " + response.code());
                    return;
                }

                ResponseBody responseBody = response.body();
                if (responseBody == null) {
                    callback.onFailure("Empty response from random word API");
                    return;
                }

                String jsonResponse = responseBody.string();
                try {
                    JSONArray wordsArray = new JSONArray(jsonResponse);
                    if (wordsArray.length() > 0) {
                        String randomWord = wordsArray.getString(0);
                        // Check if we've already used this word
                        if (usedWords.contains(randomWord)) {
                            // Get another word
                            getRandomWord(callback);
                        } else {
                            usedWords.add(randomWord);
                            callback.onWordGenerated(randomWord);
                        }
                    } else {
                        callback.onFailure("Empty word array from API");
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "Error parsing JSON response: " + e.getMessage());
                    callback.onFailure("Error parsing response: " + e.getMessage());
                }
            }
        });
    }
}