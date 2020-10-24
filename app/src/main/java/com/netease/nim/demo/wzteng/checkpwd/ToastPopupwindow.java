package com.netease.nim.demo.wzteng.checkpwd;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.netease.nim.demo.R;

/**
 *         自己定义的一个toast作用的popupwindow
 *         主要是为了弹出验证框的时候有时会遮住toast导致不显示，一般情况下直接用myApplication.showToast(); 就行了
 */
public class ToastPopupwindow extends PopupWindow {

    private View mMenuView;
    private TextView mToastTv;

    public ToastPopupwindow() {
    }


    @SuppressLint("InflateParams")
    public ToastPopupwindow(Context mContext, View view, String ss) {
        super(mContext);

        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMenuView = inflater.inflate(R.layout.checkpwd_view_toast, null);

        mToastTv = (TextView) mMenuView.findViewById(R.id.title_tv);
        mToastTv.setText(ss);

        //设置ToastPopupwindow的View
        this.setContentView(mMenuView);
        //设置ToastPopupwindow弹出窗体的宽
        this.setWidth(LayoutParams.WRAP_CONTENT);
        //设置ToastPopupwindow弹出窗体的高
        this.setHeight(LayoutParams.WRAP_CONTENT);
        //设置ToastPopupwindow弹出窗体可点击
        this.setFocusable(false);
        //设置ToastPopupwindow弹出窗体动画效果
        this.setAnimationStyle(R.style.AnimBottom);
        //实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        //设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
        //设置popup的位置
        this.showAtLocation(view,
                Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 10);

        Message message = handler.obtainMessage(1);     // Message
        handler.sendMessageDelayed(message, 1000);

    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        public void dispatchMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    dismiss();
                    break;

                default:
                    break;
            }
        }

        ;
    };


}
