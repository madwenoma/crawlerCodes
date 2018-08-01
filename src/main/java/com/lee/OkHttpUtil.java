package com.lee;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class OkHttpUtil {

    public static boolean downloadPic(String picUrl, String localFileName) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder().build();
        final File file = new File(localFileName);
        if (file.exists()) {
            file.delete();
        } else {
            try {
                FileUtils.forceMkdir(file.getParentFile());
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("create file failed, file = " + file.getAbsolutePath());
            }
        }

        InputStream is = null;
        FileOutputStream fos = null;

        try {
            Request request = new Request.Builder().url(picUrl).build();
            Call call = okHttpClient.newCall(request);
            Response response = call.execute();
            byte[] buf = new byte[2048];
            int len = 0;
            is = response.body().byteStream();
            fos = new FileOutputStream(file);
            while ((len = is.read(buf)) != -1) {
                fos.write(buf, 0, len);
            }
            fos.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("down loaded failed" + e.getStackTrace());
            }
        }

    }
}