package com.netease.nim.demo.contact.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.netease.nim.demo.R;
import com.netease.nim.demo.main.model.Extras;
import com.netease.nim.demo.session.SessionHelper;
import com.netease.nim.uikit.cache.RobotInfoCache;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.common.ui.imageview.HeadImageView;
import com.netease.nim.uikit.model.ToolBarOptions;
import com.netease.nimlib.sdk.robot.model.NimRobotInfo;

/**
 * Created by hzchenkang on 2017/6/30.
 */

public class RobotProfileActivity extends UI {

    private String robotAccount;

    private NimRobotInfo robotInfo;

    private HeadImageView headImageView;

    private TextView robotNameText;

    private TextView robotInfoText;

    private TextView robotAccountText;

    public static void start(Context context, String robotAccount) {
        Intent intent = new Intent();
        intent.setClass(context, RobotProfileActivity.class);
        intent.putExtra(Extras.EXTRA_ACCOUNT, robotAccount);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_robot_profile);

        ToolBarOptions options = new ToolBarOptions();
        options.titleId = R.string.nim_robot_title;
        setToolBar(R.id.toolbar, options);

        parseIntent();
        findViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateRobotInfo();
    }

    private void parseIntent() {
        robotAccount = getIntent().getStringExtra(Extras.EXTRA_ACCOUNT);
        robotInfo = RobotInfoCache.getInstance().getRobotByAccount(robotAccount);
    }

    private void findViews() {
        headImageView = findView(R.id.hv_robot);
        robotNameText = findView(R.id.tv_robot_name);
        robotInfoText = findView(R.id.tv_robot_info);
        robotAccountText = findView(R.id.tv_robot_account);
        findView(R.id.bt_chat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SessionHelper.startP2PSession(RobotProfileActivity.this, robotAccount);
            }
        });
    }

    private void updateRobotInfo() {
        if (robotInfo != null) {
            headImageView.loadAvatar(robotInfo.getAvatar());
            robotNameText.setText(robotInfo.getName());
            robotInfoText.setText(robotInfo.getIntroduce());
            robotAccountText.setText("@" + robotInfo.getAccount());
        }
    }
}
