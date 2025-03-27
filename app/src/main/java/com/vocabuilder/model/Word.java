package com.vocabuilder.model;

import android.text.Html;
import android.text.Spanned;

import java.util.List;

public class Word {
    private String word;
    private String phonetic;
    private String partOfSpeech;
    private List<String> definitions;
    private List<String> examples;
    private List<String> synonyms;
    private List<String> antonyms;

    public Word(String word, String phonetic, String partOfSpeech, List<String> definitions, List<String> examples, 
               List<String> synonyms, List<String> antonyms) {
        this.word = word;
        this.phonetic = phonetic;
        this.partOfSpeech = partOfSpeech;
        this.definitions = definitions;
        this.examples = examples;
        this.synonyms = synonyms;
        this.antonyms = antonyms;
    }
    
    // Add a constructor that maintains backward compatibility with existing code
    public Word(String word, List<String> definitions, List<String> examples, 
               List<String> synonyms, List<String> antonyms) {
        this(word, "'"+word+"'", "adj.", definitions, examples, synonyms, antonyms);
    }

    public String getWord() {
        return word;
    }
    
    public String getPhonetic() {
        return phonetic;
    }
    
    public String getPartOfSpeech() {
        return partOfSpeech;
    }

    public List<String> getDefinitions() {
        return definitions;
    }

    public String getFormattedDefinitions() {
        return getFormattedDefinitions(false);
    }
    
    public String getFormattedDefinitions(boolean collapsed) {
        StringBuilder sb = new StringBuilder();
        int limit = collapsed ? Math.min(2, definitions.size()) : definitions.size();
        
        for (int i = 0; i < limit; i++) {
            String definition = definitions.get(i);
            
            // Start each definition with the number
            sb.append(i + 1).append(". ");
            
            // Check if the definition starts with a part of speech in parentheses
            if (definition.startsWith("(") && definition.contains(")")) {
                int closingParenIndex = definition.indexOf(')');
                if (closingParenIndex > 0) {
                    // Format ONLY the part of speech in italics using HTML
                    String partOfSpeech = definition.substring(0, closingParenIndex + 1);
                    String restOfDefinition = definition.substring(closingParenIndex + 1);
                    sb.append("<i>").append(partOfSpeech).append("</i>").append(restOfDefinition);
                } else {
                    sb.append(definition);
                }
            } else {
                sb.append(definition);
            }
            
            // Add line breaks between definitions
            if (i < limit - 1) {
                sb.append("<br><br>"); // Use HTML breaks instead of newlines
            }
        }
        
        return sb.toString();
    }

    public List<String> getExamples() {
        return examples;
    }

    public String getFormattedExamples() {
        return getFormattedExamples(false);
    }
    
    public String getFormattedExamples(boolean collapsed) {
        StringBuilder sb = new StringBuilder();
        int limit = collapsed ? Math.min(1, examples.size()) : examples.size();
        
        for (int i = 0; i < limit; i++) {
            sb.append("• ").append(examples.get(i));
            if (i < limit - 1) {
                sb.append("<br><br>"); // Use HTML breaks instead of newlines
            }
        }
        
        return sb.toString();
    }

    public List<String> getSynonyms() {
        return synonyms;
    }

    public String getFormattedSynonyms() {
        return String.join(", ", synonyms);
    }

    public List<String> getAntonyms() {
        return antonyms;
    }

    public String getFormattedAntonyms() {
        return String.join(", ", antonyms);
    }
}