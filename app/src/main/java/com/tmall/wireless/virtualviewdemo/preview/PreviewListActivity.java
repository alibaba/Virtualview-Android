package com.tmall.wireless.virtualviewdemo.preview;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.tmall.wireless.virtualviewdemo.preview.util.HttpUtil;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PreviewListActivity extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(android.R.layout.list_content);
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(getUrl())
                        .build();

                try {
                    Response response = client.newCall(request).execute();
                    if (response != null && response.isSuccessful()) {
                        if (response.body() != null) {
                            String string = response.body().string();
                            final String[] dirs = new Gson().fromJson(string, String[].class);
                            if (dirs != null) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ListAdapter adapter = new ArrayAdapter<>(PreviewListActivity.this,
                                                android.R.layout.simple_list_item_1, android.R.id.text1, dirs);
                                        setListAdapter(adapter);
                                    }
                                });
                            } else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(PreviewListActivity.this, "No templates!",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(PreviewListActivity.this, "Server is not running!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        String demoDir = (String) getListAdapter().getItem(position);
        PreviewActivity.startForRealPreview(this, demoDir);
    }

    private String getUrl() {
        return HttpUtil.getHostUrl() + ".dir";
    }

}
