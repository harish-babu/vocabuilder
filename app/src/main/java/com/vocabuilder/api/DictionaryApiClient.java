package com.vocabuilder.api;

import android.util.Log;

import com.vocabuilder.model.Word;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class DictionaryApiClient {
    private static final String TAG = "DictionaryApiClient";
    private static final String BASE_URL = "https://api.dictionaryapi.dev/api/v2/entries/en/";
    
    // Alternative API (requires API key)
    // private static final String WORDS_API_BASE_URL = "https://wordsapiv1.p.rapidapi.com/words/";
    // private static final String WORDS_API_KEY = "YOUR_API_KEY"; // Replace with your actual API key
    
    private final OkHttpClient client;
    private final Executor executor;

    public DictionaryApiClient() {
        client = new OkHttpClient();
        executor = Executors.newSingleThreadExecutor();
    }

    public void getWordDetails(String wordText, WordApiCallback callback) {
        executor.execute(() -> {
            String url = BASE_URL + wordText;
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    callback.onFailure("Network error: " + e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        if (response.code() == 404) {
                            // Word not found in dictionary, try another word
                            Log.w(TAG, "Word not found in dictionary: " + wordText);
                            callback.onFailure("Word not found, try another");
                        } else {
                            callback.onFailure("API error: " + response.code());
                        }
                        return;
                    }

                    ResponseBody responseBody = response.body();
                    if (responseBody == null) {
                        callback.onFailure("Empty response body");
                        return;
                    }

                    String json = responseBody.string();
                    Word word = parseDictionaryApiResponse(json, wordText);
                    
                    if (word != null) {
                        callback.onSuccess(word);
                    } else {
                        callback.onFailure("Failed to parse response");
                    }
                }
            });
        });
    }

    private Word parseDictionaryApiResponse(String json, String originalWord) {
        try {
            List<String> definitions = new ArrayList<>();
            List<String> examples = new ArrayList<>();
            List<String> synonyms = new ArrayList<>();
            List<String> antonyms = new ArrayList<>();

            JSONArray jsonArray = new JSONArray(json);
            JSONObject wordObject = jsonArray.getJSONObject(0);
            
            // Get word
            String word = wordObject.getString("word");
            
            // Get meanings
            JSONArray meaningsArray = wordObject.getJSONArray("meanings");
            for (int i = 0; i < meaningsArray.length(); i++) {
                JSONObject meaning = meaningsArray.getJSONObject(i);
                String partOfSpeech = meaning.getString("partOfSpeech");
                
                // Get definitions, examples, synonyms, antonyms
                JSONArray definitionsArray = meaning.getJSONArray("definitions");
                for (int j = 0; j < definitionsArray.length(); j++) {
                    JSONObject definitionObject = definitionsArray.getJSONObject(j);
                    
                    // Add definition with part of speech
                    String definition = "(" + partOfSpeech + ") " + definitionObject.getString("definition");
                    definitions.add(definition);
                    
                    // Add example if available
                    if (definitionObject.has("example")) {
                        examples.add(definitionObject.getString("example"));
                    }
                }
                
                // Add synonyms if available
                if (meaning.has("synonyms")) {
                    JSONArray synonymsArray = meaning.getJSONArray("synonyms");
                    for (int j = 0; j < synonymsArray.length(); j++) {
                        synonyms.add(synonymsArray.getString(j));
                    }
                }
                
                // Add antonyms if available
                if (meaning.has("antonyms")) {
                    JSONArray antonymsArray = meaning.getJSONArray("antonyms");
                    for (int j = 0; j < antonymsArray.length(); j++) {
                        antonyms.add(antonymsArray.getString(j));
                    }
                }
            }
            
            // If no examples found, add a placeholder
            if (examples.isEmpty()) {
                examples.add("No examples available for this word.");
            }
            
            // If no synonyms found, add a placeholder
            if (synonyms.isEmpty()) {
                synonyms.add("No synonyms available");
            }
            
            // If no antonyms found, add a placeholder
            if (antonyms.isEmpty()) {
                antonyms.add("No antonyms available");
            }
            
            return new Word(word, definitions, examples, synonyms, antonyms);
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing JSON response: " + e.getMessage());
            
            // Return a fallback word object with error information
            List<String> definitions = new ArrayList<>();
            definitions.add("Definition not available");
            
            List<String> examples = new ArrayList<>();
            examples.add("Examples not available");
            
            List<String> synonyms = new ArrayList<>();
            synonyms.add("Synonyms not available");
            
            List<String> antonyms = new ArrayList<>();
            antonyms.add("Antonyms not available");
            
            return new Word(originalWord, definitions, examples, synonyms, antonyms);
        }
    }
}