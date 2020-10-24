package com.netease.nim.demo.session.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.netease.nim.demo.R;
import com.netease.nim.demo.session.extension.StickerAttachment;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseMultiItemFetchLoadAdapter;
import com.netease.nim.uikit.common.util.sys.ScreenUtil;
import com.netease.nim.uikit.session.emoji.StickerManager;
import com.netease.nim.uikit.session.viewholder.MsgViewHolderBase;
import com.netease.nim.uikit.session.viewholder.MsgViewHolderThumbBase;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.IOException;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

/**
 * Created by zhoujianghua on 2015/8/7.
 */
public class MsgViewHolderSticker extends MsgViewHolderBase {

    private ImageView imageView;
    private GifImageView gifImageView;//teng

    public MsgViewHolderSticker(BaseMultiItemFetchLoadAdapter adapter) {
        super(adapter);
    }

    @Override
    protected int getContentResId() {
        return R.layout.nim_message_item_sticker;
    }

    @Override
    protected void inflateContentView() {
        imageView = findViewById(R.id.message_item_sticker_image);
        gifImageView = findViewById(R.id.message_item_sticker_gif);//teng
        imageView.setMaxWidth(MsgViewHolderThumbBase.getImageMaxEdge());
        gifImageView.setMaxWidth(MsgViewHolderThumbBase.getImageMaxEdge());//teng
    }

    @Override
    protected void bindContentView() {
        StickerAttachment attachment = (StickerAttachment) message.getAttachment();
        if (attachment == null) {
            return;
        }

        //teng
        //path格式为：assets://sticker/xxx/xxx.png 或者 gif
        String path = StickerManager.getInstance().getStickerBitmapUri(attachment.getCatalog(), attachment.getChartlet());
        if (path.contains(".png")) {
            imageView.setVisibility(View.VISIBLE);
            gifImageView.setVisibility(View.GONE);
            ImageLoader.getInstance().displayImage(
                    path,
                    imageView,
                    StickerManager.getInstance().getStickerImageOptions(ScreenUtil.dip2px(R.dimen.mask_sticker_bubble_width))
            );
        } else if (path.contains(".gif")) {
            imageView.setVisibility(View.GONE);
            gifImageView.setVisibility(View.VISIBLE);
            GifDrawable gifDrawable = null;
            try {
                gifDrawable = new GifDrawable(context.getAssets(), path.substring("assets://".length(), path.length()));
                gifImageView.setImageDrawable(gifDrawable);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

//        Toast.makeText(context, "" + StickerManager.getInstance().getStickerBitmapUri(attachment.getCatalog(), attachment.getChartlet()), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected int leftBackground() {
        return 0;
    }

    @Override
    protected int rightBackground() {
        return 0;
    }
}
