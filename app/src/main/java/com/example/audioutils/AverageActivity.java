package com.example.audioutils;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.audiofx.Equalizer;
import android.media.audiofx.Visualizer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.itzheng.view.VisualizerFFTView;
import org.itzheng.view.VolumeView;
import org.itzheng.view.WaveFormView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class AverageActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    WaveFormView waveFormView;
    VisualizerFFTView fftView;
    Button btnShowLog;
    TextView tvAvgFFT;
    TextView tvAvgWave;
    VolumeView volumeView;
    // 权限
    private static final String[] PERMISSIONS = new String[]{
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.MODIFY_AUDIO_SETTINGS
    };
    Visualizer visualizer = null;
    MediaPlayer mMediaPlayer;
    long delayMillis = 1000 / 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_average);
        waveFormView = findViewById(R.id.waveFormView);
        fftView = findViewById(R.id.fftView);
        btnShowLog = findViewById(R.id.btnShowLog);
        tvAvgFFT = findViewById(R.id.tvAvgFFT);
        tvAvgFFT = findViewById(R.id.tvAvgFFT);
        tvAvgWave = findViewById(R.id.tvAvgWave);
        volumeView = findViewById(R.id.volumeView);
        btnShowLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplication(), LogActivity.class));
            }
        });
        initMediaPlayer();
        initVisualizer();
        mMediaPlayer.start();
        mHandler.postDelayed(runnable, delayMillis);
    }

    Handler mHandler = new Handler(Looper.getMainLooper());
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (true) {
                return;
            }
            ArrayList<byte[]> fft;
            ArrayList<byte[]> wave;
            if (saveFFT.isEmpty() || saveWaveform.isEmpty()) {
                return;
            }
            synchronized (this) {
                fft = (ArrayList<byte[]>) saveFFT.clone();
                saveFFT.clear();
                wave = (ArrayList<byte[]>) saveWaveform.clone();
                saveWaveform.clear();
            }
            tvAvgFFT.setText(getAvg(fft) + "");
            tvAvgWave.setText(getAvg(wave) + "");
            mHandler.postDelayed(runnable, delayMillis);
        }
    };

    private int getAvg(ArrayList<byte[]> fft) {
        if (fft == null) {
            return 0;
        }
        int sum = 0;
        for (int i = 0; i < fft.size(); i++) {
//            sum = sum + getMaximum(fft.get(i));
//            sum = sum + getNoZero(fft.get(i));
            sum = sum + getPartValue(fft.get(i));
        }
        return sum / fft.size();
    }

    private int getPartValue(byte[] bytes) {
//        Arrays.sort(bytes);
        int sum = 0;
        int count = 2 * 2 * 2;
//        for (int i = bytes.length - count; i < bytes.length; i++) {
//            sum = sum + bytes[i];
//        }
        for (int i = 0; i < count; i++) {
            sum = sum + bytes[i];
        }
        return sum / count;
    }

    private int getNoZero(byte[] bytes) {
        int count = 0;
        int sum = 0;
        for (int i = 0; i < bytes.length; i++) {
            byte b = bytes[i];
            if (b != 0) {
                count++;
                sum = sum + b;
            }
        }
        if (count == 0) {
            return 0;
        }
        return sum / count;
    }

    /**
     * 获取出现最多次数的值
     *
     * @param bytes
     * @return
     */
    private int getMaximum(byte[] bytes) {
        if (false) {
            int sum = 0;
            for (int i = 0; i < bytes.length; i++) {
                sum = sum + bytes[i];
            }
            return sum / bytes.length;
        }
        if (bytes == null) {
            return 0;
        }
        //获取最大值，最小值，统计每个值出现的次数
        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < bytes.length; i++) {
            int key = bytes[i] * 1;
            Integer value = map.get(key);
            if (value == null) {
                map.put(key, 1);
            } else {
                map.put(key, value + 1);
            }
        }
        map = LogActivity.sortMapByKey(map);  //按Key进行排序
        /**
         * k - v
         */
        int iv[] = new int[]{0, 0};
        for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
            //Map.entry<Integer,String> 映射项（键-值对）  有几个方法：用上面的名字entry
            //entry.getKey() ;entry.getValue(); entry.setValue();
            //map.entrySet()  返回此映射中包含的映射关系的 Set视图。
            int key = entry.getValue();
            int value = entry.getValue();
            if (key != 1024 && key != 0 && value > iv[1]) {
                iv[0] = key;
                iv[1] = value;
            }
        }
        return iv[0];
    }

    public static ArrayList<Byte> mWaveform = new ArrayList<>();
    public static ArrayList<Byte> mFFT = new ArrayList<>();
    public ArrayList<byte[]> saveWaveform = new ArrayList<byte[]>();
    public ArrayList<byte[]> saveFFT = new ArrayList<byte[]>();

    private void initMediaPlayer() {
        if (mMediaPlayer == null) {
            mMediaPlayer = MediaPlayer.create(this, R.raw.msa);
            mMediaPlayer.setLooping(true);
        }
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (visualizer != null) {
                    visualizer.setEnabled(false);
                }
            }
        });
    }

    /**
     * 获取声音
     *
     * @param buffer
     * @param length
     * @return
     */
    public double getVolume(byte[] buffer, int length) {
        if (false) {
            float BASE = 32768f;
            float maxAmplitude = 0;

            for (int i = 0; i < buffer.length; i++) {
                maxAmplitude += buffer[i] * buffer[i];
            }
            maxAmplitude = (float) Math.sqrt(maxAmplitude / buffer.length);
            float ratio = maxAmplitude / BASE;
            float db = 0;
            if (ratio > 0) {
                db = (float) (20 * Math.log10(ratio)) + 100;
            }

            return db;

        }
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

    Equalizer mEqualizer;

    private void initVisualizer() {
        //        visualizer.setCaptureSize(Visualizer.getCaptureSizeRange().length / 2);
        if (visualizer == null) {
            try {
                visualizer = new Visualizer(mMediaPlayer.getAudioSessionId());
//                visualizer = new Visualizer(0);
            } catch (Exception e) {
                startAppDetailSetting(this);
                return;
            }
//            visualizer.setCaptureSize(Visualizer.getMaxCaptureRate());
            visualizer.setCaptureSize(Visualizer.getMaxCaptureRate());
            visualizer.setDataCaptureListener(new Visualizer.OnDataCaptureListener() {
                @Override
                public void onWaveFormDataCapture(Visualizer visualizer, byte[] waveform, int samplingRate) {
                    Log.d(TAG, "onWaveFormDataCapture: " + waveform.length);
                    saveWaveform.add(waveform);
                    waveFormView.updateVisualizer(waveform);
                    for (int i = 0; i < waveform.length; i++) {
                        mWaveform.add(waveform[i]);
                    }
                    if (tvAvgWave != null) {
//                        tvAvgWave.setText("" + getVolume(waveform));
                        tvAvgWave.setText("" + samplingRate);
                    }
                }

                @Override
                public void onFftDataCapture(Visualizer visualizer, byte[] fft, int samplingRate) {
                    saveFFT.add(fft);
                    fftView.updateVisualizer(fft);
                    volumeView.updateVisualizer(fft);
                    for (int i = 0; i < fft.length; i++) {
                        mFFT.add(fft[i]);
                    }
                    if (tvAvgFFT != null) {
                        tvAvgFFT.setText("" + getVolume(getFrequency(fft), fft.length / 2));
//                        tvAvgFFT.setText("" + samplingRate * 2 / visualizer.getCaptureSize());
                    }
                }
            }, Visualizer.getMaxCaptureRate(), true, true);
            visualizer.setEnabled(true);
            {
                mEqualizer = new Equalizer(0, mMediaPlayer.getAudioSessionId());
                mEqualizer.setEnabled(true);
            }

        }
    }

    private int getAvg(byte[] bytes) {
        if (bytes == null) {
            return 0;
        }
        double sum = 0;
        for (byte b : bytes) {
            sum = sum + b;
        }
        return (int) (sum / bytes.length);
    }

    /**
     * 真实的频率
     *
     * @param bytes
     * @return
     */
    public byte[] getFrequency(byte[] bytes) {
        int frequencyCounts = bytes.length / 2 + 1; //  =513
        byte[] fft = new byte[frequencyCounts];    //  (n-2)/2+2 = n/2+1  容量 = getCaptureSize()/2+
        fft[0] = (byte) Math.abs(bytes[0]);
        for (int i = 1; i < frequencyCounts - 1; i++) {
            fft[i] = (byte) Math.hypot(bytes[2 * i], bytes[2 * i + 1]);
        }
        fft[frequencyCounts - 1] = (byte) Math.abs(bytes[1]);
        return fft;
    }

    private void showToast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

//    private void startAppSettings() {
//        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//        intent.setData(Uri.parse(this.getPackageName()));
//        startActivity(intent);
//    }

    /**
     * 跳转到app详情界面
     */
    public static void startAppDetailSetting(Context context) {
        Intent localIntent = new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            localIntent.setData(Uri.fromParts("package", context.getPackageName(), null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            localIntent.setAction(Intent.ACTION_VIEW);
            localIntent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
            localIntent.putExtra("com.android.settings.ApplicationPkgName", context.getPackageName());
        }
        context.startActivity(localIntent);
    }
}
