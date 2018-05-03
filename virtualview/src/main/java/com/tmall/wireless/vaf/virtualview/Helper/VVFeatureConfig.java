/*
 * MIT License
 *
 * Copyright (c) 2018 Alibaba Group
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.tmall.wireless.vaf.virtualview.Helper;

import com.tmall.wireless.vaf.framework.VafContext;
import com.tmall.wireless.vaf.virtualview.view.slider.SliderCompact;

/**
 * Created by longerian on 2018/3/30.
 *
 * @author longerian
 * @date 2018/03/30
 */

public class VVFeatureConfig {

    private static boolean sliderCompat = false;

    /**
     * Call this before create {@link VafContext} instance.
     * @param enableBorderRadius true, enable border radius and clip view.
     */
    public static void setEnableBorderRadius(boolean enableBorderRadius) {
        VirtualViewUtils.setEnableBorderRadius(enableBorderRadius);
    }

    /**
     *
     * @param compat true, use {@link SliderCompact} to render slider, otherwise use {@link com.tmall.wireless.vaf.virtualview.view.slider.Slider}
     */
    public static void setSliderCompat(boolean compat) {
        sliderCompat = compat;
    }

    public static boolean isSliderCompat() {
        return sliderCompat;
    }

    /**
     * @param available true, enable rtl in layout duration.
     */
    public static void setRtlAvailable(boolean available) {
        RtlHelper.setEnable(available);
    }

    public static boolean isRtlAvailable() {
        return RtlHelper.isEnable();
    }

}
