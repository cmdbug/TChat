package com.netease.nim.demo.wzteng.friends.adapter.viewholder;

import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.netease.nim.demo.R;


/**
 * Created by suneee on 2016/8/16.
 */
public class MusicViewHolder extends CircleViewHolder{
    public LinearLayout musicBody;
    /** 图片 */
    public ImageView musicAlbumIv;
    /** 标题 */
    public TextView musicTitleTv;
    /** 歌手 */
    public TextView musicArtistTv;

    public MusicViewHolder(View itemView){
        super(itemView, TYPE_MUSIC);
    }

    @Override
    public void initSubView(int viewType, ViewStub viewStub) {
        if(viewStub == null){
            throw new IllegalArgumentException("viewStub is null...");
        }

        viewStub.setLayoutResource(R.layout.friends_viewstub_musicbody);
        View subViw  = viewStub.inflate();
        LinearLayout musicBodyView = (LinearLayout) subViw.findViewById(R.id.musicBody);
        if(musicBodyView != null){
            musicBody = musicBodyView;
            musicAlbumIv = (ImageView) subViw.findViewById(R.id.musicAlbumIv);
            musicTitleTv = (TextView) subViw.findViewById(R.id.musicTitleTv);
            musicArtistTv = (TextView) subViw.findViewById(R.id.musicArtistTv);
        }
    }
}
