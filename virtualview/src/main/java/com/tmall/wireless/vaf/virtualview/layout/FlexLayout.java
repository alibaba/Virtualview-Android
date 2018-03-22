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

package com.tmall.wireless.vaf.virtualview.layout;

import android.graphics.drawable.Drawable;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.util.SparseIntArray;
import android.view.View;

import com.libra.virtualview.common.StringBase;
import com.libra.virtualview.common.ViewBaseCommon;
import com.tmall.wireless.vaf.framework.VafContext;
import com.tmall.wireless.vaf.virtualview.core.Layout;
import com.tmall.wireless.vaf.virtualview.core.ViewBase;
import com.tmall.wireless.vaf.virtualview.core.ViewCache;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.libra.virtualview.common.FlexLayoutCommon.ALIGN_CONTENT_CENTER;
import static com.libra.virtualview.common.FlexLayoutCommon.ALIGN_CONTENT_FLEX_END;
import static com.libra.virtualview.common.FlexLayoutCommon.ALIGN_CONTENT_FLEX_START;
import static com.libra.virtualview.common.FlexLayoutCommon.ALIGN_CONTENT_SPACE_AROUND;
import static com.libra.virtualview.common.FlexLayoutCommon.ALIGN_CONTENT_SPACE_BETWEEN;
import static com.libra.virtualview.common.FlexLayoutCommon.ALIGN_CONTENT_STRETCH;
import static com.libra.virtualview.common.FlexLayoutCommon.ALIGN_ITEMS_BASELINE;
import static com.libra.virtualview.common.FlexLayoutCommon.ALIGN_ITEMS_CENTER;
import static com.libra.virtualview.common.FlexLayoutCommon.ALIGN_ITEMS_FLEX_END;
import static com.libra.virtualview.common.FlexLayoutCommon.ALIGN_ITEMS_FLEX_START;
import static com.libra.virtualview.common.FlexLayoutCommon.ALIGN_ITEMS_STRETCH;
import static com.libra.virtualview.common.FlexLayoutCommon.FLEX_DIRECTION_COLUMN;
import static com.libra.virtualview.common.FlexLayoutCommon.FLEX_DIRECTION_COLUMN_REVERSE;
import static com.libra.virtualview.common.FlexLayoutCommon.FLEX_DIRECTION_ROW;
import static com.libra.virtualview.common.FlexLayoutCommon.FLEX_DIRECTION_ROW_REVERSE;
import static com.libra.virtualview.common.FlexLayoutCommon.FLEX_WRAP_NOWRAP;
import static com.libra.virtualview.common.FlexLayoutCommon.FLEX_WRAP_WRAP;
import static com.libra.virtualview.common.FlexLayoutCommon.FLEX_WRAP_WRAP_REVERSE;
import static com.libra.virtualview.common.FlexLayoutCommon.JUSTIFY_CONTENT_CENTER;
import static com.libra.virtualview.common.FlexLayoutCommon.JUSTIFY_CONTENT_FLEX_END;
import static com.libra.virtualview.common.FlexLayoutCommon.JUSTIFY_CONTENT_FLEX_START;
import static com.libra.virtualview.common.FlexLayoutCommon.JUSTIFY_CONTENT_SPACE_AROUND;
import static com.libra.virtualview.common.FlexLayoutCommon.JUSTIFY_CONTENT_SPACE_BETWEEN;

/**
 * Created by gujicheng on 16/8/22.
 */
@Deprecated
public class FlexLayout extends Layout {
    private final static String TAG = "FlexLayout_TMTEST";

    @IntDef({FLEX_DIRECTION_ROW, FLEX_DIRECTION_ROW_REVERSE, FLEX_DIRECTION_COLUMN,
            FLEX_DIRECTION_COLUMN_REVERSE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface FlexDirection {
    }

//    public static final int FLEX_DIRECTION_ROW = 0;
//
//    public static final int FLEX_DIRECTION_ROW_REVERSE = 1;
//
//    public static final int FLEX_DIRECTION_COLUMN = 2;
//
//    public static final int FLEX_DIRECTION_COLUMN_REVERSE = 3;
//
    private int mFlexDirection;

    @IntDef({FLEX_WRAP_NOWRAP, FLEX_WRAP_WRAP, FLEX_WRAP_WRAP_REVERSE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface FlexWrap {
    }

//    public static final int FLEX_WRAP_NOWRAP = 0;
//
//    public static final int FLEX_WRAP_WRAP = 1;
//
//    public static final int FLEX_WRAP_WRAP_REVERSE = 2;

    private int mFlexWrap;


    @IntDef({JUSTIFY_CONTENT_FLEX_START, JUSTIFY_CONTENT_FLEX_END, JUSTIFY_CONTENT_CENTER,
            JUSTIFY_CONTENT_SPACE_BETWEEN, JUSTIFY_CONTENT_SPACE_AROUND})
    @Retention(RetentionPolicy.SOURCE)
    public @interface JustifyContent {
    }

//    public static final int JUSTIFY_CONTENT_FLEX_START = 0;
//
//    public static final int JUSTIFY_CONTENT_FLEX_END = 1;
//
//    public static final int JUSTIFY_CONTENT_CENTER = 2;
//
//    public static final int JUSTIFY_CONTENT_SPACE_BETWEEN = 3;
//
//    public static final int JUSTIFY_CONTENT_SPACE_AROUND = 4;

    private int mJustifyContent;


    @IntDef({ALIGN_ITEMS_FLEX_START, ALIGN_ITEMS_FLEX_END, ALIGN_ITEMS_CENTER,
            ALIGN_ITEMS_BASELINE, ALIGN_ITEMS_STRETCH})
    @Retention(RetentionPolicy.SOURCE)
    public @interface AlignItems {

    }

//    public static final int ALIGN_ITEMS_FLEX_START = 0;
//
//    public static final int ALIGN_ITEMS_FLEX_END = 1;
//
//    public static final int ALIGN_ITEMS_CENTER = 2;
//
//    public static final int ALIGN_ITEMS_BASELINE = 3;
//
//    public static final int ALIGN_ITEMS_STRETCH = 4;

    private int mAlignItems;


    @IntDef({ALIGN_CONTENT_FLEX_START, ALIGN_CONTENT_FLEX_END, ALIGN_CONTENT_CENTER,
            ALIGN_CONTENT_SPACE_BETWEEN, ALIGN_CONTENT_SPACE_AROUND, ALIGN_CONTENT_STRETCH})
    @Retention(RetentionPolicy.SOURCE)
    public @interface AlignContent {

    }

//    public static final int ALIGN_CONTENT_FLEX_START = 0;
//
//    public static final int ALIGN_CONTENT_FLEX_END = 1;
//
//    public static final int ALIGN_CONTENT_CENTER = 2;
//
//    public static final int ALIGN_CONTENT_SPACE_BETWEEN = 3;
//
//    public static final int ALIGN_CONTENT_SPACE_AROUND = 4;
//
//    public static final int ALIGN_CONTENT_STRETCH = 5;

    private int mAlignContent;

    @IntDef(flag = true,
            value = {
                    SHOW_DIVIDER_NONE,
                    SHOW_DIVIDER_BEGINNING,
                    SHOW_DIVIDER_MIDDLE,
                    SHOW_DIVIDER_END
            })
    @Retention(RetentionPolicy.SOURCE)
    public @interface DividerMode {

    }

    /** Constant to how no dividers */
    public static final int SHOW_DIVIDER_NONE = 0;

    /** Constant to show a divider at the beginning of the flex lines (or flex items). */
    public static final int SHOW_DIVIDER_BEGINNING = 1;

    /** Constant to show dividers between flex lines or flex items. */
    public static final int SHOW_DIVIDER_MIDDLE = 1 << 1;

    /** Constant to show a divider at the end of the flex lines or flex items. */
    public static final int SHOW_DIVIDER_END = 1 << 2;

    /** The drawable to be drawn for the horizontal dividers. */
    private Drawable mDividerDrawableHorizontal;

    /** The drawable to be drawn for the vertical dividers. */
    private Drawable mDividerDrawableVertical;

    /**
     * Indicates the divider mode for the {@link #mDividerDrawableHorizontal}. The value needs to
     * be the combination of the value of {@link #SHOW_DIVIDER_NONE},
     * {@link #SHOW_DIVIDER_BEGINNING}, {@link #SHOW_DIVIDER_MIDDLE} and {@link #SHOW_DIVIDER_END}
     */
    private int mShowDividerHorizontal;

    /**
     * Indicates the divider mode for the {@link #mDividerDrawableVertical}. The value needs to
     * be the combination of the value of {@link #SHOW_DIVIDER_NONE},
     * {@link #SHOW_DIVIDER_BEGINNING}, {@link #SHOW_DIVIDER_MIDDLE} and {@link #SHOW_DIVIDER_END}
     */
    private int mShowDividerVertical;

    /** The height of the {@link #mDividerDrawableHorizontal}. */
    private int mDividerHorizontalHeight;

    /** The width of the {@link #mDividerDrawableVertical}. */
    private int mDividerVerticalWidth;

    /**
     */
    private int[] mReorderedIndices;

    /**
     * Key: the index of the view ({@link #mReorderedIndices} isn't taken into account)
     * Value: the value for the order attribute
     */
    private SparseIntArray mOrderCache;

    private List<FlexLine> mFlexLines = new ArrayList<>();

    /**
     * Holds the 'frozen' state of children during measure. If a view is frozen it will no longer
     * expand or shrink regardless of flexGrow/flexShrink. Items are indexed by the child's
     * reordered index.
     */
    private boolean[] mChildrenFrozen;

    public FlexLayout(VafContext context, ViewCache viewCache) {
        super(context, viewCache);

        mFlexDirection = FLEX_DIRECTION_ROW;
        mFlexWrap = FLEX_WRAP_NOWRAP;
        mJustifyContent = JUSTIFY_CONTENT_FLEX_START;
        mAlignItems = ALIGN_ITEMS_FLEX_START;
        mAlignContent = ALIGN_CONTENT_FLEX_START;
    }

    @Override
    public Params generateParams() {
        return new Params();
    }

    @Override
    public void onComMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        Log.d(TAG, "onComMeasure");
        if (isOrderChangedFromLastMeasurement()) {
            mReorderedIndices = createReorderedIndices();
        }
//        if (mChildrenFrozen == null || mChildrenFrozen.length < getChildCount()) {
//            mChildrenFrozen = new boolean[getChildCount()];
//        }
        if (mChildrenFrozen == null || mChildrenFrozen.length < mSubViews.size()) {
            mChildrenFrozen = new boolean[mSubViews.size()];
        }

        // TODO: Only calculate the children views which are affected from the last measure.

        switch (mFlexDirection) {
            case FLEX_DIRECTION_ROW: // Intentional fall through
            case FLEX_DIRECTION_ROW_REVERSE:
                measureHorizontal(widthMeasureSpec, heightMeasureSpec);
                break;
            case FLEX_DIRECTION_COLUMN: // Intentional fall through
            case FLEX_DIRECTION_COLUMN_REVERSE:
                measureVertical(widthMeasureSpec, heightMeasureSpec);
                break;
            default:
                throw new IllegalStateException(
                        "Invalid value for the flex direction is set: " + mFlexDirection);
        }

        Arrays.fill(mChildrenFrozen, false);
    }

    private void measureVertical(int widthMeasureSpec, int heightMeasureSpec) {
        int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);
        int childState = 0;

        mFlexLines.clear();

        // Determine how many flex lines are needed in this layout by measuring each child.
        // (Expand or shrink the view depending on the flexGrow and flexShrink attributes in a later
        // loop)
        int childCount = mSubViews.size();
        int paddingTop = getComPaddingTop();
        int paddingBottom = getComPaddingBottom();
        int largestWidthInColumn = Integer.MIN_VALUE;
        FlexLine flexLine = new FlexLine();
        flexLine.mMainSize = paddingTop + paddingBottom;
        // The index of the view in a same flex line.
        int indexInFlexLine = 0;
        for (int i = 0; i < childCount; i++) {
            ViewBase child = getReorderedChildAt(i);
            if (child == null) {
                addFlexLineIfLastFlexItem(i, childCount, flexLine);
                continue;
//            } else if (child.getVisibility() == View.GONE) {
//            } else if (!child.isVisible()) {
            } else if (child.getVisibility() == ViewBaseCommon.GONE) {
                flexLine.mItemCount++;
                addFlexLineIfLastFlexItem(i, childCount, flexLine);
                continue;
            }

            Params lp = (Params) child.getComLayoutParams();
            if (lp.alignSelf == Params.ALIGN_SELF_STRETCH) {
                flexLine.mIndicesAlignSelfStretch.add(i);
            }

            int childHeight = lp.mLayoutHeight;
            if (lp.flexBasisPercent != Params.FLEX_BASIS_PERCENT_DEFAULT
                    && heightMode == View.MeasureSpec.EXACTLY) {
                childHeight = Math.round(heightSize * lp.flexBasisPercent);
                // Use the dimension from the layout_height attribute if the heightMode is not
                // MeasureSpec.EXACTLY even if any fraction value is set to layout_flexBasisPercent.
                // There are likely quite few use cases where assigning any fraction values
                // with heightMode is not MeasureSpec.EXACTLY (e.g. FlexboxLayout's layout_height
                // is set to wrap_content)
            }

            int childWidthMeasureSpec = getChildMeasureSpec(widthMeasureSpec,
                    getComPaddingLeft() + getComPaddingRight() + lp.mLayoutMarginLeft
                            + lp.mLayoutMarginRight, lp.mLayoutWidth);
            int childHeightMeasureSpec = getChildMeasureSpec(heightMeasureSpec,
                    getComPaddingTop() + getComPaddingBottom() + lp.mLayoutMarginTop
                            + lp.mLayoutMarginBottom, childHeight);
            child.measureComponent(childWidthMeasureSpec, childHeightMeasureSpec);

            // Check the size constraint after the first measurement for the child
            // To prevent the child's width/height violate the size constraints imposed by the
            // {@link LayoutParams#minWidth}, {@link LayoutParams#minHeight},
            // {@link LayoutParams#maxWidth} and {@link LayoutParams#maxHeight} attributes.
            // E.g. When the child's layout_height is wrap_content the measured height may be
            // less than the min height after the first measurement.
            checkSizeConstraints(child);

            childState = ViewCompat
                    .combineMeasuredStates(childState, 0);
//                    .combineMeasuredStates(childState, ViewCompat.getMeasuredState(child));
            largestWidthInColumn = Math.max(largestWidthInColumn,
                    child.getComMeasuredWidth() + lp.mLayoutMarginLeft + lp.mLayoutMarginRight);

            if (isWrapRequired(heightMode, heightSize, flexLine.mMainSize,
                    child.getComMeasuredHeight() + lp.mLayoutMarginTop + lp.mLayoutMarginBottom, lp,
                    i, indexInFlexLine)) {
                if (flexLine.mItemCount > 0) {
                    addFlexLine(flexLine);
                }

                flexLine = new FlexLine();
                flexLine.mItemCount = 1;
                flexLine.mMainSize = paddingTop + paddingBottom;
                largestWidthInColumn = child.getComMeasuredWidth() + lp.mLayoutMarginLeft + lp.mLayoutMarginRight;
                indexInFlexLine = 0;
            } else {
                flexLine.mItemCount++;
                indexInFlexLine++;
            }
            flexLine.mMainSize += child.getComMeasuredHeight() + lp.mLayoutMarginTop + lp.mLayoutMarginBottom;
            flexLine.mTotalFlexGrow += lp.flexGrow;
            flexLine.mTotalFlexShrink += lp.flexShrink;
            // Temporarily set the cross axis length as the largest child width in the column
            // Expand along the cross axis depending on the mAlignContent property if needed
            // later
            flexLine.mCrossSize = Math.max(flexLine.mCrossSize, largestWidthInColumn);

            if (hasDividerBeforeChildAtAlongMainAxis(i, indexInFlexLine)) {
                flexLine.mMainSize += mDividerHorizontalHeight;
            }
            addFlexLineIfLastFlexItem(i, childCount, flexLine);
        }

        determineMainSize(mFlexDirection, widthMeasureSpec, heightMeasureSpec);
        determineCrossSize(mFlexDirection, widthMeasureSpec, heightMeasureSpec,
                getComPaddingLeft() + getComPaddingRight());
        // Now cross size for each flex line is determined.
        // Expand the views if alignItems (or alignSelf in each child view) is set to stretch
        stretchViews(mFlexDirection, mAlignItems);
        setMeasuredDimensionForFlex(mFlexDirection, widthMeasureSpec, heightMeasureSpec,
                childState);
    }

    private void measureHorizontal(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
        int childState = 0;

        mFlexLines.clear();

        // Determine how many flex lines are needed in this layout by measuring each child.
        // (Expand or shrink the view depending on the flexGrow and flexShrink attributes in a later
        // loop)
        {
//            int childCount = getChildCount();
            int childCount = mSubViews.size();
            int paddingStart = mPaddingLeft;
//            int paddingStart = ViewCompat.getPaddingStart(this);
//            int paddingEnd = ViewCompat.getPaddingEnd(this);
            int paddingEnd = mPaddingRight;
            int largestHeightInRow = Integer.MIN_VALUE;
            FlexLine flexLine = new FlexLine();

            // The index of the view in a same flex line.
            int indexInFlexLine = 0;
            flexLine.mMainSize = paddingStart + paddingEnd;
            for (int i = 0; i < childCount; i++) {
//                View child = getReorderedChildAt(i);
                ViewBase child = getReorderedChildAt(i);
                if (child == null) {
                    addFlexLineIfLastFlexItem(i, childCount, flexLine);
                    continue;
//                } else if (child.getVisibility() == View.GONE) {
//                } else if (!child.isVisible()) {
                } else if (child.getVisibility() == ViewBaseCommon.GONE) {
                    flexLine.mItemCount++;
                    addFlexLineIfLastFlexItem(i, childCount, flexLine);
                    continue;
                }

                Params lp = (Params) child.getComLayoutParams();
                if (lp.alignSelf == Params.ALIGN_SELF_STRETCH) {
                    flexLine.mIndicesAlignSelfStretch.add(i);
                }

                int childWidth = lp.mLayoutWidth;
                if (lp.flexBasisPercent != Params.FLEX_BASIS_PERCENT_DEFAULT
                        && widthMode == View.MeasureSpec.EXACTLY) {
                    childWidth = Math.round(widthSize * lp.flexBasisPercent);
                    // Use the dimension from the layout_width attribute if the widthMode is not
                    // MeasureSpec.EXACTLY even if any fraction value is set to
                    // layout_flexBasisPercent.
                    // There are likely quite few use cases where assigning any fraction values
                    // with widthMode is not MeasureSpec.EXACTLY (e.g. FlexboxLayout's layout_width
                    // is set to wrap_content)
                }
                int childWidthMeasureSpec = getChildMeasureSpec(widthMeasureSpec,
                        getComPaddingLeft() + getComPaddingRight() + lp.mLayoutMarginLeft
                                + lp.mLayoutMarginRight, childWidth);
                int childHeightMeasureSpec = getChildMeasureSpec(heightMeasureSpec,
                        getComPaddingTop() + getComPaddingBottom() + lp.mLayoutMarginTop
                                + lp.mLayoutMarginBottom, lp.mLayoutHeight);
                child.measureComponent(childWidthMeasureSpec, childHeightMeasureSpec);

                // Check the size constraint after the first measurement for the child
                // To prevent the child's width/height violate the size constraints imposed by the
                // {@link LayoutParams#minWidth}, {@link LayoutParams#minHeight},
                // {@link LayoutParams#maxWidth} and {@link LayoutParams#maxHeight} attributes.
                // E.g. When the child's layout_width is wrap_content the measured width may be
                // less than the min width after the first measurement.
                checkSizeConstraints(child);

                childState = ViewCompat
                        .combineMeasuredStates(childState, 0);
//                        .combineMeasuredStates(childState, ViewCompat.getMeasuredState(child));
                largestHeightInRow = Math.max(largestHeightInRow,
                        child.getComMeasuredHeight() + lp.mLayoutMarginTop + lp.mLayoutMarginBottom);

                if (isWrapRequired(widthMode, widthSize, flexLine.mMainSize,
                        child.getComMeasuredWidth() + lp.mLayoutMarginLeft + lp.mLayoutMarginRight, lp,
                        i, indexInFlexLine)) {
                    if (flexLine.mItemCount > 0) {
                        addFlexLine(flexLine);
                    }

                    flexLine = new FlexLine();
                    flexLine.mItemCount = 1;
                    flexLine.mMainSize = paddingStart + paddingEnd;
                    largestHeightInRow = child.getComMeasuredHeight() + lp.mLayoutMarginTop + lp.mLayoutMarginBottom;
                    indexInFlexLine = 0;
                } else {
                    flexLine.mItemCount++;
                    indexInFlexLine++;
                }
                flexLine.mMainSize += child.getComMeasuredWidth() + lp.mLayoutMarginLeft + lp.mLayoutMarginRight;
                flexLine.mTotalFlexGrow += lp.flexGrow;
                flexLine.mTotalFlexShrink += lp.flexShrink;
                // Temporarily set the cross axis length as the largest child in the row
                // Expand along the cross axis depending on the mAlignContent property if needed
                // later
                flexLine.mCrossSize = Math.max(flexLine.mCrossSize, largestHeightInRow);

                // Check if the beginning or middle divider is required for the flex item
                if (hasDividerBeforeChildAtAlongMainAxis(i, indexInFlexLine)) {
                    flexLine.mMainSize += mDividerVerticalWidth;
                    flexLine.mDividerLengthInMainSize += mDividerVerticalWidth;
                }

                if (mFlexWrap != FLEX_WRAP_WRAP_REVERSE) {
                    flexLine.mMaxBaseline = Math
                            .max(flexLine.mMaxBaseline, child.getComBaseline() + lp.mLayoutMarginTop);
                } else {
                    // if the flex wrap property is FLEX_WRAP_WRAP_REVERSE, calculate the
                    // baseline as the distance from the cross end and the baseline
                    // since the cross size calculation is based on the distance from the cross end
                    flexLine.mMaxBaseline = Math
                            .max(flexLine.mMaxBaseline,
                                    child.getComMeasuredHeight() - child.getComBaseline()
                                            + lp.mLayoutMarginBottom);
                }
                addFlexLineIfLastFlexItem(i, childCount, flexLine);
            }
        }

        determineMainSize(mFlexDirection, widthMeasureSpec, heightMeasureSpec);

        // TODO: Consider the case any individual child's alignSelf is set to ALIGN_SELF_BASELINE
        if (mAlignItems == ALIGN_ITEMS_BASELINE) {
            int viewIndex = 0;
            for (FlexLine flexLine : mFlexLines) {
                // The largest height value that also take the baseline shift into account
                int largestHeightInLine = Integer.MIN_VALUE;
                for (int i = viewIndex; i < viewIndex + flexLine.mItemCount; i++) {
                    ViewBase child = getReorderedChildAt(i);
                    Params lp = (Params) child.getComLayoutParams();
                    if (mFlexWrap != FLEX_WRAP_WRAP_REVERSE) {
                        int marginTop = flexLine.mMaxBaseline - child.getComBaseline();
                        marginTop = Math.max(marginTop, lp.mLayoutMarginTop);
                        largestHeightInLine = Math.max(largestHeightInLine,
//                                child.getHeight() + marginTop + lp.bottomMargin);
                                child.getComMeasuredHeight() + marginTop + lp.mLayoutMarginBottom);
                    } else {
                        int marginBottom = flexLine.mMaxBaseline - child.getComMeasuredHeight() +
                                child.getComBaseline();
                        marginBottom = Math.max(marginBottom, lp.mLayoutMarginBottom);
                        largestHeightInLine = Math.max(largestHeightInLine,
                                child.getComMeasuredHeight() + lp.mLayoutMarginTop + marginBottom);
//                                child.getHeight() + lp.topMargin + marginBottom);
                    }
                }
                flexLine.mCrossSize = largestHeightInLine;
                viewIndex += flexLine.mItemCount;
            }
        }

        determineCrossSize(mFlexDirection, widthMeasureSpec, heightMeasureSpec,
                getComPaddingTop() + getComPaddingBottom());
        // Now cross size for each flex line is determined.
        // Expand the views if alignItems (or alignSelf in each child view) is set to stretch
        stretchViews(mFlexDirection, mAlignItems);
        setMeasuredDimensionForFlex(mFlexDirection, widthMeasureSpec, heightMeasureSpec,
                childState);
    }

    private void setMeasuredDimensionForFlex(@FlexDirection int flexDirection, int widthMeasureSpec,
                                             int heightMeasureSpec, int childState) {
        int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);
        int calculatedMaxHeight;
        int calculatedMaxWidth;
        switch (flexDirection) {
            case FLEX_DIRECTION_ROW: // Intentional fall through
            case FLEX_DIRECTION_ROW_REVERSE:
                calculatedMaxHeight = getSumOfCrossSize() + getComPaddingTop()
                        + getComPaddingBottom();
                calculatedMaxWidth = getLargestMainSize();
                break;
            case FLEX_DIRECTION_COLUMN: // Intentional fall through
            case FLEX_DIRECTION_COLUMN_REVERSE:
                calculatedMaxHeight = getLargestMainSize();
                calculatedMaxWidth = getSumOfCrossSize() + getComPaddingLeft() + getComPaddingRight();
                break;
            default:
                throw new IllegalArgumentException("Invalid flex direction: " + flexDirection);
        }

        int widthSizeAndState;
        switch (widthMode) {
            case View.MeasureSpec.EXACTLY:
                if (widthSize < calculatedMaxWidth) {
                    childState = ViewCompat
                            .combineMeasuredStates(childState, ViewCompat.MEASURED_STATE_TOO_SMALL);
                }
                widthSizeAndState = ViewCompat.resolveSizeAndState(widthSize, widthMeasureSpec,
                        childState);
                break;
            case View.MeasureSpec.AT_MOST: {
                if (widthSize < calculatedMaxWidth) {
                    childState = ViewCompat
                            .combineMeasuredStates(childState, ViewCompat.MEASURED_STATE_TOO_SMALL);
                } else {
                    widthSize = calculatedMaxWidth;
                }
                widthSizeAndState = ViewCompat.resolveSizeAndState(widthSize, widthMeasureSpec,
                        childState);
                break;
            }
            case View.MeasureSpec.UNSPECIFIED: {
                widthSizeAndState = ViewCompat
                        .resolveSizeAndState(calculatedMaxWidth, widthMeasureSpec, childState);
                break;
            }
            default:
                throw new IllegalStateException("Unknown width mode is set: " + widthMode);
        }
        int heightSizeAndState;
        switch (heightMode) {
            case View.MeasureSpec.EXACTLY:
                if (heightSize < calculatedMaxHeight) {
                    childState = ViewCompat.combineMeasuredStates(childState,
                            ViewCompat.MEASURED_STATE_TOO_SMALL
                                    >> ViewCompat.MEASURED_HEIGHT_STATE_SHIFT);
                }
                heightSizeAndState = ViewCompat.resolveSizeAndState(heightSize, heightMeasureSpec,
                        childState);
                break;
            case View.MeasureSpec.AT_MOST: {
                if (heightSize < calculatedMaxHeight) {
                    childState = ViewCompat.combineMeasuredStates(childState,
                            ViewCompat.MEASURED_STATE_TOO_SMALL
                                    >> ViewCompat.MEASURED_HEIGHT_STATE_SHIFT);
                } else {
                    heightSize = calculatedMaxHeight;
                }
                heightSizeAndState = ViewCompat.resolveSizeAndState(heightSize, heightMeasureSpec,
                        childState);
                break;
            }
            case View.MeasureSpec.UNSPECIFIED: {
                heightSizeAndState = ViewCompat.resolveSizeAndState(calculatedMaxHeight,
                        heightMeasureSpec, childState);
                break;
            }
            default:
                throw new IllegalStateException("Unknown height mode is set: " + heightMode);
        }
        setComMeasuredDimension(widthSizeAndState, heightSizeAndState);
    }


    private void stretchViews(int flexDirection, int alignItems) {
        if (alignItems == ALIGN_ITEMS_STRETCH) {
            int viewIndex = 0;
            for (FlexLine flexLine : mFlexLines) {
                for (int i = 0; i < flexLine.mItemCount; i++, viewIndex++) {
                    ViewBase view = getReorderedChildAt(viewIndex);
                    Params lp = (Params) view.getComLayoutParams();
                    if (lp.alignSelf != Params.ALIGN_SELF_AUTO &&
                            lp.alignSelf != Params.ALIGN_SELF_STRETCH) {
                        continue;
                    }
                    switch (flexDirection) {
                        case FLEX_DIRECTION_ROW: // Intentional fall through
                        case FLEX_DIRECTION_ROW_REVERSE:
                            stretchViewVertically(view, flexLine.mCrossSize);
                            break;
                        case FLEX_DIRECTION_COLUMN:
                        case FLEX_DIRECTION_COLUMN_REVERSE:
                            stretchViewHorizontally(view, flexLine.mCrossSize);
                            break;
                        default:
                            throw new IllegalArgumentException(
                                    "Invalid flex direction: " + flexDirection);
                    }
                }
            }
        } else {
            for (FlexLine flexLine : mFlexLines) {
                for (Integer index : flexLine.mIndicesAlignSelfStretch) {
                    ViewBase view = getReorderedChildAt(index);
                    switch (flexDirection) {
                        case FLEX_DIRECTION_ROW: // Intentional fall through
                        case FLEX_DIRECTION_ROW_REVERSE:
                            stretchViewVertically(view, flexLine.mCrossSize);
                            break;
                        case FLEX_DIRECTION_COLUMN:
                        case FLEX_DIRECTION_COLUMN_REVERSE:
                            stretchViewHorizontally(view, flexLine.mCrossSize);
                            break;
                        default:
                            throw new IllegalArgumentException(
                                    "Invalid flex direction: " + flexDirection);
                    }
                }
            }
        }
    }

    private void stretchViewHorizontally(ViewBase view, int crossSize) {
        Params lp = (Params) view.getComLayoutParams();
        int newWidth = crossSize - lp.mLayoutMarginLeft - lp.mLayoutMarginRight;
        newWidth = Math.max(newWidth, 0);
        view.measureComponent(View.MeasureSpec
                        .makeMeasureSpec(newWidth, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(view.getComMeasuredHeight(), View.MeasureSpec.EXACTLY));
    }


    private void stretchViewVertically(ViewBase view, int crossSize) {
        Params lp = (Params) view.getComLayoutParams();
        int newHeight = crossSize - lp.mLayoutMarginTop - lp.mLayoutMarginBottom;
        newHeight = Math.max(newHeight, 0);
        view.measureComponent(View.MeasureSpec
                        .makeMeasureSpec(view.getComMeasuredWidth(), View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(newHeight, View.MeasureSpec.EXACTLY));
    }

    private void determineCrossSize(int flexDirection, int widthMeasureSpec,
                                    int heightMeasureSpec, int paddingAlongCrossAxis) {
        // The MeasureSpec mode along the cross axis
        int mode;
        // The MeasureSpec size along the cross axis
        int size;
        switch (flexDirection) {
            case FLEX_DIRECTION_ROW: // Intentional fall through
            case FLEX_DIRECTION_ROW_REVERSE:
                mode = View.MeasureSpec.getMode(heightMeasureSpec);
                size = View.MeasureSpec.getSize(heightMeasureSpec);
                break;
            case FLEX_DIRECTION_COLUMN: // Intentional fall through
            case FLEX_DIRECTION_COLUMN_REVERSE:
                mode = View.MeasureSpec.getMode(widthMeasureSpec);
                size = View.MeasureSpec.getSize(widthMeasureSpec);
                break;
            default:
                throw new IllegalArgumentException("Invalid flex direction: " + flexDirection);
        }
        if (mode == View.MeasureSpec.EXACTLY) {
            int totalCrossSize = getSumOfCrossSize() + paddingAlongCrossAxis;
            if (mFlexLines.size() == 1) {
                mFlexLines.get(0).mCrossSize = size - paddingAlongCrossAxis;
                // alignContent property is valid only if the Flexbox has at least two lines
            } else if (mFlexLines.size() >= 2 && totalCrossSize < size) {
                switch (mAlignContent) {
                    case ALIGN_CONTENT_STRETCH: {
                        float freeSpaceUnit = (size - totalCrossSize) / (float) mFlexLines.size();
                        float accumulatedError = 0;
                        for (int i = 0, flexLinesSize = mFlexLines.size(); i < flexLinesSize; i++) {
                            FlexLine flexLine = mFlexLines.get(i);
                            float newCrossSizeAsFloat = flexLine.mCrossSize + freeSpaceUnit;
                            if (i == mFlexLines.size() - 1) {
                                newCrossSizeAsFloat += accumulatedError;
                                accumulatedError = 0;
                            }
                            int newCrossSize = Math.round(newCrossSizeAsFloat);
                            accumulatedError += (newCrossSizeAsFloat - newCrossSize);
                            if (accumulatedError > 1) {
                                newCrossSize += 1;
                                accumulatedError -= 1;
                            } else if (accumulatedError < -1) {
                                newCrossSize -= 1;
                                accumulatedError += 1;
                            }
                            flexLine.mCrossSize = newCrossSize;
                        }
                        break;
                    }
                    case ALIGN_CONTENT_SPACE_AROUND: {
                        // The value of free space along the cross axis which needs to be put on top
                        // and below the bottom of each flex line.
                        int spaceTopAndBottom = size - totalCrossSize;
                        // The number of spaces along the cross axis
                        int numberOfSpaces = mFlexLines.size() * 2;
                        spaceTopAndBottom = spaceTopAndBottom / numberOfSpaces;
                        List<FlexLine> newFlexLines = new ArrayList<>();
                        FlexLine dummySpaceFlexLine = new FlexLine();
                        dummySpaceFlexLine.mCrossSize = spaceTopAndBottom;
                        for (FlexLine flexLine : mFlexLines) {
                            newFlexLines.add(dummySpaceFlexLine);
                            newFlexLines.add(flexLine);
                            newFlexLines.add(dummySpaceFlexLine);
                        }
                        mFlexLines = newFlexLines;
                        break;
                    }
                    case ALIGN_CONTENT_SPACE_BETWEEN: {
                        // The value of free space along the cross axis between each flex line.
                        float spaceBetweenFlexLine = size - totalCrossSize;
                        int numberOfSpaces = mFlexLines.size() - 1;
                        spaceBetweenFlexLine = spaceBetweenFlexLine / (float) numberOfSpaces;
                        float accumulatedError = 0;
                        List<FlexLine> newFlexLines = new ArrayList<>();
                        for (int i = 0, flexLineSize = mFlexLines.size(); i < flexLineSize; i++) {
                            FlexLine flexLine = mFlexLines.get(i);
                            newFlexLines.add(flexLine);

                            if (i != mFlexLines.size() - 1) {
                                FlexLine dummySpaceFlexLine = new FlexLine();
                                if (i == mFlexLines.size() - 2) {
                                    // The last dummy space block in the flex container.
                                    // Adjust the cross size by the accumulated error.
                                    dummySpaceFlexLine.mCrossSize = Math
                                            .round(spaceBetweenFlexLine + accumulatedError);
                                    accumulatedError = 0;
                                } else {
                                    dummySpaceFlexLine.mCrossSize = Math
                                            .round(spaceBetweenFlexLine);
                                }
                                accumulatedError += (spaceBetweenFlexLine
                                        - dummySpaceFlexLine.mCrossSize);
                                if (accumulatedError > 1) {
                                    dummySpaceFlexLine.mCrossSize += 1;
                                    accumulatedError -= 1;
                                } else if (accumulatedError < -1) {
                                    dummySpaceFlexLine.mCrossSize -= 1;
                                    accumulatedError += 1;
                                }
                                newFlexLines.add(dummySpaceFlexLine);
                            }
                        }
                        mFlexLines = newFlexLines;
                        break;
                    }
                    case ALIGN_CONTENT_CENTER: {
                        int spaceAboveAndBottom = size - totalCrossSize;
                        spaceAboveAndBottom = spaceAboveAndBottom / 2;
                        List<FlexLine> newFlexLines = new ArrayList<>();
                        FlexLine dummySpaceFlexLine = new FlexLine();
                        dummySpaceFlexLine.mCrossSize = spaceAboveAndBottom;
                        for (int i = 0, flexLineSize = mFlexLines.size(); i < flexLineSize; i++) {
                            if (i == 0) {
                                newFlexLines.add(dummySpaceFlexLine);
                            }
                            FlexLine flexLine = mFlexLines.get(i);
                            newFlexLines.add(flexLine);
                            if (i == mFlexLines.size() - 1) {
                                newFlexLines.add(dummySpaceFlexLine);
                            }
                        }
                        mFlexLines = newFlexLines;
                        break;
                    }
                    case ALIGN_CONTENT_FLEX_END: {
                        int spaceTop = size - totalCrossSize;
                        FlexLine dummySpaceFlexLine = new FlexLine();
                        dummySpaceFlexLine.mCrossSize = spaceTop;
                        mFlexLines.add(0, dummySpaceFlexLine);
                        break;
                    }
                }
            }
        }
    }

    private int getSumOfCrossSize() {
        int sum = 0;
        for (int i = 0, size = mFlexLines.size(); i < size; i++) {
            FlexLine flexLine = mFlexLines.get(i);

            // Judge if the beginning or middle dividers are required
            if (hasDividerBeforeFlexLine(i)) {
                if (isMainAxisDirectionHorizontal(mFlexDirection)) {
                    sum += mDividerHorizontalHeight;
                } else {
                    sum += mDividerVerticalWidth;
                }
            }

            // Judge if the end divider is required
            if (hasEndDividerAfterFlexLine(i)) {
                if (isMainAxisDirectionHorizontal(mFlexDirection)) {
                    sum += mDividerHorizontalHeight;
                } else {
                    sum += mDividerVerticalWidth;
                }
            }
            sum += flexLine.mCrossSize;
        }
        return sum;
    }

    private boolean hasEndDividerAfterFlexLine(int flexLineIndex) {
        if (flexLineIndex < 0 || flexLineIndex >= mFlexLines.size()) {
            return false;
        }

        for (int i = flexLineIndex + 1; i < mFlexLines.size(); i++) {
            if (mFlexLines.get(i).mItemCount > 0) {
                return false;
            }
        }
        if (isMainAxisDirectionHorizontal(mFlexDirection)) {
            return (mShowDividerHorizontal & SHOW_DIVIDER_END) != 0;
        } else {
            return (mShowDividerVertical & SHOW_DIVIDER_END) != 0;
        }

    }

    private boolean hasDividerBeforeFlexLine(int flexLineIndex) {
        if (flexLineIndex < 0 || flexLineIndex >= mFlexLines.size()) {
            return false;
        }
        if (allFlexLinesAreDummyBefore(flexLineIndex)) {
            if (isMainAxisDirectionHorizontal(mFlexDirection)) {
                return (mShowDividerHorizontal & SHOW_DIVIDER_BEGINNING) != 0;
            } else {
                return (mShowDividerVertical & SHOW_DIVIDER_BEGINNING) != 0;
            }
        } else {
            if (isMainAxisDirectionHorizontal(mFlexDirection)) {
                return (mShowDividerHorizontal & SHOW_DIVIDER_MIDDLE) != 0;
            } else {
                return (mShowDividerVertical & SHOW_DIVIDER_MIDDLE) != 0;
            }
        }
    }

    private boolean allFlexLinesAreDummyBefore(int flexLineIndex) {
        for (int i = 0; i < flexLineIndex; i++) {
            if (mFlexLines.get(i).mItemCount > 0) {
                return false;
            }
        }
        return true;
    }

    private void determineMainSize(@FlexDirection int flexDirection, int widthMeasureSpec,
                                   int heightMeasureSpec) {
        int mainSize;
        int paddingAlongMainAxis;
        switch (flexDirection) {
            case FLEX_DIRECTION_ROW: // Intentional fall through
            case FLEX_DIRECTION_ROW_REVERSE:
                int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
                int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
                if (widthMode == View.MeasureSpec.EXACTLY) {
                    mainSize = widthSize;
                } else {
                    mainSize = getLargestMainSize();
                }
                paddingAlongMainAxis = getComPaddingLeft() + getComPaddingRight();
                break;
            case FLEX_DIRECTION_COLUMN: // Intentional fall through
            case FLEX_DIRECTION_COLUMN_REVERSE:
                int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);
                int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);
                if (heightMode == View.MeasureSpec.EXACTLY) {
                    mainSize = heightSize;
                } else {
                    mainSize = getLargestMainSize();
                }
                paddingAlongMainAxis = getComPaddingTop() + getComPaddingBottom();
                break;
            default:
                throw new IllegalArgumentException("Invalid flex direction: " + flexDirection);
        }

        int childIndex = 0;
        for (FlexLine flexLine : mFlexLines) {
            if (flexLine.mMainSize < mainSize) {
                childIndex = expandFlexItems(flexLine, flexDirection, mainSize,
                        paddingAlongMainAxis, childIndex);
            } else {
                childIndex = shrinkFlexItems(flexLine, flexDirection, mainSize,
                        paddingAlongMainAxis, childIndex);
            }
        }
    }

    private int shrinkFlexItems(FlexLine flexLine, @FlexDirection int flexDirection,
                                int maxMainSize, int paddingAlongMainAxis, int startIndex) {
        int childIndex = startIndex;
        int sizeBeforeShrink = flexLine.mMainSize;
        if (flexLine.mTotalFlexShrink <= 0 || maxMainSize > flexLine.mMainSize) {
            childIndex += flexLine.mItemCount;
            return childIndex;
        }
        boolean needsReshrink = false;
        float unitShrink = (flexLine.mMainSize - maxMainSize) / flexLine.mTotalFlexShrink;
        float accumulatedRoundError = 0;
        flexLine.mMainSize = paddingAlongMainAxis + flexLine.mDividerLengthInMainSize;
        for (int i = 0; i < flexLine.mItemCount; i++) {
            ViewBase child = getReorderedChildAt(childIndex);
            if (child == null) {
                continue;
//            } else if (child.getVisibility() == View.GONE) {
//            } else if (!child.isVisible()) {
            } else if (child.getVisibility() == ViewBaseCommon.GONE) {
                childIndex++;
                continue;
            }
            Params lp = (Params) child.getComLayoutParams();
            if (isMainAxisDirectionHorizontal(flexDirection)) {
                // The direction of main axis is horizontal
                if (!mChildrenFrozen[childIndex]) {
                    float rawCalculatedWidth = child.getComMeasuredWidth()
                            - unitShrink * lp.flexShrink;
                    if (i == flexLine.mItemCount - 1) {
                        rawCalculatedWidth += accumulatedRoundError;
                        accumulatedRoundError = 0;
                    }
                    int newWidth = Math.round(rawCalculatedWidth);
                    if (newWidth < lp.minWidth) {
                        // This means the child doesn't have enough space to distribute the negative
                        // free space. To adjust the flex line length down to the maxMainSize, remaining
                        // negative free space needs to be re-distributed to other flex items
                        // (children views). In that case, invoke this method again with the same
                        // startIndex.
                        needsReshrink = true;
                        newWidth = lp.minWidth;
                        mChildrenFrozen[childIndex] = true;
                        flexLine.mTotalFlexShrink -= lp.flexShrink;
                    } else {
                        accumulatedRoundError += (rawCalculatedWidth - newWidth);
                        if (accumulatedRoundError > 1.0) {
                            newWidth += 1;
                            accumulatedRoundError -= 1;
                        } else if (accumulatedRoundError < -1.0) {
                            newWidth -= 1;
                            accumulatedRoundError += 1;
                        }
                    }
                    child.measureComponent(View.MeasureSpec.makeMeasureSpec(newWidth, View.MeasureSpec.EXACTLY),
                            View.MeasureSpec.makeMeasureSpec(child.getComMeasuredHeight(),
                                    View.MeasureSpec.EXACTLY));
                }
                flexLine.mMainSize += child.getComMeasuredWidth() + lp.mLayoutMarginLeft + lp.mLayoutMarginRight;
            } else {
                // The direction of main axis is vertical
                if (!mChildrenFrozen[childIndex]) {
                    float rawCalculatedHeight = child.getComMeasuredHeight()
                            - unitShrink * lp.flexShrink;
                    if (i == flexLine.mItemCount - 1) {
                        rawCalculatedHeight += accumulatedRoundError;
                        accumulatedRoundError = 0;
                    }
                    int newHeight = Math.round(rawCalculatedHeight);
                    if (newHeight < lp.minHeight) {
                        // Need to invoke this method again like the case flex direction is vertical
                        needsReshrink = true;
                        newHeight = lp.minHeight;
                        mChildrenFrozen[childIndex] = true;
                        flexLine.mTotalFlexShrink -= lp.flexShrink;
                    } else {
                        accumulatedRoundError += (rawCalculatedHeight - newHeight);
                        if (accumulatedRoundError > 1.0) {
                            newHeight += 1;
                            accumulatedRoundError -= 1;
                        } else if (accumulatedRoundError < -1.0) {
                            newHeight -= 1;
                            accumulatedRoundError += 1;
                        }
                    }
                    child.measureComponent(View.MeasureSpec.makeMeasureSpec(child.getComMeasuredWidth(),
                            View.MeasureSpec.EXACTLY),
                            View.MeasureSpec.makeMeasureSpec(newHeight, View.MeasureSpec.EXACTLY));
                }
                flexLine.mMainSize += child.getComMeasuredHeight() + lp.mLayoutMarginTop + lp.mLayoutMarginBottom;
            }
            childIndex++;
        }

        if (needsReshrink && sizeBeforeShrink != flexLine.mMainSize) {
            // Re-invoke the method with the same startIndex to distribute the negative free space
            // that wasn't fully distributed (because some views length were not enough)
            shrinkFlexItems(flexLine, flexDirection, maxMainSize, paddingAlongMainAxis, startIndex);
        }
        return childIndex;
    }

    private int expandFlexItems(FlexLine flexLine, @FlexDirection int flexDirection,
                                int maxMainSize, int paddingAlongMainAxis, int startIndex) {
        int childIndex = startIndex;
        if (flexLine.mTotalFlexGrow <= 0 || maxMainSize < flexLine.mMainSize) {
            childIndex += flexLine.mItemCount;
            return childIndex;
        }
        int sizeBeforeExpand = flexLine.mMainSize;
        boolean needsReexpand = false;
        float unitSpace = (maxMainSize - flexLine.mMainSize) / flexLine.mTotalFlexGrow;
        flexLine.mMainSize = paddingAlongMainAxis + flexLine.mDividerLengthInMainSize;
        float accumulatedRoundError = 0;
        for (int i = 0; i < flexLine.mItemCount; i++) {
            ViewBase child = getReorderedChildAt(childIndex);
            if (child == null) {
                continue;
//            } else if (child.getVisibility() == View.GONE) {
//            } else if (!child.isVisible()) {
            } else if (child.getVisibility() == ViewBaseCommon.GONE) {
                childIndex++;
                continue;
            }
            Params lp = (Params) child.getComLayoutParams();
            if (isMainAxisDirectionHorizontal(flexDirection)) {
                // The direction of the main axis is horizontal
                if (!mChildrenFrozen[childIndex]) {
                    float rawCalculatedWidth = child.getComMeasuredWidth() + unitSpace * lp.flexGrow;
                    if (i == flexLine.mItemCount - 1) {
                        rawCalculatedWidth += accumulatedRoundError;
                        accumulatedRoundError = 0;
                    }
                    int newWidth = Math.round(rawCalculatedWidth);
                    if (newWidth > lp.maxWidth) {
                        // This means the child can't expand beyond the value of the maxWidth attribute.
                        // To adjust the flex line length to the size of maxMainSize, remaining
                        // positive free space needs to be re-distributed to other flex items
                        // (children views). In that case, invoke this method again with the same
                        // startIndex.
                        needsReexpand = true;
                        newWidth = lp.maxWidth;
                        mChildrenFrozen[childIndex] = true;
                        flexLine.mTotalFlexGrow -= lp.flexGrow;
                    } else {
                        accumulatedRoundError += (rawCalculatedWidth - newWidth);
                        if (accumulatedRoundError > 1.0) {
                            newWidth += 1;
                            accumulatedRoundError -= 1.0;
                        } else if (accumulatedRoundError < -1.0) {
                            newWidth -= 1;
                            accumulatedRoundError += 1.0;
                        }
                    }
                    child.measureComponent(View.MeasureSpec.makeMeasureSpec(newWidth, View.MeasureSpec.EXACTLY),
                            View.MeasureSpec
                                    .makeMeasureSpec(child.getComMeasuredHeight(),
                                            View.MeasureSpec.EXACTLY));
                }
                flexLine.mMainSize += child.getComMeasuredWidth() + lp.mLayoutMarginLeft + lp.mLayoutMarginRight;
            } else {
                // The direction of the main axis is vertical
                if (!mChildrenFrozen[childIndex]) {
                    float rawCalculatedHeight = child.getComMeasuredHeight() + unitSpace * lp.flexGrow;
                    if (i == flexLine.mItemCount - 1) {
                        rawCalculatedHeight += accumulatedRoundError;
                        accumulatedRoundError = 0;
                    }
                    int newHeight = Math.round(rawCalculatedHeight);
                    if (newHeight > lp.maxHeight) {
                        // This means the child can't expand beyond the value of the maxHeight
                        // attribute.
                        // To adjust the flex line length to the size of maxMainSize, remaining
                        // positive free space needs to be re-distributed to other flex items
                        // (children views). In that case, invoke this method again with the same
                        // startIndex.
                        needsReexpand = true;
                        newHeight = lp.maxHeight;
                        mChildrenFrozen[childIndex] = true;
                        flexLine.mTotalFlexGrow -= lp.flexGrow;
                    } else {
                        accumulatedRoundError += (rawCalculatedHeight - newHeight);
                        if (accumulatedRoundError > 1.0) {
                            newHeight += 1;
                            accumulatedRoundError -= 1.0;
                        } else if (accumulatedRoundError < -1.0) {
                            newHeight -= 1;
                            accumulatedRoundError += 1.0;
                        }
                    }
                    child.measureComponent(View.MeasureSpec.makeMeasureSpec(child.getComMeasuredWidth(),
                            View.MeasureSpec.EXACTLY),
                            View.MeasureSpec.makeMeasureSpec(newHeight, View.MeasureSpec.EXACTLY));
                }
                flexLine.mMainSize += child.getComMeasuredHeight() + lp.mLayoutMarginTop + lp.mLayoutMarginBottom;
            }
            childIndex++;
        }

        if (needsReexpand && sizeBeforeExpand != flexLine.mMainSize) {
            // Re-invoke the method with the same startIndex to distribute the positive free space
            // that wasn't fully distributed (because of maximum length constraint)
            expandFlexItems(flexLine, flexDirection, maxMainSize, paddingAlongMainAxis, startIndex);
        }
        return childIndex;
    }


    private int getLargestMainSize() {
        int largestSize = Integer.MIN_VALUE;
        for (FlexLine flexLine : mFlexLines) {
            largestSize = Math.max(largestSize, flexLine.mMainSize);
        }
        return largestSize;
    }

    private boolean isWrapRequired(int mode, int maxSize, int currentLength, int childLength,
                                   Params lp, int childAbsoluteIndex, int childRelativeIndexInFlexLine) {
        if (mFlexWrap == FLEX_WRAP_NOWRAP) {
            return false;
        }
        if (lp.wrapBefore) {
            return true;
        }
        if (mode == View.MeasureSpec.UNSPECIFIED) {
            return false;
        }
        if (isMainAxisDirectionHorizontal(mFlexDirection)) {
            if (hasDividerBeforeChildAtAlongMainAxis(childAbsoluteIndex,
                    childRelativeIndexInFlexLine)) {
                childLength += mDividerVerticalWidth;
            }
            if ((mShowDividerVertical & SHOW_DIVIDER_END) > 0) {
                childLength += mDividerVerticalWidth;
            }
        } else {
            if (hasDividerBeforeChildAtAlongMainAxis(childAbsoluteIndex,
                    childRelativeIndexInFlexLine)) {
                childLength += mDividerHorizontalHeight;
            }
            if ((mShowDividerHorizontal & SHOW_DIVIDER_END) > 0) {
                childLength += mDividerHorizontalHeight;
            }
        }
        return maxSize < currentLength + childLength;
    }

    private boolean hasDividerBeforeChildAtAlongMainAxis(int childAbsoluteIndex,
                                                         int childRelativeIndexInFlexLine) {
        if (allViewsAreGoneBefore(childAbsoluteIndex, childRelativeIndexInFlexLine)) {
            if (isMainAxisDirectionHorizontal(mFlexDirection)) {
                return (mShowDividerVertical & SHOW_DIVIDER_BEGINNING) != 0;
            } else {
                return (mShowDividerHorizontal & SHOW_DIVIDER_BEGINNING) != 0;
            }
        } else {
            if (isMainAxisDirectionHorizontal(mFlexDirection)) {
                return (mShowDividerVertical & SHOW_DIVIDER_MIDDLE) != 0;
            } else {
                return (mShowDividerHorizontal & SHOW_DIVIDER_MIDDLE) != 0;
            }
        }
    }

    private boolean allViewsAreGoneBefore(int childAbsoluteIndex,
                                          int childRelativeIndexInFlexLine) {
        for (int i = 1; i <= childRelativeIndexInFlexLine; i++) {
            ViewBase view = getReorderedChildAt(childAbsoluteIndex - i);
//            if (view != null && view.getVisibility() != View.GONE) {
//            if (view != null && view.isVisible()) {
            if (view != null && view.getVisibility() != ViewBaseCommon.GONE) {
                return false;
            }
        }
        return true;
    }

    private void checkSizeConstraints(ViewBase view) {
        boolean needsMeasure = false;
        Params lp = (Params) view.getComLayoutParams();
        int childWidth = view.getComMeasuredWidth();
        int childHeight = view.getComMeasuredHeight();

        if (view.getComMeasuredWidth() < lp.minWidth) {
            needsMeasure = true;
            childWidth = lp.minWidth;
        } else if (view.getComMeasuredWidth() > lp.maxWidth) {
            needsMeasure = true;
            childWidth = lp.maxWidth;
        }

        if (childHeight < lp.minHeight) {
            needsMeasure = true;
            childHeight = lp.minHeight;
        } else if (childHeight > lp.maxHeight) {
            needsMeasure = true;
            childHeight = lp.maxHeight;
        }
        if (needsMeasure) {
            view.measureComponent(View.MeasureSpec.makeMeasureSpec(childWidth, View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(childHeight, View.MeasureSpec.EXACTLY));
        }
    }

    private void addFlexLineIfLastFlexItem(int childIndex, int childCount, FlexLine flexLine) {
        if (childIndex == childCount - 1 && flexLine.mItemCount != 0) {
            // Add the flex line if this item is the last item
            addFlexLine(flexLine);
        }
    }

    private void addFlexLine(FlexLine flexLine) {
        // The size of the end divider isn't added until the flexLine is added to the flex container
        // take the divider width (or height) into account when adding the flex line.
        if (isMainAxisDirectionHorizontal(mFlexDirection)) {
            if ((mShowDividerVertical & SHOW_DIVIDER_END) > 0) {
                flexLine.mMainSize += mDividerVerticalWidth;
                flexLine.mDividerLengthInMainSize += mDividerVerticalWidth;
            }
        } else {
            if ((mShowDividerHorizontal & SHOW_DIVIDER_END) > 0) {
                flexLine.mMainSize += mDividerHorizontalHeight;
                flexLine.mDividerLengthInMainSize += mDividerHorizontalHeight;
            }
        }
        mFlexLines.add(flexLine);
    }

    private boolean isMainAxisDirectionHorizontal(@FlexDirection int flexDirection) {
        return flexDirection == FLEX_DIRECTION_ROW
                || flexDirection == FLEX_DIRECTION_ROW_REVERSE;
    }

    public ViewBase getReorderedChildAt(int index) {
        if (index < 0 || index >= mReorderedIndices.length) {
            return null;
        }
//        return getChildAt(mReorderedIndices[index]);
        return mSubViews.get(mReorderedIndices[index]);
    }

    private int[] createReorderedIndices() {
//        int childCount = getChildCount();
//        List<Order> orders = createOrders(childCount);
        int childCount = mSubViews.size();
        List<Order> orders = createOrders(childCount);
        return sortOrdersIntoReorderedIndices(childCount, orders);
    }

    private int[] sortOrdersIntoReorderedIndices(int childCount, List<Order> orders) {
        Collections.sort(orders);
        if (mOrderCache == null) {
            mOrderCache = new SparseIntArray(childCount);
        }
        mOrderCache.clear();
        int[] reorderedIndices = new int[childCount];
        int i = 0;
        for (Order order : orders) {
            reorderedIndices[i] = order.index;
            mOrderCache.append(i, order.order);
            i++;
        }
        return reorderedIndices;
    }

    @NonNull
    private List<Order> createOrders(int childCount) {
        List<Order> orders = new ArrayList<>();
        for (int i = 0; i < childCount; i++) {
            ViewBase child = mSubViews.get(i);
            Params params = (Params) child.getComLayoutParams();
            Order order = new Order();
            order.order = params.order;
            order.index = i;
            orders.add(order);
        }
        return orders;
    }

    private boolean isOrderChangedFromLastMeasurement() {
//        int childCount = getChildCount();
        int childCount = mSubViews.size();
        if (mOrderCache == null) {
            mOrderCache = new SparseIntArray(childCount);
        }
        if (mOrderCache.size() != childCount) {
            return true;
        }
        for (int i = 0; i < childCount; i++) {
//            View view = getChildAt(i);
            ViewBase view = mSubViews.get(i);
            if (view == null) {
                continue;
            }
            Params lp = (Params) view.getComLayoutParams();
            if (lp.order != mOrderCache.get(i)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onComLayout(boolean changed, int left, int top, int right, int bottom) {
//        Log.d(TAG, "onComLayout: changed:" + changed + "  left:" + left + "  top:" + top + "  right:" + right + "  bottom:" + bottom);
//        int layoutDirection = ViewCompat.getLayoutDirection(this);
        boolean isRtl = false;
        switch (mFlexDirection) {
            case FLEX_DIRECTION_ROW:
//                isRtl = layoutDirection == ViewCompat.LAYOUT_DIRECTION_RTL;
                layoutHorizontal(isRtl, left, top, right, bottom);
                break;
            case FLEX_DIRECTION_ROW_REVERSE:
//                isRtl = layoutDirection != ViewCompat.LAYOUT_DIRECTION_RTL;
                isRtl = true;
                layoutHorizontal(isRtl, left, top, right, bottom);
                break;
            case FLEX_DIRECTION_COLUMN:
//                isRtl = layoutDirection == ViewCompat.LAYOUT_DIRECTION_RTL;
                if (mFlexWrap == FLEX_WRAP_WRAP_REVERSE) {
                    isRtl = !isRtl;
                }
                layoutVertical(isRtl, false, left, top, right, bottom);
                break;
            case FLEX_DIRECTION_COLUMN_REVERSE:
//                isRtl = layoutDirection == ViewCompat.LAYOUT_DIRECTION_RTL;
                if (mFlexWrap == FLEX_WRAP_WRAP_REVERSE) {
                    isRtl = !isRtl;
                }
                layoutVertical(isRtl, true, left, top, right, bottom);
                break;
            default:
                throw new IllegalStateException("Invalid flex direction is set: " + mFlexDirection);
        }
    }

    private void layoutVertical(boolean isRtl, boolean fromBottomToTop, int left, int top,
                                int right, int bottom) {
//        Log.d(TAG, "layoutVertical id:" + this.getId());
        int paddingTop = getComPaddingTop();
        int paddingBottom = getComPaddingBottom();

        int paddingRight = getComPaddingRight();
        int childLeft = left + getComPaddingLeft();
//        int childLeft = getComPaddingLeft();
        int currentViewIndex = 0;

        int width = right - left;
        int height = bottom - top;
        // childRight is used if the mFlexWrap is FLEX_WRAP_WRAP_REVERSE otherwise
        // childLeft is used to align the horizontal position of the children views.
        int childRight = left + width - paddingRight;
//        int childRight = width - paddingRight;

        // Use float to reduce the round error that may happen in when justifyContent ==
        // SPACE_BETWEEN or SPACE_AROUND
        float childTop;

        // Used only for if the direction is from bottom to top
        float childBottom;

        for (int i = 0, size = mFlexLines.size(); i < size; i++) {
            FlexLine flexLine = mFlexLines.get(i);
            if (hasDividerBeforeFlexLine(i)) {
                childLeft += mDividerVerticalWidth;
                childRight -= mDividerVerticalWidth;
            }
            float spaceBetweenItem = 0f;
            switch (mJustifyContent) {
                case JUSTIFY_CONTENT_FLEX_START:
                    childTop = top + paddingTop;
                    childBottom = top + height - paddingBottom;
//                    childTop = paddingTop;
//                    childBottom = height - paddingBottom;
                    break;
                case JUSTIFY_CONTENT_FLEX_END:
                    childTop = top + height - flexLine.mMainSize + paddingBottom;
                    childBottom = top + flexLine.mMainSize - paddingTop;
//                    childTop = height - flexLine.mMainSize + paddingBottom;
//                    childBottom = flexLine.mMainSize - paddingTop;
                    break;
                case JUSTIFY_CONTENT_CENTER:
                    childTop = top + paddingTop + (height - flexLine.mMainSize) / 2f;
                    childBottom = top + height - paddingBottom - (height - flexLine.mMainSize) / 2f;
//                    childTop = paddingTop + (height - flexLine.mMainSize) / 2f;
//                    childBottom = height - paddingBottom - (height - flexLine.mMainSize) / 2f;
                    break;
                case JUSTIFY_CONTENT_SPACE_AROUND:
                    if (flexLine.mItemCount != 0) {
                        spaceBetweenItem = (height - flexLine.mMainSize)
                                / (float) flexLine.mItemCount;
                    }
                    childTop = top + paddingTop + spaceBetweenItem / 2f;
                    childBottom = top + height - paddingBottom - spaceBetweenItem / 2f;
//                    childTop = paddingTop + spaceBetweenItem / 2f;
//                    childBottom = height - paddingBottom - spaceBetweenItem / 2f;
                    break;
                case JUSTIFY_CONTENT_SPACE_BETWEEN:
                    childTop = top + paddingTop;
//                    childTop = paddingTop;
                    float denominator = flexLine.mItemCount != 1 ? flexLine.mItemCount - 1 : 1f;
                    spaceBetweenItem = (height - flexLine.mMainSize) / denominator;
                    childBottom = top + height - paddingBottom;
//                    childBottom = height - paddingBottom;
                    break;
                default:
                    throw new IllegalStateException(
                            "Invalid justifyContent is set: " + mJustifyContent);
            }
            spaceBetweenItem = Math.max(spaceBetweenItem, 0);

            for (int j = 0; j < flexLine.mItemCount; j++) {
                ViewBase child = getReorderedChildAt(currentViewIndex);
                if (child == null) {
                    continue;
//                } else if (child.getVisibility() == View.GONE) {
//                } else if (!child.isVisible()) {
                } else if (child.getVisibility() == ViewBaseCommon.GONE) {
                    currentViewIndex++;
                    continue;
                }
                Params lp = ((Params) child.getComLayoutParams());
                childTop += lp.mLayoutMarginTop;
                childBottom -= lp.mLayoutMarginBottom;
                if (hasDividerBeforeChildAtAlongMainAxis(currentViewIndex, j)) {
                    childTop += mDividerHorizontalHeight;
                    childBottom -= mDividerHorizontalHeight;
                }
                if (isRtl) {
                    if (fromBottomToTop) {
                        layoutSingleChildVertical(child, flexLine, true, mAlignItems,
                                childRight - child.getComMeasuredWidth(),
                                Math.round(childBottom) - child.getComMeasuredHeight(), childRight,
                                Math.round(childBottom));
                    } else {
                        layoutSingleChildVertical(child, flexLine, true, mAlignItems,
                                childRight - child.getComMeasuredWidth(), Math.round(childTop),
                                childRight, Math.round(childTop) + child.getComMeasuredHeight());
                    }
                } else {
                    if (fromBottomToTop) {
                        layoutSingleChildVertical(child, flexLine, false, mAlignItems,
                                childLeft, Math.round(childBottom) - child.getComMeasuredHeight(),
                                childLeft + child.getComMeasuredWidth(), Math.round(childBottom));
                    } else {
//                        Log.d(TAG, "layout vertical child:" + child.getId());
                        layoutSingleChildVertical(child, flexLine, false, mAlignItems,
                                childLeft, Math.round(childTop),
                                childLeft + child.getComMeasuredWidth(),
                                Math.round(childTop) + child.getComMeasuredHeight());
                    }
                }
                childTop += child.getComMeasuredHeight() + spaceBetweenItem + lp.mLayoutMarginBottom;
                childBottom -= child.getComMeasuredHeight() + spaceBetweenItem + lp.mLayoutMarginTop;
                currentViewIndex++;

//                flexLine.mLeft = Math.min(flexLine.mLeft, child.getLeft() - lp.mLayoutMarginLeft);
//                flexLine.mTop = Math.min(flexLine.mTop, child.getTop() - lp.mLayoutMarginTop);
//                flexLine.mRight = Math.max(flexLine.mRight, child.getRight() + lp.mLayoutMarginRight);
//                flexLine.mBottom = Math.max(flexLine.mBottom, child.getBottom() + lp.mLayoutMarginBottom);
            }
            childLeft += flexLine.mCrossSize;
            childRight -= flexLine.mCrossSize;
        }
    }

    private void layoutSingleChildVertical(ViewBase view, FlexLine flexLine, boolean isRtl,
                                           int alignItems, int left, int top, int right, int bottom) {
//        Log.d(TAG, "layoutSingleChildVertical left:" + left + "  top:" + top);
        Params lp = (Params) view.getComLayoutParams();
        if (lp.alignSelf != Params.ALIGN_SELF_AUTO) {
            // Expecting the values for alignItems and alignSelf match except for ALIGN_SELF_AUTO.
            // Assigning the alignSelf value as alignItems should work.
            alignItems = lp.alignSelf;
        }
        int crossSize = flexLine.mCrossSize;
        switch (alignItems) {
            case ALIGN_ITEMS_FLEX_START: // Intentional fall through
            case ALIGN_ITEMS_STRETCH: // Intentional fall through
            case ALIGN_ITEMS_BASELINE:
                if (!isRtl) {
                    view.comLayout(left + lp.mLayoutMarginLeft, top, right + lp.mLayoutMarginLeft, bottom);
                } else {
                    view.comLayout(left - lp.mLayoutMarginRight, top, right - lp.mLayoutMarginRight, bottom);
                }
                break;
            case ALIGN_ITEMS_FLEX_END:
                if (!isRtl) {
                    view.comLayout(left + crossSize - view.getComMeasuredWidth() - lp.mLayoutMarginRight,
                            top, right + crossSize - view.getComMeasuredWidth() - lp.mLayoutMarginRight,
                            bottom);
                } else {
                    // If the flexWrap == FLEX_WRAP_WRAP_REVERSE, the direction of the
                    // flexEnd is flipped (from left to right).
                    view.comLayout(left - crossSize + view.getComMeasuredWidth() + lp.mLayoutMarginLeft, top,
                            right - crossSize + view.getComMeasuredWidth() + lp.mLayoutMarginLeft,
                            bottom);
                }
                break;
            case ALIGN_ITEMS_CENTER:
                int leftFromCrossAxis = (crossSize - view.getComMeasuredWidth()) / 2;
                if (!isRtl) {
                    view.comLayout(left + leftFromCrossAxis + lp.mLayoutMarginLeft - lp.mLayoutMarginRight,
                            top, right + leftFromCrossAxis + lp.mLayoutMarginLeft - lp.mLayoutMarginRight,
                            bottom);
                } else {
                    view.comLayout(left - leftFromCrossAxis + lp.mLayoutMarginLeft - lp.mLayoutMarginRight,
                            top, right - leftFromCrossAxis + lp.mLayoutMarginLeft - lp.mLayoutMarginRight,
                            bottom);
                }
                break;
        }
    }

    private void layoutHorizontal(boolean isRtl, int left, int top, int right, int bottom) {
        int paddingLeft = getComPaddingLeft();
        int paddingRight = getComPaddingRight();
        // Use float to reduce the round error that may happen in when justifyContent ==
        // SPACE_BETWEEN or SPACE_AROUND
        float childLeft;
        int currentViewIndex = 0;

        int height = bottom - top;
        int width = right - left;
        // childBottom is used if the mFlexWrap is FLEX_WRAP_WRAP_REVERSE otherwise
        // childTop is used to align the vertical position of the children views.
//        int childBottom = height - getComPaddingBottom();
        int childBottom = bottom - getComPaddingBottom();
//        int childTop = getComPaddingTop();
        int childTop = top + getComPaddingTop();

        // Used only for RTL layout
        // Use float to reduce the round error that may happen in when justifyContent ==
        // SPACE_BETWEEN or SPACE_AROUND
        float childRight;
        for (int i = 0, size = mFlexLines.size(); i < size; i++) {
            FlexLine flexLine = mFlexLines.get(i);
            if (hasDividerBeforeFlexLine(i)) {
                childBottom -= mDividerHorizontalHeight;
                childTop += mDividerHorizontalHeight;
            }
            float spaceBetweenItem = 0f;
            switch (mJustifyContent) {
                case JUSTIFY_CONTENT_FLEX_START:
                    childLeft = left + paddingLeft;
                    childRight = right - paddingRight;
//                    childLeft = paddingLeft;
//                    childRight = width - paddingRight;
                    break;
                case JUSTIFY_CONTENT_FLEX_END:
                    childLeft = left + width - flexLine.mMainSize + paddingRight;
                    childRight = left + flexLine.mMainSize - paddingLeft;
//                    childLeft = width - flexLine.mMainSize + paddingRight;
//                    childRight = flexLine.mMainSize - paddingLeft;
                    break;
                case JUSTIFY_CONTENT_CENTER:
                    childLeft = left + paddingLeft + (width - flexLine.mMainSize) / 2f;
                    childRight = left + width - paddingRight - (width - flexLine.mMainSize) / 2f;

//                    childLeft = paddingLeft + (width - flexLine.mMainSize) / 2f;
//                    childRight = width - paddingRight - (width - flexLine.mMainSize) / 2f;
                    break;
                case JUSTIFY_CONTENT_SPACE_AROUND:
                    if (flexLine.mItemCount != 0) {
                        spaceBetweenItem = (width - flexLine.mMainSize)
                                / (float) flexLine.mItemCount;
                    }
                    childLeft = left + paddingLeft + spaceBetweenItem / 2f;
                    childRight = left + width - paddingRight - spaceBetweenItem / 2f;
//                    childLeft = paddingLeft + spaceBetweenItem / 2f;
//                    childRight = width - paddingRight - spaceBetweenItem / 2f;
                    break;
                case JUSTIFY_CONTENT_SPACE_BETWEEN:
                    childLeft = left + paddingLeft;
//                    childLeft = paddingLeft;
                    float denominator = flexLine.mItemCount != 1 ? flexLine.mItemCount - 1 : 1f;
                    spaceBetweenItem = (width - flexLine.mMainSize) / denominator;
//                    childRight = width - paddingRight;
                    childRight = left + width - paddingRight;
                    break;
                default:
                    throw new IllegalStateException(
                            "Invalid justifyContent is set: " + mJustifyContent);
            }
            spaceBetweenItem = Math.max(spaceBetweenItem, 0);

            for (int j = 0; j < flexLine.mItemCount; j++) {
                ViewBase child = getReorderedChildAt(currentViewIndex);
                if (child == null) {
                    continue;
//                } else if (child.getVisibility() == View.GONE) {
//                } else if (!child.isVisible()) {
                } else if (child.getVisibility() == ViewBaseCommon.GONE) {
                    currentViewIndex++;
                    continue;
                }
                Params lp = ((Params) child.getComLayoutParams());
                childLeft += lp.mLayoutMarginLeft;
                childRight -= lp.mLayoutMarginRight;
                if (hasDividerBeforeChildAtAlongMainAxis(currentViewIndex, j)) {
                    childLeft += mDividerVerticalWidth;
                    childRight -= mDividerVerticalWidth;
                }

                if (mFlexWrap == FLEX_WRAP_WRAP_REVERSE) {
                    if (isRtl) {
                        layoutSingleChildHorizontal(child, flexLine, mFlexWrap, mAlignItems,
                                Math.round(childRight) - child.getComMeasuredWidth(),
                                childBottom - child.getComMeasuredHeight(), Math.round(childRight),
                                childBottom);
                    } else {
                        layoutSingleChildHorizontal(child, flexLine, mFlexWrap, mAlignItems,
                                Math.round(childLeft), childBottom - child.getComMeasuredHeight(),
                                Math.round(childLeft) + child.getComMeasuredWidth(),
                                childBottom);
                    }
                } else {
                    if (isRtl) {
                        layoutSingleChildHorizontal(child, flexLine, mFlexWrap, mAlignItems,
                                Math.round(childRight) - child.getComMeasuredWidth(), childTop,
                                Math.round(childRight), childTop + child.getComMeasuredHeight());
                    } else {
//                        Log.d(TAG, "layout child id:" + child.getId());
                        layoutSingleChildHorizontal(child, flexLine, mFlexWrap, mAlignItems,
                                Math.round(childLeft), childTop,
                                Math.round(childLeft) + child.getComMeasuredWidth(),
                                childTop + child.getComMeasuredHeight());
                    }
                }
                childLeft += child.getComMeasuredWidth() + spaceBetweenItem + lp.mLayoutMarginRight;
                childRight -= child.getComMeasuredWidth() + spaceBetweenItem + lp.mLayoutMarginLeft;
                currentViewIndex++;

//                flexLine.mLeft = Math.min(flexLine.mLeft, child.getLeft() - lp.mLayoutMarginLeft);
//                flexLine.mTop = Math.min(flexLine.mTop, child.getTop() - lp.mLayoutMarginTop);
//                flexLine.mRight = Math.max(flexLine.mRight, child.getRight() + lp.mLayoutMarginRight);
//                flexLine.mBottom = Math.max(flexLine.mBottom, child.getBottom() + lp.mLayoutMarginBottom);
            }
            childTop += flexLine.mCrossSize;
            childBottom -= flexLine.mCrossSize;
        }
    }

    private void layoutSingleChildHorizontal(ViewBase view, FlexLine flexLine, @FlexWrap int flexWrap,
                                             int alignItems, int left, int top, int right, int bottom) {
        Params lp = (Params) view.getComLayoutParams();
        if (lp.alignSelf != Params.ALIGN_SELF_AUTO) {
            // Expecting the values for alignItems and alignSelf match except for ALIGN_SELF_AUTO.
            // Assigning the alignSelf value as alignItems should work.
            alignItems = lp.alignSelf;
        }
        int crossSize = flexLine.mCrossSize;
        switch (alignItems) {
            case ALIGN_ITEMS_FLEX_START: // Intentional fall through
            case ALIGN_ITEMS_STRETCH:
                if (flexWrap != FLEX_WRAP_WRAP_REVERSE) {
//                    Log.d(TAG, "layoutSingleChildHorizontal  left:" + left + "  top:" + top + lp.mLayoutMarginTop + "  right:" + right + "  bottom:" + bottom + lp.mLayoutMarginTop) ;
                    view.comLayout(left, top + lp.mLayoutMarginTop, right, bottom + lp.mLayoutMarginTop);
                } else {
                    view.comLayout(left, top - lp.mLayoutMarginBottom, right, bottom - lp.mLayoutMarginBottom);
                }
                break;
            case ALIGN_ITEMS_BASELINE:
                if (flexWrap != FLEX_WRAP_WRAP_REVERSE) {
                    int marginTop = flexLine.mMaxBaseline - view.getComBaseline();
                    marginTop = Math.max(marginTop, lp.mLayoutMarginTop);
                    view.comLayout(left, top + marginTop, right, bottom + marginTop);
                } else {
                    int marginBottom = flexLine.mMaxBaseline - view.getComMeasuredHeight() + view
                            .getComBaseline();
                    marginBottom = Math.max(marginBottom, lp.mLayoutMarginBottom);
                    view.comLayout(left, top - marginBottom, right, bottom - marginBottom);
                }
                break;
            case ALIGN_ITEMS_FLEX_END:
                if (flexWrap != FLEX_WRAP_WRAP_REVERSE) {
                    view.comLayout(left,
                            top + crossSize - view.getComMeasuredHeight() - lp.mLayoutMarginBottom,
                            right, top + crossSize - lp.mLayoutMarginBottom);
                } else {
                    // If the flexWrap == FLEX_WRAP_WRAP_REVERSE, the direction of the
                    // flexEnd is flipped (from top to bottom).
                    view.comLayout(left, top - crossSize + view.getComMeasuredHeight() + lp.mLayoutMarginTop,
                            right, bottom - crossSize + view.getComMeasuredHeight() + lp.mLayoutMarginTop);
                }
                break;
            case ALIGN_ITEMS_CENTER:
                int topFromCrossAxis = (crossSize - view.getComMeasuredHeight()) / 2;
                if (flexWrap != FLEX_WRAP_WRAP_REVERSE) {
                    view.comLayout(left, top + topFromCrossAxis + lp.mLayoutMarginTop - lp.mLayoutMarginBottom,
                            right, top + topFromCrossAxis + view.getComMeasuredHeight() + lp.mLayoutMarginTop
                                    - lp.mLayoutMarginBottom);
                } else {
                    view.comLayout(left, top - topFromCrossAxis + lp.mLayoutMarginTop - lp.mLayoutMarginBottom,
                            right, top - topFromCrossAxis + view.getComMeasuredHeight() + lp.mLayoutMarginTop
                                    - lp.mLayoutMarginBottom);
                }
                break;
        }
    }

    @Override
    protected boolean setAttribute(int key, int value) {
        boolean ret = super.setAttribute(key, value);

        if (!ret) {
            ret = true;
            switch (key) {
                case StringBase.STR_ID_flexDirection:
                    mFlexDirection = value;
                    break;

                case StringBase.STR_ID_flexWrap:
                    mFlexWrap = value;
                    break;

                case StringBase.STR_ID_justifyContent:
                    mJustifyContent = value;
                    break;

                case StringBase.STR_ID_alignItems:
                    mAlignItems = value;
                    break;

                case StringBase.STR_ID_alignContent:
                    mAlignContent = value;
                    break;

                default:
                    ret = false;
                    break;
            }
        }

        return ret;
    }

    public static class Params extends Layout.Params {

        private static final int ORDER_DEFAULT = 1;

        private static final float FLEX_GROW_DEFAULT = 0f;

        private static final float FLEX_SHRINK_DEFAULT = 1f;

        public static final float FLEX_BASIS_PERCENT_DEFAULT = -1f;

        public static final int ALIGN_SELF_AUTO = -1;

        public static final int ALIGN_SELF_FLEX_START = ALIGN_ITEMS_FLEX_START;

        public static final int ALIGN_SELF_FLEX_END = ALIGN_ITEMS_FLEX_END;

        public static final int ALIGN_SELF_CENTER = ALIGN_ITEMS_CENTER;

        public static final int ALIGN_SELF_BASELINE = ALIGN_ITEMS_BASELINE;

        public static final int ALIGN_SELF_STRETCH = ALIGN_ITEMS_STRETCH;

        private static final int MAX_SIZE = Integer.MAX_VALUE & ViewCompat.MEASURED_SIZE_MASK;

        /**
         * This attribute can change the ordering of the children views are laid out.
         * By default, children are displayed and laid out in the same order as they appear in the
         * layout XML. If not specified, {@link #ORDER_DEFAULT} is set as a default value.
         */
        public int order = ORDER_DEFAULT;

        /**
         * This attribute determines how much this child will grow if positive free space is
         * distributed relative to the rest of other flex items included in the same flex line.
         * If not specified, {@link #FLEX_GROW_DEFAULT} is set as a default value.
         */
        public float flexGrow = FLEX_GROW_DEFAULT;

        /**
         * This attributes determines how much this child will shrink is negative free space is
         * distributed relative to the rest of other flex items included in the same flex line.
         * If not specified, {@link #FLEX_SHRINK_DEFAULT} is set as a default value.
         */
        public float flexShrink = FLEX_SHRINK_DEFAULT;

        /**
         * This attributes determines the alignment along the cross axis (perpendicular to the
         * main axis). The alignment in the same direction can be determined by the
         * {@link #mAlignItems} in the parent, but if this is set to other than
         * {@link #ALIGN_SELF_AUTO}, the cross axis alignment is overridden for this child.
         * The value needs to be one of the values in ({@link #ALIGN_SELF_AUTO},
         * {@link #ALIGN_SELF_STRETCH}, {@link #ALIGN_SELF_FLEX_START}, {@link
         * #ALIGN_SELF_FLEX_END}, {@link #ALIGN_SELF_CENTER}, or {@link #ALIGN_SELF_BASELINE}).
         * If not specified, {@link #ALIGN_SELF_AUTO} is set as a default value.
         */
        public int alignSelf = ALIGN_SELF_AUTO;

        /**
         * The initial flex item length in a fraction format relative to its parent.
         * The initial main size of this child View is trying to be expanded as the specified
         * fraction against the parent main size.
         * If this value is set, the length specified from layout_width
         * (or layout_height) is overridden by the calculated value from this attribute.
         * This attribute is only effective when the parent's MeasureSpec mode is
         * MeasureSpec.EXACTLY. The default value is {@link #FLEX_BASIS_PERCENT_DEFAULT}, which
         * means not set.
         */
        public float flexBasisPercent = FLEX_BASIS_PERCENT_DEFAULT;

        /**
         * This attribute determines the minimum width the child can shrink to.
         */
        public int minWidth;

        /**
         * This attribute determines the minimum height the child can shrink to.
         */
        public int minHeight;

        /**
         * This attribute determines the maximum width the child can expand to.
         */
        public int maxWidth = MAX_SIZE;

        /**
         * This attribute determines the maximum height the child can expand to.
         */
        public int maxHeight = MAX_SIZE;

        /**
         * This attribute forces a flex line wrapping. i.e. if this is set to {@code true} for a
         * flex item, the item will become the first item of the new flex line. (A wrapping happens
         * regardless of the flex items being processed in the the previous flex line)
         * This attribute is ignored if the flex_wrap attribute is set as nowrap.
         * The equivalent attribute isn't defined in the original CSS Flexible Box Module
         * specification, but having this attribute is useful for Android developers to flatten
         * the layouts when building a grid like layout or for a situation where developers want
         * to put a new flex line to make a semantic difference from the previous one, etc.
         */
        public boolean wrapBefore;

        public Params() {
            order = ORDER_DEFAULT;
            flexGrow = FLEX_GROW_DEFAULT;
            flexShrink = FLEX_SHRINK_DEFAULT;
            alignSelf = ALIGN_SELF_AUTO;
            flexBasisPercent = FLEX_BASIS_PERCENT_DEFAULT;
            minWidth = 0;
            minHeight = 0;
            maxWidth = MAX_SIZE;
            maxHeight = MAX_SIZE;
            wrapBefore = false;
        }

        public Params(Params source) {
            order = source.order;
            flexGrow = source.flexGrow;
            flexShrink = source.flexShrink;
            alignSelf = source.alignSelf;
            flexBasisPercent = source.flexBasisPercent;
            minWidth = source.minWidth;
            minHeight = source.minHeight;
            maxWidth = source.maxWidth;
            maxHeight = source.maxHeight;
            wrapBefore = source.wrapBefore;
        }

        @Override
        public boolean setAttribute(int key, int value) {
            boolean ret = super.setAttribute(key, value);

            if (!ret) {
                ret = true;
                switch (key) {
                    case StringBase.STR_ID_flexGrow:
//                        Log.d(TAG, "flexgrow:" + value);
                        flexGrow = value;
                        break;

                    default:
                        ret = false;
                }
            }

            return ret;
        }
    }

    public static class Builder implements ViewBase.IBuilder {
        @Override
        public ViewBase build(VafContext context, ViewCache viewCache) {
            return new FlexLayout(context, viewCache);
        }
    }

    private static class Order implements Comparable<Order> {

        /** {@link View}'s index */
        int index;

        /** order property in the Flexbox */
        int order;

        @Override
        public int compareTo(@NonNull Order another) {
            if (order != another.order) {
                return order - another.order;
            }
            return index - another.index;
        }

        @Override
        public String toString() {
            return "Order{" +
                    "order=" + order +
                    ", index=" + index +
                    '}';
        }
    }

}
