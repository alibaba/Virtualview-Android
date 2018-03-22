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

import android.util.Log;

import com.libra.expr.common.ExprCode;

public class CodeReader {
    private final static String TAG = "CodeReader";

    private ExprCode mCode;
    private int mCurIndex;
    private int mStartPos;
//    private int mCount;

    public void setCode(ExprCode code) {
        mCode = code;

        mStartPos = mCode.mStartPos;
        mCurIndex = mStartPos;
    }

    public void release() {
//        Log.d(TAG, "release");

        if (null != mCode) {
//            mCode.clear();
            mCode = null;
        }
    }

    public int curPos() {
        return mCurIndex - mStartPos;
    }

    public void setPos(int pos) {
        mCurIndex = mStartPos + pos;
    }

    public boolean isEndOfCode() {
        return (mCurIndex == mCode.mEndPos) ? true : false;
    }

    public byte readByte() {
        byte ret = 0;

        if (null != mCode && mCurIndex < mCode.mEndPos) {
            ret = mCode.mCodeBase[mCurIndex++];
        } else {
            Log.e(TAG, "readByte error mCode:" + mCode + "  mCurIndex:"
                    + mCurIndex);
        }

        return ret;
    }

    public short readShort() {
        short ret = 0;

        if (null != mCode && mCurIndex < mCode.mEndPos - 1) {
            ret = (short) (mCode.mCodeBase[mCurIndex++] & 0xff);
            ret = (short) ((((short) mCode.mCodeBase[mCurIndex++]) << 8) | ret);
        } else {
            Log.e(TAG, "readShort error mCode:" + mCode + "  mCurIndex:"
                    + mCurIndex);
        }

        return ret;
    }

    public int readInt() {
        int ret = 0;

        if (null != mCode && mCurIndex < mCode.mEndPos - 3) {
            int move = 0;
            int step = 8;
            for (int i = 0; i < 4; ++i) {
                int value = ((int) (mCode.mCodeBase[mCurIndex++] & 0xff)) << move;
                ret |= value;

                move += step;
            }
        } else {
            Log.e(TAG, "readInt error mCode:" + mCode + "  mCurIndex:"
                    + mCurIndex);
        }

        return ret;
    }
}
