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

package com.tmall.wireless.virtualviewdemo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import com.tmall.wireless.vaf.virtualview.core.IContainer;
import com.tmall.wireless.vaf.virtualview.core.Layout;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by longerian on 2017/7/24.
 *
 * @author longerian
 * @date 2017/07/24
 */

public class ComponentActivity extends Activity {

    private LinearLayout mLinearLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_component);
        mLinearLayout = (LinearLayout)findViewById(R.id.container);
        String name = getIntent().getStringExtra("name");
        String data = getIntent().getStringExtra("data");
        View container = ((VirtualViewApplication) getApplication()).getVafContext().getContainerService().getContainer(name, true);
        IContainer iContainer = (IContainer)container;
        if (!TextUtils.isEmpty(data)) {
            JSONObject json = getJSONDataFromAsset(data);
            if (json != null) {
                iContainer.getVirtualView().setVData(json);
            }
        }
        Layout.Params p = iContainer.getVirtualView().getComLayoutParams();
        LinearLayout.LayoutParams marginLayoutParams = new LinearLayout.LayoutParams(p.mLayoutWidth, p.mLayoutHeight);
        marginLayoutParams.leftMargin = p.mLayoutMarginLeft;
        marginLayoutParams.topMargin = p.mLayoutMarginTop;
        marginLayoutParams.rightMargin = p.mLayoutMarginRight;
        marginLayoutParams.bottomMargin = p.mLayoutMarginBottom;
        mLinearLayout.addView(container, marginLayoutParams);

    }

    private JSONObject getJSONDataFromAsset(String name) {
        try {
            InputStream inputStream = getAssets().open(name);
            BufferedReader inputStreamReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder sb = new StringBuilder();
            String str;
            while ((str = inputStreamReader.readLine()) != null) {
                sb.append(str);
            }
            inputStreamReader.close();
            return new JSONObject(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

}
