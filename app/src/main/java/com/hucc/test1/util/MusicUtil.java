package com.hucc.test1.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.hucc.test1.MainActivity;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by chunchun.hu on 2018/1/17.
 */
public class MusicUtil {

    public static String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/musicplayer/albumPic/" + MainActivity.currentMusicTitle + "-" + MainActivity.currentMusicArtist + ".jpg";
    public static String localUrl = Environment.getExternalStorageDirectory().getAbsolutePath() + "/musicplayer/lrc/" + MainActivity.currentMusicTitle + MainActivity.currentMusicArtist + "lrc";

    public static void getMp3Info(Context context) {
        if (MainActivity.dbMusic.size() > 0)
             MainActivity.dbMusic.clear();
        ContentResolver resolver = context.getContentResolver();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String sort = MediaStore.Audio.Media.DEFAULT_SORT_ORDER;
        Cursor cursor = resolver.query(uri,null, null, null, sort);

        getMusic(cursor);
        /*List<MusicInfo> mListInfo = new ArrayList<MusicInfo>();
        for (int i = 0; i<cursor.getCount();i++){
                cursor.moveToNext();
                MusicInfo musicInfo = new MusicInfo();
                long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
                String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
              //  String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
              //  String display_name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
              //  long album_id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
                long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));  // 时长
              //  long size = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE)); // 文件大小
                String url = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)); // 文件路径
                int isMusic = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.IS_MUSIC)); // 是否为音乐
                if (isMusic != 0){  // 只把音乐添加到集合当中
                    musicInfo.setId(id);
                    musicInfo.setTitle(title);
                    musicInfo.setArtist(artist);
                  //  musicInfo.setAlbum(album);
                   // musicInfo.setDisplayName(display_name);
                  //  musicInfo.setAlbumId(album_id);
                    musicInfo.setDuration(duration);
                  //  musicInfo.setSize(size);
                    musicInfo.setUrl(url);
                    mListInfo.add(musicInfo);
                }
            getMusic(mListInfo);
            }*/
    }

    private static void getMusic(Cursor cursor) {
          while (cursor.moveToNext()){
              Map<String,Object> item = new HashMap<String, Object>();
              long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
              String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
              String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
              if (artist != null && artist.equals("<unknown>")){
                  continue;
              }
              long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
              long size = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE));
              String url = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
              int isMusic = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.IS_MUSIC));
              if (isMusic != 0){
                  item.put("id",id);
                  item.put("title",title);
                  item.put("artist",artist);
                  item.put("duration",formatTime(duration));
                  item.put("size",size);
                  item.put("url",url);
                  Log.d("MusicUtil", "MusicTitle = " + title);
                  Log.d("MusicUtil", "MusicArtist = " + artist);
                  Log.d("MusicUtil", "MusicUrl = " + url);
                  MainActivity.dbMusic.add(item);
              }
          }
    }

    /**
     * 格式化时间，将毫秒转换为分:秒格式
     * @param time
     * @return
     */
    public static String formatTime(long time) {
        String min = time / (1000 * 60) + "";
        String sec = time % (1000 * 60) + "";
        if (min.length() < 2){
            min = "0" + time / (1000 * 60) + "";
        }else {
            min = time / (1000 * 60) + "";
        }
        if (sec.length() == 4){
            sec = "0" + (time % (1000 * 60)) + "";
        }else if (sec.length() == 3){
            sec = "00" + (time % (1000 * 60)) + "";
        }else if (sec.length() == 2){
            sec = "000" + (time % (1000 * 60)) + "";
        }else if (sec.length() == 1){
            sec = "0000" + (time % (1000 * 60)) + "";
        }
        return min + ":" + sec.trim().substring(0,2);
    }

    /*
     * music play start time
     */
    public static String formatPlayTime(int time) {
        int allsecond = time / 1000;
        String minute = allsecond / 60 + "";
        if (minute.length() < 2){
            minute = "0" + minute;
        }
        String second = allsecond % 60 + "";
        if (second.length() < 2){
            second = "0" + second;
        }
        return minute + ":" + second;
    }

    /*
     * music play duration time
     */
    public static String formatDuration(int duration) {
        long totalSecond = duration / 1000;
        String minute = totalSecond / 60 + "";
        if (minute.length() < 2)
            minute = "0" + minute;
        String second = totalSecond % 60 + "";
        if (second.length() < 2)
            second = "0" + second;
        return minute + ";" + second;
    }

    public static int translateToInt(String startTime) {
        int allTime = 0;
        String[] lines = startTime.split(":");
        if (lines[0].startsWith("0") || lines[0].startsWith("1")){
            allTime += Integer.parseInt(lines[0]) * 60 * 1000;
        }else{
             return 0;
        }
        String[] str = lines[1].split("\\.");
        allTime += Integer.parseInt(str[0]) * 1000;
        allTime += Integer.parseInt(str[1]) * 10;
        return allTime;
    }
}
