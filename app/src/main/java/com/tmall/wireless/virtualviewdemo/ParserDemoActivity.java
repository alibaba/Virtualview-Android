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
            copyPlayXML(PLAY, PATH);
        }
        boolean result = compile(TYPE, PATH, OUT_PATH);
        if (result) {
            byte[] bin = readBin(OUT_PATH);
            if (bin != null) {
                mLinearLayout.removeAllViews();
                ((VirtualViewApplication) getApplication()).getViewManager().loadBinBufferSync(bin);
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

    private boolean compile(String type, String sourcePath, String outputPath) {
        boolean ret = false;
        ViewCompiler compiler = new ViewCompiler();
        compiler.resetString();
        compiler.resetExpr();
        if (compiler.newOutputFile(outputPath, 1, 1)) {
            if (!compiler.compile(type, sourcePath)) {
                Log.d("ParserDemoActivity", "compile file error --> " + sourcePath);
            }
            ret = compiler.compileEnd();
            if (!ret) {
                Log.d("ParserDemoActivity", "compile file end error --> " + sourcePath);
            }
        } else {
            Log.d("ParserDemoActivity", "new output file failed --> " + sourcePath);
        }
        return ret;
    }

    private byte[] readBin(String path) {
        try {
            FileInputStream fin = new FileInputStream(path);
            int length = fin.available();
            byte[] buf = new byte[length];
            fin.read(buf);
            fin.close();
            return buf;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
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
