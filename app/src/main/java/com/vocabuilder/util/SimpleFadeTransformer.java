package com.vocabuilder.util;

import android.view.View;
import androidx.annotation.NonNull;
import androidx.viewpager2.widget.ViewPager2;

/**
 * A simple page transformer that provides a clean fade transition between pages
 */
public class SimpleFadeTransformer implements ViewPager2.PageTransformer {

    @Override
    public void transformPage(@NonNull View page, float position) {
        page.setTranslationX(0); // No horizontal movement
        
        if (position <= -1.0f || position >= 1.0f) {
            // Page is not visible - make it transparent
            page.setAlpha(0.0f);
        } else if (position == 0.0f) {
            // Page is in the center - make it fully visible
            page.setAlpha(1.0f);
        } else {
            // Page is in transition - partially fade it
            // Calculate alpha based on position (linear fade)
            page.setAlpha(1.0f - Math.abs(position));
        }
    }
}