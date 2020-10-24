package com.netease.nim.demo.wzteng.friends.widgets.ijkplayer;

import android.content.Context;
import android.media.MediaPlayer;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.netease.nim.demo.R;
import com.netease.nim.demo.wzteng.friends.widgets.videolist.model.VideoLoadMvpView;
import com.netease.nim.demo.wzteng.friends.widgets.videolist.visibility.items.ListItem;
import com.netease.nim.demo.wzteng.friends.widgets.videolist.widget.TextureVideoView;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;
import com.shuyu.gsyvideoplayer.video.base.GSYBaseVideoPlayer;

/**
 * 带封面
 * Created by guoshuyu on 2017/9/3.
 */

public class SampleCoverVideo extends StandardGSYVideoPlayer implements VideoLoadMvpView, ListItem {

    ImageView mCoverImage;
    String mCoverOriginUrl;
    int mDefaultRes;

    private static final int STATE_IDLE = 0;
    private static final int STATE_ACTIVED = 1;
    private static final int STATE_DEACTIVED = 2;
    private int videoState = STATE_IDLE;

    private int postion;

    public SampleCoverVideo(Context context, Boolean fullFlag) {
        super(context, fullFlag);
    }

    public SampleCoverVideo(Context context) {
        super(context);
    }

    public SampleCoverVideo(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void init(Context context) {
        super.init(context);
        mCoverImage = (ImageView) findViewById(R.id.thumbImage);

        if (mThumbImageViewLayout != null &&
                (mCurrentState == -1 || mCurrentState == CURRENT_STATE_NORMAL || mCurrentState == CURRENT_STATE_ERROR)) {
            mThumbImageViewLayout.setVisibility(VISIBLE);
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.video_layout_cover;
    }

    public void loadCoverImage(String url, int res) {
        mCoverOriginUrl = url;
        mDefaultRes = res;
        Glide.with(getContext().getApplicationContext())
//                .setDefaultRequestOptions(
//                        new RequestOptions()
//                                .frame(1000000)
//                                .centerCrop()
//                                .error(res)
//                                .placeholder(res))
                .load(url)
                .placeholder(res)
                .error(res)
                .centerCrop()
                .into(mCoverImage);
    }

    @Override
    public GSYBaseVideoPlayer startWindowFullscreen(Context context, boolean actionBar, boolean statusBar) {
        GSYBaseVideoPlayer gsyBaseVideoPlayer = super.startWindowFullscreen(context, actionBar, statusBar);
        SampleCoverVideo sampleCoverVideo = (SampleCoverVideo) gsyBaseVideoPlayer;
        sampleCoverVideo.loadCoverImage(mCoverOriginUrl, mDefaultRes);
        return gsyBaseVideoPlayer;
    }

    @Override
    public TextureVideoView getVideoView() {
        return null;
    }

    @Override
    public void videoBeginning() {

    }

    @Override
    public void videoStopped() {
        GSYVideoManager.onPause();
    }

    @Override
    public void videoPrepared(MediaPlayer player) {

    }

    @Override
    public void videoResourceReady(String videoPath) {

    }

    @Override
    public void setActive(View newActiveView, int newActiveViewPosition) {

    }

    @Override
    public void deactivate(View currentView, int position) {
        if (this.postion == position) {
            videoState = STATE_DEACTIVED;
//            if (!TextUtils.isEmpty(mCoverOriginUrl)) {
//                videoPlayer.stop();
//                videoStopped();
                GSYVideoManager.onPause();
//            }
        }
    }
}
