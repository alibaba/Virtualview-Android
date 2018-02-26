package com.tmall.wireless.rx.vaf.framework;

/**
 * Created by longerian on 2018/2/26.
 *
 * @author longerian
 * @date 2018/02/26
 */

public class Template {

    public static final Template NOT_FOUND = new Template("", 0, null);

    public final String type;

    public final int version;

    public final byte[] data;

    public Template(String type, int version, byte[] data) {
        this.type = type;
        this.version = version;
        this.data = data;
    }
}
