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

package com.tmall.wireless.vaf.expr.engine;

import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.util.Log;

import com.libra.expr.common.StringSupport;
import com.tmall.wireless.vaf.expr.engine.data.Data;
import com.tmall.wireless.vaf.virtualview.core.ViewBase;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by gujicheng on 16/9/12.
 */
public class NativeObjectManager {
    private final static String TAG = "NObjManager_TMTEST";

    private List<ViewBase> mViews = new ArrayList<>();
    private Map<String, Object> mNativeObjects = new ArrayMap<>();
    private StringSupport mStringLoader;

    public void setStringManager(StringSupport sm) {
        mStringLoader = sm;
    }

    public void destroy() {
        reset();

        mNativeObjects = null;
        mStringLoader = null;
    }

    public void reset() {
//        Log.d(TAG, "reset");
        mViews.clear();
        mNativeObjects.clear();
    }

    public void addView(ViewBase v) {
        if (null != v) {
            mViews.add(v);
        }
    }

    public boolean registerObject(String name, Object obj) {
        if (!TextUtils.isEmpty(name) && null != obj) {
            mNativeObjects.put(name, obj);

//            Log.d(TAG, "registerObject name:" + name);
            return true;
        } else {
            Log.e(TAG, "registerObject param invalidate");
        }

        return false;
    }

    public boolean unregisterObject(String name) {
        if (!TextUtils.isEmpty(name)) {
//            Log.d(TAG, "unregisterObject name:" + name);
            mNativeObjects.remove(name);
            return true;
        }
        return false;
    }

    public void removeView(ViewBase v) {
        if (null != v) {
            mViews.remove(v);
        }
    }

    public Object getPropertyImp(Object obj, int propertyId) {
        Object ret = null;

        if (null != obj && propertyId != 0) {
            try {
                String property = mStringLoader.getString(propertyId);
                String prop = String.format("get%c%s", Character.toUpperCase(property.charAt(0)), property.substring(1).toString());
//                Log.d(TAG, "getPropertyImp prop:" + prop);
                Method method = obj.getClass().getMethod(prop);
                if (null != method) {
                    ret = method.invoke(obj);
                }
            } catch (InvocationTargetException e) {
//                e.printStackTrace();
//                Log.e(TAG, "getProperty failed:" + e);
            } catch (IllegalAccessException e) {
//                e.printStackTrace();
//                Log.e(TAG, "getProperty failed:" + e);
            } catch (NoSuchMethodException e) {
//                e.printStackTrace();
//                Log.e(TAG, "getProperty failed:" + e);
            }

            if (null == ret && obj instanceof ViewBase) {
                ret = ((ViewBase)obj).getUserVar(propertyId);
            }
        } else {
            Log.e(TAG, "getProperty param invalidate");
        }

        return ret;
    }

    public boolean setPropertyImp(Object obj, int propertyNameId, Data value) {
        boolean ret = false;

        if (null != obj && propertyNameId != 0 && null != value) {
            try {
                String propertyName = mStringLoader.getString(propertyNameId);
                String prop = String.format("set%c%s", Character.toUpperCase(propertyName.charAt(0)), propertyName.substring(1).toString());
                Method method = null;
//                Log.d(TAG, "setPropertyImp prop:" + prop + "  obj:" + obj);
                method = obj.getClass().getMethod(prop, value.mValue.getValueClass());
                if (null != method) {
                    method.invoke(obj, value.mValue.getValue());
                    ret = true;
                } else {
                    Log.e(TAG, "view:" + obj.getClass() + "  setIntegerPropertyImp find method failed:" + prop);
                }
            } catch (InvocationTargetException e) {
//                e.printStackTrace();
                Log.e(TAG, "view:" + obj.getClass() + "  setIntegerPropertyImp failed:" + e);
            } catch (IllegalAccessException e) {
//                e.printStackTrace();
                Log.e(TAG, "view:" + obj.getClass() + "  setIntegerPropertyImp failed:" + e);
            } catch (NoSuchMethodException e) {
//                e.printStackTrace();
                Log.e(TAG, "view:" + obj.getClass() + "  setIntegerPropertyImp failed:" + e);
            }

            if (!ret && obj instanceof ViewBase) {
                ViewBase vb = (ViewBase)obj;
                ret = vb.setUserVar(propertyNameId, value);
            }
        } else {
            Log.e(TAG, "setIntegerPropertyImp param invalidate");
        }

        return ret;
    }

    public Object getModule(String key) {
        return mNativeObjects.get(key);
    }

    public ViewBase findCom(String name) {
        ViewBase ret = null;

        if (!TextUtils.isEmpty(name)) {
            for (int i = 0, length = mViews.size(); i < length; i++) {
                ViewBase com = mViews.get(i);
                if (TextUtils.equals(com.getName(), name)) {
                    ret = com;
                    break;
                }
            }
        }

        return ret;
    }

    public ViewBase findCom(int name) {
        return findCom(mStringLoader.getString(name));
    }
}
