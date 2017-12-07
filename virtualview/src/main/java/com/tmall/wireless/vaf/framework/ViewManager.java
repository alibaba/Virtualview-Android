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

package com.tmall.wireless.vaf.framework;

import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import com.tmall.wireless.vaf.virtualview.ViewFactory;
import com.tmall.wireless.vaf.virtualview.core.Layout;
import com.tmall.wireless.vaf.virtualview.core.ViewBase;
import com.tmall.wireless.vaf.virtualview.core.ViewCache;
import com.tmall.wireless.vaf.virtualview.view.image.VirtualImage;

/**
 * Created by gujicheng on 16/9/22.
 */
public class ViewManager {
    private final static String TAG = "ViewManager_TMTEST";

    private ViewFactory mViewFactory = new ViewFactory();

    private ArrayMap<String, List<ViewBase>> mViewCache = new ArrayMap<>();
    private SparseArray<ViewBase> mUuidContainers = new SparseArray<>();

    private VafContext mAppContext;

    public void setPageContext(VafContext context) {
        mAppContext = context;
        mViewFactory.setPageContext(context);
    }

    public ViewFactory getViewFactory() {
        return mViewFactory;
    }

    public boolean init(Context context) {
        return mViewFactory.init(context);
    }

    public int loadBinFileSync(String path) {
        return mViewFactory.loadBinFile(path);
    }

    public int loadBinBufferSync(byte[] buffer) {
        return mViewFactory.loadBinBuffer(buffer);
    }

    public ViewBase getViewFromUuid(int uuid) {
        return mUuidContainers.get(uuid);
    }

    public void destroy() {
        for (int i = 0; i < mViewCache.size(); ++i) {
            List<ViewBase> viewBases = mViewCache.valueAt(i);
            if (null != viewBases) {
                for ( int j = 0; j < viewBases.size(); ++j) {
                    ViewBase viewBase = viewBases.get(j);
                    viewBase.destroy();
                    ViewCache viewCache = viewBase.getViewCache();
                    if (viewCache != null) {
                        viewCache.destroy();
                    }
                }
                viewBases.clear();
            }
        }
        mViewCache.clear();
        mViewCache = null;
        mViewFactory.destroy();
        mUuidContainers.clear();
        mUuidContainers = null;
    }

    public ViewBase getDefaultImage() {
        ViewBase vb = new VirtualImage(mAppContext, new ViewCache());
        vb.setComLayoutParams(new Layout.Params());
        return vb;
    }

    public int getViewVersion(String type) {
        return mViewFactory.getViewVersion(type);
    }

    public ViewBase getView(String type) {
        ViewBase v;
        List<ViewBase> vList = mViewCache.get(type);
        if (null == vList || 0 == vList.size()) {
            v = mViewFactory.newView(type, mUuidContainers);
            if (null != v) {
                if (v.supportDynamic()) {
                    mAppContext.getNativeObjectManager().addView(v);
                }
                v.setViewType(type);
            } else {
                Log.e(TAG, "new view failed type:" + type);
            }
        } else {
            v = vList.remove(0);
        }

        return v;
    }

    public void recycle(ViewBase v) {
        if (null != v) {
            String type = v.getViewType();
            if (!TextUtils.isEmpty(type)) {
                v.reset();
                List<ViewBase> vList = mViewCache.get(type);
                if (null == vList) {
                    vList = new LinkedList<>();
                    mViewCache.put(type, vList);
                }
                vList.add(v);
            } else {
                Log.e(TAG, "recycle type invalidate:" + type);
                RuntimeException here = new RuntimeException("here");
                here.fillInStackTrace();
                Log.w(TAG, "Called: " + this, here);
            }
        }
    }
}
