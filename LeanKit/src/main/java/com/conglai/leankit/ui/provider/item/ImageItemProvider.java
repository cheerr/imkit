package com.conglai.leankit.ui.provider.item;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.conglai.common.Debug;
import com.conglai.dblib.android.Message;
import com.conglai.leankit.R;
import com.conglai.leankit.core.LeanIM;
import com.conglai.leankit.engine.imageloader.IMImageLoader;
import com.conglai.leankit.engine.imageloader.IMImageLoaderCallback;
import com.conglai.leankit.engine.imageloader.ImgOpt;
import com.conglai.leankit.engine.store.IMStorageController;
import com.conglai.leankit.model.message.IMImageMessage;
import com.conglai.leankit.ui.model.UIMessage;
import com.conglai.leankit.ui.widget.LeanImageView;
import com.conglai.leankit.util.MessageUtils;
import com.conglai.leankit.util.Utils;

/**
 * Created by chenwei on 16/7/22.
 */

public class ImageItemProvider extends ItemProvider<IMImageMessage> {

    private String lastContent;

    public ImageItemProvider(Context context, UIMessage uiMessage) {
        super(context, uiMessage);
    }

    @Override
    public View onCreateView(Context context, ViewGroup viewGroup) {
        View view = LayoutInflater.from(context).inflate(R.layout.lean_im_image_conversation_item, viewGroup, false);
        view.setTag(new ViewHolder(view));
        return view;
    }

    @Override
    public void onBindView(View itemView, int position, final Message message) {
        final IMImageMessage imImageMessage = messageTo(message);
        final ViewHolder holder = (ViewHolder) itemView.getTag();

        if (imImageMessage == null) {
            holder.itemView.setVisibility(View.GONE);
            return;
        } else {
            holder.itemView.setVisibility(View.VISIBLE);
        }

        //自己
        if (LeanIM.getInstance().selfUid().equals(imImageMessage.getFrom())) {
            holder.imageView.setShape(R.drawable.selector_lean_im_conversation_bg_right);
        }
        //别人
        else {
            holder.imageView.setShape(R.drawable.selector_lean_im_conversation_bg_left);
        }

        if (imImageMessage.getExtraStr().equals(lastContent)) {
            return;
        }

        lastContent = imImageMessage.getExtraStr();

        if (imImageMessage.getWidth() > 0 && imImageMessage.getHeight() > 0) {
            resetWH(holder.imageView, imImageMessage.getWidth(), imImageMessage.getHeight());
            IMImageLoader.getInstance().displayImage(holder.imageView,
                    new ImgOpt().ofKey(imImageMessage.getKey())
                            .ofPath(imImageMessage.getSource()));
        } else {
            resetWH(holder.imageView, Utils.dp2px(getContext(), 100), Utils.dp2px(getContext(), 100));
            IMImageLoader.getInstance().displayImage(holder.imageView,
                    new ImgOpt().ofKey(imImageMessage.getKey())
                            .ofPath(imImageMessage.getSource()), new IMImageLoaderCallback() {
                        @Override
                        public void onSuccess(Bitmap bitmap) {
                            if (bitmap != null) {
                                resetWH(holder.imageView, bitmap.getWidth(), bitmap.getHeight());
                                imImageMessage.setWidth(bitmap.getWidth());
                                imImageMessage.setHeight(bitmap.getHeight());
                                message.setContent(imImageMessage.getContent());
                                IMStorageController.storeMessage(imImageMessage);
                            }
                        }

                        @Override
                        public void onFailure(int code, String errorMsg) {

                        }
                    });
        }
    }

    @Override
    public IMImageMessage messageTo(Message message) {
        return MessageUtils.messageToIMImageMessage(message);
    }

    @Override
    public View getEventView(View itemView) {
        if (itemView.getTag() instanceof ViewHolder) {
            final ViewHolder holder = (ViewHolder) itemView.getTag();
            if (holder != null) {
                return holder.imageView;
            }
        }
        return itemView;
    }

    /**
     * 重置长宽
     *
     * @param imageView
     * @param w
     * @param h
     */
    private void resetWH(LeanImageView imageView, int w, int h) {
        if (w <= 0 || h <= 0) return;

        float max = getContext().getResources().getDimensionPixelOffset(R.dimen.lean_im_max_conversation_width);
        float min = max / 3;

        if (w > max || h > max) {
            float scale = max / Math.max(w, h);
            w = (int) (scale * w);
            h = (int) (scale * h);
        } else if (w < min && h < min) {
            float scale = min / Math.max(w, h);
            w = (int) (scale * w);
            h = (int) (scale * h);
        }

        Debug.i("ImageItemProvider", "w=" + w + ";h=" + h);

        imageView.resetWidthAndHeight(w, h);
    }

    @Override
    public Spannable getContentSummary(Message message) {
        return new SpannableString("[photo]");
    }

    public static class ViewHolder {

        public View itemView;
        public LeanImageView imageView;

        public ViewHolder(View view) {
            this.itemView = view;
            this.imageView = (LeanImageView) view.findViewById(R.id.im_image_content);
            this.imageView.setClickShadow(true);
        }
    }
}
