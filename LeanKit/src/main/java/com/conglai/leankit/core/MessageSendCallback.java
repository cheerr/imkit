package com.conglai.leankit.core;

import com.avos.avoscloud.im.v2.AVIMException;
import com.conglai.dblib.android.Message;

/**
 * Created by chenwei on 16/7/14.
 */

public interface MessageSendCallback {

    public void onSuccess(Message message);

    public void onFailure(AVIMException e);
}
