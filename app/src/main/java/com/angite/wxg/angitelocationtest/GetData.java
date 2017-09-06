package com.angite.wxg.angitelocationtest;
import android.util.Log;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by wxg on 2017/7/25.
 */

public class GetData {
    // 定义一个获取网络图片数据的方法:
    public static byte[] getImage(String path) throws Exception {
        URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        // 设置连接超时为5秒
        conn.setConnectTimeout(5000);
        // 设置请求类型为Get类型
        conn.setRequestMethod("GET");
        // 判断请求Url是否成功
        if (conn.getResponseCode() != 200) {
            throw new RuntimeException("请求url失败");
        }
        InputStream inStream = conn.getInputStream();
        byte[] bt = StreamTool.read(inStream);
        inStream.close();
        return bt;
    }

    // 获取网页的html源代码
    public static String getHtml(String path) throws Exception {
        URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setConnectTimeout(5000);

        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setUseCaches(false);
        String data="DeviceID=1&useID=1&lat=0&lon=0";
        OutputStream out=conn.getOutputStream();
        out.write(data.getBytes());
        out.flush();
        Log.i("wxg","responseCode:"+conn.getResponseCode());

        if (conn.getResponseCode() == 200||conn.getResponseCode()==302) {
            InputStream in = conn.getInputStream();
            byte[] data1 = StreamTool.read(in);
            String html = new String(data1, "UTF-8");
            return html;
        }
        return null;
    }
    public static boolean postLocation(String strUrl,String strParam)throws Exception{
        URL url=new URL(strUrl);
        HttpURLConnection conn=(HttpURLConnection)url.openConnection();
        conn.setConnectTimeout(5000);
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setUseCaches(false);
        OutputStream out=conn.getOutputStream();
        out.write(strParam.getBytes());
        out.flush();
        if (conn.getResponseCode() == 200) {
            return true;
        }
        return false;

    }
}
