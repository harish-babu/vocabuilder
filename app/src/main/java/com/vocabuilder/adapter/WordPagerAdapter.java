package com.vocabuilder.adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.vocabuilder.R;
import com.vocabuilder.model.Word;
import com.vocabuilder.ui.WordDetailBottomSheet;

import java.util.List;

public class WordPagerAdapter extends RecyclerView.Adapter<WordPagerAdapter.WordViewHolder> {
    // Constants for view types
    private static final int VIEW_TYPE_LOADING = 0;
    private static final int VIEW_TYPE_WORD = 1;
    
    private final Context context;
    private final List<Word> words;

    public WordPagerAdapter(Context context, List<Word> words) {
        this.context = context;
        this.words = words;
    }

    @NonNull
    @Override
    public WordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_word, parent, false);
        return new WordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WordViewHolder holder, int position) {
        if (position < words.size()) {
            Word word = words.get(position);
            holder.bind(word);
        }
    }

    @Override
    public int getItemCount() {
        return words.size();
    }

    class WordViewHolder extends RecyclerView.ViewHolder {
        TextView tvWord, tvPhonetic, tvPartOfSpeech, tvExample, tvVocabularyLabel;
        View progressBar, contentLayout;
        ScrollView scrollView;

        public WordViewHolder(@NonNull View itemView) {
            super(itemView);
            tvWord = itemView.findViewById(R.id.tvWord);
            tvPhonetic = itemView.findViewById(R.id.tvPhonetic);
            tvPartOfSpeech = itemView.findViewById(R.id.tvPartOfSpeech);
            tvExample = itemView.findViewById(R.id.tvExample);
            tvVocabularyLabel = itemView.findViewById(R.id.tvVocabularyLabel);
            progressBar = itemView.findViewById(R.id.progressBar);
            contentLayout = itemView.findViewById(R.id.contentLayout);
            scrollView = itemView.findViewById(R.id.scrollView);
            
            // Improve scroll handling
            setupScrollViewTouchListener();
        }

        public void bind(Word word) {
            if (word != null) {
                // Hide progress bar and show content
                progressBar.setVisibility(View.GONE);
                tvWord.setVisibility(View.VISIBLE);
                tvPhonetic.setVisibility(View.VISIBLE);
                tvPartOfSpeech.setVisibility(View.VISIBLE);
                tvExample.setVisibility(View.VISIBLE);
                tvVocabularyLabel.setVisibility(View.VISIBLE);
                
                // Set word data in lowercase to match the design
                tvWord.setText(word.getWord().toLowerCase());
                
                // Set phonetic pronunciation
                tvPhonetic.setText(word.getPhonetic());
                
                // Set part of speech and primary definition
                String firstDefinition = word.getDefinitions().isEmpty() ? "" : word.getDefinitions().get(0);
                String partOfSpeech = word.getPartOfSpeech().isEmpty() ? "adj." : word.getPartOfSpeech();
                tvPartOfSpeech.setText("(" + partOfSpeech + ") " + firstDefinition);
                
                // Set the example
                String example = word.getExamples().isEmpty() ? 
                        "By nature, Sheila is a taciturn woman who keeps her thoughts to herself." : 
                        word.getExamples().get(0);
                tvExample.setText(example);
                
            } else {
                // Show progress bar and hide content
                progressBar.setVisibility(View.VISIBLE);
                tvWord.setVisibility(View.GONE);
                tvPhonetic.setVisibility(View.GONE);
                tvPartOfSpeech.setVisibility(View.GONE);
                tvExample.setVisibility(View.GONE);
                tvVocabularyLabel.setVisibility(View.GONE);
            }
        }
        
        private void setupScrollViewTouchListener() {
            // Detect when user has reached the top or bottom of the ScrollView
            // to enable/disable ViewPager2 swiping appropriately
            scrollView.setOnTouchListener((v, event) -> {
                // Allow parent view to intercept touch events
                v.getParent().requestDisallowInterceptTouchEvent(false);
                
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    // When user first touches, temporarily prevent parent from stealing events
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                }
                else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    // Check if we've reached the scroll limits
                    boolean canScrollUp = scrollView.canScrollVertically(-1);
                    boolean canScrollDown = scrollView.canScrollVertically(1);
                    
                    // If we're at the top and trying to scroll up, or
                    // at the bottom and trying to scroll down,
                    // let the parent handle the scroll/swipe
                    if ((!canScrollUp && event.getY() > 0) || (!canScrollDown && event.getY() < 0)) {
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                    } else {
                        // Otherwise, let the ScrollView handle scrolling
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                    }
                }
                
                // Don't consume the event, let the ScrollView process it as usual
                return false;
            });
        }
    }
}