package com.tmall.wireless.virtualviewdemo.preview;

import com.google.gson.JsonObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created on 2018/1/6.
 * Description:
 *
 * @author bianyue
 */
public class PreviewData implements Serializable {

    public ArrayList<String> templates;
    public JsonObject data;
}
