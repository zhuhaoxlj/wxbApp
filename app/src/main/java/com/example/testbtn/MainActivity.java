package com.example.testbtn;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends Activity {
    private static final String TAG = "mark";
    private TextView sentence;
    private TextView source;
    boolean isPermissionRequested;
    private String url = "https://lindum.top/yiyan/api.php?index=";
    private int index = -1;
    private int sentenceQuantity = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 控件绑定
        {
            sentence = findViewById(R.id.sentence);
            source = findViewById(R.id.source);
        }
        requestPermission();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN); //隐藏状态栏
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {
            sentenceQuantity = Integer.parseInt(getSentence(url + index));
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 设置字体样式
        AssetManager mgr = getAssets();
        Typeface tf = Typeface.createFromAsset(mgr, "fonts/1.ttf");
        sentence.setTypeface(tf);
        source.setTypeface(tf);
    }

    /**
     * 按键监听
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        source.setVisibility(View.VISIBLE);
        if (keyCode == KeyEvent.KEYCODE_G) {
            try {
                if (index < sentenceQuantity) {
                    index++;
                    String temp = getSentence(url + index);
                    String upText = temp.split("——")[0];
                    String downText = "——" + temp.split("——")[1];
                    Log.e("sentence", temp);
                    sentence.setText(upText);
                    source.setText(downText);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }

        if (keyCode == KeyEvent.KEYCODE_H) {
            try {
                if (index >= 1) {
                    index--;
                    String temp = getSentence(url + index);
                    String upText = temp.split("——")[0];
                    String downText = "——" + temp.split("——")[1];
                    Log.e("sentence", temp);
                    sentence.setText(upText);
                    source.setText(downText);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }

        if (keyCode == KeyEvent.KEYCODE_F) {
            try {
                String temp = getSentence(url.split("\\?")[0]);
                Log.e("sentence", url.split("\\?")[0]);
                String upText = temp.split("——")[0];
                String downText = "——" + temp.split("——")[1];
                Log.e("sentence", temp);
                sentence.setText(upText);
                source.setText(downText);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }
//        if (keyCode == KeyEvent.KEYCODE_E) {
//            sentence.setText("E按下" + (countH++) + "次");
//            return true;
//        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 获取每日一句
     */
    public static String getSentence(String url) throws IOException {
        Document document = Jsoup.connect(url)
                .maxBodySize(Integer.MAX_VALUE)
                .data("query", "Java")
                .cookie("auth", "token")
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.110 Safari/537.36 Edg/96.0.1054.62")
                .timeout(10000)
                .get();

        return document.getElementsByTag("body").text();
    }

    /**
     * Android6.0之后需要动态申请权限
     */
    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= 23 && !isPermissionRequested) {
            isPermissionRequested = true;
            ArrayList<String> permissionsList = new ArrayList<>();
            String[] permissions = {
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.ACCESS_WIFI_STATE,
                    Manifest.permission.INTERNET,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_WIFI_STATE,
            };

            for (String perm : permissions) {
                if (PackageManager.PERMISSION_GRANTED != checkSelfPermission(perm)) {
                    permissionsList.add(perm);
                    // 进入到这里代表没有权限.
                }
            }

            if (!permissionsList.isEmpty()) {
                String[] strings = new String[permissionsList.size()];
                requestPermissions(permissionsList.toArray(strings), 0);
            }
        }
    }
}