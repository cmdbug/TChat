package com.netease.nim.demo.teamavchat;

import android.content.Context;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;

import com.netease.nim.demo.R;
import com.netease.nim.demo.teamavchat.adapter.TeamAVChatVoiceMuteAdapter;
import com.netease.nim.demo.teamavchat.module.TeamAVChatVoiceMuteItem;
import com.netease.nim.uikit.cache.TeamDataCache;
import com.netease.nim.uikit.common.ui.dialog.CustomAlertDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hzchenkang on 2017/5/9.
 */

public class TeamAVChatVoiceMuteDialog extends CustomAlertDialog {

    private TeamAVChatVoiceMuteAdapter adapter;
    private TeamVoiceMuteListener listener;
    private List<Pair<String, Boolean>> beforeMutes;

    public TeamAVChatVoiceMuteDialog(Context context, String teamId, List<Pair<String, Boolean>> voiceMutes) {
        super(context, voiceMutes == null ? 0 : voiceMutes.size());
        beforeMutes = voiceMutes;
        if (voiceMutes == null) {
            return;
        }
        setTitle("屏蔽音频");
        setCanceledOnTouchOutside(false);

        List<TeamAVChatVoiceMuteItem> data = new ArrayList<>();
        for (Pair<String, Boolean> voiceMute : voiceMutes) {
            TeamAVChatVoiceMuteItem item = new TeamAVChatVoiceMuteItem();
            item.setAccount(voiceMute.first);
            item.setMute(voiceMute.second);
            item.setDisplayName(TeamDataCache.getInstance().getTeamMemberDisplayName(teamId, item.getAccount()));
            data.add(item);
        }
        adapter = new TeamAVChatVoiceMuteAdapter(context, data);
        setAdapter(adapter, new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //
                TeamAVChatVoiceMuteItem item = (TeamAVChatVoiceMuteItem) adapter.getItem(position);
                if (item == null) {
                    return;
                }
                item.setMute(!item.isMute());

                adapter.notifyDataSetChanged();
            }
        });
    }

    public void setTeamVoiceMuteListener(TeamVoiceMuteListener listener) {
        this.listener = listener;
    }

    public interface TeamVoiceMuteListener {
        void onVoiceMuteChange(List<Pair<String, Boolean>> voiceMuteAccounts);
    }

    @Override
    protected void addFootView(LinearLayout parent) {
        View footView = getLayoutInflater().inflate(R.layout.nim_easy_alert_dialog_bottom_button, null);
        Button positiveButton = (Button) footView.findViewById(R.id.easy_dialog_positive_btn);
        positiveButton.setVisibility(View.VISIBLE);
        positiveButton.setText(getContext().getString(R.string.save));

        Button negativeButton = (Button) footView.findViewById(R.id.easy_dialog_negative_btn);
        negativeButton.setVisibility(View.VISIBLE);

        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    List<Pair<String, Boolean>> items = new ArrayList<>();
                    List<TeamAVChatVoiceMuteItem> afterItems = adapter.getItems();

                    for (int i = 0; i < afterItems.size(); i++) {
                        if (afterItems.get(i).isMute() != beforeMutes.get(i).second) {
                            items.add(new Pair<>(beforeMutes.get(i).first, !beforeMutes.get(i).second));
                        }
                    }

                    listener.onVoiceMuteChange(items);
                }
                dismiss();
            }
        });
        negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        parent.addView(footView);
    }
}
