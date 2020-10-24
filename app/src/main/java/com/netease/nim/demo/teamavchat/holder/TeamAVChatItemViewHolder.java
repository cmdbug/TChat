package com.netease.nim.demo.teamavchat.holder;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.netease.nim.demo.DemoCache;
import com.netease.nim.demo.R;
import com.netease.nim.demo.teamavchat.module.TeamAVChatItem;
import com.netease.nim.uikit.NimUIKit;
import com.netease.nim.uikit.cache.TeamDataCache;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseMultiItemFetchLoadAdapter;
import com.netease.nim.uikit.common.ui.recyclerview.holder.BaseViewHolder;
import com.netease.nimlib.sdk.avchat.model.AVChatVideoRender;
import com.netease.nimlib.sdk.nos.model.NosThumbParam;
import com.netease.nimlib.sdk.nos.util.NosThumbImageUtil;
import com.netease.nimlib.sdk.uinfo.UserInfoProvider;

import static android.view.View.GONE;

/**
 * Created by huangjun on 2017/5/4.
 */

public class TeamAVChatItemViewHolder extends TeamAVChatItemViewHolderBase {
    private static final int DEFAULT_AVATAR_THUMB_SIZE = (int) NimUIKit.getContext().getResources().getDimension(com.netease.nim.uikit.R.dimen.avatar_max_size);
    private ImageView avatarImage;
    private ImageView loadingImage;
    private AVChatVideoRender surfaceView;
    private TextView nickNameText;
    private TextView stateText;
    private ProgressBar volumeBar;


    public TeamAVChatItemViewHolder(BaseMultiItemFetchLoadAdapter adapter) {
        super(adapter);
    }

    protected void inflate(final BaseViewHolder holder) {
        avatarImage = holder.getView(R.id.avatar_image);
        loadingImage = holder.getView(R.id.loading_image);
        surfaceView = holder.getView(R.id.surface);
        nickNameText = holder.getView(R.id.nick_name_text);
        stateText = holder.getView(R.id.avchat_state_text);
        volumeBar = holder.getView(R.id.avchat_volume);
    }

    protected void refresh(final TeamAVChatItem data) {
        nickNameText.setText(TeamDataCache.getInstance().getDisplayNameWithoutMe(data.teamId, data.account));

        final UserInfoProvider.UserInfo userInfo = NimUIKit.getUserInfoProvider().getUserInfo(data.account);
        final int defaultResId = R.drawable.t_avchat_avatar_default;
        final String thumbUrl = makeAvatarThumbNosUrl(userInfo != null ? userInfo.getAvatar() : null, DEFAULT_AVATAR_THUMB_SIZE);
        Glide.with(DemoCache.getContext())
                .load(thumbUrl).asBitmap().centerCrop()
                .placeholder(defaultResId)
                .error(defaultResId)
                .override(DEFAULT_AVATAR_THUMB_SIZE, DEFAULT_AVATAR_THUMB_SIZE)
                .into(avatarImage);

        if (data.state == TeamAVChatItem.STATE.STATE_WAITING) {
            // 等待接听
            Glide.with(DemoCache.getContext())
                    .load(R.drawable.t_avchat_loading).asGif()
                    .into(loadingImage);
            loadingImage.setVisibility(View.VISIBLE);
            surfaceView.setVisibility(View.INVISIBLE);
            stateText.setVisibility(GONE);
        } else if (data.state == TeamAVChatItem.STATE.STATE_PLAYING) {
            // 正在通话
            loadingImage.setVisibility(GONE);
            surfaceView.setVisibility(data.videoLive ? View.VISIBLE : View.INVISIBLE); // 有视频流才需要SurfaceView
            stateText.setVisibility(GONE);
        } else if (data.state == TeamAVChatItem.STATE.STATE_END || data.state == TeamAVChatItem.STATE.STATE_HANGUP) {
            // 未接听/挂断
            loadingImage.setVisibility(GONE);
            surfaceView.setVisibility(GONE);
            stateText.setVisibility(View.VISIBLE);
            stateText.setText(data.state == TeamAVChatItem.STATE.STATE_HANGUP ? R.string.avchat_has_hangup : R.string.avchat_no_pick_up);
        }

        updateVolume(data.volume);
    }

    /**
     * 生成头像缩略图NOS URL地址（用作ImageLoader缓存的key）
     */
    private static String makeAvatarThumbNosUrl(final String url, final int thumbSize) {
        if (TextUtils.isEmpty(url)) {
            return url;
        }

        return thumbSize > 0 ? NosThumbImageUtil.makeImageThumbUrl(url, NosThumbParam.ThumbType.Crop, thumbSize, thumbSize) : url;
    }

    public AVChatVideoRender getSurfaceView() {
        return surfaceView;
    }

    public void updateVolume(int volume) {
        volumeBar.setProgress(volume);
    }
}
