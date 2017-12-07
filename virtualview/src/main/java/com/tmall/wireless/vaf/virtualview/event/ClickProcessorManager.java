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

package com.tmall.wireless.vaf.virtualview.event;

import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.util.SparseArray;

import org.json.JSONObject;

/**
 * Created by gujicheng on 17/1/11.
 */

public class ClickProcessorManager {
    private final static String TAG = "CliProManager_TMTEST";

    private ArrayMap<String, IClickProcessor> mProcessors = new ArrayMap<>();
    private IClickProcessor mDefaultProcessor;

    public void register(String type, IClickProcessor p) {
        if (!TextUtils.isEmpty(type) && null != p) {
            mProcessors.put(type, p);
        }
    }

    public void registerDefault(IClickProcessor p) {
        if (null != p) {
            mDefaultProcessor = p;
        }
    }

    public void unregister(String type) {
        mProcessors.remove(type);
    }

    public void unregisterDefault() {
        mDefaultProcessor = null;
    }

    public boolean process(EventData data) {
        boolean ret = false;
        if (null != data) {
            JSONObject comData = (JSONObject) data.mVB.getViewCache().getComponentData();
            if (null != comData) {
                IClickProcessor p = mProcessors.get(comData.optString("type"));
                if (null != p) {
                    ret = p.process(data);
                } else if (null != mDefaultProcessor){
                    // default
                    ret = mDefaultProcessor.process(data);
                } else {
//                    Log.e(TAG, "no processor");
                }
            }
        }

        return ret;
    }
}
