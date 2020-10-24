package com.netease.nim.demo.wzteng.friends.widgets.videolist.model;

import android.media.MediaPlayer;

import com.netease.nim.demo.wzteng.friends.widgets.videolist.widget.TextureVideoView;


/**
 * @author Wayne
 */
public interface VideoLoadMvpView {

    TextureVideoView getVideoView();

    void videoBeginning();

    void videoStopped();

    void videoPrepared(MediaPlayer player);

    void videoResourceReady(String videoPath);
}
