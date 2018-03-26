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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ListActivity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

/**
 * Created by longerian on 2017/7/24.
 *
 * @author longerian
 * @date 2017/07/24
 */

public class ScrollerListActivity extends ListActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(android.R.layout.list_content);
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        HashMap<String, String> scroll0 = new HashMap<String, String>();
        scroll0.put("name", "ScrollerVL");
        scroll0.put("desp", "orientation = V | mode = linear");
        scroll0.put("class", ComponentActivity.class.getName());
        scroll0.put("data", "component_demo/slider_item.json");
        list.add(scroll0);
        HashMap<String, String> scroll1 = new HashMap<String, String>();
        scroll1.put("name", "ScrollerVS");
        scroll1.put("desp", "orientation = V | mode = staggered");
        scroll1.put("class", ComponentActivity.class.getName());
        scroll1.put("data", "component_demo/slider_item.json");
        list.add(scroll1);
        HashMap<String, String> scroll2 = new HashMap<String, String>();
        scroll2.put("name", "ScrollerH");
        scroll2.put("desp", "orientation = H");
        scroll2.put("class", ComponentActivity.class.getName());
        scroll2.put("data", "component_demo/slider_item.json");
        list.add(scroll2);
        ListAdapter listAdapter = new SimpleAdapter(this, list, android.R.layout.simple_list_item_2,
            new String[] {"name", "desp"}, new int[] {android.R.id.text1, android.R.id.text2});
        setListAdapter(listAdapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Map<String, String> item = (Map<String, String>)l.getItemAtPosition(position);
        String className = item.get("class");
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(this, className));
        intent.putExtra("name", item.get("name"));
        intent.putExtra("data", item.get("data"));
        startActivity(intent);
    }

}
