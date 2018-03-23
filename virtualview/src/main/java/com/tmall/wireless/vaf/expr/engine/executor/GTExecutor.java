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

import com.tmall.wireless.vaf.expr.engine.data.Data;

/**
 * Created by gujicheng on 16/9/18.
 */
public class GTExecutor extends BinExecutor {
    @Override
    protected int calcIntInt(Data result, int l, int r) {
        result.setInt((l > r) ? 1 : 0);
        return RESULT_STATE_SUCCESSFUL;
    }

    @Override
    protected int calcIntFloat(Data result, int l, float r) {
        result.setInt((l > r) ? 1 : 0);
        return RESULT_STATE_SUCCESSFUL;
    }

    @Override
    protected int calcFloatInt(Data result, float l, int r) {
        result.setInt((l > r) ? 1 : 0);
        return RESULT_STATE_SUCCESSFUL;
    }

    @Override
    protected int calcFloatFloat(Data result, float l, float r) {
        result.setInt((l > r) ? 1 : 0);
        return RESULT_STATE_SUCCESSFUL;
    }
}
