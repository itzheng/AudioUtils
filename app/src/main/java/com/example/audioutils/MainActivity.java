package com.example.audioutils;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.media.AudioRecord;
import android.media.audiofx.Visualizer;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.audioutils.permissions.PermissionsChecker;

import org.itzheng.view.VisualizerFFTView;
import org.itzheng.view.WaveFormView;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    WaveFormView waveFormView;
    VisualizerFFTView fftView;
    // 权限
    private static final String[] PERMISSIONS = new String[]{
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.MODIFY_AUDIO_SETTINGS
    };
    Visualizer visualizer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        waveFormView = findViewById(R.id.waveFormView);
        fftView = findViewById(R.id.fftView);


//        visualizer.setCaptureSize(Visualizer.getCaptureSizeRange().length / 2);
        if (visualizer == null) {
            try {
                visualizer = new Visualizer(0);
            } catch (Exception e) {
                startAppDetailSetting(this);
                return;
            }
            visualizer.setCaptureSize(1);
            visualizer.setDataCaptureListener(new Visualizer.OnDataCaptureListener() {
                @Override
                public void onWaveFormDataCapture(Visualizer visualizer, byte[] waveform, int samplingRate) {
                    Log.d(TAG, "onWaveFormDataCapture: " + waveform.length);
                    waveFormView.updateVisualizer(waveform);
                }

                @Override
                public void onFftDataCapture(Visualizer visualizer, byte[] fft, int samplingRate) {
                    fftView.updateVisualizer(fft);
                }
            }, Visualizer.getMaxCaptureRate() / 2 / 2, true, true);
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
