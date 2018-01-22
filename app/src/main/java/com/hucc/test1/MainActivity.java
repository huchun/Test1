package com.hucc.test1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hucc.test1.util.MusicUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static com.hucc.test1.MusicInfo.CYCLE;
import static com.hucc.test1.MusicInfo.CYCLE_LIST;
import static com.hucc.test1.MusicInfo.CYCLE_RANDOM;
import static com.hucc.test1.MusicInfo.MSG;
import static com.hucc.test1.MusicInfo.MSG_CONTINUEPLAYING;
import static com.hucc.test1.MusicInfo.MSG_PAUSE;
import static com.hucc.test1.MusicInfo.MSG_PLAY;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, MusicService.Callbacks{

    public static final String TAG = "MainActivity";

    public static String currentMusicTitle;
    public static String currentMusicArtist;
    public static String currentMusicDuration;
    public static String currentMusicUrl;
    public static int currentMusicPosition;
    public static int prePosition;
    public static List<Map<String, Object>> dbMusic = new ArrayList<Map<String, Object>>();
    public static boolean isPlaying = false;
    public static boolean firstPlay = true;

    private MusicService.Callbacks mCallback= null;
    private GestureDetector detector = null;
    private Intent  ServiceIntent = null;
    private MusicListAdapter mAdapter;
    private ListView mListView;
    private TextView mEmptyTxt;
    private LinearLayout mLayoutplay;
    private ImageView    mPlayView;
    private TextView   mTxtMusic;
    private ImageButton mProImageButton, mPlayImageButton,mNextImageButton;

    private BroadcastReceiver completeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
              String action = intent.getAction();
              if (action.equals("action.nextsong")){
                  Log.d(TAG, "receive broadcast:next song");
                  //  autoChangeSong();
              }else if (action.equals("action.pre")){

              }else if (action.equals("action.play")){
                    play();
              }else if (action.equals("action.pause")){
                    pause();
              }else if (action.equals("action.next")){

             // }else if (action.equals("action.refreshMusicList")){
             //     Log.d(TAG, "receive action.refreshMusicList");
                 // mListView.setAdapter(mAdapter);
                 // refresh();
              }else if (action.equals("action.exit")){

            //  }else if (action.equals("action.startMainActivity")){
             //     Log.d(TAG,"startMainActivity");
                 // Intent intent1 = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
                //  context.sendBroadcast(intent1);
                //  Intent intent2 = new Intent(MainActivity.this,MainActivity.class);
                //  startActivity(intent2);
              }
        }
    };

    @Override
    public void onItemSelected(Integer id) {
        Log.d(TAG, "onItemSelected");
        isPlaying = false;
        firstPlay = true;
        notifyPlayService();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "savedInstanceState");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mListView = findViewById(android.R.id.list);
        mEmptyTxt = findViewById(android.R.id.empty);
        initLayoutView();
        //if (dbMusic.size() > 0)
        //    dbMusic.clear();
        MusicUtil.getMp3Info(MainActivity.this);
        if (dbMusic.size() != 0){
            mEmptyTxt.setVisibility(View.INVISIBLE);
            Map<String, Object> map = dbMusic.get(currentMusicPosition);
            currentMusicTitle = (String) map.get("title");
            currentMusicArtist = (String) map.get("artist");
            currentMusicDuration = (String) map.get("duration");
            currentMusicUrl = (String) map.get("url");
            mTxtMusic.setText(currentMusicTitle + "-" + currentMusicArtist);
        }

        mAdapter = new MusicListAdapter(this, dbMusic);
        mListView.setAdapter(mAdapter);

        initReceiver();
        ServiceIntent = new Intent(MainActivity.this, MusicService.class);
        startService(ServiceIntent);

        mCallback = (MusicService.Callbacks) MainActivity.this;
    }

    private void initLayoutView() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               currentMusicPosition = position;
                //isPlaying = true;
                MusicInfo.MSG = MusicInfo.MSG_PLAY;
                mCallback.onItemSelected(position);
            }
        });
        mLayoutplay = findViewById(R.id.main_play);
        mPlayView = findViewById(R.id.playtag);
        mTxtMusic = findViewById(R.id.songmusic);
        mProImageButton = findViewById(R.id.prebutton);
        mPlayImageButton = findViewById(R.id.playtopause);
        mNextImageButton = findViewById(R.id.nextbutton);
        mLayoutplay.setOnClickListener(this);
        mProImageButton.setOnClickListener(this);
        mPlayImageButton.setOnClickListener(this);
        mNextImageButton.setOnClickListener(this);
        /*mLayoutplay.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return detector.onTouchEvent(event);
            }
        });*/
    }

    /**
     * A dummy fragment representing a section of the app, but that simply
     * displays dummy text.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_play:

                break;
            case R.id.prebutton:

                break;
            case R.id.playtopause:
                    if (isPlaying) {
                        pause();
                    } else {
                        play();
                    }
                break;
            case R.id.nextbutton:

                break;
        }
    }

    private void pause() {
        Log.d(TAG, "pause");
        MSG = MSG_PAUSE;
        prePosition = currentMusicPosition;
        isPlaying = false;
        notifyPlayService();
    }

    private void play() {
        Log.d(TAG, "play");
        if (prePosition != 0 && prePosition == currentMusicPosition){
               MusicInfo.MSG = MSG_CONTINUEPLAYING;
        }else{
               MusicInfo.MSG = MSG_PLAY;
        }
        isPlaying = true;
        notifyPlayService();
    }

    /*
     * play to pause  service
     */
    private void notifyPlayService() {
        Log.d(TAG, "notifyPlayService");
        if (!isPlaying){
            Toast.makeText(this, "notify play service" + "position = " + currentMusicPosition , Toast.LENGTH_SHORT).show();
            Log.d(TAG, "dbMusic.size() = " + dbMusic.size());
            if (dbMusic.size() == 0){
                return;
            }
            Map<String,Object> map = dbMusic.get(currentMusicPosition);
            currentMusicTitle = (String) map.get("title");
            currentMusicArtist = (String) map.get("artist");
            currentMusicUrl = (String) map.get("url");
            currentMusicDuration = (String) map.get("duration");
        }
        Intent intent = new Intent();
        intent.setAction("action.changesong");
        this.sendBroadcast(intent);

        refreshPlayState();
        //notifyPlayActivity();
    }

    /*
     * play to pause  status
     */
    private void refreshPlayState() {
        Log.d(TAG, "refreshPlayState");
        firstPlay = true;
        mTxtMusic.setText(currentMusicTitle + "-" + currentMusicArtist);
        mTxtMusic.requestFocus();
        mTxtMusic.setMovementMethod(ScrollingMovementMethod.getInstance());

        if (isPlaying){
            mPlayImageButton.setImageResource(android.R.drawable.ic_media_pause);
           // mTxtMusic.setText(currentMusicTitle + "-" + currentMusicArtist);
            TranslateAnimation animation = new TranslateAnimation(-mPlayView.getWidth()-mTxtMusic.getWidth(),-mTxtMusic.getWidth(), 0, 0);
            animation.setDuration(10000);
            animation.setRepeatCount(Animation.INFINITE);
            animation.setRepeatMode(Animation.RESTART);
        }else{
            mPlayImageButton.setImageResource(android.R.drawable.ic_media_play);

           // mTxtMusic.setText(currentMusicTitle + "-" + currentMusicArtist);
            mTxtMusic.clearAnimation();
        }
    }

    private void autoChangeSong() {
        Log.d(TAG, "autoChangeSong");
        if (CYCLE == CYCLE_RANDOM){
            currentMusicPosition = new Random().nextInt(dbMusic.size()-1);
        }else if (CYCLE == CYCLE_LIST){
            if (currentMusicPosition == dbMusic.size() - 1){
                currentMusicPosition = 0;
            }else {
                currentMusicPosition++;
            }
        }
        isPlaying = true;
        MSG = MSG_PLAY;
        notifyPlayService();
    }

    private void notifyPlayActivity() {
        Log.d(TAG, "notifyPlayActivity");
        Intent intent = new Intent();
        intent.putExtra("title",currentMusicTitle);
        intent.putExtra("artist",currentMusicArtist);
        intent.putExtra("duration",currentMusicDuration);
        intent.setAction("action.notifyPlayActivity");
        this.sendBroadcast(intent);
    }

    private void initReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("action.nextsong");
        intentFilter.addAction("action.play");
        intentFilter.addAction("action.pause");
        intentFilter.addAction("action.pre");
        intentFilter.addAction("action.next");
        intentFilter.addAction("action.exit");
        //intentFilter.addAction("action.refreshMusicList");
        intentFilter.addAction("action.startMainActivity");
        registerReceiver(completeReceiver, intentFilter);
    }

    private void refresh() {
        Log.d(TAG, "refresh");
        dbMusic.clear();
        MusicUtil.getMp3Info(MainActivity.this);
        Intent intent = new Intent();
        intent.setAction("action.refreshMusicList");
        this.sendBroadcast(intent);
    }

    @Override
    protected void onResume() {
        Log.i(TAG, "onResume");
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.i(TAG, "onPause");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.i(TAG, "onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "onDestroy");
        unregisterReceiver(completeReceiver);
        this.stopService(ServiceIntent);
        isPlaying = false;
        super.onDestroy();
    }

    /*
     * 设置回调接口对象成员变量
     */
    public void setCallback(MusicService.Callbacks callback) {
        this.mCallback = callback;
    }

    public class MusicListAdapter extends BaseAdapter{
        Context mContext;
        List<Map<String, Object>> mListInfos;
        MusicInfo  musicInfo;
        /**
         * 构造函数
         * @param context   上下文
         * @param mp3Infos  集合对象
         */
        public MusicListAdapter(Context context, List<Map<String, Object>> mp3Infos) {
            this.mContext = context;
            this.mListInfos = mp3Infos;
        }

        @Override
        public int getCount() {
            return mListInfos != null ? mListInfos.size() : 0;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null){
                holder = new ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item,null);
                holder.musicTitle = convertView.findViewById(R.id.name);
                holder.musicArtist = convertView.findViewById(R.id.actor);
                holder.musicDuration = convertView.findViewById(R.id.time);
                convertView.setTag(holder); //表示给View添加一个格外的数据，
            }else{
                holder = (ViewHolder) convertView.getTag(); //通过getTag的方法将数据取出来
            }

            Map<String,Object> item = dbMusic.get(position);
            holder.musicTitle.setText((String) item.get("title"));
            holder.musicArtist.setText((String)item.get("artist"));
            holder.musicDuration.setText((String)item.get("duration"));//显示时长
            return convertView;
        }

        /**
         * 定义一个内部类
         * 声明相应的控件引用
         */
        public class ViewHolder{
            //所有控件对象引用
            public ImageView albumImage;    //专辑图片
            public TextView musicTitle;     //音乐标题
            public TextView musicDuration;  //音乐时长
            public TextView musicArtist;    //音乐艺术家
        }
    }
}
