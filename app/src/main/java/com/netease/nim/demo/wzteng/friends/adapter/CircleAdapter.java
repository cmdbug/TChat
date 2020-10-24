package com.netease.nim.demo.wzteng.friends.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.Browser;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hitomi.tilibrary.style.index.NumberIndexIndicator;
import com.hitomi.tilibrary.style.progress.ProgressBarIndicator;
import com.hitomi.tilibrary.transfer.TransferConfig;
import com.jude.swipbackhelper.SwipeBackHelper;
import com.netease.nim.demo.R;
import com.netease.nim.demo.wzteng.friends.activity.FriendsActivity;
import com.netease.nim.demo.wzteng.friends.activity.ImagePagerActivity;
import com.netease.nim.demo.wzteng.friends.adapter.viewholder.CircleViewHolder;
import com.netease.nim.demo.wzteng.friends.adapter.viewholder.ImageViewHolder;
import com.netease.nim.demo.wzteng.friends.adapter.viewholder.MusicViewHolder;
import com.netease.nim.demo.wzteng.friends.adapter.viewholder.URLViewHolder;
import com.netease.nim.demo.wzteng.friends.adapter.viewholder.VideoPlayerViewHolder;
import com.netease.nim.demo.wzteng.friends.adapter.viewholder.VideoViewHolder;
import com.netease.nim.demo.wzteng.friends.bean.ActionItem;
import com.netease.nim.demo.wzteng.friends.bean.CircleItem;
import com.netease.nim.demo.wzteng.friends.bean.CommentConfig;
import com.netease.nim.demo.wzteng.friends.bean.CommentItem;
import com.netease.nim.demo.wzteng.friends.bean.FavortItem;
import com.netease.nim.demo.wzteng.friends.bean.PhotoInfo;
import com.netease.nim.demo.wzteng.friends.mvp.presenter.CirclePresenter;
import com.netease.nim.demo.wzteng.friends.utils.DatasUtil;
import com.netease.nim.demo.wzteng.friends.utils.GlideCircleTransform;
import com.netease.nim.demo.wzteng.friends.utils.UrlUtils;
import com.netease.nim.demo.wzteng.friends.widgets.CircleVideoView;
import com.netease.nim.demo.wzteng.friends.widgets.CommentListView;
import com.netease.nim.demo.wzteng.friends.widgets.ExpandTextView;
import com.netease.nim.demo.wzteng.friends.widgets.MultiImageView;
import com.netease.nim.demo.wzteng.friends.widgets.PraiseListView;
import com.netease.nim.demo.wzteng.friends.widgets.SnsPopupWindow;
import com.netease.nim.demo.wzteng.friends.widgets.dialog.CommentDialog;
import com.netease.nim.demo.wzteng.friends.widgets.ijkplayer.SampleListener;
import com.netease.nim.demo.wzteng.friends.widgets.music.MusicActivity;
import com.netease.nim.demo.wzteng.webview.WebViewActivity;
import com.shuyu.gsyvideoplayer.GSYVideoManager;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yiwei on 16/5/17.
 */
public class CircleAdapter extends BaseRecycleViewAdapter {

    public final static int TYPE_HEAD = 0;

    private static final int STATE_IDLE = 0;
    private static final int STATE_ACTIVED = 1;
    private static final int STATE_DEACTIVED = 2;
    private int videoState = STATE_IDLE;
    public static final int HEADVIEW_SIZE = 1;

    int curPlayIndex = -1;

    private CirclePresenter presenter;
    private Context context;

    public void setCirclePresenter(CirclePresenter presenter) {
        this.presenter = presenter;
    }

    public CircleAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEAD;
        }

        int itemType = 0;
        CircleItem item = (CircleItem) datas.get(position - 1);
        if (CircleItem.TYPE_URL.equals(item.getType())) {
            itemType = CircleViewHolder.TYPE_URL;
        } else if (CircleItem.TYPE_IMG.equals(item.getType())) {
            itemType = CircleViewHolder.TYPE_IMAGE;
        } else if (CircleItem.TYPE_VIDEO.equals(item.getType())) {
            itemType = CircleViewHolder.TYPE_VIDEO;
        } else if (CircleItem.TYPE_MUSIC.equals(item.getType())) {
            itemType = CircleViewHolder.TYPE_MUSIC;
        }
        return itemType;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        if (viewType == TYPE_HEAD) {
            View headView = LayoutInflater.from(parent.getContext()).inflate(R.layout.friends_head_circle, parent, false);
            viewHolder = new HeaderViewHolder(headView);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friends_adapter_circle_item, parent, false);

            if (viewType == CircleViewHolder.TYPE_URL) {
                viewHolder = new URLViewHolder(view);
            } else if (viewType == CircleViewHolder.TYPE_IMAGE) {
                viewHolder = new ImageViewHolder(view);
            } else if (viewType == CircleViewHolder.TYPE_VIDEO) {
//                viewHolder = new VideoViewHolder(view);
                viewHolder = new VideoPlayerViewHolder(view);
            } else if (viewType == CircleViewHolder.TYPE_MUSIC) {
                viewHolder = new MusicViewHolder(view);
            }
        }

        return viewHolder;
    }

    //爬虫后刷新item
    WHandler handler = new WHandler();

    public class WHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what < datas.size()) {
                notifyItemChanged(msg.what);
            }
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {

        if (getItemViewType(position) == TYPE_HEAD) {
            //HeaderViewHolder holder = (HeaderViewHolder) viewHolder;
        } else {

            final int circlePosition = position - HEADVIEW_SIZE;
            final CircleViewHolder holder = (CircleViewHolder) viewHolder;
            final CircleItem circleItem = (CircleItem) datas.get(circlePosition);
            final String circleId = circleItem.getId();
            String name = circleItem.getUser().getName();
            String headImg = circleItem.getUser().getHeadUrl();
            final String content = circleItem.getContent();
            String createTime = circleItem.getCreateTime();
            final List<FavortItem> favortDatas = circleItem.getFavorters();
            final List<CommentItem> commentsDatas = circleItem.getComments();
            boolean hasFavort = circleItem.hasFavort();
            boolean hasComment = circleItem.hasComment();

            Glide.with(context)
                    .load(headImg)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.color.bg_no_photo)
                    .transform(new GlideCircleTransform(context))
                    .into(holder.headIv);

            holder.nameTv.setText(name);
            holder.timeTv.setText(createTime);

            if (!TextUtils.isEmpty(content)) {
                holder.contentTv.setExpand(circleItem.isExpand());
                holder.contentTv.setExpandStatusListener(new ExpandTextView.ExpandStatusListener() {
                    @Override
                    public void statusChange(boolean isExpand) {
                        circleItem.setExpand(isExpand);
                    }
                });

                holder.contentTv.setText(UrlUtils.formatUrlString(content));
            }
            holder.contentTv.setVisibility(TextUtils.isEmpty(content) ? View.GONE : View.VISIBLE);

            if (DatasUtil.curUser.getId().equals(circleItem.getUser().getId())) {
                holder.deleteBtn.setVisibility(View.VISIBLE);
            } else {
                holder.deleteBtn.setVisibility(View.GONE);
            }
            holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //删除
                    if (presenter != null) {
                        presenter.deleteCircle(circleId);
                    }
                }
            });
            if (hasFavort || hasComment) {
                if (hasFavort) {//处理点赞列表
                    holder.praiseListView.setOnItemClickListener(new PraiseListView.OnItemClickListener() {
                        @Override
                        public void onClick(int position) {
                            String userName = favortDatas.get(position).getUser().getName();
                            String userId = favortDatas.get(position).getUser().getId();
                            Toast.makeText(context, userName + " &id = " + userId, Toast.LENGTH_SHORT).show();
                        }
                    });
                    holder.praiseListView.setDatas(favortDatas);
                    holder.praiseListView.setVisibility(View.VISIBLE);
                } else {
                    holder.praiseListView.setVisibility(View.GONE);
                }

                if (hasComment) {//处理评论列表
                    holder.commentList.setOnItemClickListener(new CommentListView.OnItemClickListener() {
                        @Override
                        public void onItemClick(int commentPosition) {
                            CommentItem commentItem = commentsDatas.get(commentPosition);
                            if (DatasUtil.curUser.getId().equals(commentItem.getUser().getId())) {//复制或者删除自己的评论

                                CommentDialog dialog = new CommentDialog(context, presenter, commentItem, circlePosition);
                                dialog.show();
                            } else {//回复别人的评论
                                if (presenter != null) {
                                    CommentConfig config = new CommentConfig();
                                    config.circlePosition = circlePosition;
                                    config.commentPosition = commentPosition;
                                    config.commentType = CommentConfig.Type.REPLY;
                                    config.replyUser = commentItem.getUser();
                                    presenter.showEditTextBody(config);
                                }
                            }
                        }
                    });
                    holder.commentList.setOnItemLongClickListener(new CommentListView.OnItemLongClickListener() {
                        @Override
                        public void onItemLongClick(int commentPosition) {
                            //长按进行复制或者删除
                            CommentItem commentItem = commentsDatas.get(commentPosition);
                            CommentDialog dialog = new CommentDialog(context, presenter, commentItem, circlePosition);
                            dialog.show();
                        }
                    });
                    holder.commentList.setDatas(commentsDatas);
                    holder.commentList.setVisibility(View.VISIBLE);

                } else {
                    holder.commentList.setVisibility(View.GONE);
                }
                holder.digCommentBody.setVisibility(View.VISIBLE);
            } else {
                holder.digCommentBody.setVisibility(View.GONE);
            }

            holder.digLine.setVisibility(hasFavort && hasComment ? View.VISIBLE : View.GONE);

            final SnsPopupWindow snsPopupWindow = holder.snsPopupWindow;
            //判断是否已点赞
            String curUserFavortId = circleItem.getCurUserFavortId(DatasUtil.curUser.getId());
            if (!TextUtils.isEmpty(curUserFavortId)) {
                snsPopupWindow.getmActionItems().get(0).mTitle = "取消";
            } else {
                snsPopupWindow.getmActionItems().get(0).mTitle = "赞";
            }
            snsPopupWindow.update();
            snsPopupWindow.setmItemClickListener(new PopupItemClickListener(circlePosition, circleItem, curUserFavortId));
            holder.snsBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //弹出popupwindow
                    snsPopupWindow.showPopupWindow(view);
                }
            });

            holder.urlTipTv.setVisibility(View.GONE);
            switch (holder.viewType) {
                case CircleViewHolder.TYPE_URL:// 处理链接动态的链接内容和和图片
                    if (holder instanceof URLViewHolder) {
                        String linkImg = circleItem.getLinkImg();
                        String linkTitle = circleItem.getLinkTitle();
                        if (linkImg.contains(".gif")) {
                            Glide.with(context).load(linkImg).asGif().into(((URLViewHolder) holder).urlImageIv);
                        } else {
                            Glide.with(context).load(linkImg).into(((URLViewHolder) holder).urlImageIv);
                        }
                        //不要gif
//                        Glide.with(context).load(linkImg).into(((URLViewHolder) holder).urlImageIv);
                        ((URLViewHolder) holder).urlContentTv.setText(linkTitle);
                        ((URLViewHolder) holder).urlBody.setVisibility(View.VISIBLE);
                        ((URLViewHolder) holder).urlTipTv.setVisibility(View.VISIBLE);
                        ((URLViewHolder) holder).urlBody.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                WebViewActivity.start(circleItem.getLinkUrl(), circleItem.getLinkTitle(), context);
//                                Uri uri = Uri.parse(circleItem.getLinkUrl());
//                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//                                intent.putExtra(Browser.EXTRA_APPLICATION_ID, context.getPackageName());
//                                context.startActivity(intent);
                            }
                        });

                        //通过标志位来区别是否是爬虫后的内容
                        if (!circleItem.isJsoup()) {
                            final int p = position - 1;//有顶部背景
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Message message = new Message();
                                        message.what = position;
                                        //这里开始是做一个解析，需要在非UI线程进行
                                        String imgStr = "";
                                        Document document = Jsoup.parse(new URL(circleItem.getLinkUrl()), 5000);
                                        String title = document.head().getElementsByTag("title").text();
                                        Elements imgs = document.getElementsByTag("img");//取得所有Img标签的值
                                        if (imgs.size() > 0) {
                                            imgStr = imgs.get(0).attr("abs:src");//默认取第一个为图片
                                        }
                                        CircleItem circleItem1 = (CircleItem) datas.get(p);
                                        //不要gif
//                                        if (!imgStr.contains(".gif")) {
//                                            circleItem1.setLinkImg(imgStr);
//                                        }
                                        if (imgStr.contains(".jpeg") || imgStr.contains(".jpg")
                                                || imgStr.contains(".png") || imgStr.contains(".gif")) {
                                            circleItem1.setLinkImg(imgStr);
                                        }
                                        circleItem1.setLinkTitle(title);
                                        circleItem1.setJsoup(true);
                                        if (p < datas.size()) {
                                            datas.set(p, circleItem1);
                                        }
                                        handler.sendMessage(message);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, "URL jsoup").start();
                        }
                    }

                    break;
                case CircleViewHolder.TYPE_IMAGE:// 处理图片
                    if (holder instanceof ImageViewHolder) {
                        final List<PhotoInfo> photos = circleItem.getPhotos();
                        if (photos != null && photos.size() > 0) {
                            ((ImageViewHolder) holder).multiImageView.setVisibility(View.VISIBLE);
                            ((ImageViewHolder) holder).multiImageView.setList(photos);
                            ((ImageViewHolder) holder).multiImageView.setOnItemClickListener(new MultiImageView.OnItemClickListener() {
                                @Override
                                public void onItemClick(View view, int position) {
                                    //imagesize是作为loading时的图片size
                                    List<String> photoUrls = new ArrayList<String>();
                                    for (PhotoInfo photoInfo : photos) {
                                        photoUrls.add(photoInfo.url);
                                    }
//                                    ImagePagerActivity.ImageSize imageSize = new ImagePagerActivity.ImageSize(view.getMeasuredWidth(), view.getMeasuredHeight());
//                                    ImagePagerActivity.startImagePagerActivity(((FriendsActivity) context), photoUrls, position, imageSize);

                                    //新的图片浏览器
                                    ((FriendsActivity) context).config = TransferConfig.build()
//                                            .setThumbnailImageList(ImageConfig.getThumbnailPicUrlList())
                                            .setSourceImageList(photoUrls)
                                            .setProgressIndicator(new ProgressBarIndicator())
                                            .setIndexIndicator(new NumberIndexIndicator())
                                            .setJustLoadHitImage(true)
                                            .bindImageViewList(((ImageViewHolder) holder).multiImageView.getImageViewList(), photoUrls);
                                    ((FriendsActivity) context).config.setNowThumbnailIndex(position);
                                    ((FriendsActivity) context).transferee.apply(((FriendsActivity) context).config).show();
                                }
                            });
                        } else {
                            ((ImageViewHolder) holder).multiImageView.setVisibility(View.GONE);
                        }
                    }

                    break;
                case CircleViewHolder.TYPE_VIDEO:
//                    if (holder instanceof VideoViewHolder) {
//                        ((VideoViewHolder) holder).videoView.setVideoUrl(circleItem.getVideoUrl());
//                        ((VideoViewHolder) holder).videoView.setVideoImgUrl(circleItem.getVideoImgUrl());//视频封面图片
//                        ((VideoViewHolder) holder).videoView.setPostion(position);
//                        ((VideoViewHolder) holder).videoView.setOnPlayClickListener(new CircleVideoView.OnPlayClickListener() {
//                            @Override
//                            public void onPlayClick(int pos) {
//                                curPlayIndex = pos;
//                            }
//                        });
//                    }
                    if (holder instanceof VideoPlayerViewHolder) {
                        ImageView imageView = new ImageView(context);
//                      imageView.setImageResource(R.drawable.room_cover_64);
                        Glide.with(context)
                                .load(circleItem.getVideoImgUrl())
                                .error(R.drawable.nim_default_img_failed)
                                .centerCrop()
                                .into(imageView);

                        //防止错位，离开释放
                        //gsyVideoPlayer.initUIState();
                        ((VideoPlayerViewHolder) holder).gsyVideoOptionBuilder
                                .setIsTouchWiget(false)
                                .setThumbImageView(imageView)
                                .setUrl(circleItem.getVideoUrl())
                                .setSetUpLazy(true)//lazy可以防止滑动卡顿
                                .setVideoTitle(circleItem.getVideoTitle())
                                .setCacheWithPlay(true)
                                .setRotateViewAuto(true)
                                .setLockLand(false)
                                .setPlayTag("PlayTag")
                                .setShowFullAnimation(true)
                                .setNeedLockFull(true)
                                .setPlayPosition(position)
                                .setStandardVideoAllCallBack(new SampleListener() {
                                    @Override
                                    public void onPrepared(String url, Object... objects) {
                                        super.onPrepared(url, objects);
                                        if (!((VideoPlayerViewHolder) holder).videoView.isIfCurrentIsFullscreen()) {
                                            //静音
                                            GSYVideoManager.instance().setNeedMute(false);
                                        }

                                    }

                                    @Override
                                    public void onQuitFullscreen(String url, Object... objects) {
                                        super.onQuitFullscreen(url, objects);
                                        SwipeBackHelper.getCurrentPage((Activity) context).setSwipeBackEnable(true);//滑动退出
                                        //静音
                                        GSYVideoManager.instance().setNeedMute(false);
                                    }

                                    @Override
                                    public void onEnterFullscreen(String url, Object... objects) {
                                        super.onEnterFullscreen(url, objects);
                                        SwipeBackHelper.getCurrentPage((Activity) context).setSwipeBackEnable(false);//禁止滑动退出
                                        GSYVideoManager.instance().setNeedMute(false);
                                        ((VideoPlayerViewHolder) holder).videoView.getCurrentPlayer().getTitleTextView().setText((String) objects[0]);
                                    }
                                }).build(((VideoPlayerViewHolder) holder).videoView);


                        //增加title
                        ((VideoPlayerViewHolder) holder).videoView.getTitleTextView().setVisibility(View.GONE);
                        //设置返回键
                        ((VideoPlayerViewHolder) holder).videoView.getBackButton().setVisibility(View.GONE);
                        //设置全屏按键功能
                        ((VideoPlayerViewHolder) holder).videoView.getFullscreenButton().setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ((VideoPlayerViewHolder) holder).videoView.startWindowFullscreen(context, true, true);
                            }
                        });

                    }

                    break;

                case CircleViewHolder.TYPE_MUSIC:
                    if (holder instanceof MusicViewHolder) {
                        String musicImg = circleItem.getMusicAlbum();
                        String musicTitle = circleItem.getMusicTitle();
                        String musicArtist = circleItem.getMusicArtist();
                        Glide.with(context).load(musicImg).into(((MusicViewHolder) holder).musicAlbumIv);
                        ((MusicViewHolder) holder).musicBody.setVisibility(View.VISIBLE);
                        ((MusicViewHolder) holder).urlTipTv.setVisibility(View.VISIBLE);
                        ((MusicViewHolder) holder).urlTipTv.setText("分享了一首音乐");
                        ((MusicViewHolder) holder).musicTitleTv.setText(musicTitle);
                        ((MusicViewHolder) holder).musicArtistTv.setText(musicArtist);
                        ((MusicViewHolder) holder).musicBody.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //测试点击
//                                WebViewActivity.start("http://play.baidu.com/?__m=mboxCtrl.playSong&__a=569080829&__o=/||newIcon#", "音乐测试", context);
                                MusicActivity.start(context);
                            }
                        });
                    }
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return datas.size() + 1;//有head需要加1
    }

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {

        public HeaderViewHolder(View itemView) {
            super(itemView);
        }
    }

    private class PopupItemClickListener implements SnsPopupWindow.OnItemClickListener {
        private String mFavorId;
        //动态在列表中的位置
        private int mCirclePosition;
        private long mLasttime = 0;
        private CircleItem mCircleItem;

        public PopupItemClickListener(int circlePosition, CircleItem circleItem, String favorId) {
            this.mFavorId = favorId;
            this.mCirclePosition = circlePosition;
            this.mCircleItem = circleItem;
        }

        @Override
        public void onItemClick(ActionItem actionitem, int position) {
            switch (position) {
                case 0://点赞、取消点赞
                    if (System.currentTimeMillis() - mLasttime < 700)//防止快速点击操作
                        return;
                    mLasttime = System.currentTimeMillis();
                    if (presenter != null) {
                        if ("赞".equals(actionitem.mTitle.toString())) {
                            presenter.addFavort(mCirclePosition);
                        } else {//取消点赞
                            presenter.deleteFavort(mCirclePosition, mFavorId);
                        }
                    }
                    break;
                case 1://发布评论
                    if (presenter != null) {
                        CommentConfig config = new CommentConfig();
                        config.circlePosition = mCirclePosition;
                        config.commentType = CommentConfig.Type.PUBLIC;
                        presenter.showEditTextBody(config);
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
