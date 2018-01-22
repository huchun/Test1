package com.hucc.test1;

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

    public static boolean isLoadMusic;

    public MusicInfo(){
        super();
    }



}
