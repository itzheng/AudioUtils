package com.example.audioutils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.media.audiofx.Equalizer;
import android.media.audiofx.Visualizer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.itzheng.and.audio.visualizer.impl.MyChannelRenderer;
import org.itzheng.and.audio.visualizer.impl.MyVolumeRenderer;
import org.itzheng.view.VisualizerFFTView;
import org.itzheng.view.VolumeView;
import org.itzheng.view.WaveFormView;

/**
 * Title:<br>
 * Description: <br>
 *
 * @email ItZheng@ZoHo.com
 * Created by itzheng on 2018-3-23.
 */
public class EqualizerActivity extends AppCompatActivity {
    private static final String TAG = "AudioFxDemo";

    private MediaPlayer mMediaPlayer;
    private Visualizer mVisualizer;
    private Equalizer mEqualizer;
    WaveFormView waveFormView;
    VisualizerFFTView fftView;
    Button btnShowLog;
    TextView tvAvgFFT;
    TextView tvAvgWave;
    VolumeView volumeView;
    /**
     * 是否使用系统播放器
     */
    private boolean isSystemDef = true;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_average);
        waveFormView = findViewById(R.id.waveFormView);
        fftView = findViewById(R.id.fftView);
        btnShowLog = findViewById(R.id.btnShowLog);
        tvAvgFFT = findViewById(R.id.tvAvgFFT);
        tvAvgFFT = findViewById(R.id.tvAvgFFT);
        tvAvgWave = findViewById(R.id.tvAvgWave);
        volumeView = findViewById(R.id.volumeView);
        // Create the MediaPlayer
        if (!isSystemDef) {
            mMediaPlayer = MediaPlayer.create(this, R.raw.xiaomi);
            mMediaPlayer.setLooping(true);
            Log.d(TAG, "MediaPlayer audio session ID: " + mMediaPlayer.getAudioSessionId());
        }


        setupVisualizerFxAndUI();
        if (!isSystemDef) {
            mMediaPlayer.start();
        }

    }

    /**
     * 一个和，一个次数
     */
    long[] fftAvg = new long[2];

    private void setupVisualizerFxAndUI() {
        // Create a VisualizerView (defined below), which will render the simplified audio
        // wave form to a Canvas.

        // Create the Visualizer object and attach it to our media player.
        if (!isSystemDef) {
            mVisualizer = new Visualizer(mMediaPlayer.getAudioSessionId());
        } else {
            mVisualizer = new Visualizer(0);
        }


        mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
        mVisualizer.setDataCaptureListener(new Visualizer.OnDataCaptureListener() {
            public void onWaveFormDataCapture(Visualizer visualizer, byte[] waveform,
                                              int samplingRate) {
                waveFormView.updateVisualizer(waveform);
                if (tvAvgWave != null) {
                    byte[] test = new byte[500];
                    for (int i = 0; i < test.length; i++) {
                        test[i] = (byte) 150;
                    }
                    tvAvgWave.setText("" + getAveragePowerForChannel(test));
                }
            }

            public void onFftDataCapture(Visualizer visualizer, byte[] fft, int samplingRate) {
                fftView.updateVisualizer(fft);
                volumeView.updateVisualizer(fft);
                if (tvAvgFFT != null) {
//                    tvAvgFFT.setText("" + MyVolumeRenderer.getVolumeOfFFT(fft));
                    byte[] frequency = MyVolumeRenderer.fft2Frequency(fft);
                    int max = (int) MyChannelRenderer.getMaxPowerForChannel(frequency);
                    int volume = (int) MyChannelRenderer.getAveragePowerForChannel(frequency);
                    tvAvgFFT.setText("max:" + max// + ",min:" + getMin(frequency)
//                            + ",volume:" + MyVolumeRenderer.getVolumeOfFFT(fft));
                            + ",volume:" + volume
                            + "\n " + (max > volume));
                }
            }
        }, Visualizer.getMaxCaptureRate() / 2, true, true);

        // Make sure the visualizer is enabled only when you actually want to receive data, and
        // when it makes sense to receive data.
        mVisualizer.setEnabled(true);

        // When the stream ends, we don't need to collect any more data. We don't do this in
        // setupVisualizerFxAndUI because we likely want to have more, non-Visualizer related code
        // in this callback.
        if (!isSystemDef) {
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                public void onCompletion(MediaPlayer mediaPlayer) {
                    mVisualizer.setEnabled(false);
                    Log.d(TAG, "onCompletion:" + fftAvg[0]);
                }
            });
        }

        //静音是否显示波形图案
        if (true) {
            //均衡器只对部分歌曲有效，应该和音频的长度有关，这个有待验证
            // Create the Equalizer object (an AudioEffect subclass) and attach it to our media player,
            // with a default priority (0).
            if (!isSystemDef) {
                mEqualizer = new Equalizer(0, mMediaPlayer.getAudioSessionId());
            } else {
                mEqualizer = new Equalizer(0, 0);
            }
            mEqualizer.setEnabled(true);
        }
    }

    private int getAveragePowerForChannel(byte[] frequency) {
        int sum = 0;
//        int count = 0;
        for (int i = 0; i < frequency.length; i++) {
//            if ()
            sum = sum + frequency[i] * frequency[i];
        }
//        if (false && count == 0) {
//            return 0;
//        }
        return (int) Math.sqrt(sum / frequency.length);
    }

    private byte getMin(byte[] frequency) {
        byte min = frequency[0];
        for (int i = 1; i < frequency.length; i++) {
            if (frequency[i] < min) {
                min = frequency[i];
            }
        }
        return min;
    }

    private byte getMax(byte[] frequency) {

        byte max = frequency[0];
        for (int i = 1; i < frequency.length; i++) {
            if (frequency[i] > max) {
                max = frequency[i];
            }
        }
        if (true) {
            return (byte) Math.sqrt(max);
        }
        return max;
    }

    private int sumOfBytes(byte[] bytes) {
        int sum = 0;
        for (byte b : bytes) {
            sum += b & 0xff;
        }
        return sum;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing() && mMediaPlayer != null) {
            mVisualizer.release();
            mEqualizer.release();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    public void getVisualizer(Visualizer visualizer, byte[] rawVizData) {
        int status = Visualizer.ERROR;

        if (visualizer != null) {

            //音樂頻譜獲取
            status = visualizer.getFft(rawVizData);//獲取波形圖

            if (status != Visualizer.SUCCESS) {
                Log.i("answer", "getWaveFail");
            } else {
                int j = 0;

                for (int i = 0; i < 128; i++) {

                    if (rawVizData[i] == 0) {
                        j++;
                    }
                }

                Log.i(TAG, "getWave j = " + j);
            }
        }
    }

    public static String toHexString(byte[] bytes) {
        if (bytes == null) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        for (byte b : bytes) {
            String hexString = Integer.toHexString(b & 0xff);
            if (hexString.length() < 2) {
                sb.append("0");
            }
            sb.append(hexString);
        }
        return sb.toString();
    }

    /**
     * A simple class that draws waveform data received from a
     * {@link Visualizer.OnDataCaptureListener#onWaveFormDataCapture }
     */
    public class VisualizerView extends View {
        private byte[] mBytes;
        private float[] mPoints;
        private Rect mRect = new Rect();

        private Paint mForePaint = new Paint();

        public VisualizerView(Context context) {
            super(context);
            init();
        }

        private void init() {
            mBytes = null;

            mForePaint.setStrokeWidth(1f);
            mForePaint.setAntiAlias(true);
            mForePaint.setColor(Color.rgb(0, 128, 255));
        }

        public void updateVisualizer(byte[] bytes) {
            mBytes = bytes;
            invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            if (mBytes == null) {
                return;
            }

            if (mPoints == null || mPoints.length < mBytes.length * 4) {
                mPoints = new float[mBytes.length * 4];
            }

            mRect.set(0, 0, getWidth(), getHeight());

            for (int i = 0; i < mBytes.length - 1; i++) {
                mPoints[i * 4] = mRect.width() * i / (mBytes.length - 1);
                mPoints[i * 4 + 1] = mRect.height() / 2
                        + ((byte) (mBytes[i] + 128)) * (mRect.height() / 2) / 128;
                mPoints[i * 4 + 2] = mRect.width() * (i + 1) / (mBytes.length - 1);
                mPoints[i * 4 + 3] = mRect.height() / 2
                        + ((byte) (mBytes[i + 1] + 128)) * (mRect.height() / 2) / 128;
            }

            canvas.drawLines(mPoints, mForePaint);
        }
    }
}
