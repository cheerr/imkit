package com.conglai.leankit.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TextView;

import com.conglai.emoji.Utils;
import com.conglai.leankit.ui.widget.util.ShadowDelegate;

/**
 * Created by chenwei on 16/7/22.
 */

public class LeanTextView extends TextView {

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


    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(Utils.replaceEmoji(text, this), type);
    }

    public LeanTextView(Context context) {
        this(context, null);
    }

    public LeanTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

}
