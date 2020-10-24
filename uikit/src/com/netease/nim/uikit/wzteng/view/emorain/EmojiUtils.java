package com.netease.nim.uikit.wzteng.view.emorain;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by WZTENG on 2018/2/2 0002.
 */

public class EmojiUtils {

    private static Bitmap getImageFromAssetsFile(Context context, String fileName) {
        Bitmap image = null;
        AssetManager am = context.getResources().getAssets();
        try {
            InputStream is = am.open(fileName);
            image = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    public static List<Bitmap> getBitmaps(Context context, String text) {
        List<Bitmap> bitmaps = new ArrayList<>();
        if (text != null && text.contains("生日快乐")) {
            bitmaps.add(getImageFromAssetsFile(context, "emoji/default/emoji_75.png"));
            return bitmaps;
        } else if (text != null && text.contains("我爱你")) {
            bitmaps.add(getImageFromAssetsFile(context, "emoji/default/emoji_45.png"));
            return bitmaps;
        } else if (text != null && text.contains("么么哒")) {
            bitmaps.add(getImageFromAssetsFile(context, "emoji/default/emoji_48.png"));
            return bitmaps;
        } else {
            return null;
        }
    }

}
