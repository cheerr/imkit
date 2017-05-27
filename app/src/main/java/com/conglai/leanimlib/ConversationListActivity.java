package com.conglai.leanimlib;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class ConversationListActivity extends FragmentActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ConversationListFragment fragment = new ConversationListFragment();
        fragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction().add(R.id.frame_container, fragment)
                .commit();
    }

}
