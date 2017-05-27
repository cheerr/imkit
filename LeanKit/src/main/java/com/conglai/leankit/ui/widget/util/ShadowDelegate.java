package com.conglai.leankit.ui.widget.util;

import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.conglai.common.Debug;

/**
 * Created by chenwei on 16/7/25.
 */

public class ShadowDelegate {

    private View hostView;

    ColorFilter drawColorFilter;
    boolean clickShadow = false;
    boolean atDown = false;

    public ShadowDelegate(View view) {
        this.hostView = view;
    }

    public void delegateTouch(MotionEvent ev) {
        if (clickShadow) {
            switch (ev.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    if (inRangeOfView(ev)) {
                        atDown = true;
                        hostView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                            @Override
                            public boolean onPreDraw() {
                                checkDrawableDraw(atDown);
                                if (!atDown) {
                                    hostView.getViewTreeObserver().removeOnPreDrawListener(this);
                                }
                                return true;
                            }
                        });
                        hostView.invalidate();
                    }
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.INVALID_POINTER_ID:
                    atDown = false;
                    hostView.invalidate();
                    break;
            }
        }
    }

    /**
     * 在down时对点击区域做一次判断
     *
     * @param ev
     * @return
     */

    private boolean inRangeOfView(MotionEvent ev) {
        int[] location = new int[2];
        hostView.getLocationOnScreen(location);
        int x = location[0];
        int y = location[1];

        float cx = ev.getRawX();
        float cy = ev.getRawY();
        Debug.i("ShadowDelegate", "x=" + x + ",y=" + y + ",cx=" + cx + ",cy=" + cy);

        if (cx < x || cx > (x + hostView.getWidth()) || cy < y || cy > (y + hostView.getHeight())) {
            return false;
        }
        return true;
    }


    Drawable getDrawable() {
        if (hostView instanceof ImageView) {
            return ((ImageView) hostView).getDrawable();
        } else if (hostView instanceof TextView) {
            return hostView.getBackground();
        } else if (hostView instanceof ViewGroup) {
            return hostView.getBackground();
        }
        return null;
    }

    /**
     * @param atDown
     */
    private void checkDrawableDraw(boolean atDown) {
        Drawable drawable = getDrawable();
        if (drawable != null) {
            drawable.setCallback(null);
            if (clickShadow && atDown) {
                if (getDrawColorFilter() == null) {
                    setDrawColorFilter(new PorterDuffColorFilter(Color.argb(0x30, 0, 0, 0), PorterDuff.Mode.SRC_ATOP));
                }
            } else {
                setDrawColorFilter(null);
            }
        }
        hostView.invalidate();
    }

    public void setClickShadow(boolean clickShadow) {
        this.clickShadow = clickShadow;
        if (clickShadow) {
            hostView.setClickable(true);
        }
        hostView.invalidate();
    }

    private ColorFilter getDrawColorFilter() {
        return drawColorFilter;
    }

    private void setDrawColorFilter(ColorFilter drawColorFilter) {
        this.drawColorFilter = drawColorFilter;
        Drawable drawable = getDrawable();
        if (drawable != null) {
            drawable.setColorFilter(drawColorFilter);
        }
    }

}
