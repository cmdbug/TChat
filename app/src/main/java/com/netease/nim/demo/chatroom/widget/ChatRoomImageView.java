package com.netease.nim.demo.chatroom.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.bumptech.glide.Glide;
import com.netease.nim.uikit.NimUIKit;
import com.netease.nim.uikit.common.ui.imageview.CircleImageView;

public class ChatRoomImageView extends CircleImageView {

    public static final int DEFAULT_THUMB_SIZE = (int) NimUIKit.getContext().getResources().getDimension(com.netease.nim.uikit.R.dimen.avatar_max_size);

    public ChatRoomImageView(Context context) {
        super(context);
    }

    public ChatRoomImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChatRoomImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void loadAvatarByUrl(String url) {
        loadAvatar(url, DEFAULT_THUMB_SIZE);
    }

    private void loadAvatar(final String url, final int thumbSize) {
        Glide.with(getContext().getApplicationContext())
                .load(url).asBitmap().centerCrop()
                .placeholder(NimUIKit.getUserInfoProvider().getDefaultIconResId())
                .error(NimUIKit.getUserInfoProvider().getDefaultIconResId())
                .override(thumbSize, thumbSize)
                .into(this);
    }
}
