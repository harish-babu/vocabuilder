package com.vocabuilder.util;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager2.widget.ViewPager2;

/**
 * Layout to wrap a scrollable component inside a ViewPager2. Provided as a solution from:
 * https://developer.android.com/jetpack/androidx/releases/viewpager2
 *
 * This solution addresses the scrolling conflict that occurs when a vertical scrollable view is nested
 * within a vertical ViewPager2.
 */
public class NestedScrollableHost extends FrameLayout {
    private int touchSlop;
    private float initialX;
    private float initialY;
    private ViewPager2 parentViewPager;

    public NestedScrollableHost(@NonNull Context context) {
        super(context);
        init(context);
    }

    public NestedScrollableHost(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    private void findParentViewPager() {
        if (parentViewPager == null) {
            View v = (View) getParent();
            while (v != null && !(v instanceof ViewPager2)) {
                v = (View) v.getParent();
            }
            parentViewPager = (ViewPager2) v;
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        findParentViewPager();
        if (parentViewPager == null) return super.onInterceptTouchEvent(e);

        int orientation = parentViewPager.getOrientation();

        // Early return if child can't scroll in same direction as parent
        if (!canChildScroll(orientation, -1) && !canChildScroll(orientation, 1)) {
            return false;
        }

        if (e.getAction() == MotionEvent.ACTION_DOWN) {
            initialX = e.getX();
            initialY = e.getY();
            getParent().requestDisallowInterceptTouchEvent(true);
        } else if (e.getAction() == MotionEvent.ACTION_MOVE) {
            float dx = e.getX() - initialX;
            float dy = e.getY() - initialY;
            boolean isVpHorizontal = orientation == ViewPager2.ORIENTATION_HORIZONTAL;

            // assuming ViewPager2 touch-slop is 2x touch-slop of child
            float scaledDx = Math.abs(dx) * (isVpHorizontal ? .5f : 1f);
            float scaledDy = Math.abs(dy) * (isVpHorizontal ? 1f : .5f);

            if (scaledDx > touchSlop || scaledDy > touchSlop) {
                if (isVpHorizontal == (scaledDy > scaledDx)) {
                    // Gesture is perpendicular, allow all parents to intercept
                    getParent().requestDisallowInterceptTouchEvent(false);
                } else {
                    // Gesture is parallel, query child if movement in that direction is possible
                    if (canChildScroll(orientation, isVpHorizontal ? (int) dx : (int) dy)) {
                        // Child can scroll, disallow all parents to intercept
                        getParent().requestDisallowInterceptTouchEvent(true);
                    } else {
                        // Child cannot scroll, allow all parents to intercept
                        getParent().requestDisallowInterceptTouchEvent(false);
                    }
                }
            }
        }

        return super.onInterceptTouchEvent(e);
    }

    private boolean canChildScroll(int orientation, int delta) {
        int direction = -(int)Math.signum(delta);
        View child = getChildAt(0);
        if (child == null) return false;

        if (orientation == ViewPager2.ORIENTATION_HORIZONTAL) {
            return child.canScrollHorizontally((int) direction);
        } else if (orientation == ViewPager2.ORIENTATION_VERTICAL) {
            return child.canScrollVertically((int) direction);
        } else {
            throw new IllegalArgumentException();
        }
    }
}