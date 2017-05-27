package com.conglai.leankit.receiver;

import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMClientEventHandler;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.conglai.leankit.core.LeanIM;

/**
 * Created by chenwei on 16/7/15.
 */

public abstract class CustomClientEventHandler extends AVIMClientEventHandler {

    @Override
    public abstract void onConnectionPaused(AVIMClient avimClient);

    @Override
    public void onConnectionResume(AVIMClient avimClient) {
        LeanIM.getInstance().loginIn(new AVIMClientCallback() {
            @Override
            public void done(AVIMClient avimClient, AVIMException e) {

            }
        });
    }

    @Override
    public final void onClientOffline(AVIMClient avimClient, int code) {
        if (code == 4111) {
            // 适当地弹出友好提示，告知当前用户的 Client Id 在其他设备上登陆了
            loginAtOther(avimClient);
        } else {
            loseClient(avimClient, code);
        }
    }

    public abstract void loginAtOther(AVIMClient avimClient);

    public abstract void loseClient(AVIMClient avimClient, int code);

}
