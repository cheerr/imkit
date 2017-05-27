package com.conglai.leanimlib;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

/**
 * Created by chenwei on 16/7/20.
 */

public class ChatListActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ChatListFragment fragment = new ChatListFragment();
        fragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction().add(R.id.frame_container, fragment)
                .commit();
    }

}
