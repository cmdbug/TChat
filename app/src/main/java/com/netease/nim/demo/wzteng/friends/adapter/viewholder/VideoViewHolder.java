package com.netease.nim.demo.wzteng.friends.adapter.viewholder;

import android.view.View;
import android.view.ViewStub;

import com.netease.nim.demo.R;
import com.netease.nim.demo.wzteng.friends.widgets.CircleVideoView;


/**
 * Created by suneee on 2016/8/16.
 */

/**
 * 已更新为IJKPlayer播放器，不再使用该ViewHolder
 */
public class VideoViewHolder extends CircleViewHolder {

    public CircleVideoView videoView;

    public VideoViewHolder(View itemView){
        super(itemView, TYPE_VIDEO);
    }

    @Override
    public void initSubView(int viewType, ViewStub viewStub) {
        if(viewStub == null){
            throw new IllegalArgumentException("viewStub is null...");
        }
        
        viewStub.setLayoutResource(R.layout.friends_viewstub_videobody);
        View subView = viewStub.inflate();

        CircleVideoView videoBody = (CircleVideoView) subView.findViewById(R.id.videoView);
        if(videoBody!=null){
            this.videoView = videoBody;
        }
    }
}
