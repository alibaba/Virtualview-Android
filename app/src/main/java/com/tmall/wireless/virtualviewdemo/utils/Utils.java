/*
 * MIT License
 *
 * Copyright (c) 2018 Alibaba Group
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.tmall.wireless.virtualviewdemo.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.Context;
import android.util.Log;
import com.libra.virtualview.compiler.ViewCompiler;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by longerian on 2018/2/26.
 *
 * @author longerian
 * @date 2018/02/26
 */

public class Utils {

    public static byte[] readBin(String path) {
        try {
            FileInputStream fin = new FileInputStream(path);
            int length = fin.available();
            byte[] buf = new byte[length];
            fin.read(buf);
            fin.close();
            return buf;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void copyPlayXML(Context context, String name, String target) {
        try {
            InputStream inputStream = context.getAssets().open(name);
            BufferedReader inputStreamReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder sb = new StringBuilder();
            String str;
            while ((str = inputStreamReader.readLine()) != null) {
                sb.append(str);
            }
            inputStreamReader.close();
            FileOutputStream fos = new FileOutputStream(target);
            fos.write(sb.toString().getBytes());
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean compile(String type, String sourcePath, String outputPath) {
        boolean ret = false;
        ViewCompiler compiler = new ViewCompiler();
        compiler.resetString();
        compiler.resetExpr();
        if (compiler.newOutputFile(outputPath, 1, 1)) {
            if (!compiler.compile(type, sourcePath)) {
                Log.d("ParserDemoActivity", "compile file error --> " + sourcePath);
            }
            ret = compiler.compileEnd();
            if (!ret) {
                Log.d("ParserDemoActivity", "compile file end error --> " + sourcePath);
            }
        } else {
            Log.d("ParserDemoActivity", "new output file failed --> " + sourcePath);
        }
        return ret;
    }

    public static JSONObject getJSONDataFromAsset(Context context, String name) {
        try {
            InputStream inputStream = context.getAssets().open(name);
            BufferedReader inputStreamReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder sb = new StringBuilder();
            String str;
            while ((str = inputStreamReader.readLine()) != null) {
                sb.append(str);
            }
            inputStreamReader.close();
            return new JSONObject(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] getBytesByInputStream(InputStream is) {
        byte[] bytes = null;
        BufferedInputStream bis = new BufferedInputStream(is);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BufferedOutputStream bos = new BufferedOutputStream(baos);
        byte[] buffer = new byte[1024 * 8];
        int length = 0;
        try {
            while ((length = bis.read(buffer)) > 0) {
                bos.write(buffer, 0, length);
            }
            bos.flush();
            bytes = baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                bis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return bytes;
    }
}
