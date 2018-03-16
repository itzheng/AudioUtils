package org.itzheng.and.audio.visualizer;

import android.graphics.Canvas;

/**
 * 音频波纹渲染接口
 * <p>
 * Created by wangchenlong on 16/2/11.
 */
public interface IVisualizerRenderer {
    /**
     * 进行渲染
     *
     * @param canvas 画布
     * @param bytes  要渲染的数据
     */
    void render(Canvas canvas, byte[] bytes);
}
