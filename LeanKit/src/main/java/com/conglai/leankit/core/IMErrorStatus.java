package com.conglai.leankit.core;

import com.avos.avoscloud.im.v2.AVIMException;

/**
 * Created by chenwei on 16/7/14.
 */

public enum IMErrorStatus {

    TARGET_EMPTY(-80, "找不到会话"),
    MESSAGE_EMPTY(-81, "消息为空"),
    MESSAGE_ARG_ERROR(-82, "信息参数错误"),
    CONVERSATION_EMPTY(-83, "Conversation为空"),
    MESSAGE_NOT_IN_NATIVE(-84, "找不到消息内容");

    private int code;
    private String errorMsg;

    private IMErrorStatus(int code, String errorMsg) {
        this.code = code;
        this.errorMsg = errorMsg;
    }

    public int getCode() {
        return code;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public AVIMException toAVIMException() {
        return new AVIMException(code, errorMsg);
    }
}
