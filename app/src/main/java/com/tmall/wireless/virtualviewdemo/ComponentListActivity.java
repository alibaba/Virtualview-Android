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

public class ComponentListActivity extends ListActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(android.R.layout.list_content);
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        HashMap<String, String> ntext = new HashMap<String, String>();
        ntext.put("name", "NText");
        ntext.put("data", "component_demo/ntext_style.json");
        ntext.put("class", ComponentActivity.class.getName());
        list.add(ntext);
        HashMap<String, String> vtext = new HashMap<String, String>();
        vtext.put("name", "VText");
        vtext.put("class", ComponentActivity.class.getName());
        list.add(vtext);
        //HashMap<String, String> tmnimage = new HashMap<String, String>();
        //tmnimage.put("name", "TMNImage");
        //tmnimage.put("class", ComponentActivity.class.getName());
        //list.add(tmnimage);
        HashMap<String, String> nimage = new HashMap<String, String>();
        nimage.put("name", "NImage");
        nimage.put("class", ComponentActivity.class.getName());
        list.add(nimage);
        HashMap<String, String> vimage = new HashMap<String, String>();
        vimage.put("name", "VImage");
        vimage.put("class", ComponentActivity.class.getName());
        list.add(vimage);
        HashMap<String, String> nline = new HashMap<String, String>();
        nline.put("name", "NLine");
        nline.put("class", ComponentActivity.class.getName());
        list.add(nline);
        HashMap<String, String> vline = new HashMap<String, String>();
        vline.put("name", "VLine");
        vline.put("class", ComponentActivity.class.getName());
        list.add(vline);
        HashMap<String, String> vgraph = new HashMap<String, String>();
        vgraph.put("name", "VGraph");
        vgraph.put("class", ComponentActivity.class.getName());
        list.add(vgraph);
        HashMap<String, String> progress = new HashMap<String, String>();
        progress.put("name", "Progress");
        progress.put("class", ComponentActivity.class.getName());
        list.add(progress);

        HashMap<String, String> page = new HashMap<String, String>();
        page.put("name", "Page");
        page.put("class", ComponentActivity.class.getName());
        page.put("data", "component_demo/page_item.json");
        list.add(page);
        HashMap<String, String> scroller = new HashMap<String, String>();
        scroller.put("name", "Scroller");
        scroller.put("class", ScrollerListActivity.class.getName());
        list.add(scroller);
        HashMap<String, String> slider = new HashMap<String, String>();
        slider.put("name", "Slider");
        slider.put("class", ComponentActivity.class.getName());
        slider.put("data", "component_demo/slider_item.json");
        list.add(slider);
        //HashMap<String, String> container = new HashMap<String, String>();
        //container.put("name", "Container");
        //container.put("class", ComponentActivity.class.getName());
        //list.add(container);
        HashMap<String, String> framelayout = new HashMap<String, String>();
        framelayout.put("name", "FrameLayout");
        framelayout.put("class", ComponentActivity.class.getName());
        framelayout.put("data", "component_demo/frame_style.json");
        list.add(framelayout);
        HashMap<String, String> ratiolayout = new HashMap<String, String>();
        ratiolayout.put("name", "RatioLayout");
        ratiolayout.put("class", ComponentActivity.class.getName());
        list.add(ratiolayout);
        HashMap<String, String> grid = new HashMap<String, String>();
        grid.put("name", "Grid");
        grid.put("class", ComponentActivity.class.getName());
        grid.put("data", "component_demo/grid_item.json");
        list.add(grid);
        HashMap<String, String> gridlayout = new HashMap<String, String>();
        gridlayout.put("name", "GridLayout");
        gridlayout.put("class", ComponentActivity.class.getName());
        list.add(gridlayout);
        HashMap<String, String> vh2layout = new HashMap<String, String>();
        vh2layout.put("name", "VH2Layout");
        vh2layout.put("class", ComponentActivity.class.getName());
        list.add(vh2layout);
        HashMap<String, String> vhlayout = new HashMap<String, String>();
        vhlayout.put("name", "VHLayout");
        vhlayout.put("class", ComponentActivity.class.getName());
        list.add(vhlayout);
        HashMap<String, String> vh = new HashMap<String, String>();
        vh.put("name", "VH");
        vh.put("class", ComponentActivity.class.getName());
        vh.put("data", "component_demo/vh_item.json");
        list.add(vh);
        HashMap<String, String> nframelayout = new HashMap<String, String>();
        nframelayout.put("name", "NFrameLayout");
        nframelayout.put("class", ComponentActivity.class.getName());
        list.add(nframelayout);
        HashMap<String, String> ngridlayout = new HashMap<String, String>();
        ngridlayout.put("name", "NGridLayout");
        ngridlayout.put("class", ComponentActivity.class.getName());
        list.add(ngridlayout);
        HashMap<String, String> nratiolayout = new HashMap<String, String>();
        nratiolayout.put("name", "NRatioLayout");
        nratiolayout.put("class", ComponentActivity.class.getName());
        list.add(nratiolayout);
        HashMap<String, String> nvhlayout = new HashMap<String, String>();
        nvhlayout.put("name", "NVHLayout");
        nvhlayout.put("class", ComponentActivity.class.getName());
        list.add(nvhlayout);
        HashMap<String, String> nvh2layout = new HashMap<String, String>();
        nvh2layout.put("name", "NVH2Layout");
        nvh2layout.put("class", ComponentActivity.class.getName());
        list.add(nvh2layout);
        //HashMap<String, String> flexlayout = new HashMap<String, String>();
        //flexlayout.put("name", "FlexLayout");
        //flexlayout.put("class", ComponentActivity.class.getName());
        //list.add(flexlayout);
        HashMap<String, String> totalcontainer = new HashMap<String, String>();
        totalcontainer.put("name", "TotalContainer");
        totalcontainer.put("class", ComponentActivity.class.getName());
        totalcontainer.put("data", "component_demo/total_container.json");
        list.add(totalcontainer);
        //TODO autoDim gravity border margin padding
        ListAdapter listAdapter = new SimpleAdapter(this, list, android.R.layout.simple_list_item_1, new String[]{"name"}, new int[]{android.R.id.text1});
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
