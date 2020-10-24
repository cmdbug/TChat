package com.netease.nim.demo.main.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.netease.nim.demo.DemoCache;
import com.netease.nim.demo.R;
import com.netease.nim.demo.contact.activity.UserProfileSettingActivity;
import com.netease.nim.demo.main.activity.SettingsActivity;
import com.netease.nim.demo.main.model.MainTab;
import com.netease.nim.demo.wzteng.qrcode.QrCodeDialog;
import com.netease.nim.uikit.cache.NimUserInfoCache;
import com.netease.nim.uikit.common.ui.imageview.HeadImageView;

/**
 * Created by WZTENG on 2017/03/06 0006.
 */

public class ProfileListFragment extends  MainTabFragment {

    HeadImageView ivMyHeader;
    TextView tvMyNick;
    TextView tvMyID;
    ImageView ivMySex;
    ImageView ivQrcode;
    RelativeLayout rlMyInfo;

    RelativeLayout rlSetting;

    public ProfileListFragment() {
        this.setContainerId(MainTab.PROFILE.fragmentId);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

//        onCurrent(); // 触发onInit，提前加载
    }

    @Override
    protected void onInit() {
        findView(R.layout.wzt_profile_contacts);
        initUI();
    }

    private void initUI() {
        ivMyHeader = (HeadImageView) getView().findViewById(R.id.iv_avatar);
        tvMyNick = (TextView) getView().findViewById(R.id.tv_name);
        tvMyID = (TextView) getView().findViewById(R.id.tv_fxid);
        ivMySex = (ImageView) getView().findViewById(R.id.iv_sex);
        ivQrcode = (ImageView) getView().findViewById(R.id.iv_qrcode);
        rlMyInfo = (RelativeLayout) getView().findViewById(R.id.re_myinfo);
        rlSetting = (RelativeLayout) getView().findViewById(R.id.re_setting);

        ivMyHeader.loadBuddyAvatar(DemoCache.getAccount());
        tvMyNick.setText(NimUserInfoCache.getInstance().getUserDisplayName(DemoCache.getAccount()));
        tvMyID.setText(String.format("藤信号:%s", DemoCache.getAccount()));

        //个人信息
        rlMyInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserProfileSettingActivity.start(getContext(), DemoCache.getAccount());
            }
        });

        ivQrcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QrCodeDialog.Builder builder = new QrCodeDialog.Builder(getContext());
                builder.create().show();
            }
        });

        //设置
        rlSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), SettingsActivity.class));
            }
        });
    }
}
