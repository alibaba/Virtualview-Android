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

import com.libra.expr.common.StringSupport;
import com.tmall.wireless.vaf.expr.engine.CodeReader;
import com.tmall.wireless.vaf.expr.engine.DataManager;
import com.tmall.wireless.vaf.expr.engine.EngineContext;
import com.tmall.wireless.vaf.expr.engine.NativeObjectManager;
import com.tmall.wireless.vaf.expr.engine.RegisterManager;

/**
 * Created by gujicheng on 16/9/10.
 */
public abstract class Executor {
    private static final String TAG = "Executor_TMTEST";

    public static final int RESULT_STATE_SUCCESSFUL = 1;
    public static final int RESULT_STATE_ERROR = 2;

    protected Object mCom;

    protected EngineContext mEngineContext;

    protected StringSupport mStringSupport;
    protected NativeObjectManager mNativeObjectManager;
    protected CodeReader mCodeReader;
    protected RegisterManager mRegisterManger;
    protected DataManager mDataManager;

//    public void setPageContext(VafContext context) {
////        mAppContext = context;
//    }

//    public void setStringSupport(StringSupport ss) {
//        mStringSupport = ss;
//    }

    public void setEngineContext(EngineContext context) {
        mEngineContext = context;

        mStringSupport = mEngineContext.getStringSupport();
        mNativeObjectManager = mEngineContext.getNativeObjectManager();
        mCodeReader = mEngineContext.getCodeReader();
        mRegisterManger = mEngineContext.getRegisterManager();
        mDataManager = mEngineContext.getDataManager();
    }

//    public void setData(DataManager dataManager) {
//        mDataManager = dataManager;
//    }
//
//    public void setNativeObjectManager(NativeObjectManager nom) {
//        mNativeObjectManager = nom;
//    }
//
//    public void setRegisterManger(RegisterManager rm) {
//        mRegisterManger = rm;
//    }
//
//    public void setCodeReader(CodeReader reader) {
////        Log.d(TAG, "reader" + reader);
//        mCodeReader = reader;
//    }

    public void init() {
    }

    public void destroy() {
//        mAppContext = null;
//        mRegisterManger = null;
        mCom = null;
//        mCodeReader = null;
    }

    public int execute(Object com) {
        mCom = com;

        return RESULT_STATE_ERROR;
    }
}
