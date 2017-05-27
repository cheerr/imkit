package com.conglai.leankit.core;

import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCreatedCallback;
import com.conglai.common.Debug;
import com.conglai.leankit.engine.store.IMStorageController;
import com.conglai.leankit.model.query.ChatQuery;
import com.conglai.leankit.util.TextUtil;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * Created by chenwei on 16/10/10.
 */

public class ConversationCreate {

    private static Deque<Task> deque = new ArrayDeque<>();
    private static boolean isDoing = false;

    static class Task {
        String uid;
        String groupId;
        List<String> members;
        AVIMConversationCreatedCallback callback;

        @Override
        public String toString() {
            return "Task{" +
                    "members=" + members +
                    ", uid='" + uid + '\'' +
                    ", groupId='" + groupId + '\'' +
                    '}';
        }
    }

    /**
     * 根据groupId创建一个Conversation
     * <p>
     * 如果Conversation存在则返回原来的,并且把members检查加入一遍
     * <p>
     * 走单一队列,防止重复创建
     *
     * @param uid
     * @param groupId
     * @param members
     * @param callback
     */
    static void createGroupConversation(final String uid, final String groupId, final List<String> members, final AVIMConversationCreatedCallback callback) {
        Task task = new Task();
        task.uid = uid;
        task.groupId = groupId;
        task.members = members == null ? new ArrayList<String>() : members;
        task.callback = callback;
        deque.add(task);
        loopDeque();
    }

    private static void loopDeque() {
        if (!isDoing && !deque.isEmpty()) {
            create(deque.poll());
        }
    }

    private static void create(final Task task) {
        Debug.print("ConversationCreate", "createTask:" + task);
        //判断是否是同一个用户
        if (!LeanIM.getInstance().selfUid().equals(task.uid)) {
            loopDeque();
            return;
        }
        isDoing = true;
        LeanIM.getInstance().joinConversationWithMember(new IMConversationGetListener() {
            @Override
            public void onGet(AVIMConversation conversation) {
                if (conversation == null) {
                    LeanIM.getInstance().obtainClient(new AVIMClientCallback() {
                        @Override
                        public void done(AVIMClient avimClient, AVIMException e) {
                            if (avimClient == null) {
                                resultCallBack(null, e);
                                return;
                            }
                            avimClient.createConversation(task.members, "", null, false, false, new AVIMConversationCreatedCallback() {
                                @Override
                                public void done(AVIMConversation avimConversation, AVIMException e) {
                                    if (avimConversation != null && !TextUtil.isEmpty(avimConversation.getConversationId())) {
                                        ChatQuery.save(task.groupId, avimConversation.getConversationId());
                                        IMStorageController.storeGroupChat(avimConversation.getConversationId(),
                                                task.groupId);
                                    }
                                    resultCallBack(avimConversation, e);
                                }
                            });
                        }
                    });
                } else {
                    resultCallBack(conversation, null);
                }
            }

            /**
             * 结果回调
             * @param avimConversation
             * @param e
             */
            private void resultCallBack(AVIMConversation avimConversation, AVIMException e) {
                if (task.callback != null) {
                    task.callback.done(avimConversation, e);
                }
                isDoing = false;
                loopDeque();
            }

        }, task.groupId, task.members);
    }
}
