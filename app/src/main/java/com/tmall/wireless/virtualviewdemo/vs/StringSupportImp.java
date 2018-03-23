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

package com.tmall.wireless.virtualviewdemo.vs;

import android.support.v4.util.ArrayMap;
import android.text.TextUtils;

import android.util.Log;
import com.libra.expr.common.StringSupport;
import com.libra.virtualview.common.StringBase;
import com.tmall.wireless.vaf.virtualview.loader.CodeReader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by gujicheng on 17/3/27.
 */

public class StringSupportImp extends StringBase implements StringSupport {

    final private static String TAG = "StringSupportImp_TMTEST";

    private Map<String, Integer> mString2Index = new ArrayMap<>();

    private Map<Integer, String> mIndex2String = new ArrayMap<>();

    private Map<String, Integer> mSysString2Index = new ArrayMap<>();

    private Map<Integer, String> mSysIndex2String = new ArrayMap<>();

    public StringSupportImp() {
        for (int i = 0; i < StringBase.STR_ID_SYS_KEY_COUNT; ++i) {
            mSysString2Index.put(SYS_KEYS[i], SYS_KEYS[i].hashCode());
            mSysIndex2String.put(SYS_KEYS[i].hashCode(), SYS_KEYS[i]);
        }
    }

    public void destroy() {
        mString2Index.clear();
        mIndex2String.clear();
        mSysString2Index.clear();
        mSysIndex2String.clear();
    }

    @Override
    public String getString(int id) {
        if (mSysIndex2String.containsKey(id)) {
            return mSysIndex2String.get(id);
        }
        if (mIndex2String.containsKey(id)) {
            return mIndex2String.get(id);
        }
        Log.e(TAG, "getString null:" + id);
        return null;
    }

    @Override
    public int getStringId(String str) {
        return getStringId(str, true);
    }

    @Override
    public int getStringId(String str, boolean create) {
        int ret = 0;
        if (!com.libra.TextUtils.isEmpty(str)) {
            if (mSysString2Index.containsKey(str)) {
                ret = mSysString2Index.get(str);
            }
            if (0 == ret) {
                if (mString2Index.containsKey(str)) {
                    ret = mString2Index.get(str);
                }
            }
            if (0 == ret && create) {
                ret = str.hashCode();
                mString2Index.put(str, ret);
                mIndex2String.put(ret, str);
            }

        }
        return ret;
    }

    @Override
    public boolean isSysString(int id) {
        return mSysIndex2String.containsKey(id);
    }

    @Override
    public boolean isSysString(String string) {
        return mSysString2Index.containsKey(string);
    }

}
