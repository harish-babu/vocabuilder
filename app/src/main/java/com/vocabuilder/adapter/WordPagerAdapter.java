package com.vocabuilder.adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
        TextView tvWord, tvDefinition, tvExample, tvSynonyms, tvAntonyms;
        TextView tvShowMoreDefinitions, tvShowMoreExamples;
        View progressBar, contentLayout;
        boolean definitionsExpanded = false;
        boolean examplesExpanded = false;

        public WordViewHolder(@NonNull View itemView) {
            super(itemView);
            tvWord = itemView.findViewById(R.id.tvWord);
            tvDefinition = itemView.findViewById(R.id.tvDefinition);
            tvExample = itemView.findViewById(R.id.tvExample);
            tvSynonyms = itemView.findViewById(R.id.tvSynonyms);
            tvAntonyms = itemView.findViewById(R.id.tvAntonyms);
            tvShowMoreDefinitions = itemView.findViewById(R.id.tvShowMoreDefinitions);
            tvShowMoreExamples = itemView.findViewById(R.id.tvShowMoreExamples);
            progressBar = itemView.findViewById(R.id.progressBar);
            contentLayout = itemView.findViewById(R.id.contentLayout);
        }

        public void bind(Word word) {
            if (word != null) {
                // Hide progress bar and show content
                progressBar.setVisibility(View.GONE);
                tvWord.setVisibility(View.VISIBLE);
                tvDefinition.setVisibility(View.VISIBLE);
                tvExample.setVisibility(View.VISIBLE);
                tvSynonyms.setVisibility(View.VISIBLE);
                tvAntonyms.setVisibility(View.VISIBLE);
                
                // Set word data - note that we'll set the word in uppercase
                // even though the style has textAllCaps="true" as a backup
                tvWord.setText(word.getWord().toUpperCase());
                
                // Display definitions with HTML formatting for part of speech
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    tvDefinition.setText(Html.fromHtml(word.getFormattedDefinitions(true), Html.FROM_HTML_MODE_COMPACT));
                } else {
                    tvDefinition.setText(Html.fromHtml(word.getFormattedDefinitions(true)));
                }
                
                // Show the "Read More" button if there are more than 2 definitions
                if (word.getDefinitions().size() > 2) {
                    tvShowMoreDefinitions.setVisibility(View.VISIBLE);
                    tvShowMoreDefinitions.setOnClickListener(v -> {
                        // Show bottom sheet with all definitions
                        WordDetailBottomSheet bottomSheet = WordDetailBottomSheet.newInstance(
                                context.getString(R.string.complete_definitions), 
                                word.getFormattedDefinitions(false));
                        bottomSheet.show(((AppCompatActivity)context).getSupportFragmentManager(), 
                                "definitionsBottomSheet");
                    });
                } else {
                    tvShowMoreDefinitions.setVisibility(View.GONE);
                }
                
                // Display examples with HTML formatting (for line breaks)
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    tvExample.setText(Html.fromHtml(word.getFormattedExamples(true), Html.FROM_HTML_MODE_COMPACT));
                } else {
                    tvExample.setText(Html.fromHtml(word.getFormattedExamples(true)));
                }
                
                // Show the "Read More" button if there are more than 1 example
                if (word.getExamples().size() > 1) {
                    tvShowMoreExamples.setVisibility(View.VISIBLE);
                    tvShowMoreExamples.setOnClickListener(v -> {
                        // Show bottom sheet with all examples
                        WordDetailBottomSheet bottomSheet = WordDetailBottomSheet.newInstance(
                                context.getString(R.string.complete_examples), 
                                word.getFormattedExamples(false));
                        bottomSheet.show(((AppCompatActivity)context).getSupportFragmentManager(), 
                                "examplesBottomSheet");
                    });
                } else {
                    tvShowMoreExamples.setVisibility(View.GONE);
                }
                
                tvSynonyms.setText(word.getFormattedSynonyms());
                tvAntonyms.setText(word.getFormattedAntonyms());
            } else {
                // Show progress bar and hide content
                progressBar.setVisibility(View.VISIBLE);
                tvWord.setVisibility(View.GONE);
                tvDefinition.setVisibility(View.GONE);
                tvExample.setVisibility(View.GONE);
                tvSynonyms.setVisibility(View.GONE);
                tvAntonyms.setVisibility(View.GONE);
                tvShowMoreDefinitions.setVisibility(View.GONE);
                tvShowMoreExamples.setVisibility(View.GONE);
            }
        }
    }
}