package com.vocabuilder;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.vocabuilder.adapter.WordPagerAdapter;
import com.vocabuilder.api.DictionaryApiClient;
import com.vocabuilder.api.WordApiCallback;
import com.vocabuilder.model.Word;
import com.vocabuilder.util.NetworkUtil;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ViewPager2 viewPager;
    private WordPagerAdapter adapter;
    private List<Word> words;
    private DictionaryApiClient apiClient;

    // For random word generation
    private RandomWordGenerator randomWordGenerator;
    private static final int PRELOAD_THRESHOLD = 3;
    private boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.viewPager);
        words = new ArrayList<>();
        adapter = new WordPagerAdapter(this, words);
        viewPager.setAdapter(adapter);
        
        // Add a simple fade transition for crisper, cleaner page changes
        
        // Set orientation to vertical for up/down scrolling
        viewPager.setOrientation(ViewPager2.ORIENTATION_VERTICAL);
        
        // Set a simple page transformer for clean transitions
        viewPager.setPageTransformer((page, position) -> {
            // A simple fade effect with no other transformations
            float normalizedPosition = Math.abs(Math.abs(position) - 1);
            page.setAlpha(normalizedPosition);
        });

        apiClient = new DictionaryApiClient();
        randomWordGenerator = new RandomWordGenerator();

        // Load initial words
        loadInitialWords();

        // Set up pagination
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                // If we're getting close to the end, load more words
                if (position >= words.size() - PRELOAD_THRESHOLD && !isLoading) {
                    loadMoreWords();
                }
            }
        });
    }

    private void loadInitialWords() {
        isLoading = true;
        final int initialLoadCount = 5;
        for (int i = 0; i < initialLoadCount; i++) {
            fetchRandomWordAndLoad();
        }
    }

    private void loadMoreWords() {
        isLoading = true;
        final int nextLoadCount = 3;
        for (int i = 0; i < nextLoadCount; i++) {
            fetchRandomWordAndLoad();
        }
    }
    
    private void fetchRandomWordAndLoad() {
        // Check for network connectivity first
        if (!NetworkUtil.isConnected(this)) {
            runOnUiThread(() -> {
                Toast.makeText(MainActivity.this, 
                        getString(R.string.error_network), 
                        Toast.LENGTH_SHORT).show();
                isLoading = false;
            });
            return;
        }
        
        randomWordGenerator.getRandomWord(new RandomWordGenerator.RandomWordCallback() {
            @Override
            public void onWordGenerated(String word) {
                loadWordDetails(word);
            }

            @Override
            public void onFailure(String errorMessage) {
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this,
                            "Error getting random word: " + errorMessage,
                            Toast.LENGTH_SHORT).show();
                    isLoading = false;
                });
            }
        });
    }

    private void loadWordDetails(String wordText) {
        apiClient.getWordDetails(wordText, new WordApiCallback() {
            @Override
            public void onSuccess(Word word) {
                runOnUiThread(() -> {
                    words.add(word);
                    adapter.notifyItemInserted(words.size() - 1);
                    isLoading = false;
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                // If the word wasn't found in dictionary, try another random word
                if (errorMessage.contains("Word not found")) {
                    fetchRandomWordAndLoad();
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(MainActivity.this, 
                                "Error loading word: " + errorMessage, 
                                Toast.LENGTH_SHORT).show();
                        isLoading = false;
                    });
                }
            }
        });
    }
}