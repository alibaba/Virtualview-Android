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

import android.app.ListActivity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.tmall.wireless.virtualviewdemo.preview.IPDialog;
import com.tmall.wireless.virtualviewdemo.preview.PreviewListActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by longerian on 2017/7/24.
 *
 * @author longerian
 * @date 2017/07/24
 */

public class DemoListActivity extends ListActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(android.R.layout.list_content);
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        //HashMap<String, String> main = new HashMap<String, String>();
        //main.put("name", "Main(TODO)");
        //main.put("class", MainActivity.class.getName());
        //list.add(main);
        HashMap<String, String> parse = new HashMap<String, String>();
        parse.put("name", "基础流程演示");
        parse.put("class", ParserDemoActivity.class.getName());
        list.add(parse);
        HashMap<String, String> api = new HashMap<String, String>();
        api.put("name", "内置控件演示");
        api.put("class", ComponentListActivity.class.getName());
        list.add(api);
        HashMap<String, String> bizItems = new HashMap<String, String>();
        bizItems.put("name", "业务场景演示");
        bizItems.put("class", TmallComponentListActivity.class.getName());
        list.add(bizItems);
        HashMap<String, String> script = new HashMap<String, String>();
        script.put("name", "脚本引擎演示(实验中)");
        script.put("class", ScriptListActivity.class.getName());
        list.add(script);

        HashMap<String, String> preview = new HashMap<String, String>();
        preview.put("name", "模板实时预览");
        preview.put("class", PreviewListActivity.class.getName());
        list.add(preview);

        ListAdapter listAdapter = new SimpleAdapter(this, list, android.R.layout.simple_list_item_1, new String[]{"name"}, new int[]{android.R.id.text1});
        setListAdapter(listAdapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        if (position < 4) {
            startActivity(l, position);
        } else {
            new IPDialog(this).setOnOKClickListener(view -> startActivity(l, position)).show();
        }
    }

    private void startActivity(ListView l, int position) {
        Map<String, String> item = (Map<String, String>) l.getItemAtPosition(position);
        String className = item.get("class");
        if (className != null) {
            Intent intent = new Intent();
            intent.setComponent(new ComponentName(this, className));
            startActivity(intent);
        }
    }
}
