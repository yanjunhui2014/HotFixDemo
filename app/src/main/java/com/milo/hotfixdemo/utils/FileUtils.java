package com.milo.hotfixdemo.utils;

import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Title：
 * Describe：
 * Remark：
 * <p>
 * Created by Milo
 * E-Mail : 303767416@qq.com
 * 2021/4/5
 */
public class FileUtils {

    public static boolean copy(String fromPath, String filePath) {
        if (TextUtils.isEmpty(filePath) || TextUtils.isEmpty(fromPath)) {
            return false;
        }

        File file = createNewFile(filePath);
        if (file == null) {
            return false;
        }

        OutputStream outStream = null;
        FileInputStream inputStream = null;

        try {
            File fromFile = new File(fromPath);
            inputStream = new FileInputStream(fromFile);
            outStream = new FileOutputStream(file);
            byte[] buffer = new byte[4 * 1024];
            while (inputStream.read(buffer) != -1) {
                outStream.write(buffer);
            }
            outStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (outStream != null) {
                    outStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return true;
    }

    /**
     * 在SD卡上创建文件
     *
     * @param fileName - 文件名(包括路径)
     */
    @Deprecated
    public static File createNewFile(String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            return null;
        }

        File file = new File(fileName);
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return file;
    }

}
