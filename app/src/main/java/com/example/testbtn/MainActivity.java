package com.example.testbtn;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.facebook.drawee.backends.pipeline.Fresco;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import lib.lhh.fiv.library.FrescoZoomImageView;

public class MainActivity extends Activity {
    private static final String TAG = "mark";
    private TextView sentence;
    private TextView source;
    boolean isPermissionRequested;
    private String url = "https://lindum.top/yiyan/api.php?index=";
    private int index = -1;
    private int sentenceQuantity = 0;
    private FrescoZoomImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Fresco.initialize(this);
        // 控件绑定
        {
            sentence = findViewById(R.id.sentence);
            source = findViewById(R.id.source);
            img = (FrescoZoomImageView) findViewById(R.id.img);
        }
        requestPermission();
        //适配安卓11及以下版本的存储权限请求
        if (Build.VERSION.SDK_INT == 30 && !Environment.isExternalStorageManager()) {
            requestExternalStorage(this);
        }
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
        // 获取每日一图
        //            String mImgUrl = getImgURL("https://api.ixiaowai.cn/api/api.php?return=json");
//            System.out.println(temp);
        // 设置每日一图
//            Bitmap temp = img.getDrawingCache();
        img.loadView("https://cdn.seovx.com/d/?mom=302",R.mipmap.balloon);
        OneDay oneDay = new OneDay();
        try {
            oneDay.init("https://apiv3.shanbay.com/weapps/dailyquote/quote/");
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
//            Log.e("img",getImageStream((String) oneDay.getEnImageAndSentence().get(0)).toString());
            saveBitmap(getImageStream(oneDay.getEnImageAndSentence().get(0).toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取网络图片位InputStream
     * @param path of image
     * @return InputStream
     * @throws Exception
     */
    public InputStream getImageStream(String path) throws Exception{
        URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(5 * 1000);
        conn.setRequestMethod("GET");
        if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){
            return conn.getInputStream();
        }
        return null;
    }

    /**
     * 保存Bitmap到本地
     * @param inputStream inputStream
     */
    public void saveBitmap(InputStream inputStream){
        Bitmap mBitmap = BitmapFactory.decodeStream(inputStream);
        //3.保存Bitmap
        try {
            String savePath = getSDCardPath()+"/AAABBB/";
            File temp = new File(savePath);
            //文件
            String filepath = savePath + "/Screen_1.png";
            File file = new File(filepath);
            if (!temp.exists()) {
                temp.mkdirs();
            }
            if (!file.exists()) {
                file.createNewFile();
            }

            FileOutputStream fos = null;
            fos = new FileOutputStream(file);
            mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
            Toast.makeText(this, "截屏文件已保存至SDCard/AAABBB/下", Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 获取SDCard的目录路径功能
     *
     * @return String
     */
    private String getSDCardPath() {
        File sdcardDir = null;
        //判断SDCard是否存在
        boolean sdcardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
        if (sdcardExist) {
            sdcardDir = Environment.getExternalStorageDirectory();
        }
        assert sdcardDir != null;
        return sdcardDir.toString();
    }
    /**
     * 按键监听
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 获取每日一图
        try {
            String mImgUrl = getImgURL("https://api.ixiaowai.cn/api/api.php?return=json");
//            System.out.println(temp);
            // 设置每日一图
//            Bitmap temp = img.getDrawingCache();
            img.loadView(mImgUrl,R.mipmap.balloon);

        } catch (IOException e) {
            e.printStackTrace();
        }
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
     * 获取每日一图
     */
    public static String getImgURL(String url) throws IOException {
        Document document = Jsoup.connect(url)
                .maxBodySize(Integer.MAX_VALUE)
                .data("query", "Java")
                .cookie("auth", "token")
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.110 Safari/537.36 Edg/96.0.1054.62")
                .timeout(10000)
                .get();
        String temp = document.getElementsByTag("body").text();
        JSONObject object = JSONObject
                .parseObject(temp);
        return object.getString("imgurl");
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

    /**
     * Android11 Storage permission request
     * @param context context
     */
    public void requestExternalStorage(Context context) {
        Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
        intent.setData(Uri.parse("package:" + context.getPackageName()));
        context.startActivity(intent);
    }
}