package com.netease.nim.demo.main.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.netease.nim.demo.R;
import com.netease.nim.demo.main.model.MainTab;
import com.netease.nim.demo.wzteng.GlobalConfig;
import com.netease.nim.demo.wzteng.friends.activity.FriendsActivity;
import com.netease.nim.demo.wzteng.qrcode.QrCodeActivity;
import com.netease.nim.demo.wzteng.topwinmanager.TopWinManagerTestActivity;
import com.netease.nim.demo.wzteng.webview.WebViewActivity;


/**
 * Created by WZTENG on 2017/03/06 0006.
 */

public class FindListFragment extends MainTabFragment {

    private LinearLayout ll_sub_item;

    public FindListFragment() {
        this.setContainerId(MainTab.FIND.fragmentId);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

//        onCurrent(); // 触发onInit，提前加载
    }

    @Override
    protected void onInit() {
        findView(R.layout.wzt_find_contacts);
        initUI();
    }

    private void initUI() {
        ll_sub_item = findView(R.id.ll_sub_item);

        addViewToSubItem(ll_sub_item, R.drawable.find_friends, "朋友圈", 0, "", 0,
                true,
                false,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FriendsActivity.start(getContext(), null);
                    }
                });

        addViewToSubItem(ll_sub_item, R.drawable.find_scan, "扫一扫", 0, "", 0,
                true,
                true,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        QrCodeActivity.start(getContext());
                    }
                });

        addViewToSubItem(ll_sub_item, R.drawable.find_shake, "摇一摇", 0, "", 0,
                false, false, null);

        addViewToSubItem(ll_sub_item, R.drawable.find_nearby, "附近的人", 0, "", 0,
                true, true,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TopWinManagerTestActivity.start(getContext());
                    }
                });

        addViewToSubItem(ll_sub_item, R.drawable.find_bottle, "漂流瓶", 0, "", 0,
                false, false, null);

        addViewToSubItem(ll_sub_item, R.drawable.find_shopping, "购物", 0, "", 0,
                true,
                true,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        WebViewActivity.start(GlobalConfig.SHOPPINGURL, "购物", getContext());
                    }
                });

        addViewToSubItem(ll_sub_item, R.drawable.find_game, "游戏", 0, "", 0,
                false,
                false,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        WebViewActivity.start(GlobalConfig.GAMEURL, "游戏", getContext());
                    }
                });

        addViewToSubItem(ll_sub_item, R.drawable.find_programs, "小程序", 0, "", 0,
                true, false, null);

    }

    /**
     * @param view
     * @param resIcon
     * @param text
     * @param messageNum
     * @param tip
     * @param tipIcon
     * @param showTop
     * @param showLine
     * @param onItemClickListener
     */
    private void addViewToSubItem(View view, int resIcon, String text,
                                  int messageNum, String tip, int tipIcon,
                                  boolean showTop, boolean showLine,
                                  View.OnClickListener onItemClickListener) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View subView = inflater.inflate(R.layout.wzt_find_contacts_item, null, false);

        RelativeLayout llrl = subView.findViewById(R.id.re_friends);
        if (onItemClickListener != null) {
            llrl.setOnClickListener(onItemClickListener);
        }

        LinearLayout llTop = subView.findViewById(R.id.ll_top);
        llTop.setVisibility(showTop ? View.VISIBLE : View.GONE);

        ImageView ivIcon = subView.findViewById(R.id.iv_friends);
        ivIcon.setImageResource(resIcon);

        TextView tvText = subView.findViewById(R.id.tv_friends);
        tvText.setText(text);

        TextView tvMessageNum = subView.findViewById(R.id.tv_new_msg);
        if (messageNum > 0) {
            tvMessageNum.setVisibility(View.VISIBLE);
            tvMessageNum.setText(messageNum);
        } else {
            tvMessageNum.setVisibility(View.GONE);
        }

        ImageView ivTip = subView.findViewById(R.id.iv_new);
        if (0 != tipIcon) {
            ivTip.setVisibility(View.GONE);
        } else {
            ivTip.setVisibility(View.VISIBLE);
            ivTip.setImageResource(tipIcon);
        }

        TextView tipMessage = subView.findViewById(R.id.tv_new_tip);
        if ("".equals(tip)) {
            tipMessage.setVisibility(View.GONE);
        } else {
            tipMessage.setVisibility(View.VISIBLE);
            tipMessage.setText(tip);
        }

        RelativeLayout rlLine = subView.findViewById(R.id.rl_line);
        rlLine.setVisibility(showLine ? View.VISIBLE : View.GONE);

        ((LinearLayout) view).addView(subView);
    }

}
