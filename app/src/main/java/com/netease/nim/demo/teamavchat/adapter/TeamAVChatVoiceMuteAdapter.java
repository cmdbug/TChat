package com.netease.nim.demo.teamavchat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.netease.nim.demo.R;
import com.netease.nim.demo.teamavchat.module.TeamAVChatVoiceMuteItem;
import com.netease.nim.uikit.common.ui.imageview.HeadImageView;

import java.util.List;

/**
 * Created by hzchenkang on 2017/5/9.
 */

public class TeamAVChatVoiceMuteAdapter extends BaseAdapter {

    private Context context;
    private List<TeamAVChatVoiceMuteItem> items;

    public TeamAVChatVoiceMuteAdapter(Context context, List<TeamAVChatVoiceMuteItem> data) {
        this.context = context;
        items = data;
    }

    public List<TeamAVChatVoiceMuteItem> getItems() {
        return items;
    }

    @Override
    public int getCount() {
        return items == null ? 0 : items.size();
    }

    @Override
    public Object getItem(int position) {
        return items == null ? null : items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(context);
            if (inflater != null) {
                convertView = inflater.inflate(R.layout.team_avchat_voice_mute_item, null);
                viewHolder.avatarImage = (HeadImageView) convertView.findViewById(R.id.head_image);
                viewHolder.nickNameText = (TextView) convertView.findViewById(R.id.tv_nick_name);
                viewHolder.muteImage = (ImageView) convertView.findViewById(R.id.img_mute);
                convertView.setTag(viewHolder);
            }
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        TeamAVChatVoiceMuteItem item = (TeamAVChatVoiceMuteItem) getItem(position);
        viewHolder.avatarImage.loadBuddyAvatar(item.getAccount());
        viewHolder.nickNameText.setText(item.getDisplayName());
        if (!item.isMute()) {
            viewHolder.muteImage.setImageResource(R.drawable.t_avchat_voice_normal);
        } else {
            viewHolder.muteImage.setImageResource(R.drawable.t_avchat_voice_mute);
        }

        return convertView;
    }

    private static class ViewHolder {
        HeadImageView avatarImage;
        TextView nickNameText;
        ImageView muteImage;
    }
}
