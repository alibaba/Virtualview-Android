package com.tmall.wireless.virtualviewdemo.custom;

import android.util.Log;
import com.tmall.wireless.vaf.virtualview.event.EventData;
import com.tmall.wireless.vaf.virtualview.event.IEventProcessor;

/**
 * Created by longerian on 2018/3/19.
 *
 * @author longerian
 * @date 2018/03/19
 */

public class ExposureProcessorImpl implements IEventProcessor {

    @Override
    public boolean process(EventData data) {
        Log.d("ExposureProcessorImpl", "event " + data.mVB + " " + data.mVB.getAction());
        return true;
    }

}
