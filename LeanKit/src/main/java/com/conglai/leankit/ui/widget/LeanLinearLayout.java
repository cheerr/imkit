package com.conglai.leankit.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

import com.conglai.leankit.ui.widget.util.ShadowDelegate;

/**
 * Created by chenwei on 16/7/25.
 */

public class LeanLinearLayout extends LinearLayout {

    ShadowDelegate delegate;

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (delegate != null)
            delegate.delegateTouch(ev);
        return super.onTouchEvent(ev);
    }

    public void setClickShadow(boolean clickShadow) {
        if (delegate == null) {
            delegate = new ShadowDelegate(this);
        }
        delegate.setClickShadow(clickShadow);
    }

    public LeanLinearLayout(Context context) {
        this(context, null);
    }

    public LeanLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
}
