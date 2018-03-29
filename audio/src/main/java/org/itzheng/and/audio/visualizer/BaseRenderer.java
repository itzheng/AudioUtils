package org.itzheng.and.audio.visualizer;

/**
 * Title:封装一些基本算法<br>
 * Description: <br>
 *
 * @email ItZheng@ZoHo.com
 * Created by itzheng on 2018-3-29.
 */
public abstract class BaseRenderer implements IVisualizerRenderer {
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
}
