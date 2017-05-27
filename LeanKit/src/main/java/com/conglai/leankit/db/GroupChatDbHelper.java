package com.conglai.leankit.db;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.conglai.dblib.android.GroupChat;
import com.conglai.dblib.android.GroupChatDao;

import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.dao.query.WhereCondition;

/**
 * Created by chenwei on 16/7/21.
 */

public class GroupChatDbHelper extends BaseIMDbHelper<GroupChatDao, GroupChat> {

    private static String TAG = GroupChatDbHelper.class.getSimpleName();

    private static GroupChatDbHelper instance;
    private GroupChatDao dao;

    private WhereCondition SELF_COND = GroupChatDao.Properties.Uid.eq(getUid());

    private GroupChatDbHelper(Context c) {
        super(c);
    }

    @Override
    public GroupChatDao getDao() {
        return dao;
    }

    public static GroupChatDbHelper getInstance(Context context) {
        if (instance == null || instance.dao == null) {
            synchronized (GroupChatDbHelper.class) {
                if (instance == null || instance.dao == null) {
                    instance = new GroupChatDbHelper(context);
                    instance.dao = instance.getSession().getGroupChatDao();
                }
            }
        }
        return instance;
    }

    @Override
    public String getUniqueKey(@NonNull GroupChat groupChat) {
        return groupChat.getGroupId();
    }

    public void updateOrSave(GroupChat groupChat) {
        if (groupChat == null || TextUtils.isEmpty(groupChat.getGroupId())) return;


        GroupChat oldGroupChat = null;

        if (groupChat.getChatId() == null) {
            oldGroupChat = queryGroupChatByGroupId(groupChat.getGroupId());
            if (oldGroupChat != null) {
                groupChat.setId(oldGroupChat.getId());
            }
        }

        if (String.valueOf(groupChat).equals(String.valueOf(oldGroupChat)))
            return;

        if (groupChat.getId() == null) {
            dao.insertOrReplace(groupChat);
        } else {
            dao.update(groupChat);
        }
        super.saveInCache(groupChat);
    }

    /**
     * @param groupId
     * @return
     */
    public GroupChat queryGroupChatByGroupId(String groupId) {
        if (TextUtils.isEmpty(groupId))
            return null;
        if (getInCache(groupId) != null) {
            return getInCache(groupId);
        }
        QueryBuilder<GroupChat> qb = dao.queryBuilder();
        qb.where(GroupChatDao.Properties.GroupId.eq(groupId), SELF_COND);
        return unique(qb);
    }

    /**
     * @param chatId
     * @return
     */
    public GroupChat queryGroupChatByChatId(String chatId) {
        if (TextUtils.isEmpty(chatId))
            return null;
        QueryBuilder<GroupChat> qb = dao.queryBuilder();
        qb.where(GroupChatDao.Properties.ChatId.eq(chatId), SELF_COND);
        return unique(qb);
    }

    public String getChatId(String groupId) {
        GroupChat chat = queryGroupChatByGroupId(groupId);
        return chat == null ? "" : chat.getChatId();
    }

    public String getGroupId(String chatId) {
        GroupChat chat = queryGroupChatByChatId(chatId);
        return chat == null ? "" : chat.getGroupId();
    }
}
