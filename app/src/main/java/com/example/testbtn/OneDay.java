package com.example.testbtn;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;


public class OneDay {
    private String imgURL;
    private String cnSentence;
    private String enSentence;
    private String enSentenceTranslation;
    private String enSentenceAuthor;
    private JSONArray enImageAndSentence;
    private String word;


    public void init(String url) throws IOException {
        int responseCode;
        HttpURLConnection urlConnection;
        BufferedReader reader;
        String content;
        // 获取网页源码
        try {
            //生成一个URL对象
            URL m_url = new URL(url);
            //打开URL
            urlConnection = (HttpURLConnection) m_url.openConnection();
            //获取服务器响应代码
            responseCode = urlConnection.getResponseCode();
            if (responseCode == 200) {
                //得到输入流，即获得了网页的内容
                reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "GBK"));
                while ((content = reader.readLine()) != null) {
                    System.out.println(content);
                    JSONObject object = JSONObject
                            .parseObject(content);
                    this.enSentence = object.getString("content");
                    this.enSentenceAuthor = object.getString("author");
                    this.enImageAndSentence = object.getJSONArray("origin_img_urls");
                    this.enSentenceTranslation = object.getString("translation");
                }
            } else {
                System.out.println("获取不到网页的源码，服务器响应代码为：" + responseCode);
            }
        } catch (Exception e) {
            System.out.println("获取不到网页的源码,出现异常：" + e);
        }
    }

    public void showInfo() {
        System.out.println(getEnSentence());
        System.out.println(getEnSentenceAuthor());
        System.out.println(getEnSentenceTranslation());
        System.out.println(getEnImageAndSentence().get(0));
    }

    public String getImgURL() {
        return imgURL;
    }

    public String getCnSentence() {
        return cnSentence;
    }

    public String getEnSentence() {
        return enSentence;
    }

    public String getEnSentenceTranslation() {
        return enSentenceTranslation;
    }

    public String getEnSentenceAuthor() {
        return enSentenceAuthor;
    }

    public JSONArray getEnImageAndSentence() {
        return enImageAndSentence;
    }

    public String getWord() {
        return word;
    }

    public Bitmap inputStream2Bitmap(InputStream inputStream) {
        return BitmapFactory.decodeStream(inputStream);
    }

    /**
     * 获取网络图片位InputStream
     *
     * @param path 图片网址
     * @return InputStream 返回InputStream
     * @throws Exception 抛出文件异常
     */
    public InputStream getImageStream(String path) throws Exception {
        URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(5 * 1000);
        conn.setRequestMethod("GET");
        if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
            return conn.getInputStream();
        }
        return null;
    }

    /**
     * 保存InputStreamImage到本地
     *
     * @param inputStream 图片的InputStream
     */
    public void saveInputStreamImage(InputStream inputStream) {
        Bitmap mBitmap = BitmapFactory.decodeStream(inputStream);
        //3.保存Bitmap
        try {
            String savePath = getSDCardPath() + "/AAABBB/";
            File temp = new File(savePath);
            //文件
            String filepath = savePath + "/21.png";
            File file = new File(filepath);
            if (!temp.exists()) {
                Boolean b = temp.mkdirs();
                Log.e("Bitmap", "Write Successful");
            }
            if (!file.exists()) {
                Boolean b = file.createNewFile();
                Log.e("Bitmap","Success");
            }
            FileOutputStream fos = null;
            fos = new FileOutputStream(file);
            mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取SDCard的目录路径功能
     *
     * @return String
     */
    public String getSDCardPath() {
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
     * 携带头信息下载网络图片
     *
     * @param imageUrl 图片url
     * @param headers  http协议交互中header信息，如Cookie
     * @return stream 返回图片的InputStream
     */
    public static InputStream getImgInputStream(String imageUrl, Map<String, String> headers) {
        InputStream stream = null;
        try {
            URL url = new URL(imageUrl);
            URLConnection conn = url.openConnection();
            //设置头信息
            if (headers != null && !headers.isEmpty()) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    conn.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }
            //将URL作为输入
            conn.setDoInput(true);
            stream = conn.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stream;
    }
}
