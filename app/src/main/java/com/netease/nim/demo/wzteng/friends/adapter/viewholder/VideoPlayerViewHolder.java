package com.netease.nim.demo.wzteng.friends.adapter.viewholder;

import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;

import com.netease.nim.demo.R;
import com.netease.nim.demo.wzteng.friends.widgets.ijkplayer.SampleCoverVideo;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;

/**
 * Created by WZTENG on 2018/1/24 0024.
 */

public class VideoPlayerViewHolder extends CircleViewHolder {

    public final static String TAG = "VideoPlayerViewHolder";

    public SampleCoverVideo videoView;
    public GSYVideoOptionBuilder gsyVideoOptionBuilder;

    public VideoPlayerViewHolder(View itemView) {
        super(itemView, TYPE_VIDEO);
        gsyVideoOptionBuilder = new GSYVideoOptionBuilder();
    }

    @Override
    public void initSubView(int viewType, ViewStub viewStub) {
        if (viewStub == null) {
            throw new IllegalArgumentException("viewStub is null...");
        }

        viewStub.setLayoutResource(R.layout.friends_viewstub_videoplayerbody);
        View subView = viewStub.inflate();

        SampleCoverVideo videoBody = (SampleCoverVideo) subView.findViewById(R.id.videoView);
        if (videoBody != null) {
            this.videoView = videoBody;
        }

    }
}
