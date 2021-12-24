package com.example.testbtn;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

public class Activity_OneDay extends Activity {
    private TextView enSentence;
    private TextView enSentenceTranslation;
    private TextView enSentenceAuthor;
    private ImageView imageView;
    private Bitmap bitmap;
    private String today;
    private final String apiURL = "https://apiv3.shanbay.com/weapps/dailyquote/quote/?date=";

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oneday);
        // 隐藏状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // 屏幕常亮
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        // 控件绑定
        {
            imageView = findViewById(R.id.sentenceImg);
            enSentence = findViewById(R.id.enSentence);
            enSentenceTranslation = findViewById(R.id.enSentenceTranslation);
            enSentenceAuthor = findViewById(R.id.enSentenceAuthor);
        }
        // 获取今天日期
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        today = formatter.format(calendar.getTime());
        String m_apiURL = apiURL + today;
        loadImage(m_apiURL);
    }

    public void loadImage(String url) {
        //开启线程加载图片
        new Thread() {
            public void run() {
                // OneDay类初始化
                OneDay oneDay = new OneDay();
                // 获取英文句子、英文句子翻译、英文句子作者、英文图片的网址
                try {
                    oneDay.init(url);

                    {
                        Log.e("img", oneDay.getEnSentence());
                        Log.e("img", oneDay.getEnSentenceTranslation());
                        Log.e("img", oneDay.getEnSentenceAuthor());
                        Log.e("img", oneDay.getEnImageAndSentence().get(0).toString());
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
                // 通过图片网址将图片转为InputStream再转为Bitmap
                try {
                    String imgURL = oneDay.getEnImageAndSentence().get(0).toString();
                    Log.e("img", "SuccessImgURL");
                    InputStream imgInputStream;
                    // 定义存放请求头的Map
                    Map<String, String> headers = new HashMap<>();
                    // 添加请求头到Map
                    headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.110 Safari/537.36 Edg/96.0.1054.62");
                    // 获取图片的InputStream
                    imgInputStream = OneDay.getImgInputStream(imgURL, headers);
                    // 将InputStream转为Bitmap
                    bitmap = BitmapFactory.decodeStream(imgInputStream);
                    // 获取外部存储目录
                    String sdPath = Environment.getExternalStorageDirectory().getPath();
//                    File file = new File(sdPath + "/AAABBB/22.png");
//                    if (file.exists()) {
//                        FileInputStream fis = new FileInputStream(file);
//                        Bitmap bitmap = BitmapFactory.decodeStream(fis);
//                        Activity activity = (Activity) imageView.getContext();
//                        activity.runOnUiThread(() -> imageView.setImageBitmap(bitmap));
//                    } else {
//
//                    }
                    Activity activity = (Activity) imageView.getContext();
                    activity.runOnUiThread(() -> {
                        imageView.setImageBitmap(bitmap);
                        enSentence.setText(oneDay.getEnSentence());
                        enSentenceTranslation.setText(oneDay.getEnSentenceTranslation());
                        enSentenceAuthor.setText(oneDay.getEnSentenceAuthor());
                    });
                    Log.e("img", "Success");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * 按键监听
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_G) {
            String yesterdayStr = null;
            try {
                yesterdayStr = dateShift("yyyy-MM-dd", today, -1);
                Log.e("yesterday", yesterdayStr);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            String m_apiURL = apiURL + yesterdayStr;
            today = yesterdayStr;
            loadImage(m_apiURL);
            return true;
        }

        if (keyCode == KeyEvent.KEYCODE_H) {
            // 最多向后33天就没有了
            String tomorrowStr = null;
            try {
                tomorrowStr = dateShift("yyyy-MM-dd", today, 1);
                Log.e("tomorrow", tomorrowStr);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            String m_apiURL = apiURL + tomorrowStr;
            today = tomorrowStr;
            loadImage(m_apiURL);
            return true;
        }

        if (keyCode == KeyEvent.KEYCODE_F) {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            today = formatter.format(calendar.getTime());
            Log.e("today", today);
            String m_apiURL = apiURL + today;
            loadImage(m_apiURL);
            return true;
        }
//        if (keyCode == KeyEvent.KEYCODE_E) {
//            sentence.setText("E按下" + (countH++) + "次");
//            return true;
//        }
        return super.onKeyDown(keyCode, event);
    }

    public static String dateShift(String dateFormat, String date, int intNum) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);//格式工具
        Date da = simpleDateFormat.parse(date);
        Calendar calendar = new GregorianCalendar();
        assert da != null;
        calendar.setTime(da);
        calendar.add(Calendar.DAY_OF_MONTH, intNum);//日期偏移,正数向前,负数向后!
        return simpleDateFormat.format(calendar.getTime());
    }
}
