package com.netease.nim.demo.team.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.netease.nim.demo.R;
import com.netease.nim.uikit.cache.SimpleCallback;
import com.netease.nim.uikit.cache.TeamDataCache;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.model.ToolBarOptions;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.team.TeamService;
import com.netease.nimlib.sdk.team.constant.TeamTypeEnum;
import com.netease.nimlib.sdk.team.model.Team;

/**
 * 申请加入群组界面
 * Created by hzxuwen on 2015/3/20.
 */
public class AdvancedTeamJoinActivity extends UI implements View.OnClickListener {

    private static final String EXTRA_ID = "EXTRA_ID";

    private String teamId;
    private Team team;

    private TextView teamNameText;
    private TextView memberCountText;
    private TextView teamTypeText;
    private Button applyJoinButton;

    public static void start(Context context, String teamId) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_ID, teamId);
        intent.setClass(context, AdvancedTeamJoinActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.nim_advanced_team_join_activity);

        ToolBarOptions options = new ToolBarOptions();
        options.titleId = R.string.team_join;
        setToolBar(R.id.toolbar, options);

        findViews();
        parseIntentData();
        requestTeamInfo();
    }

    private void findViews() {
        teamNameText = (TextView) findViewById(R.id.team_name);
        memberCountText = (TextView) findViewById(R.id.member_count);
        applyJoinButton = (Button) findViewById(R.id.apply_join);
        teamTypeText = (TextView) findViewById(R.id.team_type);
        applyJoinButton.setOnClickListener(this);
    }

    private void parseIntentData() {
        teamId = getIntent().getStringExtra(EXTRA_ID);
    }

    private void requestTeamInfo() {
        Team t = TeamDataCache.getInstance().getTeamById(teamId);
        if (t != null) {
            updateTeamInfo(t);
        } else {
            TeamDataCache.getInstance().fetchTeamById(teamId, new SimpleCallback<Team>() {
                @Override
                public void onResult(boolean success, Team result) {
                    if (success && result != null) {
                        updateTeamInfo(result);
                    }
                }
            });
        }
    }

    /**
     * 更新群信息
     *
     * @param t 群
     */
    private void updateTeamInfo(final Team t) {
        if (t == null) {
            Toast.makeText(AdvancedTeamJoinActivity.this, R.string.team_not_exist, Toast.LENGTH_LONG).show();
            finish();
        } else {
            team = t;
            teamNameText.setText(team.getName());
            memberCountText.setText(team.getMemberCount() + "人");
            if (team.getType() == TeamTypeEnum.Advanced) {
                teamTypeText.setText(R.string.advanced_team);
            } else {
                teamTypeText.setText(R.string.normal_team);
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (team != null) {
            NIMClient.getService(TeamService.class).applyJoinTeam(team.getId(), null).setCallback(new RequestCallback<Team>() {
                @Override
                public void onSuccess(Team team) {
                    applyJoinButton.setEnabled(false);
                    String toast = getString(R.string.team_join_success, team.getName());
                    Toast.makeText(AdvancedTeamJoinActivity.this, toast, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailed(int code) {
                    if (code == 808) {
                        applyJoinButton.setEnabled(false);
                        Toast.makeText(AdvancedTeamJoinActivity.this, R.string.team_apply_to_join_send_success,
                                Toast.LENGTH_SHORT).show();
                    } else if (code == 809) {
                        applyJoinButton.setEnabled(false);
                        Toast.makeText(AdvancedTeamJoinActivity.this, R.string.has_exist_in_team,
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(AdvancedTeamJoinActivity.this, "failed, error code =" + code,
                                Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onException(Throwable exception) {

                }
            });
        }
    }
}
