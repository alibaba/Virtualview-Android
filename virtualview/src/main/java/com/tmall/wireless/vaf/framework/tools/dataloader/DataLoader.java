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

import android.util.Log;
import android.util.SparseArray;

import com.libra.virtualview.common.IDataLoaderCommon;
import com.tmall.wireless.vaf.framework.VafContext;
import com.tmall.wireless.vaf.framework.dataprocess.DownloadProcessorManager;
import com.tmall.wireless.vaf.virtualview.core.ViewBase;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by gujicheng on 16/9/27.
 */
public class DataLoader implements IDataLoader, Request.Listener, FileDataLoaderImp.Listener {
    private final static String TAG = "DataLoader_TMTEST";

    private final static String PREFIX_FILE = "file://";
    private final static String PREFIX_HTTP = "http://";
    private final static String PREFIX_HTTPS = "https://";

    private VafContext mContext;
    private Request mRequest;
    private FileDataLoaderImp mFileDataLoader;
    private SparseArray<ViewBase> mTaskIds = new SparseArray<>();
    private Listener mListener;

    public interface Listener {
        void onDataLoadStart(String api);
        void onDataLoadFinished(String api, JSONObject data, boolean successful);
    }

    public DataLoader(VafContext context) {
        mContext = context;
//        mRequest = new Request();
//        mRequest.setListener(this);
//        mRequest.start();
//
//        mFileDataLoader = new FileDataLoaderImp(context);
//        mFileDataLoader.setListener(this);
    }

    public void setListener(Listener lis) {
        mListener = lis;
    }

    @Override
    public void setDownloader(IDownloader downloader) {

//        mRequest.setDownloader(downloader);
    }

    @Override
    public void setDownloadProcessorManager(DownloadProcessorManager processorManager) {
//        mRequest.setDownloadProcessorManager(processorManager);
    }

    @Override
    public void destroy() {
        mContext = null;

//        mRequest.stop();
//        mRequest.destroy();
//        mRequest = null;

        cancelAll();

//        if (null != mFileDataLoader) {
//            mFileDataLoader.destroy();
//            mFileDataLoader = null;
//        }
    }

    @Override
    public void cancelAll() {
        mTaskIds.clear();
    }

    @Override
    public JSONObject loadSync(String url, String param, int mode) {
        JSONObject ret = null;

        if (null != url) {
            if (url.startsWith(PREFIX_FILE)) {
//                ret = mFileDataLoader.loadSync(url.substring(PREFIX_FILE.length()));
            }
        } else {
            Log.e(TAG, "url format error:" + url);
        }

        return ret;
    }

//    public boolean load(String url, String param, int destUuid) {
//        return load(url, param, destUuid, MODE_SET);
//    }

    @Override
    public boolean load(String url, String params, Map<String, String> dynamicParams, int destUuid, int mode) {
        boolean ret = false;

        if (null != url) {
            if (url.startsWith(PREFIX_FILE)) {
//                ret = mFileDataLoader.load(url.substring(PREFIX_FILE.length()), destUuid, mode);
            } else {
//                ret = mRequest.request(url, params, dynamicParams, destUuid, mode);
            }
        }

        return ret;
    }

    @Override
    public void onNetRequestBegin(String api, int uuid) {
        ViewBase vb = mContext.getViewManager().getViewFromUuid(uuid);
        mTaskIds.put(uuid, vb);

        if (null != mListener) {
            mListener.onDataLoadStart(api);
        }
        if (null != vb) {
            vb.onBeforeLoadData();
        }
//        Log.d(TAG, "onNetRequestBegin uuid:" + uuid);
    }

    @Override
    public void onNetRequestData(String api, int uuid, JSONObject content, int mode) {
//        Log.d(TAG, "onNetRequestData:" + content);
        if (null != content) {
            dealData(content, uuid, mode);
        }
    }

    @Override
    public void onNetRequestEnd(String api, int uuid, JSONObject data, boolean successful) {
        ViewBase v = mTaskIds.get(uuid);
        ViewBase vb = mContext.getViewManager().getViewFromUuid(uuid);

        if (null != mListener) {
            mListener.onDataLoadFinished(api, data, successful);
        }

        if (v != vb) {
            // skip
        } else {
            if (null != vb) {
                vb.onAfterLoadData(successful);
            }
        }
    }

    @Override
    public void onFileDataLoaderResult(JSONObject content, int uuid, int mode) {
//        Log.d(TAG, "onFileDataLoaderResult:" + content);
        if (null != content) {
            dealData(content, uuid, mode);
        }
    }

    private void dealData(JSONObject content, int uuid, int mode) {
        ViewBase vb = mContext.getViewManager().getViewFromUuid(uuid);
        long startTime = System.currentTimeMillis();
        if (null != vb) {
            switch (mode) {
                case IDataLoaderCommon.MODE_SET:
//                    Object obj = content.opt("data");
//                    Log.d(TAG, "set data ---------------" + content);
                    vb.setVData(content);
//                    vb.setData(content);
                    Log.d(TAG, "bindData set time:" + (System.currentTimeMillis() - startTime));
                    break;

                case IDataLoaderCommon.MODE_APPEND:
//                    Log.d(TAG, "dealData append:" + content);
                    vb.setVData(content, true);
//                    vb.appendData(content);
                    Log.d(TAG, "bindData append time:" + (System.currentTimeMillis() - startTime));
                    break;
            }
        } else {
            Log.e(TAG, "dealData failed, can not find view uuid:" + uuid);
        }
    }

}
