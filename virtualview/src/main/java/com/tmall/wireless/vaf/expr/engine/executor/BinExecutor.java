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

import android.util.Log;

import com.libra.virtualview.common.ExprCommon;
import com.tmall.wireless.vaf.expr.engine.data.Data;

/**
 * Created by gujicheng on 16/9/12.
 */
public abstract class BinExecutor extends ArithExecutor {
    private final static String TAG = "BinExecutor_TMTEST";

    @Override
    public int execute(Object com) {
        int ret = super.execute(com);

        byte funCode = mCodeReader.readByte();
//        Log.d(TAG, "execute funCode:" + funCode);
        Data data1 = null, data2 = null;
        switch (funCode) {
            case ExprCommon.VAR_VAR:
                data1 = readData(TYPE_Var);
                data2 = readData(TYPE_Var);
                mAriResultRegIndex = mCodeReader.readByte();
                break;
            case ExprCommon.VAR_INT:
                data1 = readData(TYPE_Var);
                data2 = readData(TYPE_Int);
                mAriResultRegIndex = mCodeReader.readByte();
                break;
            case ExprCommon.VAR_FLT:
                data1 = readData(TYPE_Var);
                data2 = readData(TYPE_Float);
                mAriResultRegIndex = mCodeReader.readByte();
                break;
            case ExprCommon.VAR_STR:
                data1 = readData(TYPE_Var);
                data2 = readData(TYPE_String);
                mAriResultRegIndex = mCodeReader.readByte();
                break;
            case ExprCommon.VAR_REG:
                data1 = readData(TYPE_Var);
                data2 = readData(TYPE_Register);
                break;

            case ExprCommon.INT_VAR:
                data1 = readData(TYPE_Int);
                data2 = readData(TYPE_Var);
                mAriResultRegIndex = mCodeReader.readByte();
                break;
            case ExprCommon.INT_REG:
                data1 = readData(TYPE_Int);
                data2 = readData(TYPE_Register);
                break;

            case ExprCommon.FLT_VAR:
                data1 = readData(TYPE_Float);
                data2 = readData(TYPE_Var);
                mAriResultRegIndex = mCodeReader.readByte();
                break;
            case ExprCommon.FLT_REG:
                data1 = readData(TYPE_Float);
                data2 = readData(TYPE_Register);
                break;

            case ExprCommon.STR_VAR:
                data1 = readData(TYPE_String);
//                Log.d(TAG, "data1:" + data1);
                data2 = readData(TYPE_Var);
                mAriResultRegIndex = mCodeReader.readByte();
                break;
            case ExprCommon.STR_REG:
                data1 = readData(TYPE_String);
                data2 = readData(TYPE_Register);
                break;

            case ExprCommon.REG_VAR:
                data1 = readData(TYPE_Register);
                data2 = readData(TYPE_Var);
                break;
            case ExprCommon.REG_INT:
                data1 = readData(TYPE_Register);
                data2 = readData(TYPE_Int);
                break;
            case ExprCommon.REG_FLT:
                data1 = readData(TYPE_Register);
                data2 = readData(TYPE_Float);
                break;
            case ExprCommon.REG_STR:
                data1 = readData(TYPE_Register);
                data2 = readData(TYPE_String);
                break;
            case ExprCommon.REG_REG:
                data1 = readData(TYPE_Register);
                data2 = readData(TYPE_Register);
                break;
        }

        if (null != data1 && null != data2) {
            ret = calc(data1, data2);
        } else {
            Log.e(TAG, "read data failed");
        }

        return ret;
    }

    protected int calcIntInt(Data result, int l, int r) {
        return RESULT_STATE_ERROR;
    }

    protected int calcIntFloat(Data result, int l, float r) {
        return RESULT_STATE_ERROR;
    }

    protected int calcIntString(Data result, int l, String r) {
        return RESULT_STATE_ERROR;
    }

    protected int calcFloatInt(Data result, float l, int r) {
        return RESULT_STATE_ERROR;
    }

    protected int calcFloatFloat(Data result, float l, float r) {
        return RESULT_STATE_ERROR;
    }

    protected int calcFloatString(Data result, float l, String r) {
        return RESULT_STATE_ERROR;
    }

    protected int calcStringInt(Data result, String l, int r) {
        return RESULT_STATE_ERROR;
    }

    protected int calcStringFloat(Data result, String l, float r) {
        return RESULT_STATE_ERROR;
    }

    protected int calcStringString(Data result, String l, String r) {
        return RESULT_STATE_ERROR;
    }

    protected int calc(Data value1, Data value2) {
        int ret = RESULT_STATE_ERROR;

        Data result = mRegisterManger.get(mAriResultRegIndex);

        if (null != result) {
            ret = RESULT_STATE_SUCCESSFUL;

            switch (value1.mType) {
                case Data.TYPE_INT:
                    switch (value2.mType) {
                        case Data.TYPE_INT:
                            ret = calcIntInt(result, value1.getInt(), value2.getInt());
                            break;
                        case Data.TYPE_FLOAT:
                            ret = calcIntFloat(result, value1.getInt(), value2.getFloat());
                            break;
                        case Data.TYPE_STR:
                            ret = calcIntString(result, value1.getInt(), value2.getString());
                            break;
                        default:
                            Log.e(TAG, "value2 invalidate type:" + value2);
                            break;
                    }
                    break;

                case Data.TYPE_FLOAT:
                    switch (value2.mType) {
                        case Data.TYPE_INT:
                            ret = calcFloatInt(result, value1.getFloat(), value2.getInt());
                            break;
                        case Data.TYPE_FLOAT:
                            ret = calcFloatFloat(result, value1.getFloat(), value2.getFloat());
                            break;
                        case Data.TYPE_STR:
                            ret = calcFloatString(result, value1.getFloat(), value2.getString());
                            break;
                        default:
                            Log.e(TAG, "value2 invalidate type:" + value2);
                            break;
                    }
                    break;

                case Data.TYPE_STR:
                    switch (value2.mType) {
                        case Data.TYPE_INT:
                            ret = calcStringInt(result, value1.getString(), value2.getInt());
                            break;
                        case Data.TYPE_FLOAT:
                            ret = calcStringFloat(result, value1.getString(), value2.getFloat());
                            break;
                        case Data.TYPE_STR:
                            ret = calcStringString(result, value1.getString(), value2.getString());
                            break;
                        default:
                            Log.e(TAG, "value2 invalidate type:" + value2);
                            break;
                    }
                    break;

                default:
                    Log.e(TAG, "value1 invalidate type:" + value1);
                    break;
            }

            if (RESULT_STATE_ERROR == ret) {
                Log.e(TAG, "type invalidate data1:" + value1 + "  data2:" + value2);
            }
        }

        return ret;
    }

}
