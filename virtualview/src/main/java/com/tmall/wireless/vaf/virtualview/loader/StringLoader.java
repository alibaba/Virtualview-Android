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

package com.tmall.wireless.vaf.virtualview.loader;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import android.util.Log;
import com.libra.TextUtils;
import com.libra.expr.common.StringSupport;
import com.libra.virtualview.common.StringBase;

/**
 * Created by gujicheng on 16/8/3.
 */
public class StringLoader extends StringBase implements StringSupport {
    final private static String TAG = "StringLoader_TMTEST";

    private Map<String, Integer> mString2Index = new ConcurrentHashMap<>();

    private Map<Integer, String> mIndex2String = new ConcurrentHashMap<>();

    private Map<String, Integer> mSysString2Index = new ConcurrentHashMap<>();

    private Map<Integer, String> mSysIndex2String = new ConcurrentHashMap<>();

    private int mCurPage;

    public StringLoader() {
        for (int i = 0; i < StringBase.STR_ID_SYS_KEY_COUNT; ++i) {
            mSysString2Index.put(SYS_KEYS[i], StringBase.SYS_KEYS_INDEX[i]);
            mSysIndex2String.put(StringBase.SYS_KEYS_INDEX[i], SYS_KEYS[i]);
        }

    }

    public void destroy() {
        mString2Index.clear();
        mIndex2String.clear();
        mSysString2Index.clear();
        mSysIndex2String.clear();
    }

    public void reset() {
    }

    public void setCurPage(int pageId) {
        mCurPage = pageId;
    }

    public void remove(int pageId) {
        //do nothing
    }

    public boolean loadFromBuffer(CodeReader reader, int pageId) {
        boolean ret = true;

        mCurPage = pageId;

        int totalSize = reader.getMaxSize();
        int count = reader.readInt();
        for (int i = 0; i < count; ++i) {
            int id = reader.readInt();
            int len = reader.readShort();
            int pos = reader.getPos();
            if (pos + len <= totalSize) {
                String str = new String(reader.getCode(), reader.getPos(), len);
                mIndex2String.put(id, str);
                mString2Index.put(str, id);
                reader.seekBy(len);
            } else {
                Log.e(TAG, "read string over");
                ret = false;
                break;
            }
        }

        return ret;
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
        if (!TextUtils.isEmpty(str)) {
            if (mSysString2Index.containsKey(str)) {
                ret = mSysString2Index.get(str);
            }
            if (0 == ret) {
                if (mString2Index.containsKey(str)) {
                    ret = mString2Index.get(str);
                }
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
