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
import com.libra.expr.compiler.api.ViewCompilerApi;
import com.libra.virtualview.compiler.ViewCompiler;
import com.libra.virtualview.compiler.config.ConfigManager;
import com.libra.virtualview.compiler.config.LocalConfigLoader;
import com.tmall.wireless.vaf.virtualview.core.IContainer;
import com.tmall.wireless.vaf.virtualview.core.Layout;
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
    private static final String PLAY = "component_demo/virtualview.xml";
    private static final String PLAY_DATA = "component_demo/virtualview.json";
    private static final String CONFIG = "config.properties";
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
        byte[] binResult = compile(TYPE, PLAY, 1);
        if (binResult != null) {
            mLinearLayout.removeAllViews();
            ((VirtualViewApplication) getApplication()).getViewManager().loadBinBufferSync(binResult);
            View container = ((VirtualViewApplication) getApplication()).getVafContext().getContainerService().getContainer(TYPE, true);
            IContainer iContainer = (IContainer)container;
            JSONObject json = getJSONDataFromAsset(PLAY_DATA);
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
            Toast.makeText(getApplicationContext(), "编译出错，检查日志", Toast.LENGTH_LONG).show();
        }

    }

    private void copyPlayXML(String name, String target) {
        try {
            InputStream inputStream = getAssets().open(name);
            BufferedReader inputStreamReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder sb = new StringBuilder();
            String str;
            while ((str = inputStreamReader.readLine()) != null) {
                sb.append(str);
            }
            inputStreamReader.close();
            FileOutputStream fos = new FileOutputStream(target);
            fos.write(sb.toString().getBytes());
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private byte[] compile(String type, String name, int version) {
        ViewCompilerApi viewCompiler = new ViewCompilerApi();
        viewCompiler.setConfigLoader(new AssetConfigLoader());
        InputStream fis = null;
        try {
            fis = getAssets().open(name);
            byte[] result = viewCompiler.compile(fis, type, version);
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
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

    private class AssetConfigLoader implements ConfigManager.ConfigLoader {

        @Override
        public InputStream getConfigResource() {
            try {
                InputStream inputStream = getAssets().open(CONFIG);
                return inputStream;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}
