package com.curonsys.army_android.activity;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.curonsys.army_android.R;

import java.util.ArrayList;

public class BottomActivity extends AppCompatActivity {

    private TextView mTextMessage;

    private ArrayList<String> myStringArray = new ArrayList<String>();
    private ArrayList<String> myStringArrayEmpty = new ArrayList<String>();

    ArrayAdapter<String> mAdapter;
    ArrayAdapter<String> mAdapterEmpty;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home: {
                    mTextMessage.setText("");
                    getMyListData();

                    ListView listView = (ListView) findViewById(R.id.mycontact_list);
                    listView.setAdapter(mAdapter);

                    return true;
                }

                case R.id.navigation_dashboard: {
                    mTextMessage.setText("");
                    ListView listView = (ListView) findViewById(R.id.mycontact_list);
                    listView.setAdapter(mAdapterEmpty);
                    mTextMessage.setText(R.string.bottom_contents);
                    return true;
                }

                case R.id.navigation_notifications: {
                    mTextMessage.setText("");
                    ListView listView = (ListView) findViewById(R.id.mycontact_list);
                    listView.setAdapter(mAdapterEmpty);
                    mTextMessage.setText(R.string.bottom_service);
                    return true;
                }

                case R.id.navigation_info: {
                    mTextMessage.setText("");
                    ListView listView = (ListView) findViewById(R.id.mycontact_list);
                    listView.setAdapter(mAdapterEmpty);
                    mTextMessage.setText(R.string.bottom_Information);
                    return true;
                }
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom);

        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        mTextMessage = (TextView) findViewById(R.id.message);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, myStringArray);
        mAdapterEmpty = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, myStringArrayEmpty);
    }

    private void getMyListData() {
        //
        myStringArray.add("부제               -");
        myStringArray.add("저자               앙투안 드 생텍쥐페리");
        myStringArray.add("카테고리        소설");
        myStringArray.add("출판년도        2013");
        myStringArray.add("페이지            144p");
        myStringArray.add("\n소개\n\n<이상한 나라의 앨리스>, <오즈의 마법사>를 잇는 '허밍버드 클래식'시리즈의 세 전째 책." +
                            "생텍쥐페리가 제2차 세계대전 중에 어른들을 위한 동화로서 집필한 소설이다. 시인이자 극작가 " +
                            "김경주가 번역을 맡았다. 섬세한 감수성으로 원작의 감동을 고스란히 살렸다.\n\n");
    }
}
