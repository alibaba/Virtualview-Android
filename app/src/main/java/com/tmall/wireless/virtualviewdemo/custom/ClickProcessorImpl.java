package com.tmall.wireless.virtualviewdemo.custom;

import android.util.Log;
import com.tmall.wireless.vaf.virtualview.event.EventData;
import com.tmall.wireless.vaf.virtualview.event.IEventProcessor;

/**
 * Created by longerian on 2017/12/15.
 *
 * @author longerian
 * @date 2017/12/15
 */

public class ClickProcessorImpl implements IEventProcessor {

    @Override
    public boolean process(EventData data) {
        Log.d("Longer", "event " + data.mVB);
        return true;
    }

}
