package com.netease.nim.demo.wzteng.topwinmanager;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.netease.nim.demo.R;

public class PopTipDialog extends Dialog {

    private RelativeLayout rlBackground;

    public PopTipDialog(Context context) {
        super(context, R.style.dialog_poptip);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tip_winmanager);
        rlBackground = findViewById(R.id.rl_bg);
        setCanceledOnTouchOutside(true);
        getWindow().setGravity(Gravity.BOTTOM | Gravity.RIGHT);
        getWindow().setWindowAnimations(0);
    }

    public void setBackgroundColorByPosition(boolean inside) {
        if (inside) {
            rlBackground.setBackgroundResource(R.drawable.tip_red_bg);
        } else {
            rlBackground.setBackgroundResource(R.drawable.tip_light_gray_bg);
        }
    }

    public void changeSize(int size) {
        ViewGroup.LayoutParams layoutParams = rlBackground.getLayoutParams();
        layoutParams.width = size;
        layoutParams.height = size;
        rlBackground.setLayoutParams(layoutParams);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


}
