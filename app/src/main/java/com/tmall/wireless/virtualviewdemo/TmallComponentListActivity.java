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
 * Created by longerian on 2017/9/2.
 *
 * @author longerian
 * @date 2017/09/02
 */

public class TmallComponentListActivity extends ListActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(android.R.layout.list_content);
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        HashMap<String, String> component1 = new HashMap<String, String>();
        component1.put("name", "TmallComponent1");
        component1.put("class", ComponentActivity.class.getName());
        component1.put("data", "component_demo/tmall_component_1.json");
        list.add(component1);
        HashMap<String, String> component2 = new HashMap<String, String>();
        component2.put("name", "TmallComponent2");
        component2.put("class", ComponentActivity.class.getName());
        component2.put("data", "component_demo/tmall_component_2.json");
        list.add(component2);
        HashMap<String, String> component3 = new HashMap<String, String>();
        component3.put("name", "TmallComponent3");
        component3.put("class", ComponentActivity.class.getName());
        component3.put("data", "component_demo/tmall_component_3.json");
        list.add(component3);
        HashMap<String, String> component4 = new HashMap<String, String>();
        component4.put("name", "TmallComponent4");
        component4.put("class", ComponentActivity.class.getName());
        component4.put("data", "component_demo/tmall_component_4.json");
        list.add(component4);
        HashMap<String, String> component5 = new HashMap<String, String>();
        component5.put("name", "TmallComponent5");
        component5.put("class", ComponentActivity.class.getName());
        component5.put("data", "component_demo/tmall_component_5.json");
        list.add(component5);
        HashMap<String, String> component6 = new HashMap<String, String>();
        component6.put("name", "TmallComponent6");
        component6.put("class", ComponentActivity.class.getName());
        component6.put("data", "component_demo/tmall_component_6.json");
        list.add(component6);
        HashMap<String, String> component7 = new HashMap<String, String>();
        component7.put("name", "TmallComponent7");
        component7.put("class", ComponentActivity.class.getName());
        component7.put("data", "component_demo/tmall_component_7.json");
        list.add(component7);
        HashMap<String, String> component8 = new HashMap<String, String>();
        component8.put("name", "TmallComponent8");
        component8.put("class", ComponentActivity.class.getName());
        component8.put("data", "component_demo/tmall_component_8.json");
        list.add(component8);
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
