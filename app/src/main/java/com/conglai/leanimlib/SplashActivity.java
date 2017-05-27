package com.conglai.leanimlib;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMException;
import com.conglai.dblib.android.Message;
import com.conglai.leankit.core.LeanIM;
import com.conglai.leankit.core.MessageSendCallback;
import com.conglai.leankit.model.message.IMCardMessage;
import com.conglai.leankit.model.message.IMImageMessage;
import com.conglai.leankit.model.message.IMTextMessage;
import com.conglai.leankit.model.message.MessageFactory;
import com.conglai.leankit.model.message.file.IMAudio;
import com.conglai.leankit.model.message.file.IMCardMedia;
import com.conglai.leankit.model.message.file.IMPhoto;
import com.conglai.leankit.model.message.file.IMVideo;
import com.conglai.leankit.util.Utils;

import java.util.Random;

/**
 * Created by chenwei on 16/7/20.
 */

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_view);


        findViewById(R.id.convlist).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SplashActivity.this, ConversationListActivity.class));
            }
        });


        findViewById(R.id.send_text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessageToGroup(randomStr(), ((EditText) findViewById(R.id.who)).getText().toString());
            }
        });

        findViewById(R.id.send_img).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendImageTo(((EditText) findViewById(R.id.who)).getText().toString());
            }
        });

        IMCardMessage taskCard = MessageFactory.createCardMessage(0, IMCardMedia.TYPE_AUDIO, "视频任务待完成", null);

        Log.i("SplashActivity", "message task:" + taskCard.getContent());

        IMCardMedia imageMedia = new IMCardMedia();
        IMPhoto photo = new IMPhoto();
        photo.setHeight(100);
        photo.setWidth(100);
        photo.setKey("key");
        photo.setSource("source");
        imageMedia.setImage(photo);
        IMCardMessage imageCard = MessageFactory.createCardMessage(1, IMCardMedia.TYPE_IMG, "图片任务完成", imageMedia);
        imageCard.setTaskMsgId("task_msg_id");
        Log.i("SplashActivity", "message image:" + imageCard.getContent());

        IMCardMedia audioMedia = new IMCardMedia();
        IMAudio audio = new IMAudio();
        audio.setDuration(45);
        audio.setKey("key");
        audio.setSource("source");
        audioMedia.setAudio(audio);
        IMCardMessage audioCard = MessageFactory.createCardMessage(1, IMCardMedia.TYPE_AUDIO, "语音任务完成", audioMedia);
        audioCard.setTaskMsgId("task_msg_id");
        Log.i("SplashActivity", "message audio:" + audioCard.getContent());

        IMCardMedia videoMedia = new IMCardMedia();
        IMVideo video = new IMVideo();
        audio.setDuration(45);
        audio.setKey("key");
        audio.setSource("source");
        video.setHeight(100);
        video.setWidth(100);
        video.setThumb_sourceAddr("thumb_sourceAddr");
        video.setThumb_key("thumb_key");
        videoMedia.setVideo(video);
        IMCardMessage videoCard = MessageFactory.createCardMessage(1, IMCardMedia.TYPE_VIDEO, "视频任务完成", videoMedia);
        videoCard.setTaskMsgId("task_msg_id");
        Log.i("SplashActivity", "message video:" + videoCard.getContent());

    }

    private void sendMessageToGroup(final String message, String group) {
        if (TextUtils.isEmpty(group)) {
            group = "android_test";
        }
        appendMessage("---- send \'" + message + "\'  to  group\' " + group + "\' ----");
    }

    private void sendMessage(AVIMConversation conversation, final String message) {
        IMTextMessage imTextMessage = MessageFactory.createTextMessage(message);
        LeanIM.getInstance().sendMessageToConversation(conversation.getConversationId(), imTextMessage, new MessageSendCallback() {

            @Override
            public void onSuccess(Message message) {
                Toast.makeText(getApplicationContext(), "发送信息成功", Toast.LENGTH_SHORT).show();
                appendMessage("success>> " + message);
            }

            @Override
            public void onFailure(AVIMException e) {
                appendMessage("error>> " + e.getMessage());

            }
        });
    }

    void appendMessage(String msg) {
        ((EditText) findViewById(R.id.message_show)).append(msg + "\n");
    }

    public String randomStr() {
        String mb = "一个幽灵，共产主义的幽灵，在欧洲游荡。为了对这个幽灵进行神圣的围剿，旧欧洲的一切势力，教皇和沙皇、梅特涅和基佐、法国的激进派和德国的警察，都联合起来了。 \n" +
                "有哪一个反对党不被它的当政的敌人骂为共产党呢？又有哪一个反对党不拿共产主义这个罪名去回敬更进步的反对党人和自己的反动敌人呢？ \n" +
                "从这一事实中可以得出两个结论： \n" +
                "共产主义已经被欧洲的一切势力公认为一种势力； \n" +
                "现在是共产党人向全世界公开说明自己的观点、自己的目的、自己的意图并且拿党自己的宣言来反驳关于共产主义幽灵的神话的时候了。 \n" +
                "为了这个目的，各国共产党人集会于伦敦，拟定了如下的宣言，用英文、法文、德文、意大利文、弗拉芒文和丹麦文公布于世。 \n";
        Random random = new Random();
        int a = random.nextInt(mb.length());
        int b = random.nextInt(mb.length());
        if (a == b) {
            a = 0;
            b = mb.length() - 1;
        }
        return mb.substring(Math.min(a, b), Math.max(a, b));
    }


    private void sendImageTo(String group) {
        if (TextUtils.isEmpty(group)) {
            group = "android_test";
        }
        appendMessage("---- sendImage \'" + "7862ef0b3475ab65826ec58943404b2e" + "\'  to  group \'" + group + "\'  ----");
    }

    private void sendImage(AVIMConversation conversation) {

        IMPhoto imFile = new IMPhoto();
        imFile.setKey("7862ef0b3475ab65826ec58943404b2e");
        imFile.setSource("/storage/emulated/0/DCIM/Screenshots/Screenshot_2016-07-24-19-39-21_com.tencent.mm_1469360457154.jpg");
        imFile.setWidth(Utils.dp2px(this, 100));
        imFile.setHeight(Utils.dp2px(this, 200));

        IMImageMessage imImageMessage = MessageFactory.createImageMessage(imFile);

        LeanIM.getInstance().sendMessageToConversation(conversation.getConversationId(), imImageMessage, new MessageSendCallback() {

            @Override
            public void onSuccess(Message message) {
                Toast.makeText(getApplicationContext(), "发送信息成功", Toast.LENGTH_SHORT).show();
                appendMessage("success>> " + message.getMessageType());
            }

            @Override
            public void onFailure(AVIMException e) {
                appendMessage("error>> " + e.getMessage());
            }
        });
    }
}

