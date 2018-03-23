package org.itzheng.and.audio.visualizer.impl;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import org.itzheng.and.audio.visualizer.IVisualizerRenderer;

/**
 * 频谱渲染逻辑
 * <p>
 * Created by wangchenlong on 16/2/12.
 */
public class MyVolumeRenderer implements IVisualizerRenderer {
    /**
     *
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

    /**
     * 将fft 转 真实音乐频率
     *
     * @param bytes
     * @return
     */
    public static byte[] fft2Frequency(byte[] bytes) {
        int frequencyCounts = bytes.length / 2 + 1; //  =513
        byte[] frequency = new byte[frequencyCounts];    //  (n-2)/2+2 = n/2+1  容量 = getCaptureSize()/2+
        frequency[0] = (byte) Math.abs(bytes[0]);
        for (int i = 1; i < frequencyCounts - 1; i++) {
            frequency[i] = (byte) Math.hypot(bytes[2 * i], bytes[2 * i + 1]);
        }
        frequency[frequencyCounts - 1] = (byte) Math.abs(bytes[1]);
        return frequency;
    }

    /**
     * 获取声音大小
     *
     * @param buffer
     * @param length
     * @return
     */
    private static double getVolume(byte[] buffer, int length) {
        long v = 0;
        // 将 buffer 内容取出，进行平方和运算
        for (int i = 0; i < buffer.length; i++) {
            v += buffer[i] * buffer[i];
        }
        // 平方和除以数据总长度，得到音量大小。
//        double mean = v / (double) buffer.length;
        if (false) {
            return Math.log10(v);
        }
        double mean = v / (double) length;
        double volume = 10 * Math.log10(mean);
        return volume;
    }

    public static double getVolumeOfFFT(byte[] fft) {
        return getVolumeOfFrequency(fft2Frequency(fft));
    }

    public static double getVolumeOfFrequency(byte[] frequency) {
        return getVolume(frequency, 600);
    }

    private Rect mRect = new Rect();

    private void draw(Canvas canvas) {

        if (mBytes == null) {
            return;
        }

        mRect.set(0, 0, canvas.getWidth(), canvas.getHeight());
        double volume = getVolumeOfFrequency(mBytes);
        canvas.drawRect(0, (int) (canvas.getHeight() * ((20 - volume) / 20)), canvas.getWidth(), canvas.getHeight(), mForePaint);
    }

    public static MyVolumeRenderer newInstance() {
        return new MyVolumeRenderer();
    }

    private Paint mForePaint = new Paint();

    private MyVolumeRenderer() {
        mForePaint.setStrokeWidth(10f);
        mForePaint.setAntiAlias(true);
        mForePaint.setColor(Color.GREEN);
    }


}
