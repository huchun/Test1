package com.hucc.test1;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import com.hucc.test1.mode.MusicInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chunchun.hu on 2018/1/26.
 */

public class MainPlayActivity extends BaseActivity {

    public static final String TAG = "MainPlayActivity";
    public static List<MusicInfo> musicList = new ArrayList<MusicInfo>();
    public static int currentLrcIndex = 0;
    public static boolean changeProgress;
    public static boolean scrollLrc;



    public Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            Log.d(TAG, "msg");
            super.handleMessage(msg);


        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "savedInstanceState");
        super.onCreate(savedInstanceState);
         setContentView(R.layout.main_play);

        getSupportActionBar().hide();

        initView();

    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onResume");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onResume");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onResume");
        super.onDestroy();
    }

    private void initView() {

    }


}
