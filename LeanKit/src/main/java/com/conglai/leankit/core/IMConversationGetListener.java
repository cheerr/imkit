package com.conglai.leankit.core;

import com.avos.avoscloud.im.v2.AVIMConversation;

/**
 * Created by chenwei on 16/7/15.
 */

public interface IMConversationGetListener {

    public void onGet(AVIMConversation conversation);

}
