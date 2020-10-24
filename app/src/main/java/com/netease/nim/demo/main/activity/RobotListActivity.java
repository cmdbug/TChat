package com.netease.nim.demo.main.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.netease.nim.demo.R;
import com.netease.nim.demo.contact.activity.RobotProfileActivity;
import com.netease.nim.uikit.NimUIKit;
import com.netease.nim.uikit.OnlineStateContentProvider;
import com.netease.nim.uikit.cache.RobotInfoCache;
import com.netease.nim.uikit.common.activity.ListActivityBase;
import com.netease.nim.uikit.common.ui.imageview.HeadImageView;
import com.netease.nim.uikit.common.ui.recyclerview.holder.BaseViewHolder;
import com.netease.nimlib.sdk.robot.model.NimRobotInfo;

import java.util.List;

/**
 * 机器人列表
 * <p>
 * Created by huangjun on 2017/6/21.
 */

public class RobotListActivity extends ListActivityBase<NimRobotInfo> {

    public static void start(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, RobotListActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected String getTitleString() {
        return "智能机器人";
    }

    @Override
    protected List<NimRobotInfo> onLoadData() {
        return RobotInfoCache.getInstance().getAllRobotAccounts();
    }

    @Override
    protected int onItemResId() {
        return R.layout.nim_robot_list_item;
    }

    @Override
    protected void convertItem(BaseViewHolder helper, NimRobotInfo item) {
        helper.setText(R.id.robot_name, item.getName());
        OnlineStateContentProvider provider = NimUIKit.getOnlineStateContentProvider();
        TextView textView = helper.getView(R.id.robot_online_state);
        if (provider == null) {
            textView.setVisibility(View.GONE);
        } else {
            textView.setVisibility(View.VISIBLE);
            textView.setText(provider.getSimpleDisplay(item.getAccount()));
        }
        HeadImageView imageView = helper.getView(R.id.robot_avatar);
        imageView.loadAvatar(item.getAvatar());
    }

    @Override
    protected void onItemClick(NimRobotInfo item) {
        RobotProfileActivity.start(this, item.getAccount());
    }
}
