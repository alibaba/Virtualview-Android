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

package com.tmall.wireless.vaf.expr.engine.executor;

import android.text.TextUtils;
import android.util.Log;

import com.tmall.wireless.vaf.expr.engine.data.Data;
import com.tmall.wireless.vaf.virtualview.core.ViewBase;

/**
 * Created by gujicheng on 16/12/13.
 */

public class JmpcExecutor extends ArithExecutor {
    private final static String TAG = "JmpcExecutor_TMTEST";

    public int execute(Object com) {
        super.execute(com);

        int jmpPos = mCodeReader.readInt();

        int type = mCodeReader.readByte();
        Data d = readData(type);

        int ret = RESULT_STATE_SUCCESSFUL;
        switch (d.mType) {
            case Data.TYPE_INT:
                if (d.getInt() <= 0 ) {
                    // jmp
//                    Log.d(TAG, "jmpc int pos:" + jmpPos);
                    mCodeReader.setPos(jmpPos);
                }
                break;

            case Data.TYPE_FLOAT:
                if (d.getFloat() <= 0 ) {
                    // jmp
//                    Log.d(TAG, "jmpc float pos:" + jmpPos);
                    mCodeReader.setPos(jmpPos);
                }
                break;

            case Data.TYPE_STR:
                if (TextUtils.isEmpty(d.getString())) {
                    // jmp
//                    Log.d(TAG, "jmpc str pos:" + jmpPos);
                    mCodeReader.setPos(jmpPos);
                }
                break;

            case Data.TYPE_OBJECT:
                if (null == d.getObject()) {
                    // jmp
//                    Log.d(TAG, "jmpc obj pos:" + jmpPos);
                    mCodeReader.setPos(jmpPos);
                }
                break;

            default:
                Log.e(TAG, "type invalidate:" + d);
                ret = RESULT_STATE_ERROR;
                break;
        }

        return ret;
    }
}
