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

package com.tmall.wireless.vaf.expr.engine.executor;

import android.text.TextUtils;
import android.util.Log;

import com.tmall.wireless.vaf.expr.engine.data.Data;

/**
 * Created by gujicheng on 16/9/14.
 */
public class TerExecutor extends ArithExecutor {
    private final static String TAG = "TerExecutor_TMTEST";

    @Override
    public int execute(Object com) {
        int ret = super.execute(com);

        short funCode = mCodeReader.readShort();
        int type1 = funCode & 0x7;
        int type2 = (funCode >> 3) & 0x7;
        int type3 = (funCode >> 6) & 0x7;

        Data data1 = readData(type1);
        Data data2 = readData(type2);
        Data data3 = readData(type3);

//        Log.d(TAG, "data1:" + data1 + " + data2:" + data2 + " + data3:" + data3);

        if (TYPE_Register != type1 && TYPE_Register != type2 && TYPE_Register != type3) {
            mAriResultRegIndex = mCodeReader.readByte();
        }

        Data result = mRegisterManger.get(mAriResultRegIndex);

        if (null != result) {
            ret = RESULT_STATE_SUCCESSFUL;

            switch (data1.mType) {
                case Data.TYPE_INT:
                    if (0 != data1.getInt()) {
                        result.copy(data2);
                    } else {
                        result.copy(data3);
                    }
                    break;

                case Data.TYPE_FLOAT:
                    float v = data1.getFloat();
                    if (v > 0.0000001 || v < -0.0000001) {
                        result.copy(data2);
                    } else {
                        result.copy(data3);
                    }
                    break;

                case Data.TYPE_STR:
                    if (!TextUtils.isEmpty(data1.getString())) {
                        result.copy(data2);
                    } else {
                        result.copy(data3);
                    }
                    break;

                default:
                    Log.e(TAG, "type error:" + type1);
                    ret = RESULT_STATE_ERROR;
                    break;
            }
        }

        return ret;
    }

}
