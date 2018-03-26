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

import com.libra.expr.common.StringSupport;
import com.tmall.wireless.vaf.expr.engine.finder.ObjectFinderManager;

/**
 * Created by gujicheng on 17/3/28.
 */

public class EngineContext {
    private CodeReader mCodeReader = new CodeReader();
    private RegisterManager mRegisterManager = new RegisterManager();
    private DataManager mDataManager = new DataManager();
    private ObjectFinderManager mObjectFinderManager = new ObjectFinderManager();
    private NativeObjectManager mNativeObjectManager;
    private StringSupport mStringSupport;

    public void destroy() {
        mCodeReader = null;

        mRegisterManager.destroy();
        mRegisterManager = null;

        mDataManager = null;
        mNativeObjectManager = null;

        mStringSupport = null;

        mObjectFinderManager = null;
    }

    public ObjectFinderManager getObjectFinderManager() {
        return mObjectFinderManager;
    }

    public StringSupport getStringSupport() {
        return mStringSupport;
    }

    public void setStringSupport(StringSupport ss) {
        mStringSupport = ss;
    }

    public void setNativeObjectManager(NativeObjectManager nom) {
        mNativeObjectManager = nom;
    }

    public CodeReader getCodeReader() {
        return mCodeReader;
    }

    public RegisterManager getRegisterManager() {
        return mRegisterManager;
    }

    public DataManager getDataManager() {
        return mDataManager;
    }

    public NativeObjectManager getNativeObjectManager() {
        return mNativeObjectManager;
    }
}
