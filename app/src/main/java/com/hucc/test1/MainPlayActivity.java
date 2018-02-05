package com.hucc.test1;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hucc.test1.mode.MusicInfo;
import com.hucc.test1.util.MusicUtil;
import com.hucc.test1.view.LrcView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by chunchun.hu on 2018/1/26.
 */

public class MainPlayActivity extends BaseActivity {

     public static final String TAG = "MainPlayActivity";
     public static List<MusicInfo> musicList = new ArrayList<MusicInfo>();
     public static int currentLrcIndex = 0;
     public static String currentLrcContent = null;
     public static boolean changeProgress;
     public static boolean scrollLrc;
     public double deltaY;
     public static int playedMesicTime;
     public static int durationMusic;
     public boolean changeSong;
     private int  progress;
     public String  lrcResponse = null;
     public String  albumResponse = null;
     public String  musicLrc = null;
     public boolean showAlbum;
     public boolean showLrc;
     public static List<Map<String,String>> searchResults = new ArrayList<Map<String, String>>();
     public LrcThread lrcThread;

    private TextView  mTitleView = null;
    private TextView  mArtistView = null;
    private ImageView mLrcpicView = null;
    private LrcView   mLrcListView = null;
    private TextView  mLrcTxtView = null;
    private TextView  mStartView = null;
    private TextView  mDurationView = null;
    private SeekBar   mSeekBar = null;
    private ImageView mPreImageView = null;
    private ImageView mPlayImageView = null;
    private ImageView mNextImageView = null;
    private ImageView mCycleImageView = null;
    private Animation mAnimation;
    private GestureDetector mDetector;

    public Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            Log.d(TAG, "msg");
            super.handleMessage(msg);
            if (msg.what == 0){
               //  selectAlbumPic();
            }else if (msg.what == 1){
               Log.d(TAG, "歌词下载完成!");
               mLrcpicView.clearAnimation();
               mLrcpicView.setVisibility(View.GONE);
               mLrcTxtView.setVisibility(View.GONE);
               showLrc = true;
               mLrcListView.setVisibility(View.VISIBLE);
            }else if (msg.what == 2){
               if (MainActivity.isPlaying && !showAlbum)
                   mLrcpicView.startAnimation(mAnimation);
                 mLrcpicView.setVisibility(View.VISIBLE);
                 mLrcTxtView.setVisibility(View.VISIBLE);
                 mLrcListView.setVisibility(View.GONE);
                 showLrc = false;
            }else if (msg.what == 3){
                if (!showLrc || musicList.size() == 0)
                    return;
                mLrcListView.invalidate();
            }else if (msg.what == 4){

            }
        }
    };

    private class LrcThread extends Thread{

        @Override
        public void run() {
            while (true){
                while (currentLrcIndex < musicList.size() && showLrc){
                     if (changeProgress)
                         currentLrcIndex = 0;
                         changeProgress = false;
                         for (int i = currentLrcIndex; i < musicList.size(); i++){
                             MusicInfo info = musicList.get(i);
                             if(info == null)
                                 return;
                             if (info.getStartTime() < MainPlayActivity.playedMesicTime){
                                 currentLrcIndex = i;
                                 mHandler.sendEmptyMessage(3);
                             }
                         }
                }
            }
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "savedInstanceState");
        super.onCreate(savedInstanceState);
         setContentView(R.layout.main_play);

        getSupportActionBar().hide();

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                deltaY = 0;
                scrollLrc = false;
            }
        }, 0, 2000);

        initView();
        lrcThread = new LrcThread();
        lrcThread.start();

        getData();
        updatePlayingUI();
    }

    private void initView() {
       mTitleView = (TextView)findViewById(R.id.title1);
        mArtistView = (TextView)findViewById(R.id.artist1);
        mLrcpicView = (ImageView) findViewById(R.id.lrcpic);
        mAnimation = AnimationUtils.loadAnimation(this,R.anim.priview_ratate);
        if (!showAlbum)
            mLrcpicView.startAnimation(mAnimation);
        mLrcListView = (LrcView)findViewById(R.id.lrcListTxtView);
        mLrcListView.setMovementMethod(ScrollingMovementMethod.getInstance());
        mLrcTxtView = (TextView)findViewById(R.id.lrcTxtView);
        mStartView = (TextView)findViewById(R.id.playedtime);
        mStartView.setText("00:00");
        mDurationView = (TextView)findViewById(R.id.duration);
        mSeekBar = (SeekBar)findViewById(R.id.seekBar);
        mPreImageView = (ImageView)findViewById(R.id.preView);
        mPlayImageView = (ImageView)findViewById(R.id.playView);
        mNextImageView = (ImageView)findViewById(R.id.nextView);
        mCycleImageView = (ImageView)findViewById(R.id.cycleView);
        mTitleView.setOnClickListener(mOnClickListener);
        mArtistView.setOnClickListener(mOnClickListener);
        mLrcpicView.setOnClickListener(mOnClickListener);
        mLrcListView.setOnClickListener(mOnClickListener);
        mLrcTxtView.setOnClickListener(mOnClickListener);
        mStartView.setOnClickListener(mOnClickListener);
        mDurationView.setOnClickListener(mOnClickListener);
        mPreImageView.setOnClickListener(mOnClickListener);
        mPlayImageView.setOnClickListener(mOnClickListener);
        mNextImageView.setOnClickListener(mOnClickListener);
        mCycleImageView.setOnClickListener(mOnClickListener);

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int newProgress, boolean fromUser) {
                Log.d(TAG, "onProgressChanged");
                if (Math.abs(newProgress - progress) < 2){
                    return;
                }
                changeProgress = true;
                playedMesicTime = newProgress * durationMusic / 100;
                Log.d(TAG, "change progress_currentLrcIndex = " + currentLrcIndex);
                seekBar.setProgress(newProgress);
                Intent intent = new Intent();
                intent.putExtra("newProgress",newProgress);
                intent.setAction("action.newProgress");
                sendBroadcast(intent);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mLrcListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mDetector.onTouchEvent(event);
            }
        });
    }

    public View.OnClickListener mOnClickListener =new  View.OnClickListener(){

        @SuppressLint("WrongConstant")
        @Override
        public void onClick(View v) {
             Intent intent = new Intent();
             switch (v.getId()){
                 case R.id.lrcpic:
                     searchAlbumPic();
                     break;
                 case R.id.lrcListTxtView:
                     mHandler.sendEmptyMessage(2);
                     break;
                 case R.id.lrcTxtView:
                     File file = new File(MusicUtil.localUrl);
                     if (file.exists()){
                         showLrc(MusicUtil.localUrl);
                     }
                    // downloadLrc(MainActivity.currentMusicTitle, MainActivity.currentMusicArtist);
                     break;
                 case R.id.preView:
                     mSeekBar.setProgress(0);
                     mStartView.setText("00:00");
                     intent.setAction("action.pre");
                     sendGB(intent);
                     break;
                 case R.id.playView:
                     if (MainActivity.isPlaying){
                          MainActivity.isPlaying = false;
                          updatePlayingUI();
                          intent.setAction("action.pause");
                          sendGB(intent);
                     }else{
                         MainActivity.isPlaying = true;
                         updatePlayingUI();
                         intent.setAction("action.play");
                         sendGB(intent);
                     }
                     break;
                 case R.id.nextView:
                     mSeekBar.setProgress(0);
                     mStartView.setText("00:00");
                     intent.setAction("action.next");
                     sendGB(intent);
                     break;
                 case R.id.cycleView:
                     if (MusicInfo.CYCLE == MusicInfo.CYCLE_LIST){
                       MusicInfo.CYCLE = MusicInfo.CYCLE_RANDOM;
                       mCycleImageView.setImageResource(R.mipmap.play_random);
                       Toast.makeText(MainPlayActivity.this, "随机循环", 300).show();
                     }else if (MusicInfo.CYCLE == MusicInfo.CYCLE_RANDOM){
                         MusicInfo.CYCLE = MusicInfo.CYCLE_SINGLE;
                         mCycleImageView.setImageResource(R.mipmap.play_repeat_one);
                         Toast.makeText(MainPlayActivity.this, "单曲循环", 300).show();
                     }else if (MusicInfo.CYCLE == MusicInfo.CYCLE_SINGLE){
                         MusicInfo.CYCLE = MusicInfo.CYCLE_LIST;
                         mCycleImageView.setImageResource(R.mipmap.play_list);
                         Toast.makeText(MainPlayActivity.this, "列表循环", 300).show();
                         updatePlayingUI();
                     }
                     break;
             }
        }
    };

    private void selectAlbumPic() {

    }

    private void searchAlbumPic() {
         File file = new File(MusicUtil.path);
         if (file.exists()){
             return;
         }
        String searchTitle = null;
        Log.d(TAG, MainActivity.currentMusicTitle + MainActivity.currentMusicArtist);
        try {
            searchTitle = URLEncoder.encode(MainActivity.currentMusicTitle, "UTF-8");
            Log.d(TAG, searchTitle);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void showLrc(String path) {
          if (showLrc){
              mHandler.sendEmptyMessage(1);
              return;
          }
          musicList.clear();
          Log.d(TAG, "showLrc");

        try {
            FileInputStream fis = new FileInputStream(path);
            InputStreamReader is = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(is);
            StringBuilder sb = new StringBuilder("");
            String line = null;
            while ((line = br.readLine()) != null){
                 sb.append(line);
            }
            br.close();
            divideMusicLrc(sb.toString());
            Log.d(TAG, "lrc = " + musicLrc);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void divideMusicLrc(String musicLrc) {
          StringBuilder sb = new StringBuilder("");
          musicLrc = musicLrc.substring(musicLrc.indexOf("[")+1);
          while (musicLrc.indexOf("[") >= 0){
              sb.append("[");
              sb.append(musicLrc.substring(0, musicLrc.indexOf("[")));
              musicLrc = musicLrc.substring(musicLrc.indexOf("[")+1);
              sb.append("\n");
          }
           if (!musicLrc.equals(""))
               sb.append(musicLrc);
          String musicLrcWithTime = sb.toString();
          Log.d(TAG, "musicLrc = " + sb.toString());
          trimTimeLrc(sb.toString());
    }

    private void trimTimeLrc(String str) {
        StringBuilder sb = new StringBuilder("");
        String[]  lrcs = str.split("\n");
        for (int i = 0; i<lrcs.length; i++){
            String line = lrcs[i];
            String startTime = line.substring(line.indexOf("[") + 1, line.indexOf("]"));
            String content = line.substring(line.indexOf("]") + 1);
            Log.d(TAG, "startTime = " + startTime + "----" );
            Log.d(TAG, "content = " + content);

            MusicInfo info = new MusicInfo();
            info.setStartTime(MusicUtil.translateToInt(startTime));
            info.setContent(content);
            musicList.add(info);
        }
        sortMusicLrcList();
    }

    private void sortMusicLrcList() {
         for (int i = 0; i < musicList.size(); i++){
             MusicInfo info = musicList.get(i);
             MusicInfo info1 = musicList.get(i + 1);
             if (info1.getContent().equals("") && info.getStartTime() > info1.getStartTime() && info.getStartTime() > 0){
                 fillContent(i);
             }
         }
             Collections.sort(musicList, new Comparator<MusicInfo>() {
                 @Override
                 public int compare(MusicInfo o1, MusicInfo o2) {
                     return o1.getStartTime() - o2.getStartTime();
                 }
             });

        for (int i=0 ; i< musicList.size(); i++){
             MusicInfo info = musicList.get(i);
             Log.d(TAG, "2-info" + i + ".startTime = " + info.getStartTime() + "/content = " + info.getContent());
        }
        mHandler.sendEmptyMessage(1);
    }

    private void fillContent(int i) {
        MusicInfo info = musicList.get(i);
        MusicInfo info1 = musicList.get(i + 1);
        if (info1.getContent().equals("")){
            fillContent(i+1);
        }
        info.setContent(info1.getContent());
    }

    private void getData() {
        mTitleView.setText(MainActivity.currentMusicTitle);
        mArtistView.setText(MainActivity.currentMusicArtist);
        mDurationView.setText(MainActivity.currentMusicDuration);
        updatePlayingUI();
    }

    private void updatePlayingUI() {
        if (MainActivity.isPlaying){
            if (!showLrc){
                mLrcTxtView.setVisibility(View.VISIBLE);
                if (!showAlbum)
                    mLrcpicView.startAnimation(mAnimation);
            }
            mPlayImageView.setImageResource(R.mipmap.pause);
        }else{
            mLrcpicView.clearAnimation();
            mPlayImageView.setImageResource(R.mipmap.play);
        }
    }

    private void sendGB(Intent intent) {
         this.sendBroadcast(intent);
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("action.updateplayedtime");
        intentFilter.addAction("action.notifyMainPlayActivity");
        intentFilter.addAction("action.refreshAlbum");
        registerReceiver(updateReceiver,intentFilter);

        if (MusicInfo.CYCLE == MusicInfo.CYCLE_LIST){
             mCycleImageView.setImageResource(R.mipmap.play_list);
        }else if (MusicInfo.CYCLE == MusicInfo.CYCLE_RANDOM){
            mCycleImageView.setImageResource(R.mipmap.play_random);
        }else if (MusicInfo.CYCLE == MusicInfo.CYCLE_SINGLE){
            mCycleImageView.setImageResource(R.mipmap.play_repeat_one);
        }

        showAlbumPic();
    }

    private void changeLrc() {
        Log.d(TAG, "changeLrc");
        showLrc = false;
        changeProgress = false;
        currentLrcIndex = 0;
        currentLrcContent = null;
        mLrcpicView.setVisibility(View.VISIBLE);
        mLrcTxtView.setVisibility(View.VISIBLE);
        mLrcListView.setVisibility(View.GONE);
        mLrcListView.invalidate();
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        musicList.clear();
        updatePlayingUI();
    }

    public BroadcastReceiver updateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
             String action = intent.getAction();
             if (action.equals("action.updateplayedtime")){
                 progress = intent.getIntExtra("progress",0);
                 playedMesicTime = intent.getIntExtra("playedTime",0);
                 if (changeSong){
                     mStartView.setText("00:00");
                 }else{
                     mStartView.setText(MusicUtil.formatPlayTime(playedMesicTime));
                 }
                 durationMusic = intent.getIntExtra("duration", 0 );
                 progress = playedMesicTime * 100 / durationMusic;
                 if (MusicInfo.playMusic)
                     MainActivity.currentMusicDuration = MusicUtil.formatDuration(durationMusic);
                 mSeekBar.setProgress(progress);
             }else if (action.equals("action.notifyMainPlayActivity")){
                 if (MusicInfo.MSG == MusicInfo.MSG_PLAY){
                     showAlbum = false;
                     changeLrc();
                     showAlbumPic();
                 }
                 mTitleView.setText(intent.getStringExtra("title"));
                 mArtistView.setText(intent.getStringExtra("artist"));
                 mDurationView.setText(intent.getStringExtra("duration"));

                 if (MainActivity.isPlaying){
                     mPlayImageView.setImageResource(R.mipmap.pause);
                 }else {
                     mPlayImageView.setImageResource(R.mipmap.play);
                 }
             }else if (action.equals("action.destroy")){
                   MainPlayActivity.this.onDestroy();
             }
        }
    };

    private void showAlbumPic() {

        File file = new File(MusicUtil.path);
        if (file.exists()){
            Bitmap bitmap = BitmapFactory.decodeFile(MusicUtil.path);
            mLrcpicView.clearAnimation();
            mLrcpicView.setImageBitmap(bitmap);
            showAlbum = true;
         }else {
             mLrcpicView.setImageResource(R.mipmap.rotate);
        }
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
        this.unregisterReceiver(updateReceiver);
        super.onDestroy();
    }
}
