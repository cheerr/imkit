package com.conglai.leankit.db;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.avos.avoscloud.im.v2.AVIMMessage;
import com.conglai.dblib.android.Init;
import com.conglai.dblib.android.Message;
import com.conglai.dblib.android.MessageDao;
import com.conglai.dblib.observer.DbObserver;
import com.conglai.leankit.model.message.IMCardMessage;
import com.conglai.leankit.model.message.IMPiTuMessage;
import com.conglai.leankit.model.message.MessageFactory;
import com.conglai.leankit.util.MessageUtils;
import com.conglai.leankit.util.TextUtil;

import java.util.List;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.query.QueryBuilder;

/**
 * Created by chenwei on 16/7/21.
 */

public class MessageDbHelper extends BaseIMDbHelper<MessageDao, Message> {

    private static String TAG = MessageDbHelper.class.getSimpleName();

    private static MessageDbHelper instance;
    private MessageDao dao;
    private ChangeListener changeListener;

    private MessageDbHelper(Context c) {
        super(c);
    }

    @Override
    public MessageDao getDao() {
        return dao;
    }

    @Override
    public String getUniqueKey(@NonNull Message message) {
        return message.getNativeMessageId();
    }

    public static MessageDbHelper getInstance(Context context) {
        if (instance == null || instance.dao == null) {
            synchronized (MessageDbHelper.class) {
                if (instance == null || instance.dao == null) {
                    instance = new MessageDbHelper(context);
                    instance.dao = instance.getSession().getMessageDao();
                }
            }
        }
        return instance;
    }

    public void updateOrSave(Message message, boolean ignore) {
        if (message == null || TextUtils.isEmpty(message.getNativeMessageId()))
            return;

        Message oldMessage = queryNativeMessage(message.getNativeMessageId(), message.getMessageId());

        if (oldMessage != null) {
            message.setNativeMessageId(oldMessage.getNativeMessageId());
            message.setMessageFlag(oldMessage.getMessageFlag());
            message.setId(oldMessage.getId());
        }

        //自己的消息默认已读
        if (message.getReadStatus() == Init.UNREAD && getUid().equals(message.getFrom())) {
            message.setReadStatus(Init.READED);
        }

        //有messageId的消息肯定已发送成功
        if (!TextUtils.isEmpty(message.getMessageId()) &&
                message.getStatus() != AVIMMessage.AVIMMessageStatus.AVIMMessageStatusReceipt.getStatusCode()) {
            message.setStatus(AVIMMessage.AVIMMessageStatus.AVIMMessageStatusSent.getStatusCode());
        }

        //需要更新的节点内容上如果和原来节点一样,就不更新了
        if (compare(message, oldMessage))
            return;

        switch (message.getMessageType()) {
            case MessageFactory.AVIMMessageType_PiTu: {
                IMPiTuMessage imPiTuMessage = MessageUtils.messageToIMPiTuMessage(message);
                if (imPiTuMessage != null && imPiTuMessage.getIsComplete() == 1 && !TextUtil.isEmpty(imPiTuMessage.getTaskMsgId())) {
                    Message taskMessage = queryMessageByMessageId(imPiTuMessage.getTaskMsgId());
                    if (taskMessage != null) {
                        delete(dao, taskMessage);
                    }
                }
            }
            break;
            case MessageFactory.AVIMMessageType_CARD: {
                IMCardMessage imCardMessage = MessageUtils.messageToIMCardMessage(message);
                if (imCardMessage != null && imCardMessage.getIsComplete() == 1 && !TextUtil.isEmpty(imCardMessage.getTaskMsgId())) {
                    Message taskMessage = queryMessageByMessageId(imCardMessage.getTaskMsgId());
                    if (taskMessage != null) {
                        delete(dao, taskMessage);
                    }
                }
            }
            break;
        }

        if (changeListener != null) {
            if (oldMessage != null) {
                message = changeListener.change(message, oldMessage);
            } else {
                message = changeListener.onNew(message);
            }
        }

        if (message.getId() == null) {
            dao.insertOrReplace(message);
        } else {
            dao.update(message);
        }

        if (!ignore) {
            super.saveInCache(message);
        } else {
            String key = getUniqueKey(message);
            super.putInCache(key, message);
        }
    }

    public void updateOrSave(Message message) {
        updateOrSave(message, false);
    }


    private Message queryNativeMessage(String nativeMessageId, String messageId) {
        Message message = null;
        if (!TextUtil.isEmpty(nativeMessageId)) {
            message = queryMessageByNativeMessageId(nativeMessageId);
        }
        if (message == null && !TextUtil.isEmpty(messageId)) {
            message = queryMessageByMessageId(messageId);
        }
        return message;
    }

    /**
     * @param messageId
     * @return
     */
    public Message queryMessageByMessageId(String messageId) {
        if (TextUtils.isEmpty(messageId))
            return null;
        QueryBuilder<Message> qb = dao.queryBuilder();
        qb.where(MessageDao.Properties.MessageId.eq(messageId), MessageDao.Properties.Uid.eq(getUid()));
        return unique(qb);
    }

    public List<Message> queryAllSendingMessage() {
        QueryBuilder<Message> qb = dao.queryBuilder();
        qb.where(MessageDao.Properties.MessageId.isNull(), MessageDao.Properties.Status.eq(AVIMMessage.AVIMMessageStatus.AVIMMessageStatusSending.getStatusCode()), MessageDao.Properties.Uid.eq(getUid()));
        return qb.list();
    }

    /**
     * @param nativeMessageId
     * @return
     */
    public Message queryMessageByNativeMessageId(String nativeMessageId) {
        if (TextUtils.isEmpty(nativeMessageId))
            return null;
        Message message = getInCache(nativeMessageId);
        if (message != null) {
            return message;
        }
        QueryBuilder<Message> qb = dao.queryBuilder();
        qb.where(MessageDao.Properties.NativeMessageId.eq(nativeMessageId), MessageDao.Properties.Uid.eq(getUid()));
        return unique(qb);
    }

    /**
     * 查询conversationId下的所有 Message
     *
     * @param conversationId
     * @return
     */
    public List<Message> queryShowMessageByConversationId(String conversationId) {
        if (TextUtils.isEmpty(conversationId))
            return null;
        QueryBuilder<Message> qb = dao.queryBuilder();
        qb.where(MessageDao.Properties.ConversationId.eq(conversationId),
                MessageDao.Properties.Hide.notEq(1), MessageDao.Properties.Uid.eq(getUid()));
        return qb.list();
    }

    public List<Message> queryUnReadMessagesByConversationId(String conversationId) {
        if (TextUtils.isEmpty(conversationId))
            return null;
        QueryBuilder<Message> qb = dao.queryBuilder();
        qb.where(MessageDao.Properties.ReadStatus.eq(Init.UNREAD)
                , MessageDao.Properties.ConversationId.eq(conversationId)
                , MessageDao.Properties.From.notEq(getUid())
                , MessageDao.Properties.Hide.notEq(1)
                , MessageDao.Properties.Uid.eq(getUid()));
        return qb.list();
    }

    /**
     * 所有未读消息设置为已读
     *
     * @param conversationId
     * @return
     */
    public int setMessageReadByConversationId(String conversationId) {
        List<Message> messages = queryUnReadMessagesByConversationId(conversationId);
        int count = messages == null ? 0 : messages.size();
        if (count > 1) {
            DbObserver.getInstance().beginInterrupt();
        }
        if (count > 0) {
            for (Message message : messages) {
                Message clone = (Message) message.clone();
                clone.setReadStatus(Init.READED);
                updateOrSave(clone);
            }
        }
        if (count > 1) {
            DbObserver.getInstance().commit();
        }
        return count;
    }

    /**
     * 删除Message
     *
     * @param nativeId
     */
    public void deleteMessageByNativeMessageId(String nativeId) {
        QueryBuilder<Message> qb = dao.queryBuilder();
        qb.where(MessageDao.Properties.NativeMessageId.eq(nativeId), MessageDao.Properties.Uid.eq(getUid()));
        List<Message> messages = qb.list();
        if (messages != null) {
            for (Message m : messages) {
                delete(dao, m);
            }
        }
    }

    @Override
    protected void delete(AbstractDao dao, Message message) {
        if (changeListener != null) {
            changeListener.onDel(message);
        }
        super.delete(dao, message);
    }

    public interface ChangeListener {
        public Message change(Message newMsg, Message oldMessage);

        public Message onNew(Message newMessage);

        public Message onDel(Message oldMessage);
    }

    public ChangeListener getChangeListener() {
        return changeListener;
    }

    public void setChangeListener(ChangeListener changeListener) {
        this.changeListener = changeListener;
    }
}
