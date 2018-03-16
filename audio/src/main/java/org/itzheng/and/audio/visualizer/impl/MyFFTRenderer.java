package org.itzheng.and.audio.visualizer.impl;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import org.itzheng.and.audio.visualizer.IVisualizerRenderer;

/**
 * 频谱渲染逻辑
 * <p>
 * Created by wangchenlong on 16/2/12.
 */
public class MyFFTRenderer implements IVisualizerRenderer {
    private byte[] mBytes;
    private int mSpectrumNum = 48 * 1;

    @Override
    public void render(Canvas canvas, byte[] fft) {
        if (fft == null) {
            return;
        }
        byte[] model = new byte[fft.length / 2 + 1];

        model[0] = (byte) Math.abs(fft[0]);
        for (int i = 2, j = 1; j < mSpectrumNum; ) {
            model[j] = (byte) Math.hypot(fft[i], fft[i + 1]);
            i += 2;
            j++;
        }
        mBytes = model;
        draw(canvas);
    }

    private float[] mPoints;
    private Rect mRect = new Rect();

    private void draw(Canvas canvas) {
        if (mBytes == null) {
            return;
        }

        if (mPoints == null || mPoints.length < mBytes.length * 4) {
            mPoints = new float[mBytes.length * 4];
        }

        mRect.set(0, 0, canvas.getWidth(), canvas.getHeight());

        //绘制频谱
        final int baseX = (int) (mRect.width() * 1.0 / mSpectrumNum);
        final int height = mRect.height();

        for (int i = 0; i < mSpectrumNum; i++) {
            if (mBytes[i] < 0) {
                mBytes[i] = 127;
            }

            final int xi = baseX * i + baseX / 2;

            mPoints[i * 4] = xi;
            mPoints[i * 4 + 1] = height;

            mPoints[i * 4 + 2] = xi;
            mPoints[i * 4 + 3] = height - mBytes[i];
        }

        canvas.drawLines(mPoints, mForePaint);
    }

    public static MyFFTRenderer newInstance() {
        return new MyFFTRenderer();
    }

    private Paint mForePaint = new Paint();

    private MyFFTRenderer() {
        mForePaint.setStrokeWidth(10f);
        mForePaint.setAntiAlias(true);
        mForePaint.setColor(Color.GREEN);
    }


}
