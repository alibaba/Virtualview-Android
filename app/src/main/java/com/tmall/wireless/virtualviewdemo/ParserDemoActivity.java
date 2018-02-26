package com.tmall.wireless.virtualviewdemo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.libra.virtualview.compiler.ViewCompiler;
import com.tmall.wireless.vaf.virtualview.core.IContainer;
import com.tmall.wireless.vaf.virtualview.core.Layout;
import com.tmall.wireless.virtualviewdemo.utils.Utils;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by longerian on 2018/1/6.
 *
 * @author longerian
 * @date 2018/01/06
 */

public class ParserDemoActivity extends Activity implements OnClickListener {

    private static final String TYPE = "PLAY_VV";
    private static final String PATH = "/sdcard/virtualview.xml";
    private static final String OUT_PATH = "/sdcard/virtualview.out";
    private static final String PLAY = "component_demo/virtualview.xml";
    private static final String PLAY_DATA = "component_demo/virtualview.json";
    private Button mDoParse;
    private LinearLayout mLinearLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        verifyStoragePermissions(this);
        setContentView(R.layout.activity_parse_xml);
        mDoParse = (Button)findViewById(R.id.parse);
        mLinearLayout = (LinearLayout)findViewById(R.id.container);
        mDoParse.setOnClickListener(this);
    }


    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
        "android.permission.READ_EXTERNAL_STORAGE",
        "android.permission.WRITE_EXTERNAL_STORAGE" };


    public static void verifyStoragePermissions(Activity activity) {
        try {
            int permission = ActivityCompat.checkSelfPermission(activity,
                "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,REQUEST_EXTERNAL_STORAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        if (!isPlayXMLExisted()) {
            Utils.copyPlayXML(this, PLAY, PATH);
        }
        boolean result = Utils.compile(TYPE, PATH, OUT_PATH);
        if (result) {
            byte[] bin = Utils.readBin(OUT_PATH);
            if (bin != null) {
                mLinearLayout.removeAllViews();
                ((VirtualViewApplication) getApplication()).getViewManager().loadBinBufferSync(bin);
                View container = ((VirtualViewApplication) getApplication()).getVafContext().getContainerService().getContainer(TYPE, true);
                IContainer iContainer = (IContainer)container;
                JSONObject json = Utils.getJSONDataFromAsset(this, PLAY_DATA);
                if (json != null) {
                    iContainer.getVirtualView().setVData(json);
                }
                Layout.Params p = iContainer.getVirtualView().getComLayoutParams();
                LinearLayout.LayoutParams marginLayoutParams = new LinearLayout.LayoutParams(p.mLayoutWidth, p.mLayoutHeight);
                marginLayoutParams.leftMargin = p.mLayoutMarginLeft;
                marginLayoutParams.topMargin = p.mLayoutMarginTop;
                marginLayoutParams.rightMargin = p.mLayoutMarginRight;
                marginLayoutParams.bottomMargin = p.mLayoutMarginBottom;
                mLinearLayout.addView(container, marginLayoutParams);
            } else {
                Toast.makeText(getApplicationContext(), "读取出错，检查日志", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "编译出错，检查日志", Toast.LENGTH_LONG).show();
        }

    }

    private boolean isPlayXMLExisted() {
        File file = new File(PATH);
        return file.exists();
    }



}
