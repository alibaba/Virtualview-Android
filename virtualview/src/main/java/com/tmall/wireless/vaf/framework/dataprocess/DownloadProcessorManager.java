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

package com.tmall.wireless.vaf.framework.dataprocess;

import android.text.TextUtils;
import android.util.Log;

import com.tmall.wireless.vaf.framework.VafContext;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by gujicheng on 17/4/5.
 */

public class DownloadProcessorManager {
    private final static String TAG = "DownloadProM_TMTEST";

    private VafContext mContext;
    private Map<String, IDownloadProcessor> mDownloadMap = new HashMap<>();

    public DownloadProcessorManager(VafContext context) {
        mContext = context;
    }

    public void reset() {
        for ( IDownloadProcessor processor : mDownloadMap.values() ) {
            processor.reset();
        }
    }

    public boolean beforeDownload(String api, Map<String, String> dynamicParams) {
        IDownloadProcessor processor = mDownloadMap.get(api);
        if (null != processor) {
            return processor.before(mContext, dynamicParams);
        }
        return true;
    }

    public void afterDownload(String api, JSONObject data, boolean successful) {
        IDownloadProcessor processor = mDownloadMap.get(api);
        if (null != processor) {
            processor.after(mContext, api, data, successful);
        }
    }

    public void registerDownloadProcessor(String api, IDownloadProcessor processor) {
        if (!TextUtils.isEmpty(api) && null != processor) {
            mDownloadMap.put(api, processor);
        } else {
            Log.d(TAG, "registerDownloadProcessor param invalidate");
        }
    }

    public void unregisterDownloadProcessor(String api) {
        if (!TextUtils.isEmpty(api)) {
            mDownloadMap.remove(api);
        } else {
            Log.d(TAG, "unregisterDownloadProcessor param invalidate");
        }
    }
}
