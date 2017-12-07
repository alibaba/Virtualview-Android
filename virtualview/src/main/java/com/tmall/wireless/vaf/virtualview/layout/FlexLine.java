/*
 * MIT License
 *
 * Copyright (c) 2017 Alibaba Group
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

package com.tmall.wireless.vaf.virtualview.layout;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds properties related to a single flex line. This class is not expected to be changed outside
 */
public class FlexLine {

    FlexLine() {
    }

    /** @see {@link #getLeft()} */
    int mLeft = Integer.MAX_VALUE;

    /** @see {@link #getTop()} */
    int mTop = Integer.MAX_VALUE;

    /** @see {@link #getRight()} */
    int mRight = Integer.MIN_VALUE;

    /** @see {@link #getBottom()} */
    int mBottom = Integer.MIN_VALUE;

    /** @see {@link #getMainSize()} */
    int mMainSize;

    /**
     * The sum of the lengths of dividers along the main axis. This value should be lower or
     * than than the value of {@link #mMainSize}.
     */
    int mDividerLengthInMainSize;

    /** @see {@link #getCrossSize()} */
    int mCrossSize;

    /** @see {@link #getItemCount()} */
    int mItemCount;

    /** @see {@link #getTotalFlexGrow()} */
    float mTotalFlexGrow;

    /** @see {@link #getTotalFlexShrink()} */
    float mTotalFlexShrink;

    /**
     * The largest value of the individual child's baseline (obtained by View#getBaseline()
     * or the flex direction is vertical, this value is not used.
     * If the alignment direction is from the bottom to top,
     * (e.g. flexWrap == FLEX_WRAP_WRAP_REVERSE and flexDirection == FLEX_DIRECTION_ROW)
     * store this value from the distance from the bottom of the view minus baseline.
     * (Calculated as view.getMeasuredHeight() - view.getBaseline - LayoutParams.bottomMargin)
     */
    int mMaxBaseline;

    /**
     * Store the indices of the children views whose alignSelf property is stretch.
     * The stored indices are the absolute indices including all children in the Flexbox,
     * not the relative indices in this flex line.
     */
    List<Integer> mIndicesAlignSelfStretch = new ArrayList<>();

    /**
     * @return the distance in pixels from the top edge of this view's parent
     * to the top edge of this FlexLine.
     */
    public int getLeft() {
        return mLeft;
    }

    /**
     * @return the distance in pixels from the top edge of this view's parent
     * to the top edge of this FlexLine.
     */
    public int getTop() {
        return mTop;
    }

    /**
     * @return the distance in pixels from the right edge of this view's parent
     * to the right edge of this FlexLine.
     */
    public int getRight() {
        return mRight;
    }

    /**
     * @return the distance in pixels from the bottom edge of this view's parent
     * to the bottom edge of this FlexLine.
     */
    public int getBottom() {
        return mBottom;
    }

    /**
     * @return the size of the flex line in pixels along the main axis of the flex container.
     */
    public int getMainSize() {
        return mMainSize;
    }

    /**
     * @return the size of the flex line in pixels along the cross axis of the flex container.
     */
    public int getCrossSize() {
        return mCrossSize;
    }

    /**
     * @return the count of the views contained in this flex line.
     */
    public int getItemCount() {
        return mItemCount;
    }

    /**
     * @return the sum of the flexGrow properties of the children included in this flex line
     */
    public float getTotalFlexGrow() {
        return mTotalFlexGrow;
    }

    /**
     * @return the sum of the flexShrink properties of the children included in this flex line
     */
    public float getTotalFlexShrink() {
        return mTotalFlexShrink;
    }
}
