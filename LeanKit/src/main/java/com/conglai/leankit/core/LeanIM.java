package com.conglai.leankit.core;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUtils;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.AVIMMessageManager;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.avos.avoscloud.im.v2.callback.AVIMClientStatusCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCreatedCallback;
import com.conglai.common.Debug;
import com.conglai.dblib.android.Message;
import com.conglai.dblib.config.DbConfig;
import com.conglai.dblib.config.DbLibOptions;
import com.conglai.dblib.config.UidProvider;
import com.conglai.dblib.observer.DbObserver;
import com.conglai.leankit.db.GroupChatDbHelper;
import com.conglai.leankit.db.MessageDbHelper;
import com.conglai.leankit.engine.store.IMStorageController;
import com.conglai.leankit.engine.upload.IMMessageUpload;
import com.conglai.leankit.model.message.IMAudioMessage;
import com.conglai.leankit.model.message.IMCardMessage;
import com.conglai.leankit.model.message.IMCustomMessage;
import com.conglai.leankit.model.message.IMFileMessage;
import com.conglai.leankit.model.message.IMImageMessage;
import com.conglai.leankit.model.message.IMLocationMessage;
import com.conglai.leankit.model.message.IMReadCallBackMessage;
import com.conglai.leankit.model.message.IMTextMessage;
import com.conglai.leankit.model.message.IMVideoMessage;
import com.conglai.leankit.model.query.CardQuery;
import com.conglai.leankit.model.query.ChatQuery;
import com.conglai.leankit.model.user.IMUser;
import com.conglai.leankit.model.user.IMUserProvider;
import com.conglai.leankit.receiver.CustomClientEventHandler;
import com.conglai.leankit.util.MessageUtils;
import com.conglai.leankit.util.TextUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by chenwei on 16/7/13.
 */

public class LeanIM {

    //IM的版本
    public static final int VERSION = 1;
    private static final String TAG = "LeanIM";

    public static Context appContext;

    private static LeanIM instance;
    private static IMUserProvider imUserProvider;

    private String selfUid;

    private String loginUid; //登录后的uid
    //自己的会话引擎
    private AVIMClient client;

    //用于保证不重复发送
    private HashSet<String> sendingMap = new HashSet<>();

    private boolean inited = false;

    private LeanIM() {
        registerAllCustomMessageType();
    }

    public synchronized static LeanIM getInstance() {
        if (instance == null) {
            synchronized (LeanIM.class) {
                instance = new LeanIM();
            }
        }
        return instance;
    }

    public static void registerApp(Application context, String appId, String appKey) {
        AVOSCloud.initialize(context, appId, appKey);
        DbObserver.registerDbObserver(MessageDbHelper.class);

        AVIMClient.setClientEventHandler(new CustomClientEventHandler() {

            @Override
            public void onConnectionResume(AVIMClient avimClient) {
                super.onConnectionResume(avimClient);
                Log.i(TAG, "onConnectionResume:");
            }

            @Override
            public void onConnectionPaused(AVIMClient avimClient) {
                Log.i(TAG, "onConnectionPaused:");
            }

            @Override
            public void loginAtOther(AVIMClient avimClient) {
                Log.i(TAG, "loginAtOther");

            }

            @Override
            public void loseClient(AVIMClient avimClient, int code) {
                Log.i(TAG, "loseClient:" + code);
            }
        });
    }

    /**
     * 初始化
     *
     * @param context
     * @param userId  不能为空
     */
    public void initAndLogin(Context context, @NonNull final String userId) {
        this.appContext = context.getApplicationContext();
        this.selfUid = userId;
        this.loginIn(new AVIMClientCallback() {
            @Override
            public void done(AVIMClient avimClient, AVIMException e) {
                if (e == null)
                    Log.i(TAG, "loginIn success ");
            }
        });
        DbLibOptions options = new DbLibOptions();
        options.setUidProvider(new UidProvider() {
            @Override
            public String getUid() {
                return selfUid;
            }
        });
        DbConfig.setOptions(options);
        this.inited = true;
    }

    public boolean isInited() {
        return inited;
    }

    /**
     * 注册自定义的MessageType
     */
    private void registerAllCustomMessageType() {
        AVIMMessageManager.registerAVIMMessageType(IMAudioMessage.class);
        AVIMMessageManager.registerAVIMMessageType(IMTextMessage.class);
        AVIMMessageManager.registerAVIMMessageType(IMImageMessage.class);
        AVIMMessageManager.registerAVIMMessageType(IMLocationMessage.class);
        AVIMMessageManager.registerAVIMMessageType(IMVideoMessage.class);
        AVIMMessageManager.registerAVIMMessageType(IMReadCallBackMessage.class);
    }

    /**
     * 用户信息加载器
     *
     * @param imUserProvider
     */
    public static void setImUserProvider(IMUserProvider imUserProvider) {
        LeanIM.imUserProvider = imUserProvider;
    }

    /**
     * 加载本地的UserInfo
     *
     * @return
     */
    public IMUser getIMUser(String userId) {
        if (TextUtil.isEmpty(userId))
            return null;
        if (imUserProvider != null) {
            return imUserProvider.getIMUser(userId);
        }
        IMUser user = new IMUser();
        user.setUserId(userId);
        return user;
    }

    /**
     * 自己的token
     *
     * @return
     */
    public String selfUid() {
        if (TextUtil.isEmpty(selfUid)) {
            return "";
        }
        return selfUid;
    }

    @NonNull
    public IMUser getSelf() {
        IMUser user = getIMUser(selfUid());
        if (user != null)
            return user;
        return new IMUser();
    }

    /***********
     * 登录
     ************/

    public void loginIn(final AVIMClientCallback clientCallback) {
        //先检测退出
        if (isClientEnable(client) && !TextUtil.isEmpty(loginUid) && !selfUid().equals(loginUid)) {
            loginOut(new AVIMClientCallback() {
                @Override
                public void done(AVIMClient client, AVIMException e) {
                    doLogin(clientCallback);
                }
            });
        } else {
            doLogin(clientCallback);
        }
    }

    private void doLogin(final AVIMClientCallback clientCallback) {
        loginUid = null;
        client = AVIMClient.getInstance(selfUid(), "mobile");
        client.open(new AVIMClientCallback() {
            @Override
            public void done(AVIMClient avimClient, AVIMException e) {
                LeanIM.this.client = avimClient;
                loginUid = selfUid();
                if (clientCallback != null) {
                    clientCallback.done(avimClient, e);
                }
            }
        });
    }

    /**
     * 退出登录
     *
     * @param clientCallback
     */
    public void loginOut(final AVIMClientCallback clientCallback) {
        if (isClientEnable(client)) {
            client.close(new AVIMClientCallback() {
                @Override
                public void done(AVIMClient avimClient, AVIMException e) {
                    client = null;
                    loginUid = null;
                    if (clientCallback != null) {
                        clientCallback.done(avimClient, e);
                    }
                }
            });
        } else {
            if (clientCallback != null) {
                clientCallback.done(null, null);
            }
        }
    }

    /**
     * 1、获取某个会话
     * 2、如果不在会话则加入会话
     *
     * @param conversationId
     * @return
     */
    private void joinConversationByConversationId(final String conversationId, final IMConversationGetListener getListener) {
        obtainClient(new AVIMClientCallback() {
            @Override
            public void done(final AVIMClient avimClient, AVIMException e) {
                if (avimClient != null && !TextUtils.isEmpty(conversationId)) {
                    AVIMConversation conversation = avimClient.getConversation(conversationId);
                    if (conversation == null) {
                        if (getListener != null)
                            getListener.onGet(null);
                    } else {
                        checkJoinConversation(conversation, new AVIMConversationCallback() {
                            @Override
                            public void done(AVIMException e) {
                                if (getListener != null)
                                    getListener.onGet(avimClient.getConversation(conversationId));
                            }
                        });
                    }
                } else {
                    if (getListener != null)
                        getListener.onGet(null);
                }
            }
        });
    }

    private void joinConversationByGroupId(final String groupId, final IMConversationGetListener getListener) {
        String chatId = GroupChatDbHelper.getInstance(appContext).getChatId(groupId);
        if (!TextUtil.isEmpty(chatId)) {
            joinConversationByConversationId(chatId, getListener);
        } else {
            //LeanCloud上存储的groupChat表
            ChatQuery.findByGroupId(groupId, new FindCallback<AVObject>() {
                @Override
                public void done(List<AVObject> list, AVException e) {
                    if (list != null && list.size() > 0) {
                        String conversationId = list.get(0).getString("chatId");
                        if (!TextUtils.isEmpty(conversationId)) {
                            IMStorageController.storeGroupChat(conversationId, groupId);
                        }
                        joinConversationByConversationId(conversationId, getListener);
                    } else {
                        if (getListener != null)
                            getListener.onGet(null);
                    }
                }
            });
        }
    }

    /**
     * @param getListener
     * @param groupId
     * @param members
     */
    public void joinConversationWithMember(final IMConversationGetListener getListener, String groupId, final List<String> members) {
        joinConversationByGroupId(groupId, new IMConversationGetListener() {
            @Override
            public void onGet(final AVIMConversation conversation) {
                if (conversation != null) {
                    List<String> joinList = checkUnJoinList(conversation, members);
                    if (joinList != null && joinList.size() > 0) {
                        conversation.addMembers(joinList, new AVIMConversationCallback() {
                            @Override
                            public void done(AVIMException e) {
                                if (getListener != null)
                                    getListener.onGet(conversation);
                            }
                        });
                    } else {
                        if (getListener != null)
                            getListener.onGet(conversation);
                    }
                } else {
                    if (getListener != null)
                        getListener.onGet(null);
                }
            }
        });
    }

    /**
     * @param groupId
     * @param members
     */
    public void joinConversationWithMember(String groupId, final List<String> members) {
        joinConversationByGroupId(groupId, new IMConversationGetListener() {
            @Override
            public void onGet(final AVIMConversation conversation) {
                if (conversation != null) {
                    List<String> joinList = checkUnJoinList(conversation, members);
                    if (joinList != null && joinList.size() > 0) {
                        conversation.addMembers(joinList, new AVIMConversationCallback() {
                            @Override
                            public void done(AVIMException e) {
                                Debug.print(TAG, "joinConversationWithMember:" + e);
                            }
                        });
                    }
                }
            }
        });
    }

    /**
     * AVIMClient是否可用,不实时,没有连接会重新登录
     *
     * @param client
     * @return
     */
    private boolean isClientEnable(AVIMClient client) {
        return client != null;
    }

    /**
     * 保证获取AVIMClient
     *
     * @param clientCallback
     */
    public void obtainClient(final @NonNull AVIMClientCallback clientCallback) {
        if (isClientEnable(client) && selfUid().equals(loginUid)) {
            client.getClientStatus(new AVIMClientStatusCallback() {
                @Override
                public void done(AVIMClient.AVIMClientStatus avimClientStatus) {
                    if (avimClientStatus != null && avimClientStatus.getCode() != AVIMClient.AVIMClientStatus.AVIMClientStatusOpened.getCode()) {
                        doLogin(clientCallback);
                    } else {
                        clientCallback.done(client, null);
                    }
                }
            });
        } else {
            loginIn(clientCallback);
        }
    }

    /**************************************************
     * 消息发送入口
     **************************************************/

    /**
     * 验证消息
     *
     * @param targets 用户数组
     * @return
     */
    private boolean checkTargetArgs(MessageSendCallback messageSendCallback, String... targets) {

        boolean args = true;
        if (targets == null || targets.length == 0) {
            args = false;
        } else {
            for (String tar : targets) {
                if (TextUtil.isEmpty(tar)) {
                    args = false;
                    break;
                }
            }
        }
        if (!args) {
            if (messageSendCallback != null) {
                messageSendCallback.onFailure(IMErrorStatus.TARGET_EMPTY.toAVIMException());
            }
        }
        return args;
    }

    /**
     * 保证获取AVIMConversation
     *
     * @param getListener
     */
    public void obtainConversationIdByGroupId(final String groupId, @NonNull final IMConversationIdGetListener getListener) {
        String chatId = GroupChatDbHelper.getInstance(appContext).getChatId(groupId);
        if (!TextUtil.isEmpty(chatId)) {
            getListener.onGet(chatId);
        } else {
            ChatQuery.findByGroupId(groupId, new FindCallback<AVObject>() {
                @Override
                public void done(List<AVObject> list, AVException e) {
                    if (list != null && list.size() > 0) {
                        String conversationId = list.get(0).getString("chatId");
                        if (!TextUtils.isEmpty(conversationId)) {
                            IMStorageController.storeGroupChat(conversationId, groupId);
                        }
                        getListener.onGet(conversationId);
                    } else {
                        getListener.onGet(null);
                    }
                }
            });
        }
    }


    public void obtainGroupIdByConversationId(final String chatId, @NonNull final IMConversationIdGetListener getListener) {
        String groupId = GroupChatDbHelper.getInstance(appContext).getChatId(chatId);
        if (!TextUtil.isEmpty(groupId)) {
            getListener.onGet(groupId);
        } else {
            ChatQuery.findByChatId(chatId, new FindCallback<AVObject>() {
                @Override
                public void done(List<AVObject> list, AVException e) {
                    if (list != null && list.size() > 0) {
                        String groupId = list.get(0).getString("groupId");
                        if (!TextUtils.isEmpty(groupId)) {
                            IMStorageController.storeGroupChat(chatId, groupId);
                        }
                        getListener.onGet(groupId);
                    } else {
                        getListener.onGet(null);
                    }
                }
            });
        }
    }

    /**
     * 创建多人会话
     *
     * @param groupId
     * @param members
     * @param callback
     */
    public void createGroupConversation(final String groupId, final List<String> members, final AVIMConversationCreatedCallback callback) {
        Debug.print(TAG, "createGroupConversation");
        ConversationCreate.createGroupConversation(selfUid(), groupId, members, callback);
    }

    /**
     * 发送前先查leancloud的groupChat表,获取conversationId
     *
     * @param groupId
     * @param message
     * @param messageSendCallback
     */
    public void sendMessageToGroupId(final String groupId, final IMCustomMessage message,
                                     final MessageSendCallback messageSendCallback) {
        sendMessageToGroupId(groupId, message, AVIMConversation.NONTRANSIENT_MESSAGE_FLAG, messageSendCallback);
    }

    public void sendMessageToGroupId(final String groupId, final IMCustomMessage message, final int messageFlag,
                                     final MessageSendCallback messageSendCallback) {
        obtainConversationIdByGroupId(groupId, new IMConversationIdGetListener() {
            @Override
            public void onGet(String conversationId) {
                sendMessageToConversation(conversationId, message, messageFlag, messageSendCallback);
            }
        });
    }


    public void sendMessageToConversation(final String conversationId, final IMCustomMessage message,
                                          final MessageSendCallback messageSendCallback) {
        sendMessageToConversation(conversationId, message, AVIMConversation.NONTRANSIENT_MESSAGE_FLAG, messageSendCallback);
    }

    /**
     * 直接针对conversationId发送信息
     *
     * @param conversationId
     * @param message
     * @param messageSendCallback
     */
    public void sendMessageToConversation(final String conversationId, final IMCustomMessage message, final int messageFlag,
                                          final MessageSendCallback messageSendCallback) {
        //第一步验证数据
        if (!checkTargetArgs(messageSendCallback, conversationId)) {
            return;
        }
        if (message == null) {
            if (messageSendCallback != null)
                messageSendCallback.onFailure(IMErrorStatus.MESSAGE_EMPTY.toAVIMException());
            return;
        }
        judgeMessageSend(conversationId, message, messageFlag, new AVIMConversationCallback() {
            @Override
            public void done(AVIMException e) {
                if (messageSendCallback != null) {
                    if (e == null) {
                        Message msg = MessageDbHelper.getInstance(appContext).
                                queryMessageByNativeMessageId(message.getNativeMessageId());
                        if (msg != null) {
                            messageSendCallback.onSuccess(msg);
                        } else {
                            messageSendCallback.onFailure(IMErrorStatus.MESSAGE_NOT_IN_NATIVE.toAVIMException());
                        }
                    } else {
                        messageSendCallback.onFailure(e);
                    }
                }
            }
        });
    }

    /**
     * 检测是否在avimConversation里
     *
     * @param avimConversation
     * @param conversationCallback
     */
    private void checkJoinConversation(@NonNull AVIMConversation avimConversation, final AVIMConversationCallback conversationCallback) {
        List<String> members = avimConversation.getMembers();
        if (members == null || members.isEmpty() || !members.contains(selfUid())) {
            avimConversation.join(conversationCallback);
        } else {
            if (conversationCallback != null)
                conversationCallback.done(null);
        }
    }


    private List<String> checkUnJoinList(@NonNull AVIMConversation avimConversation, List<String> members) {
        if (members == null) return new ArrayList<>();
        List<String> joinedList = avimConversation.getMembers();
        if (joinedList == null) joinedList = new ArrayList<>();
        List<String> unJoinList = new ArrayList<>();
        for (String s : members) {
            if (!joinedList.contains(s) && !selfUid().equals(s)) {
                unJoinList.add(s);
            }
        }
        return unJoinList;
    }

    /**
     * 对消息作出判断
     *
     * @param message
     */
    private void judgeMessageSend(String conversationId, @NonNull IMCustomMessage message, final int messageFlag,
                                  final AVIMConversationCallback conversationCallback) {
        //如股票消息符合要求
        if (message.checkArgs()) {
            //先本地保存
            nativeStore(conversationId, message, messageFlag);
            //保存本地之后就算发送成功,之后在后台发送消息
            if (conversationCallback != null)
                conversationCallback.done(null);
            //如果是文件,且没有上传,直接启动上传操作
            if (!message.checkCanSend() && message instanceof IMFileMessage) {
                IMMessageUpload.getInstance().upload((IMFileMessage) message);
            }
            //如果已经是可以上传状态的节点就直接上传
            if (message.checkCanSend()) {
                sendCMDToPushService(conversationId, message, messageFlag, null);
            }
        } else {
            if (conversationCallback != null)
                conversationCallback.done(IMErrorStatus.MESSAGE_ARG_ERROR.toAVIMException());
        }
    }

    /**
     * 本地存储
     *
     * @param conversationId
     * @param message
     * @param messageFlag
     */
    private void nativeStore(final String conversationId, final IMCustomMessage message, final int messageFlag) {
        message.setConversationId(conversationId);
        message.setFrom(selfUid());
        message.setMessageFlag(messageFlag);
        if (!AVUtils.isConnected(AVOSCloud.applicationContext)) {
            message.setMessageStatus(AVIMMessage.AVIMMessageStatus.AVIMMessageStatusFailed);
            message.setTimestamp(System.currentTimeMillis());
        } else {
            message.setTimestamp(System.currentTimeMillis());
            message.setMessageStatus(AVIMMessage.AVIMMessageStatus.AVIMMessageStatusSending);
        }
        Debug.i(TAG, "nativeStore :" + message);
        IMStorageController.storeMessage(message);
    }

    /**
     * 慎用!!!
     * 正真的发送到服务器
     *
     * @param conversationId
     * @param message
     * @param messageFlag
     * @param conversationCallback
     */
    public void sendCMDToPushService(@NonNull final String conversationId, final IMCustomMessage message, final int messageFlag,
                                     final AVIMConversationCallback conversationCallback) {
        Debug.i(TAG, "sendCMDToPushService :" + message);
        /**
         * 判断消息
         */
        if (message == null || !message.checkCanSend()) {
            sendMessageFailureWithAVIMException(message, conversationCallback, IMErrorStatus.MESSAGE_ARG_ERROR.toAVIMException());
            return;
        }

        final Message uploadMessage = MessageDbHelper.getInstance(appContext).queryMessageByNativeMessageId(message.getNativeMessageId());
        if (uploadMessage == null) {
            if (conversationCallback != null)
                conversationCallback.done(IMErrorStatus.MESSAGE_NOT_IN_NATIVE.toAVIMException());
            return;
        } else {
            if (!TextUtils.isEmpty(uploadMessage.getMessageId())) {
                sendMessageFailureWithAVIMException(message, conversationCallback, IMErrorStatus.MESSAGE_ARG_ERROR.toAVIMException());
                return;
            }
        }

        obtainGroupIdByConversationId(conversationId, new IMConversationIdGetListener() {
            @Override
            public void onGet(String groupId) {
                message.setGroupId(groupId);
                //发消息之前确保已经加入群组
                joinConversationByConversationId(conversationId, new IMConversationGetListener() {
                    @Override
                    public void onGet(final AVIMConversation conversation) {
                        if (conversation != null) {
                            sendAndStore(conversation, message, messageFlag, conversationCallback);
                        } else {
                            sendMessageFailureWithAVIMException(message, conversationCallback, IMErrorStatus.CONVERSATION_EMPTY.toAVIMException());
                        }
                    }
                });
            }
        });
    }

    /**
     * 发送时失败,需要更行数据库状态
     *
     * @param customMessage
     * @param conversationCallback
     * @param e
     */
    private void sendMessageFailureWithAVIMException(IMCustomMessage customMessage, AVIMConversationCallback conversationCallback, AVIMException e) {
        if (customMessage != null) {
            customMessage.setMessageStatus(AVIMMessage.AVIMMessageStatus.AVIMMessageStatusFailed);
            MessageDbHelper.getInstance(appContext).updateOrSave(MessageUtils.toMessage(customMessage));
        }
        if (conversationCallback != null)
            conversationCallback.done(e);
    }

    /**
     * 内部调用,发送并且存储
     *
     * @param conversation
     * @param message
     * @param messageFlag
     * @param conversationCallback
     */
    private void sendAndStore(AVIMConversation conversation, final IMCustomMessage message, final int messageFlag, final AVIMConversationCallback conversationCallback) {
        Debug.i(TAG, "sendAndStore :" + message);
        if (!sendingMap.contains(message.getNativeMessageId())) {
            sendingMap.add(message.getNativeMessageId());
            Debug.i(TAG, "sendAndStore sendMessage :" + message);
            conversation.sendMessage(message, messageFlag, new AVIMConversationCallback() {
                @Override
                public void done(AVIMException e) {
                    Debug.i(TAG, "sendAndStore done AVIMException :" + com.conglai.dblib.util.Utils.toStringDo(e));
                    Debug.i(TAG, "sendAndStore done :" + message);
                    Message oldMessage = MessageDbHelper.getInstance(LeanIM.appContext).queryMessageByNativeMessageId(message.getNativeMessageId());
                    if (oldMessage != null && TextUtil.isEmpty(oldMessage.getMessageId())) {
                        message.setMessageStatus(TextUtils.isEmpty(message.getMessageId()) ? AVIMMessage.AVIMMessageStatus.AVIMMessageStatusFailed :
                                AVIMMessage.AVIMMessageStatus.AVIMMessageStatusSent);
                        message.setMessageFlag(messageFlag);
                        MessageDbHelper.getInstance(appContext).updateOrSave(MessageUtils.toMessage(message));
                    }
                    if (conversationCallback != null) {
                        conversationCallback.done(e);
                    }

                    if (TextUtil.isEmpty(message.getMessageId())) {
                        sendingMap.remove(message.getNativeMessageId());
                    } else {
                        //所有的卡片任务保存到云端
                        if (message instanceof IMCardMessage) {
                            CardQuery.save((IMCardMessage) message);
                        }
                    }
                    //如果是影藏消息,发送后直接删除
                    if (message.getHide() == 1) {
                        MessageDbHelper.getInstance(appContext).deleteMessageByNativeMessageId(message.getNativeMessageId());
                    }
                }
            });
        }
    }

}
