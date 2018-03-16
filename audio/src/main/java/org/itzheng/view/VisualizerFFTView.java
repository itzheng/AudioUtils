package org.itzheng.view;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import org.itzheng.and.audio.visualizer.IVisualizerRenderer;
import org.itzheng.and.audio.visualizer.impl.MyFFTRenderer;
import org.itzheng.and.audio.visualizer.impl.MyWaveformRenderer;

import java.util.Arrays;

/**
 * 显示频谱fft的自定义view
 *
 * @author smnan
 */
public class VisualizerFFTView extends View implements IVisualizer {
    private byte[] mBytes;
    private IVisualizerRenderer mRenderer;

    {
        //设置默认渲染器
        if (mRenderer == null) {
            setRenderer(MyFFTRenderer.newInstance());
        }
    }

    public VisualizerFFTView(Context context) {
        super(context);
    }

    public VisualizerFFTView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public VisualizerFFTView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void updateVisualizer(byte[] bytes) {
        mBytes = bytes == null ? null : Arrays.copyOf(bytes, bytes.length);
        //设置完数据后，需要重新渲染
        invalidate();
    }

    @Override
    public void setRenderer(IVisualizerRenderer renderer) {
        mRenderer = renderer;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mRenderer != null) {
            mRenderer.render(canvas, mBytes);
        }
    }
}
