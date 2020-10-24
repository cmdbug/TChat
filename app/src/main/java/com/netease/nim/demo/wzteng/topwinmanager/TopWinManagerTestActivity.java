package com.netease.nim.demo.wzteng.topwinmanager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.jude.swipbackhelper.SwipeBackHelper;
import com.jude.swipbackhelper.SwipeBackLayoutTouchListener;
import com.jude.swipbackhelper.SwipeBackPage;
import com.jude.swipbackhelper.SwipeListener;
import com.netease.nim.demo.R;
import com.netease.nim.demo.wzteng.topwinmanager.floatwindow.FloatWindow;
import com.netease.nim.demo.wzteng.topwinmanager.floatwindow.MoveType;
import com.netease.nim.demo.wzteng.topwinmanager.floatwindow.PermissionListener;
import com.netease.nim.demo.wzteng.topwinmanager.floatwindow.ViewStateListener;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.common.util.sys.ScreenUtil;
import com.netease.nim.uikit.model.ToolBarOptions;
import com.yalantis.ucrop.util.ScreenUtils;

public class TopWinManagerTestActivity extends UI {

    //没有进行封装，只是学习相关的技巧

    SwipeBackPage swipeBackPage;
    SwipeListener swipeListener;

    SwipeBackLayoutTouchListener touchListener;

    private int ICON_WIDTH = 100;
    private int ICON_HEIGHT = 100;

    private int TIP_WIDTH = ScreenUtil.dip2px(150);
    private int TIP_HEIGHT = ScreenUtil.dip2px(150);

    private boolean startShowTip = false;
    private float curPercent;

    private int SCREEN_WIDTH;
    private int SCREEN_HEIGHT;

    private View tipView;
    private View iconView;

    private int curViewWidth = Integer.MAX_VALUE;
    private int curViewHeight = Integer.MAX_VALUE;

    private boolean permissionOk = true;

    private PopTipDialog popTipDialog;

    public static void start(Context context) {
        start(context, null);
    }

    private static void start(Context context, Intent extras) {
        Intent intent = new Intent();
        intent.setClass(context, TopWinManagerTestActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        if (extras != null) {
            intent.putExtras(extras);
        }
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topwinmanager);

        ToolBarOptions options = new ToolBarOptions();
        options.titleId = R.string.top_window_manager;
        setToolBar(R.id.toolbar, options);

        SCREEN_WIDTH = ScreenUtils.getScreenWidth(this);
        SCREEN_HEIGHT = ScreenUtils.getScreenHeight(this);

        initSwipeBackPage();

        //如果已经有就删除
        if (FloatWindow.get() != null) {
            FloatWindow.destroy();
        }

    }

    private void initSwipeBackPage() {
        SwipeBackHelper.onCreate(this);
        swipeBackPage = SwipeBackHelper.getCurrentPage(this);
        swipeBackPage.setSwipeEdge(10)
                .setSwipeRelateEnable(true)
                .setSwipeRelateOffset(200);
        swipeListener = new SwipeListener() {
            @Override
            public void onScroll(float percent, int px) {
//                Log.d("wzt", "percent:" + percent + " ,px:" + px);
                startShowTip = percent >= 0.2f;
                curPercent = percent;
                if (percent <= 0 || px <= 0) {
                    //删除提示
                    if (popTipDialog != null && popTipDialog.isShowing()) {
                        popTipDialog.dismiss();
                    }
                }
            }

            @Override
            public void onEdgeTouch() {
//                Log.d("wzt", "onEdgeTouch");
//                buildAndShowIconFloat();
            }

            @Override
            public void onScrollToClose() {
//                Log.d("wzt", "onScrollToClose");
                //删除提示
                if (popTipDialog != null && popTipDialog.isShowing()) {
                    popTipDialog.dismiss();
                }
            }
        };
        swipeBackPage.getSwipeBackLayout().addSwipeListener(swipeListener);

        touchListener = new SwipeBackLayoutTouchListener() {
            @Override
            public void onTouchEvent(MotionEvent event) {
//                Log.d("wzt", "x:" + event.getX() + " ,y:" + event.getY());
                if (!permissionOk) {
                    return;
                }

                float x = event.getX();
                float y = event.getY();
                if (startShowTip) {
                    //显示提示
                    startShowTip(curPercent);
                } else {
                    //删除提示
                    if (popTipDialog != null && popTipDialog.isShowing()) {
                        popTipDialog.dismiss();
                    }
                }
                if (x > (SCREEN_WIDTH - curViewWidth) && y > (SCREEN_HEIGHT - curViewHeight)) {
                    if (event.getAction() == MotionEvent.ACTION_UP) {//范围内抬起
                        if (toShowBydistance(SCREEN_WIDTH, SCREEN_HEIGHT, x, y, curViewWidth, curViewHeight)) {
                            buildAndShowIconFloat();
                            return;
                        }
                    }
                    //在范围内变红
                    if (toShowBydistance(SCREEN_WIDTH, SCREEN_HEIGHT, x, y, curViewWidth, curViewHeight)) {
                        if (popTipDialog != null && popTipDialog.isShowing()) {
                            popTipDialog.setBackgroundColorByPosition(true);
                        }
                    } else {
                        if (popTipDialog != null && popTipDialog.isShowing()) {
                            popTipDialog.setBackgroundColorByPosition(false);
                        }
                    }
                } else {//速度太快会有问题，还是加上
                    if (popTipDialog != null && popTipDialog.isShowing()) {
                        popTipDialog.setBackgroundColorByPosition(false);
                    }
                }
                //抬起时立即消失
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (popTipDialog != null && popTipDialog.isShowing()) {
                        popTipDialog.dismiss();
                    }
                }
            }
        };
        swipeBackPage.getSwipeBackLayout().setTouchListener(touchListener);
    }

    private boolean toShowBydistance(int screenWidth, int screenHeight, float touchX, float touchY, float curViewWidth, float curViewHeight) {
        float touchDis = (float) Math.sqrt(Math.pow(screenWidth - touchX, 2) + Math.pow(screenHeight - touchY, 2));
        float viewDis = curViewWidth;
        return viewDis >= touchDis;
    }

    private void startShowTip(float percent) {//0.05-0.5
        if (percent < 0.05f) {
            //删除提示
            if (popTipDialog != null && popTipDialog.isShowing()) {
                popTipDialog.dismiss();
            }
            return;
        }
        if (percent > 0.5f) {
            //更新大小，固定值
            if (popTipDialog != null) {
                if (!popTipDialog.isShowing()) {
                    popTipDialog.show();
                }
                popTipDialog.changeSize(TIP_WIDTH);
            }
            return;
        }

        float p = percent + 0.5f;
        if (popTipDialog != null) {
            //根据位置调整
            curViewWidth = (int) (TIP_WIDTH * p);
            curViewHeight = (int) (TIP_WIDTH * p);
            if (!popTipDialog.isShowing()) {
                popTipDialog.show();
            }
            popTipDialog.changeSize(curViewWidth);
        } else {
            //创建
            popTipDialog = new PopTipDialog(this);
            if (!popTipDialog.isShowing()) {
                popTipDialog.show();
            }
        }
    }

    /**
     * 小圆点
     */
    private void buildAndShowIconFloat() {
        if (FloatWindow.get() != null) {
            FloatWindow.get().show();
        } else {
            iconView = new ImageView(getApplicationContext());
            ((ImageView) iconView).setImageResource(R.drawable.icon_verify_remindx);
            iconView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Toast.makeText(getBaseContext(), "点击浮窗", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), TopWinManagerTestActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getApplicationContext().startActivity(intent);
                    if (FloatWindow.get() != null) {
                        FloatWindow.destroy();
                    }
                }
            });
            FloatWindow
                    .with(getApplicationContext())
                    .setView(iconView)
                    .setWidth(ICON_WIDTH)                               //设置控件宽高
                    .setHeight(ICON_HEIGHT)
                    .setX(SCREEN_WIDTH - ICON_WIDTH - 10) //设置控件初始位置
                    .setY(SCREEN_HEIGHT / 4)
                    .setDesktopShow(true)                        //桌面显示
                    .setViewStateListener(mViewStateListener)    //监听悬浮控件状态改变
                    .setPermissionListener(mPermissionListener)  //监听权限申请结果
                    .setMoveType(MoveType.active)
                    .build();
        }
    }

    private PermissionListener mPermissionListener = new PermissionListener() {
        @Override
        public void onSuccess() {
//            Log.d("wzt", "PermissionListener onSuccess");
            permissionOk = true;
        }

        @Override
        public void onFail() {
//            Log.d("wzt", "PermissionListener onFail");
            permissionOk = false;
        }
    };

    private ViewStateListener mViewStateListener = new ViewStateListener() {
        @Override
        public void onPositionUpdate(int x, int y) {
//            Log.d("wzt", "onPositionUpdate: x=" + x + " y=" + y);
        }

        @Override
        public void onShow() {
//            Log.d("wzt", "onShow");
        }

        @Override
        public void onHide() {
//            Log.d("wzt", "onHide");
        }

        @Override
        public void onDismiss() {
//            Log.d("wzt", "onDismiss");
        }

        @Override
        public void onMoveAnimStart() {
//            Log.d("wzt", "onMoveAnimStart");
        }

        @Override
        public void onMoveAnimEnd() {
//            Log.d("wzt", "onMoveAnimEnd");
        }

        @Override
        public void onBackToDesktop() {
//            Log.d("wzt", "onBackToDesktop");
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (swipeBackPage != null && swipeListener != null) {
            swipeBackPage.removeListener(swipeListener);
        }
    }

}
