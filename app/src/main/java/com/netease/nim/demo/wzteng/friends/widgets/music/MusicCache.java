package com.netease.nim.demo.wzteng.friends.widgets.music;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.util.LongSparseArray;
import android.util.Log;

import com.netease.nim.demo.wzteng.friends.widgets.music.executor.DownloadMusicInfo;
import com.netease.nim.demo.wzteng.friends.widgets.music.model.Music;
import com.netease.nim.demo.wzteng.friends.widgets.music.model.SongListInfo;
import com.netease.nim.demo.wzteng.friends.widgets.music.service.PlayService;
import com.netease.nim.demo.wzteng.friends.widgets.music.utils.CoverLoader;
import com.netease.nim.demo.wzteng.friends.widgets.music.utils.Preferences;
import com.netease.nim.demo.wzteng.friends.widgets.music.utils.ScreenUtils;
import com.netease.nim.demo.wzteng.friends.widgets.music.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

public class MusicCache {
    private Context mContext;
    private PlayService mPlayService;
    // 本地歌曲列表
    private final List<Music> mMusicList = new ArrayList<>();
    // 歌单列表
    private final List<SongListInfo> mSongListInfos = new ArrayList<>();
    private final List<Activity> mActivityStack = new ArrayList<>();
    private final LongSparseArray<DownloadMusicInfo> mDownloadList = new LongSparseArray<>();

    private MusicCache() {
    }

    private static class SingletonHolder {
        private static MusicCache instance = new MusicCache();
    }

    public static MusicCache get() {
        return SingletonHolder.instance;
    }

    public void init(Context application) {
        mContext = application.getApplicationContext();
        ToastUtils.init(mContext);
        Preferences.init(mContext);
        ScreenUtils.init(mContext);
        CoverLoader.getInstance().init(mContext);
//        application.registerActivityLifecycleCallbacks(new ActivityLifecycle());
    }

    public Context getContext() {
        return mContext;
    }

    public PlayService getPlayService() {
        return mPlayService;
    }

    public void setPlayService(PlayService service) {
        mPlayService = service;
    }

    public List<Music> getMusicList() {
        return mMusicList;
    }

    public List<SongListInfo> getSongListInfos() {
        return mSongListInfos;
    }

    public void clearStack() {
        List<Activity> activityStack = mActivityStack;
        for (int i = activityStack.size() - 1; i >= 0; i--) {
            Activity activity = activityStack.get(i);
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
        activityStack.clear();
    }

    public LongSparseArray<DownloadMusicInfo> getDownloadList() {
        return mDownloadList;
    }

    private class ActivityLifecycle implements Application.ActivityLifecycleCallbacks {
        private static final String TAG = "Activity";

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            Log.i(TAG, "onCreate: " + activity.getClass().getSimpleName());
            mActivityStack.add(activity);
        }

        @Override
        public void onActivityStarted(Activity activity) {
        }

        @Override
        public void onActivityResumed(Activity activity) {
        }

        @Override
        public void onActivityPaused(Activity activity) {
        }

        @Override
        public void onActivityStopped(Activity activity) {
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            Log.i(TAG, "onDestroy: " + activity.getClass().getSimpleName());
            mActivityStack.remove(activity);
        }
    }
}
