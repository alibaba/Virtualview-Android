package com.tmall.wireless.vaf.virtualview.Helper;

import android.annotation.TargetApi;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;

import com.libra.virtualview.common.ViewBaseCommon;

import java.util.Locale;

/**
 * Created on 2017/12/21.
 * Description:
 *
 * @author bianyue
 */
public class RtlHelper {

    /**
     * In Rtl env or not.
     * @return true if in Rtl env.
     */
    public static boolean isRtl() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return View.LAYOUT_DIRECTION_RTL == TextUtils.getLayoutDirectionFromLocale(Locale.getDefault());
        }

        return false;
    }

    /**
     * Convert (left/right) gravity to (right/left).
     * @param gravity
     * @return rtl gravity
     */
    public static int resolveRtlGravity(int gravity) {
        if (0 != (gravity & ViewBaseCommon.RIGHT)) {
            gravity &= ~ViewBaseCommon.RIGHT;
            gravity |= ViewBaseCommon.LEFT;
        } else if (0 != (gravity & ViewBaseCommon.LEFT)) {
            gravity &= ~ViewBaseCommon.LEFT;
            gravity |= ViewBaseCommon.RIGHT;
        }

        return gravity;
    }
}
