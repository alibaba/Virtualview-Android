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

package com.tmall.wireless.vaf.virtualview.core;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import android.support.v4.util.SparseArrayCompat;
import android.util.Log;
import android.view.View;

import com.tmall.wireless.vaf.framework.VafContext;
import com.tmall.wireless.vaf.virtualview.event.EventData;
import com.tmall.wireless.vaf.virtualview.event.EventManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by gujicheng on 16/12/19.
 */

public class ArrayAdapter extends Adapter {
    private final static String TAG = "ArrayAdapter_TMTEST";

    private AtomicInteger mTypeId = new AtomicInteger(0);
    private ConcurrentHashMap<String, Integer> mTypeMap = new ConcurrentHashMap<>();
    private SparseArrayCompat<String> mId2Types = new SparseArrayCompat<>();
    private JSONArray mData;

    public ArrayAdapter(VafContext context) {
        super(context);
    }

    @Override
    public void setData(Object str) {
        if (null == str) {
            mData = null;
        } else if (str instanceof JSONArray) {
            mData = (JSONArray) str;
        } else {
            Log.e(TAG, "setData failed:" + str);
        }
    }

    @Override
    public int getItemCount() {
        if (null != mData) {
            return mData.length();
        }
        return 0;
    }

    @Override
    public int getType(int pos) {
        if (null != mData) {
            try {
                JSONObject obj = mData.getJSONObject(pos);
                String type = obj.optString(TYPE);
                if (mTypeMap.containsKey(type)) {
                    return mTypeMap.get(type).intValue();
                } else {
                    int newType = mTypeId.getAndIncrement();
                    mTypeMap.put(type, newType);
                    mId2Types.put(newType, type);
                    return newType;
                }
            } catch (JSONException e) {
            }
        }
        return 0;
    }

    @Override
    public void onBindViewHolder(Adapter.ViewHolder vh, int pos) {
        try {
            Object obj = mData.get(pos);

            if (obj instanceof JSONObject) {
                JSONObject jObj = (JSONObject)obj;
                ViewBase vb = ((IContainer)vh.mItemView).getVirtualView();
                if (null != vb) {
                    vb.setVData(jObj);
                }

                if (vb.supportExposure()) {
                    mContext.getEventManager().emitEvent(EventManager.TYPE_Exposure, EventData.obtainData(mContext, vb));
                }

                vb.ready();
            } else {
                Log.e(TAG, "failed");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Adapter.ViewHolder onCreateViewHolder(int viewType) {
        String type = mId2Types.get(viewType);
        View container = mContainerService.getContainer(type, mContainerId);
        return new ViewHolder(container);
    }
}
