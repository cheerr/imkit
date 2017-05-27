package com.conglai.leankit.model.input;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.View;

/**
 * Created by chenwei on 16/7/13.
 */

public class EmojiInputProvider extends InputProvider {
    @Override
    public Drawable obtainPluginDrawable() {
        return null;
    }

    @Override
    public CharSequence obtainPluginTitle() {
        return null;
    }

    @Override
    public void onPluginClick(View view) {

    }

    @Override
    public boolean onPluginLongClick(View view) {
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }
}
