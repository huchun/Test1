package com.hucc.test1;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import static com.hucc.test1.mode.MusicInfo.MSG;
import static com.hucc.test1.mode.MusicInfo.MSG_CONTINUEPLAYING;
import static com.hucc.test1.mode.MusicInfo.MSG_PAUSE;
import static com.hucc.test1.mode.MusicInfo.MSG_PLAY;

/**
 * Created by chunchun.hu on 2018/1/18.
 */

public class MusicService extends Service {
    private static String TAG = "MusicService";
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private int msg;
    private static int musicPosition;
    private Handler mHandler;
    private Timer mTimer;
    private String musicName;
    private String preMusicName;

    //获取Item的内容
    public interface Callbacks{
        public void onItemSelected(Integer id);
    }

    private BroadcastReceiver updateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
               String action = intent.getAction();
               if (action.equals("action.newProgress")){
                   int newProgress = intent.getIntExtra("newProgress",0);
                   int time = newProgress * mediaPlayer.getDuration() / 100;
                   mediaPlayer.seekTo(time);
               }else if (action.equals("action.changesong")){
                     preMusicName();
               }
        }
    };

    private void preMusicName() {
        Log.d(TAG,"preMusicName");
        if (MSG == MSG_PLAY){
             play();
        }else if (MSG == MSG_PAUSE){
             pause();
        }else if (MSG == MSG_CONTINUEPLAYING){
            continuePlaying();
        }
    }

    private void continuePlaying() {
        Log.d(TAG, "continuePlaying");
        try {
            mediaPlayer.start();
          }catch (IllegalStateException e){
            e.printStackTrace();
        }

        mTimer.cancel();
        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (mediaPlayer != null && mediaPlayer.isPlaying()){
                    int playedTime = mediaPlayer.getCurrentPosition();
                    int duration = mediaPlayer.getDuration();
                    Intent updateIntent = new Intent();
                    updateIntent.putExtra("playedTime",playedTime);
                    updateIntent.putExtra("duration",duration);
                    updateIntent.putExtra("progrees",playedTime/duration);
                    updateIntent.setAction("action.updateplayedtime");
                    MusicService.this.sendBroadcast(updateIntent);
                }
            }
        },0,1000);
    }

    private void play() {
        Log.d(TAG, "play currentMusicUrl =" + MainActivity.currentMusicUrl);
        Log.d(TAG, "play currentMusicTitle =" + MainActivity.currentMusicTitle);
        Log.d(TAG, "play currentMusicArtist =" + MainActivity.currentMusicArtist);
        /*new Thread(new Runnable() {
            @Override
            public void run() {*/

                try {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.stop();
                    }
                    musicPosition = 0;
                    mediaPlayer.reset();
                    Log.d(TAG, "play reset ");
                    mediaPlayer.setDataSource(MainActivity.currentMusicUrl);
                    Log.d(TAG, "play setDataSource ");
                    mediaPlayer.prepare();
                    Log.d(TAG, "play prepare ");
                    mediaPlayer.start();
                    Log.d(TAG, "play start ");
                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            if (MainActivity.isPlaying)
                                MainActivity.isPlaying = false;
                            Intent intent = new Intent();
                            intent.setAction("action.nextsong");
                            MusicService.this.sendBroadcast(intent);
                            Log.d(TAG, "sendBroadcast");
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }catch (IllegalStateException e){
                     e.printStackTrace();
                }
         //   }
       // }).start();
        mTimer.cancel();
        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                 if (mediaPlayer != null && mediaPlayer.isPlaying()){
                     int playedTime = mediaPlayer.getCurrentPosition();
                     int duration = mediaPlayer.getDuration();
                     Intent updateIntent = new Intent();
                     updateIntent.putExtra("playedTime",playedTime);
                     updateIntent.putExtra("duration",duration);
                     if(duration >0){
                         updateIntent.putExtra("progrees",playedTime/duration);
                     }
                     updateIntent.setAction("action.updateplayedtime");
                     MusicService.this.sendBroadcast(updateIntent);
                 }
            }
        },0,200);
    }

    private void pause() {
        Log.d(TAG, "pause");
        try {
            mediaPlayer.pause();
            musicPosition = mediaPlayer.getCurrentPosition();
            mTimer.cancel();
        }catch (IllegalStateException e){
             e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        super.onCreate();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("action.newProgress");
        intentFilter.addAction("action.changesong");
        registerReceiver(updateReceiver, intentFilter);
        mTimer = new Timer();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null){
            if (mediaPlayer.isPlaying())
                 mTimer.cancel();
            mediaPlayer.stop();
            mediaPlayer.release();
         }
         this.unregisterReceiver(updateReceiver);
    }
}
