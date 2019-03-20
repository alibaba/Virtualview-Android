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

package com.tmall.wireless.vaf.expr.engine;

import android.support.annotation.Keep;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by gujicheng on 17/3/27.
 */
@Keep
public class DataManager {
    private JSONObject mData = new JSONObject();
    private com.alibaba.fastjson.JSONObject mFastData = new com.alibaba.fastjson.JSONObject();

    public DataManager() {
        try {
            mData.put("time", 10);
        } catch (JSONException e) {
        }
        mFastData.put("time", 10);
    }

    public void replaceData(Object obj) {
        if (null != obj) {
            if (obj instanceof JSONObject) {
                mData = (JSONObject) obj;
            } else if (obj instanceof com.alibaba.fastjson.JSONObject) {
                mFastData = (com.alibaba.fastjson.JSONObject) obj;
            }
        }
    }

    public void add(String key, Object value) {
        if (!TextUtils.isEmpty(key)) {
            try {
                mData.put(key, value);
                mFastData.put(key, value);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void add(Map<String, Object> map) {
        if (null != map) {
            Iterator<String> iterator = map.keySet().iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                Object value = map.get(key);
                try {
                    mData.put(key, value);
                    mFastData.put(key, value);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public Object getData(String key) {
        return mData.opt(key);
    }

    public void setData(String key, Object value) {
        try {
            mData.put(key, value);
            mFastData.put(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
