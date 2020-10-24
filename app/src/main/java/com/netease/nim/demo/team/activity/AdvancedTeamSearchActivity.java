package com.netease.nim.demo.team.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.netease.nim.demo.R;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.common.ui.widget.ClearableEditTextWithIcon;
import com.netease.nim.uikit.model.ToolBarOptions;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.team.TeamService;
import com.netease.nimlib.sdk.team.model.Team;

/**
 * 搜索加入群组界面
 * Created by hzxuwen on 2015/3/20.
 */
public class AdvancedTeamSearchActivity extends UI {

    private ClearableEditTextWithIcon searchEditText;

    public static final void start(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, AdvancedTeamSearchActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nim_advanced_team_search_activity);
        setTitle(R.string.search_join_team);

        ToolBarOptions options = new ToolBarOptions();
        options.titleId = R.string.search_join_team;
        setToolBar(R.id.toolbar, options);

        findViews();
        initActionbar();
    }

    private void findViews() {
        searchEditText = (ClearableEditTextWithIcon) findViewById(R.id.team_search_edittext);
        searchEditText.setDeleteImage(R.drawable.nim_grey_delete_icon);
    }

    private void initActionbar() {
        TextView toolbarView = findView(R.id.action_bar_right_clickable_textview);
        toolbarView.setText(R.string.search);
        toolbarView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(searchEditText.getText().toString())) {
                    Toast.makeText(AdvancedTeamSearchActivity.this, R.string.not_allow_empty, Toast.LENGTH_SHORT).show();
                } else {
                    queryTeamById();
                }
            }
        });
    }

    private void queryTeamById() {
        NIMClient.getService(TeamService.class).searchTeam(searchEditText.getText().toString()).setCallback(new RequestCallback<Team>() {
            @Override
            public void onSuccess(Team team) {
                updateTeamInfo(team);
            }

            @Override
            public void onFailed(int code) {
                if(code == 803) {
                    Toast.makeText(AdvancedTeamSearchActivity.this, R.string.team_number_not_exist, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(AdvancedTeamSearchActivity.this,"search team failed: " + code, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onException(Throwable exception) {
                Toast.makeText(AdvancedTeamSearchActivity.this, "search team exception：" + exception.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * 搜索群组成功的回调
     * @param team 群
     */
    private void updateTeamInfo(Team team) {
        if (team.getId().equals(searchEditText.getText().toString())) {
            AdvancedTeamJoinActivity.start(AdvancedTeamSearchActivity.this, team.getId());
        }

    }
}
