package com.conglai.leankit.ui;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.avos.avoscloud.im.v2.AVIMMessage;
import com.conglai.common.Debug;
import com.conglai.dblib.android.Message;
import com.conglai.dblib.observer.ObserverItem;
import com.conglai.dblib.observer.ObserverNode;
import com.conglai.leankit.R;
import com.conglai.leankit.core.LeanIM;
import com.conglai.leankit.db.MessageDbHelper;
import com.conglai.leankit.engine.imageloader.IMImageLoader;
import com.conglai.leankit.engine.imageloader.ImgOpt;
import com.conglai.leankit.model.message.MessageFactory;
import com.conglai.leankit.model.user.IMUser;
import com.conglai.leankit.ui.config.ConversationConfig;
import com.conglai.leankit.ui.model.IMReceive;
import com.conglai.leankit.ui.model.UIMessage;
import com.conglai.leankit.ui.provider.adapter.BaseAdapter;
import com.conglai.leankit.ui.provider.factory.ProviderFactory;
import com.conglai.leankit.ui.provider.item.ItemProvider;
import com.conglai.leankit.ui.widget.CircleImageView;
import com.conglai.leankit.ui.widget.SendStatusView;
import com.conglai.leankit.util.MessageUtils;
import com.conglai.leankit.util.TextUtil;
import com.conglai.leankit.util.Utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * 外部的集成走IMConversationAdapter
 * <p>
 * Created by chenwei on 16/7/21.
 */
public abstract class IMConversationAdapter extends BaseAdapter<Message> {

    private static String TAG = IMConversationAdapter.class.getSimpleName();
    private ObserverNode mObserverNode;

    public IMConversationAdapter(Context context) {
        super(context);
        mObserverNode = new ObserverNode();
        mObserverNode.registerToDbObserver();
    }

    @Override
    public int getItemViewType(int position) {
        return MessageFactory.obtainType(getItem(position).getMessageType());
    }

    @Override
    public int getViewTypeCount() {
        return MessageFactory.getSupportTypes();
    }

    /**
     * 过滤collection,保证NativeMessageId唯一
     *
     * @param collection
     */
    public void addCollection(Collection<Message> collection) {
        if (getList() == null || getList().size() == 0) {
            super.addCollection(collection);
            return;
        }
        Collection<Message> coll = new ArrayList<>();
        if (collection != null && collection.size() > 0) {
            List<Message> messages = getList();
            HashSet<String> idSet = new HashSet<>();
            for (Message message : messages) {
                idSet.add(message.getNativeMessageId());
            }
            for (Message c : collection) {
                if (!TextUtils.isEmpty(c.getNativeMessageId()) && !idSet.contains(c.getNativeMessageId())
                        && !c.getHide().equals(1)) {
                    coll.add(c);
                    idSet.add(c.getNativeMessageId());
                }
            }
        }
        super.addCollection(coll);
    }

    @Override
    protected View newView(Context context, int position, ViewGroup viewGroup) {
        Message message = getItem(position);

        ViewGroup parentView = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.lean_im_conversation_item_cover, viewGroup, false);
        ViewHolder holder = new ViewHolder(parentView);

        holder.itemProvider = ProviderFactory.getProvider(context, message);
        if (holder.itemProvider != null) {
            View view = holder.itemProvider.onCreateView(context, parentView);
            holder.contentLayout.addView(view);
            holder.contentView = view;
        }
        parentView.setTag(holder);
        return parentView;
    }

    @Override
    protected void bindView(View view, final int position, Message message) {
        mObserverNode.removeObserverItem(message.getNativeMessageId());
        if (isNeedObserve(message)) {
            //针对没有完成上传的节点做状态监听
            ObserverItem observerItem = new ObserverItem(message.getNativeMessageId(), view) {
                @Override
                protected void notifyChanged(String nativeId, Object obj) {
                    Debug.i(TAG, "notifyChanged: " + nativeId);
                    Message msg = MessageDbHelper.getInstance(getContext()).queryMessageByNativeMessageId(nativeId);
                    //节点删除
                    if (msg == null) {
                        remove(findPosition(nativeId));
                        notifyDataSetChanged();
                        return;
                    }
                    observerView((View) obj, position, msg);
                }
            };
            mObserverNode.addObserverItem(observerItem);
        }
        observerView(view, position, message);
    }

    /**
     * 是否监听
     *
     * @param message
     * @return
     */
    private boolean isNeedObserve(Message message) {
        if (TextUtil.isEmpty(message.getMessageId())) return true;

        switch (message.getMessageType()) {
            case MessageFactory.AVIMMessageType_PiTu:
            case MessageFactory.AVIMMessageType_CARD:
                return true;
        }
        return false;
    }

    /**
     * 观察操作
     *
     * @param view
     * @param position
     * @param message
     */
    protected void observerView(View view, final int position, Message message) {
        message = message == null ? null : MessageDbHelper.getInstance(getContext()).queryMessageByNativeMessageId(message.getNativeMessageId());
        Debug.i(TAG, "observerView: " + message);
        final ViewHolder holder = (ViewHolder) view.getTag();
        if (holder.itemProvider == null || message == null) {
            holder.contentContainer.setVisibility(View.GONE);
            holder.errorMessage.setVisibility(View.VISIBLE);
            holder.errorMessage.setText(message == null ? R.string.lean_im_message_already_deleted : R.string.lean_im_message_un_support);
            return;
        } else {
            holder.contentContainer.setVisibility(View.VISIBLE);
            holder.errorMessage.setVisibility(View.GONE);
        }
        final UIMessage uiMessage = holder.itemProvider.getUIMessage();
        IMUser imUser = MessageUtils.getUserFromMessage(message);
        //显示头像,且是自己
        if (uiMessage.isShowPortrait() && LeanIM.getInstance().selfUid().equals(message.getFrom())) {
            holder.mLeftAvatar.setVisibility(View.GONE);
            holder.sendStatus.setStatus(message.getStatus());

            if (holder.sendStatus.getStatus() == AVIMMessage.AVIMMessageStatus.AVIMMessageStatusSent.getStatusCode()
                    && message.getReceiveStatus() == IMReceive.READ) {
                holder.receiveStatus.setVisibility(View.VISIBLE);
            } else {
                holder.receiveStatus.setVisibility(View.GONE);
            }
            holder.mRightAvatar.setVisibility(View.VISIBLE);
            holder.contentLayout.setGravity(Gravity.RIGHT);
            IMImageLoader.getInstance().displayImage(holder.mRightAvatar,
                    new ImgOpt().ofUrl(imUser.getPhoto())
                            .ofDefaultImage(ConversationConfig.DEFAULT_AVATAR));
        }
        //显示头像,别人
        else if (uiMessage.isShowPortrait()) {
            holder.mLeftAvatar.setVisibility(View.VISIBLE);
            holder.sendStatus.setVisibility(View.GONE);
            holder.mRightAvatar.setVisibility(View.GONE);
            holder.receiveStatus.setVisibility(View.GONE);
            holder.contentLayout.setGravity(Gravity.LEFT);
            IMImageLoader.getInstance().displayImage(holder.mLeftAvatar,
                    new ImgOpt().ofUrl(imUser.getPhoto())
                            .ofDefaultImage(ConversationConfig.DEFAULT_AVATAR));
        }
        //不显示头像
        else {
            holder.mLeftAvatar.setVisibility(View.GONE);
            holder.mRightAvatar.setVisibility(View.GONE);
            holder.contentLayout.setGravity(Gravity.CENTER);
        }

        holder.itemView.setPressed(false);
        if (uiMessage.isHide()) {
            holder.itemView.setVisibility(View.GONE);
        } else {
            holder.itemView.setVisibility(View.VISIBLE);
        }

        Message lastMessage = position <= 0 ? null : getItem(position - 1);
        //10分钟间隔以上显示时间
        if (lastMessage != null && Math.abs(Utils.parseLong(message.getTimestamp()) -
                Utils.parseLong(lastMessage.getTimestamp())) > ConversationConfig.MIN_TIME_DUR) {
            holder.timeText.setVisibility(View.VISIBLE);
            holder.timeText.setText(Utils.getConversationTimeStr(message.getTimestamp()));
        } else {
            holder.timeText.setVisibility(View.GONE);
        }

        if (ProviderFactory.containProvider(message.getMessageType())) {
            holder.itemProvider.onBindView(holder.contentView, position, message);
            onObserverView(holder, position, message);
        } else {
            holder.contentContainer.setVisibility(View.GONE);
            holder.errorMessage.setVisibility(View.VISIBLE);
            holder.errorMessage.setText(R.string.lean_im_message_un_support);
        }
    }

    public abstract void onObserverView(ViewHolder holder, final int position, Message message);

    public static class ViewHolder {

        public View itemView;
        public View contentView;


        public CircleImageView mLeftAvatar, mRightAvatar;
        public SendStatusView sendStatus;
        public View receiveStatus;
        public LinearLayout contentLayout;
        public TextView timeText;
        public ItemProvider itemProvider;

        public View contentContainer;
        public TextView errorMessage;

        public ViewHolder(View view) {
            itemView = view;
            mLeftAvatar = (CircleImageView) view.findViewById(R.id.im_conversation_avatar_left);
            mRightAvatar = (CircleImageView) view.findViewById(R.id.im_conversation_avatar_right);
            contentLayout = (LinearLayout) view.findViewById(R.id.im_conversation_content);
            timeText = (TextView) view.findViewById(R.id.im_conversation_time);
            sendStatus = (SendStatusView) view.findViewById(R.id.im_send_status_left);
            contentContainer = view.findViewById(R.id.im_conversation_content_container);
            errorMessage = (TextView) view.findViewById(R.id.im_conversation_error_message);
            receiveStatus = view.findViewById(R.id.im_send_receive_status);
        }
    }

    public void clear() {
        super.clear();
        mObserverNode.unregisterToDbObserver();
    }

    @Override
    protected String getItemKey(Message message) {
        return message.getNativeMessageId();
    }
}
