package com.netease.nim.demo.wzteng.checkpwd;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.netease.nim.demo.R;

/**
 * 弹出主管授权弹框
 */
public class PassValitationPopwindow extends PopupWindow implements
        OnClickListener, AdapterView.OnItemClickListener {

    private Context mContext;

    private Stack<Integer> mNumberStack;//保存输入的数字
    private List<TextView> mNumberViewList;//保存密码显示的几个text
    private final static int NUMBER_BUTTON_DELETE = 11; //删除键
    private final static int NUMBER_BUTTON_ZERO = 10;//0号按键
    private final static int NUMBER_BUTTON_CLEAR = 9;//清除按键
    private final static int NUMBER_COUNT = 6;

    private TextView mNumber1TextView;
    private TextView mNumber2TextView;
    private TextView mNumber3TextView;
    private TextView mNumber4TextView;
    private TextView mNumber5TextView;
    private TextView mNumber6TextView;
    private ExpandGridView mNumbersGridView; // 密码gridView
    private NumberAdapter mNumberAdapter;    // 数字adapter
    private boolean mIsPassword = true;
    private final static String PASSWORD_NUMBER_SYMBOL = "●";

    private ImageView ivLeft;
    private TextView tvTitle;

    private OnInputNumberCodeCallback mCallback; // 返回结果的回调

    private View mMenuView;
    private int type = 1;
    private View view;

    public PassValitationPopwindow() {
    }

    /**
     * @param mContext
     * @param type      1为主管授权，0为系统管理员授权
     * @param view      设置位置时候需要有个view值
     * @param mCallback 返回输入的6位验证密码
     */
    @SuppressWarnings("deprecation")
    @SuppressLint("InflateParams")
    public PassValitationPopwindow(Context mContext, int type, View view, OnInputNumberCodeCallback mCallback) {
        super(mContext);
        this.mContext = mContext;
        this.type = type;
        this.mCallback = mCallback;
        this.view = view;

        mNumberStack = new Stack<>();
        mNumberViewList = new ArrayList<TextView>();
        mNumberAdapter = new NumberAdapter(mContext);


        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMenuView = inflater.inflate(R.layout.checkpwd_pop_view, null);

//		mMenuView = LayoutInflater.from(mContext).inflate(R.layout.pop_view, null);

        mNumber1TextView = (TextView) mMenuView.findViewById(R.id.number_1_textView);
        mNumber2TextView = (TextView) mMenuView.findViewById(R.id.number_2_textView);
        mNumber3TextView = (TextView) mMenuView.findViewById(R.id.number_3_textView);
        mNumber4TextView = (TextView) mMenuView.findViewById(R.id.number_4_textView);
        mNumber5TextView = (TextView) mMenuView.findViewById(R.id.number_5_textView);
        mNumber6TextView = (TextView) mMenuView.findViewById(R.id.number_6_textView);
        ivLeft = (ImageView) mMenuView.findViewById(R.id.iv_pop_dismiss);
        tvTitle = (TextView) mMenuView.findViewById(R.id.tv_pop_title);

        //根据传来的type判断是主管授权还是管理员授权
//        tvTitle.setText(type == 0 ? "主管授权" : "管理员授权");
        tvTitle.setText(type == 0 ? "请输入支付密码" : "请输入支付密码");

        mNumberViewList.add(mNumber1TextView);
        mNumberViewList.add(mNumber2TextView);
        mNumberViewList.add(mNumber3TextView);
        mNumberViewList.add(mNumber4TextView);
        mNumberViewList.add(mNumber5TextView);
        mNumberViewList.add(mNumber6TextView);

        mNumbersGridView = (ExpandGridView) mMenuView.findViewById(R.id.numbers_gridView);
        mNumbersGridView.setAdapter(mNumberAdapter);
        mNumbersGridView.setOnItemClickListener(this);
        ivLeft.setOnClickListener(this);

        //设置SelectPicPopupWindow的View
        this.setContentView(mMenuView);
        //设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(LayoutParams.FILL_PARENT);
        //设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(LayoutParams.WRAP_CONTENT);
        //设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        //设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.AnimBottom);
        //实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        //设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
        //设置popup的位置
        this.showAtLocation(view,
                Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        //mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
        mMenuView.setOnTouchListener(new OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            public boolean onTouch(View v, MotionEvent event) {

                int height = mMenuView.findViewById(R.id.pop_layout).getTop();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        dismiss();
                    }
                }
                return true;
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_pop_dismiss:
                dismiss();
                break;

            default:
                break;
        }
    }

    public class SHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            StringBuilder codeBuilder = new StringBuilder();
            for (int number : mNumberStack) {
                codeBuilder.append(number);
            }
            validation(codeBuilder.toString());
        }
    }

    /**
     * 数字按键的响应事件
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        if (position == NUMBER_BUTTON_CLEAR) {
            clearnNumber();
            return;
        }
        if (position == NUMBER_BUTTON_DELETE) {
            deleteNumber();
        } else {
            if (position == NUMBER_BUTTON_ZERO) {
                mNumberStack.push(0);
            } else {
                mNumberStack.push(++position);
            }
        }
        refreshNumberViews(mNumberStack);
        final SHandler sHandler = new SHandler();
        //input 6 numbers complete
        if (mNumberStack.size() == NUMBER_COUNT) {
            sHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Message message = new Message();
                    message.what = 1;
                    message.obj = "time";
                    sHandler.sendMessage(message);
                }
            }, 100);
//            StringBuilder codeBuilder = new StringBuilder();
//            for (int number : mNumberStack) {
//                codeBuilder.append(number);
//            }
//            validation(codeBuilder.toString());
        }

    }

    /**
     * 验证密码，这里直接写在本地验证了
     *
     * @param code
     */
    private void validation(String code) {

        if ("123456".equals(code) && type == 0) {
            mCallback.onSuccess();
            dismiss();
        } else if ("123456".equals(code) && type == 1) {
            mCallback.onSuccess();
            dismiss();
        } else {
            clearnNumber();
//			Toast.makeText(mContext, "验证失败,请重新验证", Toast.LENGTH_SHORT).show();
            new ToastPopupwindow(mContext, view, "验证失败，请重新验证！");
        }

    }

    /**
     * 返回输出的结果
     */
    public interface OnInputNumberCodeCallback {
        void onSuccess();
    }

    /**
     * 清空mNumberStack的内容并刷新密码格
     */
    public void clearnNumber() {
        mNumberStack.clear();
        refreshNumberViews(mNumberStack);
    }

    /**
     * 删除密码位数
     */
    public void deleteNumber() {
        if (mNumberStack.empty() || mNumberStack.size() > NUMBER_COUNT) {
            return;
        }
        mNumberStack.pop();
    }

    /**
     * 刷新输入框显示
     *
     * @param mNumberStack
     */
    public void refreshNumberViews(Stack<Integer> mNumberStack) {

        for (int i = 0, size = mNumberViewList.size(); i < size; i++) {
            int numSize = mNumberStack.size();
            if (i < numSize) {
                if (mIsPassword) {
                    mNumberViewList.get(i).setText(PASSWORD_NUMBER_SYMBOL);
                } else {
                    mNumberViewList.get(i).setText(String.valueOf(mNumberStack.get(i)));
                }
            } else {
                mNumberViewList.get(i).setText("");
            }
        }

    }


}
