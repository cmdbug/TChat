/*
 * Copyright (C) 2013 Andreas Stuetz <andreas.stuetz@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.netease.nim.demo.common.ui.viewpager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.netease.nim.demo.R;
import com.netease.nim.demo.main.reminder.ReminderItem;
import com.netease.nim.demo.main.reminder.ReminderSettings;
import com.netease.nim.uikit.common.ui.drop.DropFake;
import com.netease.nim.uikit.common.ui.drop.DropManager;
import com.netease.nim.uikit.common.util.sys.ScreenUtil;

import java.util.Locale;

public class PagerSlidingTabStrip extends HorizontalScrollView implements OnPageChangeListener {

    // @formatter:off
    private static final int[] ATTRS = new int[]{android.R.attr.textSize, android.R.attr.textColor};

    // @formatter:on

    private LinearLayout.LayoutParams tabViewLayoutParams;

    private LinearLayout tabsContainer;

    private ViewPager pager;

    private int tabCount;

    private int currentPosition = 0;

    public int getCurrentPosition() {
        return currentPosition;
    }

    private float currentPositionOffset = 0f;

    private Paint rectPaint;

    private Paint dividerPaint;

    private int indicatorColor = 0xFF0888ff;

    private int underlineColor = getResources().getColor(R.color.skin_page_tab_underline_color);// 0xFFD9D9D9;

    private int dividerColor = 0x00000000;

//    private int checkedTextColor = R.color.color_blue_45C01A;
    private int checkedTextColor = R.color.color_blue_0888ff;//teng

    private int unCheckedTextColor = R.color.action_bar_tittle_color_9a9a9a;

    private int[] iconNormal = {
            R.drawable.weixin_normal,
            R.drawable.contact_list_normal,
            R.drawable.find_normal,
            R.drawable.profile_normal};
    private int[] iconPressed = {
            R.drawable.weixin_pressed,
            R.drawable.contact_list_pressed,
            R.drawable.find_pressed,
            R.drawable.profile_pressed};

    private boolean textAllCaps = true;

    private int scrollOffset = 52;

    private int indicatorHeight = 0;//指示线条高度3

    private int underlineHeight = 0;//未选中线条高度2

    private int dividerPadding = 12;

    private int tabPadding = 0;

    private int dividerWidth = 1;

    private int lastScrollX = 0;

    private Locale locale;

    private OnTabClickListener onTabClickListener = null;

    private OnTabDoubleTapListener onTabDoubleTapListener = null;

    private OnCustomTabListener onCustomTabListener = null;

    public PagerSlidingTabStrip(Context context) {
        this(context, null);
    }

    public PagerSlidingTabStrip(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PagerSlidingTabStrip(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        setFillViewport(true);
        setWillNotDraw(false);

        tabsContainer = new LinearLayout(context);
        tabsContainer.setOrientation(LinearLayout.HORIZONTAL);
        tabsContainer.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        addView(tabsContainer);

        DisplayMetrics dm = getResources().getDisplayMetrics();

        scrollOffset = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, scrollOffset, dm);
        indicatorHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, indicatorHeight, dm);
        underlineHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, underlineHeight, dm);
        dividerPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dividerPadding, dm);
        tabPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, tabPadding, dm);
        dividerWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dividerWidth, dm);

        // get system attrs (android:textSize and android:textColor)

        TypedArray a = context.obtainStyledAttributes(attrs, ATTRS);

        a.recycle();

        // get custom attrs

        a = context.obtainStyledAttributes(attrs, R.styleable.PagerSlidingTabStrip);

        indicatorColor = a.getColor(R.styleable.PagerSlidingTabStrip_pstsIndicatorColor, indicatorColor);
        underlineColor = a.getColor(R.styleable.PagerSlidingTabStrip_pstsUnderlineColor, underlineColor);
        dividerColor = a.getColor(R.styleable.PagerSlidingTabStrip_pstsDividerColor, dividerColor);

        checkedTextColor = a.getResourceId(R.styleable.TwoTextView_ttLeftTextColor, checkedTextColor);
        unCheckedTextColor = a.getResourceId(R.styleable.TwoTextView_ttLeftTextColor, unCheckedTextColor);
        indicatorHeight = a.getDimensionPixelSize(R.styleable.PagerSlidingTabStrip_pstsIndicatorHeight, indicatorHeight);
        underlineHeight = a.getDimensionPixelSize(R.styleable.PagerSlidingTabStrip_pstsUnderlineHeight, underlineHeight);
        dividerPadding = a.getDimensionPixelSize(R.styleable.PagerSlidingTabStrip_pstsDividerPadding, dividerPadding);
        tabPadding = a.getDimensionPixelSize(R.styleable.PagerSlidingTabStrip_pstsTabPaddingLeftRight, tabPadding);
        scrollOffset = a.getDimensionPixelSize(R.styleable.PagerSlidingTabStrip_pstsScrollOffset, scrollOffset);
        textAllCaps = a.getBoolean(R.styleable.PagerSlidingTabStrip_pstsTextAllCaps, textAllCaps);

        a.recycle();

        rectPaint = new Paint();
        rectPaint.setAntiAlias(true);
        rectPaint.setStyle(Style.FILL);

        dividerPaint = new Paint();
        dividerPaint.setAntiAlias(true);
        dividerPaint.setStrokeWidth(dividerWidth);

        tabViewLayoutParams = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1.0f);

        if (locale == null) {
            locale = getResources().getConfiguration().locale;
        }
    }

    //
    // OnPageChangeListener
    //

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        currentPosition = position;
        currentPositionOffset = positionOffset;
        scrollToChild(position, (int) (positionOffset * tabsContainer.getChildAt(position).getWidth()));

//        invalidate();//取消了底部的横条所以取消绘制
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (state == ViewPager.SCROLL_STATE_IDLE) {
            scrollToChild(pager.getCurrentItem(), 0);
        }
    }

    @Override
    public void onPageSelected(int position) {
        setChooseTabViewTextColorAndIconColor(position);
    }

    //
    // OnPageChangeListener
    //

    public void setViewPager(ViewPager pager) {
        this.pager = pager;

        if (pager.getAdapter() == null) {
            throw new IllegalStateException("ViewPager does not have adapter instance.");
        }

        notifyDataSetChanged();
    }

    public void notifyDataSetChanged() {

        tabsContainer.removeAllViews();

        tabCount = pager.getAdapter().getCount();

        for (int i = 0; i < tabCount; i++) {
            addTabView(i, pager.getAdapter().getPageTitle(i).toString());
        }

        // updateTabStyles();

        getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

            @SuppressWarnings("deprecation")
            @SuppressLint("NewApi")
            @Override
            public void onGlobalLayout() {

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }

                currentPosition = pager.getCurrentItem();
                setChooseTabViewTextColorAndIconColor(currentPosition);
                scrollToChild(currentPosition, 0);
            }
        });

    }

    /**
     * 着色
     *
     * @param drawable 图片
     * @param colors   颜色
     * @return 着色后的图片
     */
    public Drawable tintDrawable(Drawable drawable, ColorStateList colors) {
        Drawable wrappedDrawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTintList(wrappedDrawable, colors);
        return wrappedDrawable;
    }


    private void setChooseTabViewTextColorAndIconColor(int position) {
        int childCount = tabsContainer.getChildCount();
        LinearLayout tabView;
        TextView textView;
        ImageView imageView;
        for (int i = 0; i < childCount; ++i) {
            tabView = (LinearLayout) tabsContainer.getChildAt(i);
            textView = (TextView) tabView.findViewById(R.id.tab_title_label);
            imageView = (ImageView) tabView.findViewById(R.id.tab_title_icon);
            if (i == position) {
                textView.setTextColor(getResources().getColor(checkedTextColor));
                //着色teng
                imageView.setImageDrawable(tintDrawable(getResources().getDrawable(iconPressed[position]),
                        ColorStateList.valueOf(Color.rgb(8, 160, 255))));
//                imageView.setImageDrawable(getResources().getDrawable(iconPressed[position]));
            } else {
                textView.setTextColor(getResources().getColor(unCheckedTextColor));
                //teng
                imageView.setImageDrawable(getResources().getDrawable(iconNormal[i]));
            }
        }
    }

    private void addTabView(final int position, String title) {
        View tabView = null;
        boolean screenAdaptation = false;
        final LayoutInflater inflater = LayoutInflater.from(getContext());
        if (this.onCustomTabListener != null) {
            int tabResId = onCustomTabListener.getTabLayoutResId(position);
            if (tabResId != 0) {
                tabView = inflater.inflate(tabResId, null);
            } else {
                tabView = onCustomTabListener.getTabLayoutView(inflater, position);
            }
            screenAdaptation = onCustomTabListener.screenAdaptation();
        }
        if (tabView == null) {
            tabView = inflater.inflate(R.layout.tab_layout_main, null);
        }
        TextView titleTV = ((TextView) tabView.findViewById(R.id.tab_title_label));
        final boolean needAdaptation = ScreenUtil.density <= 1.5 && screenAdaptation;
        final Resources resources = getContext().getResources();
        if (titleTV != null) {
            titleTV.setTextSize(TypedValue.COMPLEX_UNIT_PX, needAdaptation ? resources.getDimensionPixelSize(R.dimen.text_size_11) : resources.getDimensionPixelSize(R.dimen.text_size_12));
            titleTV.setText(title);
        }

        final DropFake unreadTV = ((DropFake) tabView.findViewById(R.id.tab_new_msg_label));
        if (unreadTV != null) {
            unreadTV.setTouchListener(new DropFake.ITouchListener() {
                @Override
                public void onDown() {
                    DropManager.getInstance().setCurrentId(String.valueOf(position));
                    DropManager.getInstance().down(unreadTV, unreadTV.getText());
                }

                @Override
                public void onMove(float curX, float curY) {
                    DropManager.getInstance().move(curX, curY);
                }

                @Override
                public void onUp() {
                    DropManager.getInstance().up();
                }
            });

            //unreadTV.setTextSize(TypedValue.COMPLEX_UNIT_PX, needAdaptation ? resources.getDimensionPixelSize(R.dimen.text_size_9) : resources.getDimensionPixelSize(R.dimen.text_size_12));
        }
        addTab(position, tabView);
    }

    private void addTab(final int position, View tab) {
        tab.setFocusable(true);
        tab.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (pager.getCurrentItem() == position && onTabClickListener != null) {
                    onTabClickListener.onCurrentTabClicked(position);
                } else {
                    pager.setCurrentItem(position, false);
                }
            }
        });
        addTabDoubleTapListener(position, tab);
        tab.setPadding(tabPadding, 0, tabPadding, 0);
        tabsContainer.addView(tab, position, tabViewLayoutParams);
    }

    private void addTabDoubleTapListener(final int position, View tab) {
        final GestureDetector gd = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                if (onTabDoubleTapListener != null)
                    onTabDoubleTapListener.onCurrentTabDoubleTap(position);

                return true;
            }
        });

        tab.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gd.onTouchEvent(event);
            }
        });
    }

    public void updateTab(int index, ReminderItem item) {
        LinearLayout tabView = (LinearLayout) tabsContainer.getChildAt(index);
        ImageView indicatorView = (ImageView) tabView.findViewById(R.id.tab_new_indicator);
        final DropFake unreadTV = ((DropFake) tabView.findViewById(R.id.tab_new_msg_label));

        if (item == null || unreadTV == null || indicatorView == null) {
            unreadTV.setVisibility(View.GONE);
            indicatorView.setVisibility(View.GONE);
            return;
        }
        int unread = item.unread();
        boolean indicator = item.indicator();
        unreadTV.setVisibility(unread > 0 ? View.VISIBLE : View.GONE);
        indicatorView.setVisibility(indicator ? View.VISIBLE : View.GONE);
        if (unread > 0) {
            unreadTV.setText(String.valueOf(ReminderSettings.unreadMessageShowRule(unread)));
        }
    }

    private void scrollToChild(int position, int offset) {

        if (tabCount == 0) {
            return;
        }

        int newScrollX = tabsContainer.getChildAt(position).getLeft() + offset;

        if (position > 0 || offset > 0) {
            newScrollX -= scrollOffset;
        }

        if (newScrollX != lastScrollX) {
            lastScrollX = newScrollX;
            scrollTo(newScrollX, 0);
        }

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (isInEditMode() || tabCount == 0) {
            return;
        }

        final int height = getHeight();

        // draw underline

        rectPaint.setColor(underlineColor);
        canvas.drawRect(0, height - underlineHeight, tabsContainer.getWidth(), height, rectPaint);

        // draw indicator line
        rectPaint.setColor(indicatorColor);

        // default: line below current tab
        View currentTab = tabsContainer.getChildAt(currentPosition);
        float lineLeft = currentTab.getLeft();
        float lineRight = currentTab.getRight();

        // if there is an offset, start interpolating left and right coordinates between current and next tab
        if (currentPositionOffset > 0f && currentPosition < tabCount - 1) {

            View nextTab = tabsContainer.getChildAt(currentPosition + 1);
            final float nextTabLeft = nextTab.getLeft();
            final float nextTabRight = nextTab.getRight();

            lineLeft = (currentPositionOffset * nextTabLeft + (1f - currentPositionOffset) * lineLeft);
            lineRight = (currentPositionOffset * nextTabRight + (1f - currentPositionOffset) * lineRight);
        }

        canvas.drawRect(lineLeft, height - indicatorHeight, lineRight, height, rectPaint);

        // draw divider

        // dividerPaint.setColor(dividerColor);
        // for (int i = 0; i < tabCount - 1; i++) {
        // View tab = tabsContainer.getChildAt(i);
        // canvas.drawLine(tab.getRight(), dividerPadding, tab.getRight(), height - dividerPadding, dividerPaint);
        // }
    }

    public void setIndicatorColor(int indicatorColor) {
        this.indicatorColor = indicatorColor;
        invalidate();
    }

    public void setIndicatorColorResource(int resId) {
        this.indicatorColor = getResources().getColor(resId);
        invalidate();
    }

    public int getIndicatorColor() {
        return this.indicatorColor;
    }

    public void setIndicatorHeight(int indicatorLineHeightPx) {
        this.indicatorHeight = indicatorLineHeightPx;
        invalidate();
    }

    public int getIndicatorHeight() {
        return indicatorHeight;
    }

    public void setUnderlineColor(int underlineColor) {
        this.underlineColor = underlineColor;
        invalidate();
    }

    public void setUnderlineColorResource(int resId) {
        this.underlineColor = getResources().getColor(resId);
        invalidate();
    }

    public int getUnderlineColor() {
        return underlineColor;
    }

    public void setDividerColor(int dividerColor) {
        this.dividerColor = dividerColor;
        invalidate();
    }

    public void setDividerColorResource(int resId) {
        this.dividerColor = getResources().getColor(resId);
        invalidate();
    }

    public int getDividerColor() {
        return dividerColor;
    }

    public void setCheckedTextColorResource(int resId) {
        this.checkedTextColor = resId;
        invalidate();
    }

    public void setUnderlineHeight(int underlineHeightPx) {
        this.underlineHeight = underlineHeightPx;
        invalidate();
    }

    public int getUnderlineHeight() {
        return underlineHeight;
    }

    public void setDividerPadding(int dividerPaddingPx) {
        this.dividerPadding = dividerPaddingPx;
        invalidate();
    }

    public int getDividerPadding() {
        return dividerPadding;
    }

    public void setScrollOffset(int scrollOffsetPx) {
        this.scrollOffset = scrollOffsetPx;
        invalidate();
    }

    public int getScrollOffset() {
        return scrollOffset;
    }

    public boolean isTextAllCaps() {
        return textAllCaps;
    }

    public void setAllCaps(boolean textAllCaps) {
        this.textAllCaps = textAllCaps;
    }

    public int getTabPaddingLeftRight() {
        return tabPadding;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        // currentPosition = savedState.currentPosition;
        currentPosition = 0;
        requestLayout();
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState savedState = new SavedState(superState);
        savedState.currentPosition = currentPosition;
        return savedState;
    }

    static class SavedState extends BaseSavedState {

        int currentPosition;

        public SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            currentPosition = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(currentPosition);
        }

        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {

            @Override
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

    public void setOnTabClickListener(OnTabClickListener onTabClickListener) {
        this.onTabClickListener = onTabClickListener;
    }

    public void setOnTabDoubleTapListener(OnTabDoubleTapListener onTabDoubleTapListener) {
        this.onTabDoubleTapListener = onTabDoubleTapListener;
    }

    /**
     * must invoke before setViewPager
     *
     * @param onCustomTabListener
     */
    public void setOnCustomTabListener(OnCustomTabListener onCustomTabListener) {
        this.onCustomTabListener = onCustomTabListener;
    }

    /**
     * TAB 的点击监听
     */
    public interface OnTabClickListener {

        public void onCurrentTabClicked(int position);
    }

    /**
     * TAB 的双击监听
     */
    public interface OnTabDoubleTapListener {

        public void onCurrentTabDoubleTap(int position);
    }

    /**
     * 获取每个TAB的自定义布局
     */
    public static class OnCustomTabListener {

        /**
         * 需要自定义TAB的布局
         * 重写该方法，返回对应的layout id
         *
         * @param position
         * @return
         */
        public int getTabLayoutResId(int position) {
            return 0;
        }

        /**
         * 需要自定义TAB的布局
         * 重写该方法，直接返回对应的view
         *
         * @param inflater
         * @param position
         * @return
         */
        public View getTabLayoutView(LayoutInflater inflater, int position) {
            return null;
        }

        /**
         * 是否需要小屏幕适配,只在存在多个tab，且tab的布局较紧时才适配,现在只用于主界面
         *
         * @return
         */
        public boolean screenAdaptation() {
            return false;
        }
    }
}
