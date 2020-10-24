package com.netease.nim.uikit.wzteng;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.netease.nim.uikit.R;

import java.io.IOException;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

/**
 * Created by WZTENG on 2017/03/08 0008.
 */

public class EmojiPopwindows {
    private Context context;
    private PopupWindow popupWindow;

    private String categoryName;
    private String stickerName;

    private GifImageView gifImageView = null;
    private GifDrawable gifDrawable = null;

    private ImageView stickerImageView = null;

    public EmojiPopwindows(Context context, String categoryName, String stickerName) {
        this.context = context;
        this.categoryName = categoryName;
        this.stickerName = stickerName;
    }

    public void showPopupWindow(View v) {

        LayoutInflater mLayoutInflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = mLayoutInflater.inflate(R.layout.wzt_gif_emoji_popwindows_item, null);
        stickerImageView = (ImageView) view.findViewById(R.id.iv_imageview);//普通图片
        gifImageView = (GifImageView) view.findViewById(R.id.gif_imageview);//gif
//        gifImageView.setImageResource(R.drawable.test001);//测试
        if (stickerName.contains(".gif")) {
            //如果是gif则显示动画，否则显示图片
            gifImageView.setVisibility(View.VISIBLE);
            stickerImageView.setVisibility(View.GONE);
            try {
                //支持方法请查看:https://github.com/koral--/android-gif-drawable/
                gifDrawable = new GifDrawable(context.getAssets(), "sticker/" + categoryName + "/" + stickerName);
                //或者
//            gifDrawable = new GifDrawable(context.getResources().getAssets().open("sticker/" + categoryName + "/" + stickerName));
                gifImageView.setImageDrawable(gifDrawable);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        } else {
            //不是gif就是图片
            gifImageView.setVisibility(View.GONE);
            stickerImageView.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load("file:///android_asset/sticker/" + categoryName + "/" + stickerName)
                    .skipMemoryCache(false)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(stickerImageView);
        }

        // 第一个参数导入泡泡的view，后面两个指定宽和高
        popupWindow = new PopupWindow(view, v.getWidth(), v.getHeight());
        // 设置此参数获得焦点，否则无法点击

        // 设置点击窗口外边窗口消失，
        //下面两句位置不能颠倒，不然无效！（经本机测试 不知道别人如何）必须设置backgroundDrawable()

        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setOutsideTouchable(true);
        // 弹窗一般有两种展示方法，用showAsDropDown()和showAtLocation()两种方法实现。
        // 以这个v为anchor（可以理解为锚，基准），在下方弹出
//        popupWindow.showAtLocation(v, Gravity.TOP | Gravity.LEFT, 0, 0);
        //方式一
//        popupWindow.showAsDropDown(v, 0, -2 * v.getHeight());
        //方式二
        int[] location = new int[2];
        v.getLocationInWindow(location);
        popupWindow.showAtLocation(v.getRootView(), Gravity.TOP | Gravity.LEFT, location[0], location[1] - v.getHeight());
    }

    public void popDismiss() {
        if (popupWindow != null) {
            popupWindow.dismiss();
        }
        if (gifDrawable != null) {
            gifDrawable.recycle();
        }
        popupWindow = null;
        gifDrawable = null;
        gifImageView = null;
        stickerImageView = null;
    }
}
