package com.vocabuilder.api;

import com.vocabuilder.model.Word;

public interface WordApiCallback {
    void onSuccess(Word word);
    void onFailure(String errorMessage);
}