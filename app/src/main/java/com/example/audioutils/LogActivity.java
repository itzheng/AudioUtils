package com.example.audioutils;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Title:<br>
 * Description: <br>
 *
 * @email ItZheng@ZoHo.com
 * Created by itzheng on 2018-3-19.
 */
public class LogActivity extends AppCompatActivity {
    private static final String TAG = "LogActivity";
    EditText tvFFT;
    EditText tvWave;
    private TextView labFFT;
    private TextView labWave;
    Handler handler = new Handler(Looper.getMainLooper());
    int lastSize = 0;
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (isFinishing()) {
                return;
            }
            if (lastSize == MainActivity.mFFT.size()) {
                //结束了，进行数据统计分析
                Log.d(TAG, "statistics:FFT");
                statistics(MainActivity.mFFT);
                Log.d(TAG, "statistics:Waveform");
                statistics(MainActivity.mWaveform);
                return;
            }
            lastSize = MainActivity.mFFT.size();
            tvFFT.setText(byteToText(MainActivity.mFFT));
            tvFFT.setSelection(tvFFT.getText().length());
            tvWave.setText(byteToText(MainActivity.mWaveform));
            tvWave.setSelection(tvWave.getText().length());
            labFFT.setText("FFT:" + MainActivity.mFFT.size());
            labWave.setText("Waveform:" + MainActivity.mWaveform.size());
            handler.postDelayed(runnable, 2000);
        }
    };

    private void statistics(ArrayList<Byte> bytes) {
        //获取最大值，最小值，统计每个值出现的次数
        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < bytes.size(); i++) {
            int key = bytes.get(i) * 1;
            Integer value = map.get(key);
            if (value == null) {
                map.put(key, 1);
            } else {
                map.put(key, value + 1);
            }
        }
        map = sortMapByKey(map);  //按Key进行排序
        for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
            //Map.entry<Integer,String> 映射项（键-值对）  有几个方法：用上面的名字entry
            //entry.getKey() ;entry.getValue(); entry.setValue();
            //map.entrySet()  返回此映射中包含的映射关系的 Set视图。
            Log.d(TAG, "key= " + entry.getKey() + " and value= "
                    + entry.getValue());
        }
    }

    public static Map<Integer, Integer> sortMapByKey(Map<Integer, Integer> map) {
        if (map == null || map.isEmpty()) {
            return null;
        }

        Map<Integer, Integer> sortMap = new TreeMap<>(
                new ConcurrentHashMap<Integer, Integer>());

        sortMap.putAll(map);

        return sortMap;
    }

    private String byteToText(ArrayList<Byte> bytes) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < bytes.size(); i++) {
            sb.append(bytes.get(i) + " ");
        }
        return sb.toString();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);
        getSupportActionBar().hide();
        tvFFT = findViewById(R.id.tvFFT);
        tvWave = findViewById(R.id.tvWave);
        labFFT = findViewById(R.id.labFFT);
        labWave = findViewById(R.id.labWave);
        handler.postDelayed(runnable, 2000);
    }

}
