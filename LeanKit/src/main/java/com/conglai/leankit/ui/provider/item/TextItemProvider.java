package com.conglai.leankit.ui.provider.item;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.conglai.dblib.android.Message;
import com.conglai.leankit.R;
import com.conglai.leankit.core.LeanIM;
import com.conglai.leankit.model.message.IMTextMessage;
import com.conglai.leankit.ui.model.UIMessage;
import com.conglai.leankit.ui.widget.LeanTextView;
import com.conglai.leankit.util.MessageUtils;

/**
 * Created by chenwei on 16/7/21.
 */

public class TextItemProvider extends ItemProvider<IMTextMessage> {


    public TextItemProvider(Context context, UIMessage uiMessage) {
        super(context, uiMessage);
    }

    @Override
    public View onCreateView(Context context, ViewGroup viewGroup) {
        View view = LayoutInflater.from(context).inflate(R.layout.lean_im_text_conversation_item, viewGroup, false);
        view.setTag(new ViewHolder(view));
        return view;
    }

    @Override
    public void onBindView(View itemView, int position, Message message) {
        IMTextMessage imTextMessage = messageTo(message);
        ViewHolder holder = (ViewHolder) itemView.getTag();

        if (imTextMessage == null) {
            holder.itemView.setVisibility(View.GONE);
            return;
        } else {
            holder.itemView.setVisibility(View.VISIBLE);
        }
        //自己
        if (LeanIM.getInstance().selfUid().equals(imTextMessage.getFrom())) {
            holder.contentText.setBackgroundResource(R.drawable.selector_lean_im_conversation_bg_right);
            holder.contentText.setTextColor(getContext().getResources().getColor(R.color.lean_im_right_text_color));
        }
        //别人
        else {
            holder.contentText.setBackgroundResource(R.drawable.selector_lean_im_conversation_bg_left);
            holder.contentText.setTextColor(getContext().getResources().getColor(R.color.lean_im_left_text_color));
        }
        holder.contentText.setText(imTextMessage.getText());
        holder.contentText.setPressed(false);
    }

    @Override
    public IMTextMessage messageTo(Message message) {
        return MessageUtils.messageToIMTextMessage(message);
    }

    @Override
    public View getEventView(View itemView) {
        return itemView;
    }

    @Override
    public Spannable getContentSummary(Message message) {
        return new SpannableString(messageTo(message).getText());
    }

    public static class ViewHolder {

        public View itemView;
        public LeanTextView contentText;

        public ViewHolder(View view) {
            this.itemView = view;
            this.contentText = (LeanTextView) view.findViewById(R.id.im_text_content);
        }
    }
}
