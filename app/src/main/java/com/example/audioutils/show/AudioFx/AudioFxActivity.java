package com.example.audioutils.show.AudioFx;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.audiofx.Equalizer;
import android.media.audiofx.Visualizer;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.example.audioutils.R;

public class AudioFxActivity extends Activity {

    @SuppressWarnings("unused")
    private static final String TAG = "AudioFxActivity";

    private static final float VISUALIZER_HEIGHT_DIP = 50f;

    private MediaPlayer mMediaPlayer;
    private Visualizer mVisualizer;
    private Equalizer mEqualizer; // 均衡器

    private LinearLayout mLayout;
    VisualizerView mVisualizerView;
    BaseVisualizerView mBaseVisualizerView;
    private TextView mStatusTextView;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        mStatusTextView = new TextView(this);
        mLayout = new LinearLayout(this);
        mLayout.setOrientation(LinearLayout.VERTICAL);
        mLayout.addView(mStatusTextView);
        setContentView(mLayout);

        mMediaPlayer = MediaPlayer.create(this, R.raw.msa);
//		mMediaPlayer = new MediaPlayer();

        setupVisualizerFxAndUi();
        setupEqualizeFxAndUi();

        mVisualizer.setEnabled(true);
        mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                // TODO Auto-generated method stub
//				mVisualizer.setEnabled(false);

            }
        });

        mMediaPlayer.start();
        mMediaPlayer.setLooping(true);

        mStatusTextView.setText("播放中。。。");
    }

    /**
     * 通过mMediaPlayer返回的AudioSessionId创建一个优先级为0均衡器对象 并且通过频谱生成相应的UI和对应的事件
     */
    private void setupEqualizeFxAndUi() {
        mEqualizer = new Equalizer(0, mMediaPlayer.getAudioSessionId());
//		mEqualizer = new Equalizer(0, 0);
        mEqualizer.setEnabled(true);// 启用均衡器
        TextView eqTextView = new TextView(this);
        eqTextView.setText("均衡器：");
        mLayout.addView(eqTextView);

        // 通过均衡器得到其支持的频谱引擎
        short bands = mEqualizer.getNumberOfBands();

        // getBandLevelRange 是一个数组，返回一组频谱等级数组，
        // 第一个下标为最低的限度范围
        // 第二个下标为最大的上限,依次取出
        final short minEqualizer = mEqualizer.getBandLevelRange()[0];
        final short maxEqualizer = mEqualizer.getBandLevelRange()[1];

        for (short i = 0; i < bands; i++) {
            final short band = i;

            TextView freqTextView = new TextView(this);
            freqTextView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.FILL_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));

            freqTextView.setGravity(Gravity.CENTER_HORIZONTAL);

            // 取出中心频率
            freqTextView
                    .setText((mEqualizer.getCenterFreq(band) / 1000) + "HZ");
            mLayout.addView(freqTextView);

            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);

            TextView minDbTextView = new TextView(this);
            minDbTextView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));

            minDbTextView.setText((minEqualizer / 100) + " dB");

            TextView maxDbTextView = new TextView(this);
            maxDbTextView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            maxDbTextView.setText((maxEqualizer / 100) + " dB");

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.FILL_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);

            layoutParams.weight = 1;

            SeekBar seekbar = new SeekBar(this);
            seekbar.setLayoutParams(layoutParams);
            seekbar.setMax(maxEqualizer - minEqualizer);
            seekbar.setProgress(mEqualizer.getBandLevel(band));

            seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress,
                                              boolean fromUser) {
                    // TODO Auto-generated method stub
                    mEqualizer.setBandLevel(band,
                            (short) (progress + minEqualizer));
                }
            });
            row.addView(minDbTextView);
            row.addView(seekbar);
            row.addView(maxDbTextView);

            mLayout.addView(row);
        }

    }

    /**
     * 生成一个VisualizerView对象，使音频频谱的波段能够反映到 VisualizerView上
     */
    private void setupVisualizerFxAndUi() {
//		mVisualizerView = new VisualizerView(this);
//
//		mVisualizerView.setLayoutParams(new ViewGroup.LayoutParams(
//				ViewGroup.LayoutParams.FILL_PARENT,
//				(int) (VISUALIZER_HEIGHT_DIP * getResources()
//						.getDisplayMetrics().density)));
//		mLayout.addView(mVisualizerView);
        mBaseVisualizerView = new BaseVisualizerView(this);

        mBaseVisualizerView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.FILL_PARENT,
                (int) (VISUALIZER_HEIGHT_DIP * getResources()
                        .getDisplayMetrics().density)));
        mLayout.addView(mBaseVisualizerView);

        mVisualizer = new Visualizer(mMediaPlayer.getAudioSessionId());
//		mVisualizer = new Visualizer(0);
        // 参数内必须是2的位数
        mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);

        // 设置允许波形表示，并且捕获它
        mBaseVisualizerView.setVisualizer(mVisualizer);
        /*mVisualizer.setDataCaptureListener(new OnDataCaptureListener()
		{

			@Override
			public void onWaveFormDataCapture(Visualizer visualizer,
					byte[] waveform, int samplingRate)
			{
				// TODO Auto-generated method stub
//				mVisualizerView.updateVisualizer(waveform);
//				mBaseVisualizerView.updateVisualizer(waveform);
			}

			@Override
			public void onFftDataCapture(Visualizer visualizer, byte[] fft,
					int samplingRate)
			{
				// TODO Auto-generated method stub

			}
		}, Visualizer.getMaxCaptureRate() / 2, true, false);
*/
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        if (isFinishing() && mMediaPlayer != null) {
            mVisualizer.release();
            mMediaPlayer.release();
            mEqualizer.release();
            mMediaPlayer = null;
        }
    }

    class VisualizerView extends View {

        private byte[] mBytes;
        private float[] mPoints;
        // 矩形区域
        private Rect mRect = new Rect();
        // 画笔
        private Paint mPaint = new Paint();

        // 初始化画笔
        private void init() {
            mBytes = null;
            mPaint.setStrokeWidth(1f);
            mPaint.setAntiAlias(true);
            mPaint.setColor(Color.BLUE);
        }

        public VisualizerView(Context context) {
            super(context);
            init();
        }

        public void updateVisualizer(byte[] mbyte) {
            mBytes = mbyte;
            invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            // TODO Auto-generated method stub
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
                        + ((byte) (mBytes[i] + 128)) * (mRect.height() / 2)
                        / 128;
                mPoints[i * 4 + 2] = mRect.width() * (i + 1)
                        / (mBytes.length - 1);
                mPoints[i * 4 + 3] = mRect.height() / 2
                        + ((byte) (mBytes[i + 1] + 128)) * (mRect.height() / 2)
                        / 128;
            }

            canvas.drawLines(mPoints, mPaint);

        }
    }
}