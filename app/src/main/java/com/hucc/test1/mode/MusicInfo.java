package com.hucc.test1.mode;

/**
 * Created by chunchun.hu on 2018/1/17.
 */

public class MusicInfo {

    public static int MSG_PLAY = 0;
    public static int MSG_PAUSE = 1;
    public static int MSG_CONTINUEPLAYING = 2;
    public static int MSG_CHANGESONG = 3;
    public static int MSG = MSG_PLAY;
    public static int CYCLE_LIST = 1;
    public static int CYCLE_RANDOM = 2;
    public static int CYCLE_SINGLE = 3;
    public static int CYCLE = CYCLE_LIST;

    public static int screenWidth;
    public static int screenHeight;

    public static boolean playMusic;
    private String content;

    private int mStartTime;
    private String mContent;
    private int mDuration;

    public MusicInfo(){
        super();
    }

    public MusicInfo(int mStartTime, String mContent, int mDuration) {
        this.mStartTime = mStartTime;
        this.mContent = mContent;
        this.mDuration = mDuration;
    }

    public int getStartTime() {
        return mStartTime;
    }

    public void setStartTime(int startTime) {
        this.mStartTime = startTime;
    }

    public String getContent() {
        return mContent;
    }

    public String setContent(String content) {
        return mContent;
    }

    public int getDuration() {
        return mDuration;
    }

    public void setDuration(int duration) {
        this.mDuration = duration;
    }
}
