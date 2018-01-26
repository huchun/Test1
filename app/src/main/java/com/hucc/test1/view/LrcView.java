package com.hucc.test1.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.hucc.test1.MainPlayActivity;
import com.hucc.test1.mode.MusicInfo;

/**
 * Created by chunchun.hu on 2018/1/26.
 */

public class LrcView extends android.support.v7.widget.AppCompatTextView {

    private Context mContext;
    private int lrcLine = 0;
    private float mX;
    private float mY;
    private float midY;
    public float dY = 80;

    public LrcView(Context context) {
        super(context);
    }

    public LrcView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
    }

    public LrcView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();
        int width = this.getWidth();
        paint.setAntiAlias(true);
        paint.setColor(Color.RED);
        paint.setTextSize(50);
        paint.setTypeface(Typeface.SANS_SERIF);
        paint.setTextAlign(Paint.Align.CENTER);
        if (MainPlayActivity.musicList.size() == 0)
            return;
        canvas.drawText(MainPlayActivity.musicList.get(MainPlayActivity.currentLrcIndex).getContent(), mX, midY, paint);
        if (MainPlayActivity.scrollLrc)
            canvas.drawLine(0,midY, MusicInfo.screenWidth, midY, paint);
        paint.setColor(Color.BLUE);
        paint.setTextSize(45);

        for (int i=MainPlayActivity.currentLrcIndex-1; i >= 0; i--){
            MusicInfo info = MainPlayActivity.musicList.get(i);
            canvas.drawText(info.getContent(), mX, midY - dY*(MainPlayActivity.currentLrcIndex-i),paint);
        }
        for (int i = MainPlayActivity.currentLrcIndex+1; i<MainPlayActivity.musicList.size();i++){
            MusicInfo info = MainPlayActivity.musicList.get(i);
            canvas.drawText(info.getContent(),mX,midY + dY*(i - MainPlayActivity.currentLrcIndex), paint);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mX = w * 0.5f;
        mY = h;
        midY = h*0.5f;
    }
}
