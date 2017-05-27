package com.conglai.leankit.ui.provider.item;

import android.content.Context;
import android.os.Bundle;
import android.text.Spannable;
import android.view.View;
import android.view.ViewGroup;

import com.conglai.dblib.android.Message;
import com.conglai.leankit.ui.model.UIMessage;

/**
 * 消息展示扩展接口
 * Created by chenwei on 16/7/21.
 */

/**
 * 这个类是复用的,所以不要存一些暂态内容
 *
 * @param <T>
 */
public abstract class ItemProvider<T> {

    private Context context;
    private UIMessage uiMessage;
    private Bundle extra;

    public ItemProvider(Context context, UIMessage uiMessage) {
        this.context = context;
        this.uiMessage = uiMessage;
        this.extra = new Bundle();
    }

    public UIMessage getUIMessage() {
        return uiMessage;
    }

    public Context getContext() {
        return context;
    }

    public Bundle getExtra() {
        return extra;
    }

    public void setExtra(Bundle extra) {
        this.extra = extra;
    }

    public abstract View onCreateView(Context context, ViewGroup viewGroup);

    /**
     * 注意每次都要把数据重新设置一遍
     */
    public abstract void onBindView(View itemView, int position, Message message);

    public abstract T messageTo(Message message);

    public abstract View getEventView(View itemView);

    //用于复制
    public abstract Spannable getContentSummary(Message message);

}
