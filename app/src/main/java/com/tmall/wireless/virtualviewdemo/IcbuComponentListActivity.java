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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created on 2017/12/20.
 * Description: 
 * @author bianyue
 */
public class IcbuComponentListActivity extends ListActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(android.R.layout.list_content);
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        HashMap<String, String> component1 = new HashMap<String, String>();
        component1.put("name", "HomeComponent1");
        component1.put("class", ComponentActivity.class.getName());
        component1.put("data", "component_demo/home_component_1.json");
        list.add(component1);

        HashMap<String, String> component2 = new HashMap<String, String>();
        component2.put("name", "HomeComponent2");
        component2.put("class", ComponentActivity.class.getName());
        component2.put("data", "component_demo/home_component_2.json");
        list.add(component2);

        HashMap<String, String> component3 = new HashMap<String, String>();
        component3.put("name", "HomeIndustry2");
        component3.put("class", ComponentActivity.class.getName());
        component3.put("data", "component_demo/home_industry_data.json");
        list.add(component3);

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
