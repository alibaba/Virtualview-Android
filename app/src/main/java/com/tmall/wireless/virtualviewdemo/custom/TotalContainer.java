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

package com.tmall.wireless.virtualviewdemo.custom;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import com.libra.Utils;
import com.libra.expr.common.StringSupport;
import com.tmall.wireless.vaf.framework.VafContext;
import com.tmall.wireless.vaf.virtualview.core.NativeViewBase;
import com.tmall.wireless.vaf.virtualview.core.ViewBase;
import com.tmall.wireless.vaf.virtualview.core.ViewCache;

/**
 * Created by longerian on 2017/7/12.
 *
 * @author longerian
 * @date 2017/07/12
 */

public class TotalContainer extends NativeViewBase {

    private static final String TAG = "TOTAL_CONTAINER_TMTEST";

    static final Class<?>[] mConstructorSignature = new Class[] {
        Context.class, AttributeSet.class};

    private static final HashMap<String, Constructor<? extends View>> sConstructorMap =
        new HashMap<String, Constructor<? extends View>>();

    final Object[] mConstructorArgs = new Object[2];

    private IViewInterface mNative;

    private int mNativeId;

    private String mNativeViewName;

    public TotalContainer(VafContext context,
        ViewCache viewCache) {
        super(context, viewCache);
        StringSupport mStringSupport = context.getStringLoader();
        mNativeId = mStringSupport.getStringId("native", false);
    }

    @Override
    public void reset() {
        super.reset();
        if (mNative != null) {
            mNative.onUnbind();
        }
    }

    @Override
    protected boolean setAttribute(int key, String stringValue) {
        boolean ret = true;
        if (key == mNativeId) {
            if (Utils.isEL(stringValue)) {
                mViewCache.put(this, key, stringValue, ViewCache.Item.TYPE_STRING);
            } else {
                mNativeViewName = stringValue;
                parseView();
            }
        }else {
            ret = super.setAttribute(key, stringValue);
        }
        return ret;
    }

    @Override
    public void onParseValueFinished() {
        super.onParseValueFinished();
        if (mNative != null) {
            mNative.onBind(mJSONData);
        }
    }

    private void parseView() {
        try {
            Constructor<? extends View> constructor = sConstructorMap.get(mNativeViewName);
            if (constructor == null) {
                Class<?> clazz = mContext.getCompactNativeManager().getNativeViewFor(mNativeViewName);
                constructor = (Constructor<? extends View>)clazz.getConstructor(mConstructorSignature);
                constructor.setAccessible(true);
                sConstructorMap.put(mNativeViewName, constructor);
            }
            if (constructor != null) {
                mConstructorArgs[0] = mContext.getContext();
                mConstructorArgs[1] = null;
                final Object obj = constructor.newInstance(mConstructorArgs);
                if (obj instanceof IViewInterface) {
                    mNative = (IViewInterface) obj;
                    __mNative = (View)mNative;
                } else {
                    Log.e(TAG, mClass + " is not total view interface");
                }
            }
        } catch (InstantiationException e) {
            Log.e(TAG, "error:" + e);
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            Log.e(TAG, "error:" + e);
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            Log.e(TAG, "error:" + e);
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            Log.e(TAG, "error:" + e);
            e.printStackTrace();
        }
    }

    public static class Builder implements IBuilder {
        @Override
        public ViewBase build(VafContext context, ViewCache viewCache) {
            return new TotalContainer(context, viewCache);
        }
    }
}
