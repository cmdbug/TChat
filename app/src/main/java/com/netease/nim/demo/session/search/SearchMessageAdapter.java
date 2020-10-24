package com.netease.nim.demo.session.search;

import android.content.Context;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.netease.nim.demo.R;
import com.netease.nim.uikit.common.ui.imageview.HeadImageView;
import com.netease.nim.uikit.common.util.sys.ScreenUtil;
import com.netease.nim.uikit.common.util.sys.TimeUtil;
import com.netease.nim.uikit.cache.NimUserInfoCache;
import com.netease.nim.uikit.cache.TeamDataCache;
import com.netease.nim.uikit.session.emoji.MoonUtil;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import java.util.List;

/**
 * Created by vincent on 7/30/14.
 */
public class SearchMessageAdapter extends BaseAdapter {

    private Context context;
    private List<IMMessage> messages;
    private String keyword;
    
    public SearchMessageAdapter(Context context, List<IMMessage> messages) {
        this.context = context;
        this.messages = messages;
        
        this.keyword = "";
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
    
    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Object getItem(int position) {
        return messages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextSearchResultViewHolder holder;
        if (convertView != null) {
            holder = (TextSearchResultViewHolder) convertView.getTag();
        } else {
            convertView = LayoutInflater.from(context).inflate(R.layout.message_search_list_view_item, parent, false);
            holder = new TextSearchResultViewHolder(convertView);
            convertView.setTag(holder);
        }

        holder.refresh(messages.get(position));

        return convertView;
    }

    private class TextSearchResultViewHolder {
        private HeadImageView imgHead;
        private TextView tvNickname;
        private TextView tvMessage;
        private TextView tvDatetime;
        private ImageView imgMsgStatus;


        public TextSearchResultViewHolder(View view) {
            this.imgHead = (HeadImageView) view.findViewById(R.id.img_head);

            this.tvNickname = (TextView) view.findViewById(R.id.tv_nick_name);
            this.tvMessage = (TextView) view.findViewById(R.id.tv_message);
            this.tvDatetime = (TextView) view.findViewById(R.id.tv_date_time);
            this.imgMsgStatus = (ImageView) view.findViewById(R.id.img_msg_status);
        }

        public void refresh(IMMessage message) {
            imgHead.loadBuddyAvatar(message.getFromAccount());

            refreshNickname(message);
            refreshContent(message);
            refreshTime(message);
        }

        private void refreshNickname(IMMessage message) {
        	int labelWidth = ScreenUtil.screenWidth;
        	// 减去固定的头像和时间宽度
        	labelWidth -= ScreenUtil.dip2px(70 + 70);
        	tvNickname.setMaxWidth(labelWidth);
            if (message.getSessionType() == SessionTypeEnum.Team) {
                tvNickname.setText(TeamDataCache.getInstance().getTeamMemberDisplayName(message.getSessionId(), message.getFromAccount()));
            } else {
                tvNickname.setText(NimUserInfoCache.getInstance().getUserDisplayName(message.getFromAccount()));
            }
        }

        private void refreshContent(IMMessage message) {
            MoonUtil.identifyFaceExpressionAndTags(context, tvMessage, message.getContent(), ImageSpan.ALIGN_BOTTOM, 0.45f);
//            SpanUtil.makeKeywordSpan(context, tvMessage, keyword);
            
            switch (message.getStatus()) {
            case fail:
                imgMsgStatus.setImageResource(R.drawable.nim_g_ic_failed_small);
                imgMsgStatus.setVisibility(View.VISIBLE);
                break;
            default:
                imgMsgStatus.setVisibility(View.GONE);
                break;
            }
        }

        private void refreshTime(IMMessage messageHistory) {
            String timeString = TimeUtil.getTimeShowString(messageHistory.getTime(), true);
            tvDatetime.setText(timeString);
        }
    }
}
