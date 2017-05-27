package com.conglai.leankit.model.message;

import android.support.annotation.NonNull;

import com.alibaba.fastjson.JSONObject;
import com.avos.avoscloud.im.v2.AVIMMessageCreator;
import com.avos.avoscloud.im.v2.AVIMMessageField;
import com.avos.avoscloud.im.v2.AVIMMessageType;
import com.conglai.leankit.model.message.file.IMFile;
import com.conglai.leankit.model.message.file.IMHalfPiTu;
import com.conglai.leankit.util.TextUtil;

/**
 * Created by chenwei on 16/9/20.
 */
@AVIMMessageType(
        type = MessageFactory.AVIMMessageType_PiTu
)
public class IMPiTuMessage extends IMFileMessage {

    @AVIMMessageField(
            name = LeanArgs.LEFT
    )
    JSONObject left;

    @AVIMMessageField(
            name = LeanArgs.RIGHT
    )
    JSONObject right;

    @AVIMMessageField(
            name = LeanArgs.IS_COMPLETE
    )
    int isComplete;
    @AVIMMessageField(
            name = LeanArgs.COMPLETED_IMG
    )
    String completeImg;
    @AVIMMessageField(
            name = LeanArgs.COMPLETED_SOURCE
    )
    String completeSource;
    @AVIMMessageField(
            name = LeanArgs.TASK_MSG_ID
    )
    String taskMsgId;

    public static final Creator<IMPiTuMessage> CREATOR = new AVIMMessageCreator<>(IMPiTuMessage.class);

    /**
     * 构造请采用MessageFactory构造工厂
     *
     * @param format
     */
    IMPiTuMessage(@NonNull String format) {
        super(format);
    }

    public JSONObject getLeft() {
        return left;
    }

    public void setLeft(JSONObject left) {
        this.left = left;
    }

    public JSONObject getRight() {
        return right;
    }

    public void setRight(JSONObject right) {
        this.right = right;
    }

    public int getIsComplete() {
        return isComplete;
    }

    public void setIsComplete(int isComplete) {
        this.isComplete = isComplete;
    }

    public String getCompleteImg() {
        return completeImg;
    }

    public void setCompleteImg(String completeImg) {
        this.completeImg = completeImg;
    }

    public String getTaskMsgId() {
        return taskMsgId;
    }

    public void setTaskMsgId(String taskMsgId) {
        this.taskMsgId = taskMsgId;
    }

    public String getCompleteSource() {
        return completeSource;
    }

    public void setCompleteSource(String completeSource) {
        this.completeSource = completeSource;
    }

    public IMHalfPiTu leftPiTu() {
        return IMHalfPiTu.parse(left);
    }

    public IMHalfPiTu rightPiTu() {
        return IMHalfPiTu.parse(right);
    }

    @Override
    public String getExtraStr() {
        return super.getExtraStr() + left + right + isComplete + completeImg + completeSource + taskMsgId;
    }

    @Override
    public boolean checkArgs() {
        return super.checkArgs() && IMFile.TYPE_PiTu.equals(format) &&
                left != null && right != null && !left.isEmpty() && !right.isEmpty();
    }

    @Override
    public boolean checkCanSend() {
        if (!super.checkCanSend()) return false;
        if (isComplete == 1) {
            return !TextUtil.isEmpty(completeImg);
        } else {
            IMHalfPiTu leftPiTu = leftPiTu();
            if (leftPiTu == null || !TextUtil.isPiTuKey(leftPiTu.getImage())) {
                return false;
            }
            IMHalfPiTu rightPiTu = rightPiTu();
            if (rightPiTu == null || !TextUtil.isPiTuKey(rightPiTu.getImage())) {
                return false;
            }
            return true;
        }
    }
}
