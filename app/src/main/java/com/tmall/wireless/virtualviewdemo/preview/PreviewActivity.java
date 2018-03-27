package com.tmall.wireless.virtualviewdemo.preview;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.tmall.wireless.vaf.framework.VafContext;
import com.tmall.wireless.vaf.framework.ViewManager;
import com.tmall.wireless.vaf.virtualview.core.IContainer;
import com.tmall.wireless.vaf.virtualview.core.Layout;
import com.tmall.wireless.vaf.virtualview.event.EventData;
import com.tmall.wireless.vaf.virtualview.event.EventManager;
import com.tmall.wireless.vaf.virtualview.event.IEventProcessor;
import com.tmall.wireless.virtualviewdemo.R;
import com.tmall.wireless.virtualviewdemo.VirtualViewApplication;
import com.tmall.wireless.virtualviewdemo.preview.util.HttpUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PreviewActivity extends AppCompatActivity {
    private static final String TAG = "VvPreview";

    private VafContext sVafContext;
    private ViewManager sViewManager;

    private LinearLayout mLinearLayout;
    private View mContainer;

    private static final String KEY_DIR = "key_dir";
    private static final String KEY_NAME = "key_name";
    private static final String KEY_URL = "key_url";
    private static final String KEY_FROM = "key_from";
    private static final int FROM_SCAN = 1;
    private static final int FROM_REAL_PREVIEW = 2;

    private String mTemplateName;
    private JSONObject mJsonData;

    public static void start(Context context, String dir) {
        Intent intent = new Intent(context, PreviewActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(KEY_DIR, dir);
        context.startActivity(intent);
    }

    public static void startForScanPreview(Context context, String url) {
        Intent intent = new Intent(context, PreviewActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(KEY_URL, url);
        intent.putExtra(KEY_FROM, FROM_SCAN);
        context.startActivity(intent);
    }

    public static void startForRealPreview(Context context, String name) {
        Intent intent = new Intent(context, PreviewActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(KEY_NAME, name);
        intent.putExtra(KEY_FROM, FROM_REAL_PREVIEW);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        mLinearLayout = (LinearLayout)findViewById(R.id.container);

        sVafContext = ((VirtualViewApplication) getApplication()).getVafContext();
        sViewManager = ((VirtualViewApplication) getApplication()).getViewManager();

        sVafContext.getEventManager().register(EventManager.TYPE_Click, new IEventProcessor() {
            @Override
            public boolean process(EventData data) {
                Log.d(TAG, "TYPE_Click data view:" + data.mView);
                Log.d(TAG, "TYPE_Click view name:" + data.mVB.getTag("name"));
                Log.d(TAG, "TYPE_Click view traceId:" + data.mVB.getTag("activityTraceId"));
                Toast.makeText(PreviewActivity.this,
                        "TYPE_Click view name:" + data.mVB.getTag("name")
                                + "\n traceId:" + data.mVB.getTag("activityTraceId"), Toast.LENGTH_LONG).show();
                return true;
            }
        });

        sVafContext.getEventManager().register(EventManager.TYPE_Exposure, new IEventProcessor() {
            @Override
            public boolean process(EventData data) {
                Log.d(TAG, "TYPE_Exposure data view:" + data.mView);
                Log.d(TAG, "TYPE_Exposure view name:" + data.mVB.getTag("name"));
                Log.d(TAG, "TYPE_Exposure view traceId:" + data.mVB.getTag("activityTraceId"));
//                    Toast.makeText(PreviewActivity.this,
//                            "TYPE_Exposure view name:" + data.mVB.getTag("name")
//                                    + "traceId:" + data.mVB.getTag("activityTraceId"), Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = getIntent();
        if (FROM_SCAN == intent.getIntExtra(KEY_FROM, 0)) {
            String url = intent.getStringExtra(KEY_URL);
            mTemplateName = HttpUtil.getFirstPath(url);
            refreshByUrl(url);
        } else if (FROM_REAL_PREVIEW  == intent.getIntExtra(KEY_FROM, 0)) {
            mTemplateName = intent.getStringExtra(KEY_NAME);
            refreshByName(mTemplateName);
        }

        setTitle(mTemplateName);
    }

    private void preview(String templateName, JSONObject jsonData) {
        if (TextUtils.isEmpty(templateName)) {
            Log.e(TAG, "Template name should not be empty!!!!");
            return;
        }
        mContainer = sVafContext.getContainerService().getContainer(templateName, true);
        IContainer iContainer = (IContainer)mContainer;
        if (jsonData != null) {
            iContainer.getVirtualView().setVData(jsonData);
        }

        Layout.Params p = iContainer.getVirtualView().getComLayoutParams();
        LinearLayout.LayoutParams marginLayoutParams = new LinearLayout.LayoutParams(p.mLayoutWidth, p.mLayoutHeight);
        marginLayoutParams.leftMargin = p.mLayoutMarginLeft;
        marginLayoutParams.topMargin = p.mLayoutMarginTop;
        marginLayoutParams.rightMargin = p.mLayoutMarginRight;
        marginLayoutParams.bottomMargin = p.mLayoutMarginBottom;

        mLinearLayout.removeAllViews();
        mLinearLayout.addView(mContainer, marginLayoutParams);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_preview, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                Intent intent = getIntent();
                if (FROM_SCAN == intent.getIntExtra(KEY_FROM, 0)) {
                    String url = intent.getStringExtra(KEY_URL);
                    mTemplateName = HttpUtil.getFirstPath(url);
                    refreshByUrl(url);
                } else if (FROM_REAL_PREVIEW  == intent.getIntExtra(KEY_FROM, 0)) {
                    mTemplateName = intent.getStringExtra(KEY_NAME);
                    refreshByName(mTemplateName);
                }
                return true;
            case R.id.menu_rtl:
                changeRtl();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void changeRtl() {
        // simulate rtl locale
        if ("ar".equalsIgnoreCase(Locale.getDefault().getLanguage())) {
            Locale locale = new Locale("en");
            Locale.setDefault(locale);
        } else {
            Locale locale = new Locale("ar");
            Locale.setDefault(locale);
        }
        preview(mTemplateName, mJsonData);
    }

    private void refreshByName(String name) {
        if (!TextUtils.isEmpty(name)) {
            refreshByUrl(getUrl(name));
        }
    }

    private void refreshByUrl(final String url) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(url)
                        .build();

                try {
                    Response response = client.newCall(request).execute();
                    if (response != null && response.isSuccessful()) {
                        if (response.body() != null) {
                            String string = response.body().string();
                            final PreviewData previewData = new Gson().fromJson(string, PreviewData.class);
                            if (previewData != null) {
                                loadTemplates(previewData.templates);
                            }

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    JsonObject json = previewData.data;
                                    if (json != null) {
                                        try {
                                            mJsonData = new JSONObject(json.toString());
                                            preview(mTemplateName, mJsonData);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            });
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }



    private String getUrl(String name) {
        return HttpUtil.getHostUrl() + name + "/data.json";
    }

    private void loadTemplates(ArrayList<String> templates){
        for (String temp : templates) {
            sViewManager.loadBinBufferSync(Base64.decode(temp, Base64.DEFAULT));
        }
    }
}
