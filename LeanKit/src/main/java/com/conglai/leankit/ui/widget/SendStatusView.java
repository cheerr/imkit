package com.conglai.leankit.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import com.avos.avoscloud.im.v2.AVIMMessage;
import com.conglai.leankit.R;

/**
 * Created by chenwei on 16/8/7.
 */

public class SendStatusView extends FrameLayout {

    public static final int STATUS_SENT, STATUS_SENDING, STATUS_FAILED;

    static {
        STATUS_SENT = AVIMMessage.AVIMMessageStatus.AVIMMessageStatusSent.getStatusCode();
        STATUS_SENDING = AVIMMessage.AVIMMessageStatus.AVIMMessageStatusSending.getStatusCode();
        STATUS_FAILED = AVIMMessage.AVIMMessageStatus.AVIMMessageStatusFailed.getStatusCode();
    }

    private int status = STATUS_FAILED;

    public SendStatusView(Context context) {
        this(context, null);
    }

    public SendStatusView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.lean_im_sendstatus_view, this, true);
        setStatus(AVIMMessage.AVIMMessageStatus.AVIMMessageStatusNone.getStatusCode());
    }


    public void setStatus(int _status) {
        if (_status == STATUS_SENT) {
            this.status = STATUS_SENT;
            setVisibility(GONE);
        } else if (_status == STATUS_SENDING) {
            this.status = STATUS_SENDING;
            setVisibility(VISIBLE);
            findViewById(R.id.im_send_error).setVisibility(GONE);
            findViewById(R.id.im_send_sending).setVisibility(VISIBLE);
        } else {
            this.status = STATUS_FAILED;
            setVisibility(VISIBLE);
            findViewById(R.id.im_send_error).setVisibility(VISIBLE);
            findViewById(R.id.im_send_sending).setVisibility(GONE);
        }
    }

    public int getStatus() {
        return status;
    }
}
