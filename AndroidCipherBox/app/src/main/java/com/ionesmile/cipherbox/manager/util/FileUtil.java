package com.ionesmile.cipherbox.manager.util;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * Created by iOnesmile on 2017/2/17 0017.
 */
public class FileUtil {

    private static final String TAG = FileUtil.class.getSimpleName();

    public static boolean writeFile(String file, String content) {
        boolean saveSuccess = true;
        BufferedWriter bw = null;
        try {
            File writeFile = new File(file);
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(writeFile)));
            bw.write(content);
        } catch (IOException e) {
            saveSuccess = false;
            Log.w(TAG, "writeFile() file = " + file, e);
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    saveSuccess = false;
                    Log.w(TAG, "writeFile() file = " + file, e);
                }
            }
        }
        return saveSuccess;
    }

    public static String readFile(String file){
        File readFile = new File(file);
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(readFile)));
            StringBuilder sb = new StringBuilder();
            String line;
            while((line = br.readLine()) != null){
                sb.append(line);
            }
            return sb.toString();
        } catch (IOException e) {
            Log.w(TAG, "readFile() file = " + file, e);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    Log.w(TAG, "readFile() file = " + file, e);
                }
            }
        }
        return null;
    }
}
