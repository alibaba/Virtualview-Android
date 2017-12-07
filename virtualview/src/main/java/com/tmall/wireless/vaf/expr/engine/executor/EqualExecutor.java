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

import java.util.HashSet;
import java.util.Set;

/**
 * Created by gujicheng on 16/9/12.
 */
public class EqualExecutor extends ArithExecutor {
    private final static String TAG = "EqualExecutor_TMTEST";

    protected Set<Object> mMyObjs = new HashSet<>();
    protected int mPropertyNameId;

    @Override
    public void init() {
        super.init();
    }

    @Override
    public int execute(Object com) {
        int ret = super.execute(com);

//        Log.d(TAG, "execute");

        byte funCode = mCodeReader.readByte();
        Data data2 = null;
//        int type = TYPE_None;

//        Log.d(TAG, "execute funCode:" + funCode);
        switch (funCode) {
            case ExprCommon.VAR_VAR:
                loadVar();
                data2 = readData(TYPE_Var);
                mAriResultRegIndex = mCodeReader.readByte();
//                type = TYPE_Var;
                break;
            case ExprCommon.VAR_INT:
                loadVar();
                data2 = readData(TYPE_Int);
                mAriResultRegIndex = mCodeReader.readByte();
//                type = TYPE_Int;
                break;
            case ExprCommon.VAR_FLT:
                loadVar();
                data2 = readData(TYPE_Float);
                mAriResultRegIndex = mCodeReader.readByte();
//                type = TYPE_Float;
                break;
            case ExprCommon.VAR_STR:
                loadVar();
                data2 = readData(TYPE_String);
                mAriResultRegIndex = mCodeReader.readByte();
//                type = TYPE_String;
                break;
            case ExprCommon.VAR_REG:
                loadVar();
                data2 = readData(TYPE_Register);
//                type = TYPE_Register;
                break;
        }

//        Log.d(TAG, "do dow");
        if (null != data2) {
//            data2 = readData(type);

//            Log.d(TAG, "result register id:" + mAriResultRegIndex);
            Data resultReg = mRegisterManger.get(mAriResultRegIndex);
            if (null != resultReg) {
                ret = eqDeal(resultReg, data2);
            } else {
                Log.e(TAG, "result register is null");
            }
        }

        return ret;
    }

    protected int eqDeal(Data resultReg, Data data2) {
        int ret = RESULT_STATE_ERROR;

        resultReg.copy(data2);

//        Log.d(TAG, "eqDeal result:" + resultReg + "  data2:" + data2);
//        NativeObjectManager cm = mAppContext.getNativeObjectManager();
        if (mMyObjs.size() > 0) {
            for (Object obj : mMyObjs) {
                mNativeObjectManager.setPropertyImp(obj, mPropertyNameId, data2);
            }
            ret = RESULT_STATE_SUCCESSFUL;
        } else {
            Log.e(TAG, "obj is empty");
        }

        return ret;
    }

    private boolean loadVar() {
        boolean ret = true;

        Set<Object> objs = findObject();
        if (null != objs) {
            ret = true;
            mMyObjs.clear();
            mMyObjs.addAll(objs);
            mPropertyNameId = mCodeReader.readInt();
        } else{
            Log.e(TAG, "load var failed");
        }

        return ret;
    }
}
