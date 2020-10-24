package com.netease.nim.demo.wzteng.friends;

import android.content.Context;
import android.os.Environment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;
import com.bumptech.glide.module.GlideModule;

import java.io.File;

/**
 * Created by suneee on 2016/6/6.
 */
public class MyGlideModule implements GlideModule {
//自定义GlideModule只需要在Manifest文件中加入meta-data
    // 默认存放图片的路径
    public final static String DEFAULT_SAVE_IMAGE_PATH = Environment.getExternalStorageDirectory()
            + File.separator + "com.netease.nim.demo.friends"
            + File.separator + "images" + File.separator;

    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        builder.setDiskCache(
                new InternalCacheDiskCacheFactory(context, DEFAULT_SAVE_IMAGE_PATH, 50 * 1024 * 1024));
    }

    @Override
    public void registerComponents(Context context, Glide glide) {

    }
}
