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

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import com.tmall.wireless.vaf.framework.dataprocess.DownloadProcessorManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by gujicheng on 16/8/10.
 */
public class Request {
    private final static String TAG = "Request_TMTEST";

    private final static int MSG_BEGIN = 1;
    private final static int MSG_DATA = 2;
    private final static int MSG_END = 3;
    private final static int MSG_DOWNLOAD = 4;

    private Listener mListener;
    private Handler mMainHandler;
    private IDownloader mDownloader;
    private DownloadProcessorManager mDownloadProcessorManager;

    private HandlerThread mWorkThread;
    private Handler mWorkHandler;

    public interface Listener {
        void onNetRequestBegin(String api, int uuid);

        void onNetRequestData(String api, int uuid, JSONObject content, int mode);

        void onNetRequestEnd(String api, int uuid, JSONObject data, boolean successful);
    }

    public void setDownloadProcessorManager(DownloadProcessorManager processorManager) {
        mDownloadProcessorManager = processorManager;
    }

    public void setDownloader(IDownloader downloader) {
        mDownloader = downloader;
    }

    public Request() {
        mDownloader = new DefaultDownloader();

        mMainHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                if (null != mListener) {
                    switch (msg.what) {
                        case MSG_BEGIN:
                            mListener.onNetRequestBegin((String) msg.obj, msg.arg1);
                            break;

                        case MSG_DATA: {
                            JSONObject jsonData = (JSONObject) msg.obj;
                            mListener.onNetRequestData(jsonData.optString("api"), msg.arg1, jsonData.optJSONObject("data"), msg.arg2);
                            break;
                        }

                        case MSG_END: {
                            JSONObject jsonData = (JSONObject) msg.obj;
                            mListener.onNetRequestEnd(jsonData.optString("api"), msg.arg1, jsonData.optJSONObject("data"), (msg.arg2 > 0) ? true : false);
                            break;
                        }
                    }
                }
//                tvObj.setText(String.valueOf(msg.arg1));
            }
        };
    }

    public void start() {
        mWorkThread = new HandlerThread("download_data");
        mWorkThread.start();
        mWorkHandler = new Handler(mWorkThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MSG_DOWNLOAD:
                        realDownload((RequestData)msg.obj);
                        break;
                }
            }
        };
    }

    public void stop() {
        mWorkHandler.removeMessages(MSG_DOWNLOAD);
        mWorkHandler = null;

        mWorkThread.getLooper().quit();
        mWorkThread = null;
    }

    private void realDownload(RequestData data) {
        long startTime = System.currentTimeMillis();
        Message msg = mMainHandler.obtainMessage(MSG_BEGIN, data.mApi);
        msg.arg1 = data.mUuid;
        mMainHandler.sendMessage(msg);

        int reqState = 0;

        Map<String, String> dParams;
        if (null == data.mDynamicParams) {
            dParams = new HashMap<>();
        } else {
            dParams = data.mDynamicParams;
        }

        boolean canDownload = true;
        if (null != mDownloadProcessorManager) {
            canDownload = mDownloadProcessorManager.beforeDownload(data.mApi, dParams);
        }

        JSONObject obj = null;
        if (canDownload) {
            obj = mDownloader.download(data.mApi, data.mParams, dParams);
//            Log.d(TAG, "api:" + data.mApi + "  data:" + obj);
            Log.d(TAG, "download from net api:" + data.mApi + "  time:" + (System.currentTimeMillis() - startTime));

            if (null != mDownloadProcessorManager) {
                mDownloadProcessorManager.afterDownload(data.mApi, obj, obj != null);
            }

            if (null != obj) {
                JSONObject objData = new JSONObject();
                try {
                    objData.put("api", data.mApi);
                    if (null != obj) {
                        objData.put("data", obj);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                msg = mMainHandler.obtainMessage(MSG_DATA, objData);
                msg.arg1 = data.mUuid;
                msg.arg2 = data.mMode;
                mMainHandler.sendMessage(msg);
                reqState = 1;
            }
        }

        JSONObject objData = new JSONObject();
        try {
            objData.put("api", data.mApi);
            if (null != obj) {
                objData.put("data", obj);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        msg = mMainHandler.obtainMessage(MSG_END, objData);
        msg.arg1 = data.mUuid;
        msg.arg2 = reqState;
        mMainHandler.sendMessage(msg);

        RequestData.recycle(data);

        Log.d(TAG, "download deal finished api:" + data.mApi + "  time:" + (System.currentTimeMillis() - startTime));
    }

    public void destroy() {
        mListener = null;
    }

    public void setListener(Listener lis) {
        mListener = lis;
    }

    public boolean request(final String strUrl, final String params, final Map<String, String> dynamicParams, final int uuid, final int mode) {
        boolean ret = false;
//        Log.d(TAG, "request}}}}}}}}}}} strUrl:" + strUrl);
        if (null != mWorkHandler) {
            RequestData data = RequestData.obtain(strUrl, params, dynamicParams, uuid, mode);
            mWorkHandler.sendMessage(mWorkHandler.obtainMessage(MSG_DOWNLOAD, data));
            ret = true;
        }

//        if (!TextUtils.isEmpty(strUrl)) {
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
////                    String content = null;
//
//                    Message msg = mMainHandler.obtainMessage(MSG_BEGIN, strUrl);
//                    msg.arg1 = uuid;
//                    mMainHandler.sendMessage(msg);
//
//                    int reqState = 0;
//
//                    Map<String, String> dParams;
//                    if (null == dynamicParams) {
//                        dParams = new HashMap<>();
//                    } else {
//                        dParams = dynamicParams;
//                    }
//
//                    boolean canDownload = true;
//                    if (null != mDataProcessPolicyManager) {
//                        canDownload = mDataProcessPolicyManager.beforeDownload(strUrl, dParams);
//                    }
//
//                    JSONObject obj = null;
//                    if (canDownload) {
//                        obj = mDownloader.download(strUrl, params, dParams);
//                        Log.d(TAG, "api:" + strUrl + "  data:" + obj);
//
//                        if (null != mDataProcessPolicyManager) {
//                            mDataProcessPolicyManager.afterDownload(strUrl, obj, obj != null);
//                        }
//
//                        if (null != obj) {
//                            if (null != mDataProcessPolicyManager) {
//                                Object ret = mDataProcessPolicyManager.process(strUrl, obj);
//                                if (ret instanceof JSONObject) {
//                                    obj = (JSONObject) ret;
//                                } else {
//                                    Log.e(TAG, "process data failed");
//                                }
//                            }
//
//                            JSONObject objData = new JSONObject();
//                            try {
//                                objData.put("api", strUrl);
//                                if (null != obj) {
//                                    objData.put("data", obj);
//                                }
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                            msg = mMainHandler.obtainMessage(MSG_DATA, objData);
//                            msg.arg1 = uuid;
//                            msg.arg2 = mode;
//                            mMainHandler.sendMessage(msg);
//                            reqState = 1;
//                        }
//                    }
//
//                    JSONObject objData = new JSONObject();
//                    try {
//                        objData.put("api", strUrl);
//                        if (null != obj) {
//                            objData.put("data", obj);
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                    msg = mMainHandler.obtainMessage(MSG_END, objData);
//                    msg.arg1 = uuid;
//                    msg.arg2 = reqState;
//                    mMainHandler.sendMessage(msg);
//                }
//
//            }).start();
//
//            ret = true;
//        }

        return ret;
    }

    private static String getStringFromInputStream(InputStream is)
            throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = -1;
        while ((len = is.read(buffer)) != -1) {
            os.write(buffer, 0, len);
        }
        is.close();
        String state = os.toString();
        os.close();
        return state;
    }
}
