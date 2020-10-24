package com.netease.nim.uikit.wzteng.translation;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.netease.nim.uikit.R;
import com.netease.nim.uikit.common.util.sys.ScreenUtil;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

/**
 * Created by WZTENG on 2017/04/14 0014.
 */

public class TranslatorPopWin {
    private Context context;
    private PopupWindow popupWindow;
    private TranslationResult result;
    private TextView tvTranslation;

    public TranslatorPopWin(Context context, TranslationResult translationResult) {
        this.context = context;
        this.result = translationResult;
    }

    public void showPopupWindow(View v) {
        LayoutInflater mLayoutInflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = mLayoutInflater.inflate(R.layout.wzt_translator_pop_item, null);
        tvTranslation = (TextView) view.findViewById(R.id.tv_translation);

        tvTranslation.setText(Html.fromHtml(ResultUtils.resultDeal(result)));

        // 第一个参数导入泡泡的view，后面两个指定宽和高
        popupWindow = new PopupWindow(view, (int) (ScreenUtil.getDialogWidth() * 0.8), ScreenUtil.getDisplayHeight() / 4);
        // 设置此参数获得焦点，否则无法点击
        // 设置点击窗口外边窗口消失，
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setOutsideTouchable(true);
        // 弹窗一般有两种展示方法，用showAsDropDown()和showAtLocation()两种方法实现。
        // 以这个v为anchor（可以理解为锚，基准），在下方弹出
        popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
//        popupWindow.showAsDropDown(v, 0, -2 * v.getHeight());
    }

    public void popDismiss() {
        if (popupWindow != null) {
            popupWindow.dismiss();
        }

    }
}
