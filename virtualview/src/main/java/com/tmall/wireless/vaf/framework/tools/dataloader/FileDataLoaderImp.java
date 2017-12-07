/*
 * MIT License
 *
 * Copyright (c) 2017 Alibaba Group
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

package com.tmall.wireless.vaf.framework.tools.dataloader;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.tmall.wireless.vaf.framework.VafContext;
import com.tmall.wireless.vaf.virtualview.Helper.DataOpt;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by gujicheng on 16/10/8.
 */
public class FileDataLoaderImp {
    private final static String TAG = "FDLoaderImp_TMTEST";

    private final static int MSG_READ = 1;

    private Handler mUIHandler;
    private Handler mHandler;
    private HandlerThread mThread;
    private Listener mListener;
    private VafContext mContext;

    public interface Listener {
        void onFileDataLoaderResult(JSONObject content, int uuid, int mode);
    }

    public FileDataLoaderImp(VafContext context) {
        mContext = context;
        mUIHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (null != mListener) {
                    mListener.onFileDataLoaderResult((JSONObject) msg.obj, msg.arg1, msg.arg2);
                } else {
                    Log.e(TAG, "listener is null");
                }
                super.handleMessage(msg);
            }
        };

        mThread = new HandlerThread("file_parse");
        mThread.start();
        mHandler = new Handler(mThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == MSG_READ) {

                    JSONObject obj = loadSync((String) msg.obj);

                    Message m = mUIHandler.obtainMessage(MSG_READ, obj);
                    m.arg1 = msg.arg1;
                    m.arg2 = msg.arg2;
                    mUIHandler.sendMessage(m);
                }
                super.handleMessage(msg);
            }
        };
    }

    public void destroy() {
        mUIHandler.removeMessages(MSG_READ);
        mUIHandler = null;

        mHandler.removeMessages(MSG_READ);
        mHandler = null;

        mThread.getLooper().quit();
        mThread = null;

        mContext = null;
    }

    public void setListener(Listener lis) {
        mListener = lis;
    }

    public JSONObject loadSync(String path) {
        JSONObject obj = null;

        String strJson = getJson(mContext.getContext(), path);
//        Log.d(TAG, "loadSync str:" + strJson);

        if (null != strJson) {
            try {
                obj = new JSONObject(strJson);

                // filter, opt data
                DataOpt.filter(obj);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Log.e(TAG, "loadSync result is null");
        }

        return obj;
    }

    public boolean load(String path, int uuid, int mode) {
        boolean ret = false;

        if (!TextUtils.isEmpty(path)) {
            Message msg = mHandler.obtainMessage(MSG_READ, path);
            msg.arg1 = uuid;
            msg.arg2 = mode;
            ret = mHandler.sendMessage(msg);
        }

        return ret;
    }

    public static String getJson(Context context, String fileName) {
        StringBuilder sb = new StringBuilder();
        AssetManager am = context.getAssets();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    am.open(fileName)));
            String next = "";
            while (null != (next = br.readLine())) {
                sb.append(next);
            }
        } catch (IOException e) {
            e.printStackTrace();
            sb.delete(0, sb.length());
        }
        return sb.toString().trim();
    }
}
