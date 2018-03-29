package org.itzheng.and.audio.visualizer.impl;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import org.itzheng.and.audio.visualizer.BaseRenderer;

/**
 * Title:<br>
 * Description: <br>
 *
 * @email ItZheng@ZoHo.com
 * Created by itzheng on 2018-3-29.
 */
public class MyChannelRenderer extends BaseRenderer {
    /**
     * 这个是处理后的频率值
     */
    private byte[] mBytes;

    @Override
    public void render(Canvas canvas, byte[] fft) {
        if (fft == null) {
            return;
        }
        mBytes = fft2Frequency(fft);
        draw(canvas);
    }

    public static double getAveragePowerForChannel(byte[] frequency) {
        int sum = 0;
        for (int i = 0; i < frequency.length; i++) {
            sum = sum + frequency[i] * frequency[i];
        }
        return Math.sqrt(sum / frequency.length);
    }

    public static double getMaxPowerForChannel(byte[] frequency) {
        byte max = frequency[0];
        for (int i = 1; i < frequency.length; i++) {
            if (frequency[i] > max) {
                max = frequency[i];
            }
        }
        if (true) {
            return Math.sqrt(max);
        }
        return max;
    }

    /**
     * 获取面板最大值
     *
     * @return
     */
    private static double getMaxPowerForChannel() {
        return Math.sqrt(0b1111111);
    }

    private Rect mRect = new Rect();

    private void draw(Canvas canvas) {

        if (mBytes == null) {
            return;
        }

        mRect.set(0, 0, canvas.getWidth(), canvas.getHeight());
        double volume = getAveragePowerForChannel(mBytes);
        int height = (int) (canvas.getHeight() - (canvas.getHeight() * (volume / getMaxPowerForChannel())));
        int max = (int) (canvas.getHeight() - (canvas.getHeight() * (getMaxPowerForChannel(mBytes) / getMaxPowerForChannel())));
        int width = canvas.getWidth();
        drawRect(canvas, height, width);
        drawMax(canvas, max, width);
//        canvas.drawRect(0, (int) (canvas.getHeight() * (volume / 11)), canvas.getWidth(), canvas.getHeight(), mForePaint);
    }

    private void drawMax(Canvas canvas, int height, int width) {
        canvas.drawRect(0, height, width, height + canvas.getHeight() / 10, mForePaint);
    }

    private void drawRect(Canvas canvas, int height, int width) {
        canvas.drawRect(0, height, width, canvas.getHeight(), mForePaint);
    }

    public static MyChannelRenderer newInstance() {
        return new MyChannelRenderer();
    }

    private Paint mForePaint = new Paint();
//    private Paint mMaxPaint = new Paint();

    private MyChannelRenderer() {
        mForePaint.setStrokeWidth(10f);
        mForePaint.setAntiAlias(true);
        mForePaint.setColor(Color.GREEN);

//        mMaxPaint.setStrokeWidth(10f);
//        mMaxPaint.setAntiAlias(true);
//        mMaxPaint.setColor(Color.GREEN);
    }
}
