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

package com.tmall.wireless.vaf.virtualview.loader;

import android.util.Log;

/**
 * Created by gujicheng on 16/8/4.
 */
public class CodeReader {
    private final static String TAG = "CodeReader_TMTEST";

    private int patchVersion;
    private byte[] mCode;
    private int mCurIndex;
    private int mCount;

    public void setPatchVersion(int patchVersion) {
        this.patchVersion = patchVersion;
    }

    public int getPatchVersion() {
        return patchVersion;
    }

    public void setCode(byte[] code) {
        mCode = code;
        if (null != mCode) {
            mCount = mCode.length;
        } else {
            mCount = 0;
        }

        mCurIndex = 0;
    }

    public boolean seekBy(int pos) {
        return seek(mCurIndex + pos);
    }

    public boolean seek(int pos) {
        boolean ret = false;
        if (pos > mCount) {
            mCurIndex = mCount;
        } else if (pos < 0) {
            mCurIndex = 0;
        } else {
            ret = true;
            mCurIndex = pos;
        }
        return ret;
    }

    public void release() {
        if (null != mCode) {
            mCode = null;
        }
    }

    public byte[] getCode() {
        return mCode;
    }

    public int getPos() {
        return mCurIndex;
    }

    public int getMaxSize() {
        return mCount;
    }

    public boolean isEndOfCode() {
        return (mCurIndex == mCount) ? true : false;
    }

    public byte readByte()  {
        if (null != mCode && mCurIndex < mCount) {
            return mCode[mCurIndex++];
        } else {
            Log.e(TAG, "readByte error mCode:" + mCode + "  mCurIndex:"
                    + mCurIndex + "  mCount:" + mCount);
            return -1;
        }
    }

    public short readShort()  {
        if (null != mCode && mCurIndex < mCount - 1) {
            return (short)(((mCode[mCurIndex++] & 0xff) << 8) | (mCode[mCurIndex++] & 0xff));
        } else {
            Log.e(TAG, "readShort error mCode:" + mCode + "  mCurIndex:"
                    + mCurIndex + "  mCount:" + mCount);
            return -1;
        }
    }

    public int readInt() {
        if (null != mCode && mCurIndex < mCount - 3) {
            return ((mCode[mCurIndex++] & 0xff) << 24) |
                    ((mCode[mCurIndex++] & 0xff) << 16) |
                    ((mCode[mCurIndex++] & 0xff) << 8) |
                    ((mCode[mCurIndex++] & 0xff));
        } else {
            Log.e(TAG, "readInt error mCode:" + mCode + "  mCurIndex:"
                    + mCurIndex + "  mCount:" + mCount);
            return -1;
        }
    }
}
