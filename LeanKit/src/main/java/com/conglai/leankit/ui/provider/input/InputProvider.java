package com.conglai.leankit.ui.provider.input;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.View;

/**
 * 底部功能栏扩展接口
 *
 * Created by chenwei on 16/7/21.
 */

public abstract class InputProvider {

    public abstract Drawable obtainPluginDrawable();

    public abstract CharSequence obtainPluginTitle();

    public abstract void onPluginClick(View view);

    public abstract boolean onPluginLongClick(View view);

    /**
     * 点击跳转其他节点需要回调时调用
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public abstract void onActivityResult(int requestCode, int resultCode, Intent data);

}
