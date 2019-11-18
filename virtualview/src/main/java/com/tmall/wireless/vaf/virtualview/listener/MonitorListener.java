package com.tmall.wireless.vaf.virtualview.listener;

public interface MonitorListener {
    void onTemplateNotFound(String type);

    /**
     * When a template be used, would invoke this method.
     *
     * @param type template name
     */
    void monitorUsage(String type);
}
