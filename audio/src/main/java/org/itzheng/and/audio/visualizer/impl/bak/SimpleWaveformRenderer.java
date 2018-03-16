package org.itzheng.and.audio.visualizer.impl.bak;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.ColorInt;

import org.itzheng.and.audio.visualizer.IVisualizerRenderer;

/**
 * 波纹渲染逻辑
 * <p>
 * Created by wangchenlong on 16/2/12.
 */
public class SimpleWaveformRenderer implements IVisualizerRenderer {

    private static final int Y_FACTOR = 0xFF; // 2的8次方 = 256
    private static final float HALF_FACTOR = 0.5f;

    @ColorInt
    private final int mBackgroundColor;
    private final Paint mForegroundPaint;
    private final Path mWaveformPath;

    private SimpleWaveformRenderer(@ColorInt int backgroundColor, Paint foregroundPaint, Path waveformPath) {
        mBackgroundColor = backgroundColor;
        mForegroundPaint = foregroundPaint;
        mWaveformPath = waveformPath;
    }

    public static SimpleWaveformRenderer newInstance(@ColorInt int backgroundColor, @ColorInt int foregroundColour) {
        Paint paint = new Paint();
        paint.setColor(foregroundColour);
        paint.setAntiAlias(true); // 抗锯齿
        paint.setStrokeWidth(8.0f); // 设置宽度
        paint.setStyle(Paint.Style.STROKE); // 填充

        Path waveformPath = new Path();

        return new SimpleWaveformRenderer(backgroundColor, paint, waveformPath);
    }

    @Override
    public void render(Canvas canvas, byte[] waveform) {
        canvas.drawColor(mBackgroundColor);
        float width = canvas.getWidth();
        float height = canvas.getHeight();

        mWaveformPath.reset();

        // 没有数据
        if (waveform != null) {
            // 绘制波形
            renderWaveform(waveform, width, height);
        } else {
            // 绘制直线
            renderBlank(width, height);
        }

        canvas.drawPath(mWaveformPath, mForegroundPaint);
    }

    private void renderWaveform(byte[] waveform, float width, float height) {
        float xIncrement = width / (float) (waveform.length); // 水平块数
        float yIncrement = height / Y_FACTOR; // 竖直块数
        int halfHeight = (int) (height * HALF_FACTOR); // 居中位置
        mWaveformPath.moveTo(0, halfHeight);
        for (int i = 1; i < waveform.length; ++i) {
            float yPosition = waveform[i] > 0 ?
                    height - (yIncrement * waveform[i]) : -(yIncrement * waveform[i]);
            mWaveformPath.lineTo(xIncrement * i, yPosition);
        }
        mWaveformPath.lineTo(width, halfHeight); // 最后的点, 水平居中
    }

    // 居中画一条直线
    private void renderBlank(float width, float height) {
        int y = (int) (height * HALF_FACTOR);
        mWaveformPath.moveTo(0, y);
        mWaveformPath.lineTo(width, y);
    }
}
