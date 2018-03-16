package org.itzheng.view;

import org.itzheng.and.audio.visualizer.IVisualizerRenderer;

/**
 * Title:<br>
 * Description: <br>
 *
 * @email ItZheng@ZoHo.com
 * Created by itzheng on 2018-3-15.
 */
public interface IVisualizer {
    /**
     * 设置要显示的数据
     *
     * @param bytes
     */
    void updateVisualizer(byte[] bytes);

    /**
     * 设置渲染器
     *
     * @param renderer
     */
    void setRenderer(IVisualizerRenderer renderer);

}
