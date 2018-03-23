package com.example.audioutils;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.audioutils.permissions.PermissionsChecker;

import org.itzheng.view.VisualizerFFTView;
import org.itzheng.view.WaveFormView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    WaveFormView waveFormView;
    VisualizerFFTView fftView;
    Button btnShowLog;
    // 权限
    private static final String[] PERMISSIONS = new String[]{
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.MODIFY_AUDIO_SETTINGS
    };
    Visualizer visualizer = null;
    MediaPlayer mMediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        waveFormView = findViewById(R.id.waveFormView);
        fftView = findViewById(R.id.fftView);
        btnShowLog = findViewById(R.id.btnShowLog);
        btnShowLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplication(), LogActivity.class));
            }
        });
        initMediaPlayer();
        initVisualizer();
        mMediaPlayer.start();

    }

    public static ArrayList<Byte> mWaveform = new ArrayList<>();
    public static ArrayList<Byte> mFFT = new ArrayList<>();

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

    private void initVisualizer() {
        //        visualizer.setCaptureSize(Visualizer.getCaptureSizeRange().length / 2);
        if (visualizer == null) {
            try {
                visualizer = new Visualizer(mMediaPlayer.getAudioSessionId());
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
                    waveFormView.updateVisualizer(waveform);
                    for (int i = 0; i < waveform.length; i++) {
                        mWaveform.add(waveform[i]);
                    }
                }

                @Override
                public void onFftDataCapture(Visualizer visualizer, byte[] fft, int samplingRate) {
                    fftView.updateVisualizer(fft);
                    for (int i = 0; i < fft.length; i++) {
                        mFFT.add(fft[i]);
                    }
                }
            }, Visualizer.getMaxCaptureRate(), true, true);
            visualizer.setEnabled(true);
        }
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
