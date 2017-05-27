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
import com.conglai.leankit.engine.imageloader.IMImageLoader;
import com.conglai.leankit.engine.imageloader.ImgOpt;
import com.conglai.leankit.model.message.IMLocationMessage;
import com.conglai.leankit.ui.model.UIMessage;
import com.conglai.leankit.ui.widget.LeanImageView;
import com.conglai.leankit.ui.widget.LeanTextView;
import com.conglai.leankit.util.MessageUtils;
import com.conglai.leankit.util.TextUtil;
import com.conglai.leankit.util.Utils;

/**
 * Created by chenwei on 16/7/22.
 */

public class LocationItemProvider extends ItemProvider<IMLocationMessage> {

    private String lastContent;

    public LocationItemProvider(Context context, UIMessage uiMessage) {
        super(context, uiMessage);
    }

    @Override
    public View onCreateView(Context context, ViewGroup viewGroup) {
        View view = LayoutInflater.from(context).inflate(R.layout.lean_im_location_conversation_item, viewGroup, false);
        view.setTag(new ViewHolder(view));
        return view;
    }

    @Override
    public void onBindView(View itemView, int position, Message message) {
        final IMLocationMessage imLocationMessage = messageTo(message);
        final ViewHolder holder = (ViewHolder) itemView.getTag();

        if (imLocationMessage == null) {
            holder.itemView.setVisibility(View.GONE);
            return;
        } else {
            holder.itemView.setVisibility(View.VISIBLE);
        }

        //自己
        if (LeanIM.getInstance().selfUid().equals(imLocationMessage.getFrom())) {
            holder.staticMap.setShape(R.drawable.selector_lean_im_conversation_bg_right);
            Utils.setViewMargins(holder.addressText, 0, 0, Utils.dp2px(getContext(), 7), 0);
        }
        //别人
        else {
            holder.staticMap.setShape(R.drawable.selector_lean_im_conversation_bg_left);
            Utils.setViewMargins(holder.addressText, Utils.dp2px(getContext(), 7), 0, 0, 0);
        }


        if (imLocationMessage.getExtraStr().equals(lastContent)) {
            return;
        }

        lastContent = imLocationMessage.getExtraStr();


        IMImageLoader.getInstance().displayImage(holder.staticMap,
                new ImgOpt().ofPath(getStaticMapUrl(imLocationMessage)));

        if (TextUtil.isEmpty(imLocationMessage.getAddress())) {
            holder.addressText.setVisibility(View.GONE);
        } else {
            holder.addressText.setText(imLocationMessage.getAddress());
            holder.addressText.setVisibility(View.VISIBLE);
        }
    }

    public String getStaticMapUrl(IMLocationMessage message) {
        return Utils.getStaticMapUrl(message.getLatitude(), message.getLongitude());
    }

    @Override
    public IMLocationMessage messageTo(Message message) {
        return MessageUtils.messageToIMLocationMessage(message);
    }

    @Override
    public View getEventView(View itemView) {
        if (itemView.getTag() instanceof ViewHolder) {
            final ViewHolder holder = (ViewHolder) itemView.getTag();
            if (holder != null) {
                return holder.staticMap;
            }
        }
        return itemView;
    }

    @Override
    public Spannable getContentSummary(Message message) {
        IMLocationMessage imLocationMessage = messageTo(message);
        if (imLocationMessage != null) {
            return new SpannableString(imLocationMessage.getAddress() + "[" + imLocationMessage.getLatitude() + "," + imLocationMessage.getLongitude() + "]");
        } else {
            return null;
        }
    }

    public static class ViewHolder {

        public View itemView;
        public LeanImageView staticMap;
        public LeanTextView addressText;

        public ViewHolder(View view) {
            this.itemView = view;
            this.staticMap = (LeanImageView) view.findViewById(R.id.im_static_map);
            this.staticMap.setClickShadow(true);
            this.addressText = (LeanTextView) view.findViewById(R.id.im_address);
        }
    }
}
