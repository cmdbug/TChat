package com.netease.nim.demo.wzteng.friends.widgets.music;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.media.audiofx.Visualizer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.jude.swipbackhelper.SwipeBackHelper;
import com.netease.nim.demo.R;
import com.netease.nim.demo.wzteng.friends.utils.DatasUtil;
import com.netease.nim.demo.wzteng.friends.widgets.music.executor.PlayOnlineMusic;
import com.netease.nim.demo.wzteng.friends.widgets.music.executor.SearchLrc;
import com.netease.nim.demo.wzteng.friends.widgets.music.http.HttpCallback;
import com.netease.nim.demo.wzteng.friends.widgets.music.http.HttpClient;
import com.netease.nim.demo.wzteng.friends.widgets.music.http.HttpInterceptor;
import com.netease.nim.demo.wzteng.friends.widgets.music.model.Music;
import com.netease.nim.demo.wzteng.friends.widgets.music.model.OnlineMusic;
import com.netease.nim.demo.wzteng.friends.widgets.music.model.OnlineMusicList;
import com.netease.nim.demo.wzteng.friends.widgets.music.model.SongListInfo;
import com.netease.nim.demo.wzteng.friends.widgets.music.service.OnPlayerEventListener;
import com.netease.nim.demo.wzteng.friends.widgets.music.service.PlayService;
import com.netease.nim.demo.wzteng.friends.widgets.music.utils.Actions;
import com.netease.nim.demo.wzteng.friends.widgets.music.utils.CoverLoader;
import com.netease.nim.demo.wzteng.friends.widgets.music.utils.FileUtils;
import com.netease.nim.demo.wzteng.friends.widgets.music.utils.PlayModeEnum;
import com.netease.nim.demo.wzteng.friends.widgets.music.utils.Preferences;
import com.netease.nim.demo.wzteng.friends.widgets.music.utils.SystemUtils;
import com.netease.nim.demo.wzteng.friends.widgets.music.utils.ToastUtils;
import com.netease.nim.demo.wzteng.friends.widgets.music.view.AlbumCoverView;
import com.netease.nim.demo.wzteng.friends.widgets.music.view.LrcView;
import com.netease.nim.demo.wzteng.friends.widgets.music.view.VisualView;
import com.netease.nim.uikit.common.activity.UI;
import com.zhy.http.okhttp.OkHttpUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class MusicActivity extends UI implements View.OnClickListener,
        ViewPager.OnPageChangeListener, SeekBar.OnSeekBarChangeListener, OnPlayerEventListener,
        LrcView.OnPlayClickListener {

    private ImageView ivPlayingBg;
    private ImageView ivBack;
    private TextView tvTitle;
    private TextView tvArtist;
    private ViewPager vpPlay;
    private IndicatorLayout ilIndicator;
    private SeekBar sbProgress;
    private TextView tvCurrentTime;
    private TextView tvTotalTime;
    private ImageView ivMode;
    private ImageView ivPlay;
    private ImageView ivNext;
    private ImageView ivPrev;
    private AlbumCoverView mAlbumCoverView;
    private LrcView mLrcViewSingle;
    private LrcView mLrcViewFull;
    private SeekBar sbVolume;

    private AudioManager mAudioManager;
    private List<View> mViewPagerContent;
    private int mLastProgress;
    private boolean isDraggingProgress;

    protected Handler mHandler = new Handler(Looper.getMainLooper());
    private ServiceConnection mPlayServiceConnection;

    //在线音乐 总的类型与子歌曲
    private List<SongListInfo> mSongLists;

    //选择的具体类型
    private SongListInfo mListInfo;

    //具体类型获取后的歌曲
    private OnlineMusicList mOnlineMusicList;
    //获取到的歌曲
    private List<OnlineMusic> mMusicList = new ArrayList<>();

    //获取的起点
    private int mOffset = 0;
    //一次获取多少曲
    private static final int MUSIC_LIST_SIZE = 15;

    private CaptureVisualizer captureVisualizer;
    private VisualView visualView;

    public static void start(Context context) {
        start(context, null);
    }

    public static void start(Context context, Intent extras) {
        Intent intent = new Intent();
        intent.setClass(context, MusicActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        if (extras != null) {
            intent.putExtras(extras);
        }
        context.startActivity(intent);
    }

    private void initOkHttpUtils() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .addInterceptor(new HttpInterceptor())
                .build();
        OkHttpUtils.initClient(okHttpClient);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_layout);

        SwipeBackHelper.getCurrentPage(this).setSwipeBackEnable(false);//禁止滑动退出

        MusicCache.get().init(MusicActivity.this);
        initOkHttpUtils();

        toStartService();
        toBindService();

        initView();
        initPlayMode();
        initVolume();
//        onChangeImpl(getPlayService().getPlayingMusic());
        initSongLists();
    }

    private void initVisualizer() {
        captureVisualizer = new CaptureVisualizer(getPlayService().getmPlayer(),
                new CaptureVisualizer.IDataCaptureListener() {
                    @Override
                    public void onWaveFormDataCapture(Visualizer visualizer, byte[] waveform, int samplingRate) {
//                        Log.i("visualizer", "size" + waveform.length + ",[0]" + waveform[0] + ",[1]" + waveform[1]);
                        visualView.onWaveFormDataCapture(visualizer, waveform, samplingRate);
                    }

                    @Override
                    public void onFftDataCapture(Visualizer visualizer, byte[] fft, int samplingRate) {
                        visualView.onFftDataCapture(visualizer, fft, samplingRate);
                    }
                });
        captureVisualizer.initVisualizer();
    }

    private void initSongLists() {
        mSongLists = MusicCache.get().getSongListInfos();
        if (mSongLists.isEmpty()) {
            String[] titles = getResources().getStringArray(R.array.online_music_list_title);
            String[] types = getResources().getStringArray(R.array.online_music_list_type);
            for (int i = 0; i < titles.length; i++) {
                SongListInfo info = new SongListInfo();
                info.setTitle(titles[i]);
                info.setType(types[i]);
                mSongLists.add(info);
            }
        }
//        PlaylistAdapter adapter = new PlaylistAdapter(mSongLists);
//        lvPlaylist.setAdapter(adapter);
        if (mSongLists.size() > 0) {
            int x = DatasUtil.getRandomNum(mSongLists.size());
            mListInfo = mSongLists.get(x);
            getMusicListInfo(mSongLists.get(x));
        }
    }

    /**
     * 总的类型与子歌曲
     *
     * @param songListInfo
     */
    private void getMusicListInfo(final SongListInfo songListInfo) {
        Log.i("wzt", "开始请求网络音乐专辑");
        ToastUtils.show("开始请求网络音乐");
        HttpClient.getSongListInfo(songListInfo.getType(), 3, 0, new HttpCallback<OnlineMusicList>() {
            @Override
            public void onSuccess(OnlineMusicList response) {
                Log.i("wzt", "请求网络音乐专辑列表成功");
                if (response == null || response.getSong_list() == null) {
                    return;
                }
                parse(response, songListInfo);
                getMusic(0);
            }

            @Override
            public void onFail(Exception e) {
                Log.i("wzt", "请求网络音乐专辑列表失败");
                ToastUtils.show("请求网络音乐专辑列表失败");
            }
        });

    }

    private void getMusic(final int offset) {
        Log.i("wzt", "开始获取专辑的歌曲列表");
        HttpClient.getSongListInfo(mListInfo.getType(), MUSIC_LIST_SIZE, offset, new HttpCallback<OnlineMusicList>() {
            @Override
            public void onSuccess(OnlineMusicList response) {
                Log.i("wzt", "获取专辑的歌曲列表成功");
                if (response == null || response.getSong_list() == null || response.getSong_list().size() == 0) {
                    return;
                }
                mOnlineMusicList = response;
                mOffset += MUSIC_LIST_SIZE;
                mMusicList.addAll(response.getSong_list());
                play(mMusicList.get(DatasUtil.getRandomNum(mMusicList.size())));
                initVisualizer();
            }

            @Override
            public void onFail(Exception e) {
                Log.i("wzt", "获取专辑的歌曲列表失败");
                ToastUtils.show("获取专辑的歌曲列表失败");
            }
        });
    }

    private void play(OnlineMusic onlineMusic) {
        new PlayOnlineMusic(this, onlineMusic) {
            @Override
            public void onPrepare() {
//                mProgressDialog.show();
            }

            @Override
            public void onExecuteSuccess(Music music) {
//                mProgressDialog.cancel();
                getPlayService().play(music);
//                ToastUtils.show("开始播放");
            }

            @Override
            public void onExecuteFail(Exception e) {
//                mProgressDialog.cancel();
                ToastUtils.show("无法播放该音乐");
            }
        }.execute();
    }

    private void parse(OnlineMusicList response, SongListInfo songListInfo) {
        List<OnlineMusic> onlineMusics = response.getSong_list();
        songListInfo.setCoverUrl(response.getBillboard().getPic_s260());
        if (onlineMusics.size() >= 1) {
            songListInfo.setMusic1(getString(R.string.song_list_item_title_1, onlineMusics.get(0).getTitle(), onlineMusics.get(0).getArtist_name()));
        } else {
            songListInfo.setMusic1("");
        }
        if (onlineMusics.size() >= 2) {
            songListInfo.setMusic2(getString(R.string.song_list_item_title_2, onlineMusics.get(1).getTitle(), onlineMusics.get(1).getArtist_name()));
        } else {
            songListInfo.setMusic2("");
        }
        if (onlineMusics.size() >= 3) {
            songListInfo.setMusic3(getString(R.string.song_list_item_title_3, onlineMusics.get(2).getTitle(), onlineMusics.get(2).getArtist_name()));
        } else {
            songListInfo.setMusic3("");
        }
    }


    private void initView() {
        View coverView = LayoutInflater.from(this).inflate(R.layout.fragment_play_page_cover, null);
        View lrcView = LayoutInflater.from(this).inflate(R.layout.fragment_play_page_lrc, null);
        ivPlayingBg = findView(R.id.iv_play_page_bg);
        ivBack = findView(R.id.iv_back);
        tvTitle = findView(R.id.tv_title);
        tvArtist = findView(R.id.tv_artist);
        vpPlay = findView(R.id.vp_play_page);
        ilIndicator = findView(R.id.il_indicator);
        sbProgress = findView(R.id.sb_progress);
        tvCurrentTime = findView(R.id.tv_current_time);
        tvTotalTime = findView(R.id.tv_total_time);
        ivMode = findView(R.id.iv_mode);
        ivPlay = findView(R.id.iv_play);
        ivNext = findView(R.id.iv_next);
        ivPrev = findView(R.id.iv_prev);
        ivMode.setVisibility(View.GONE);
        ivNext.setVisibility(View.GONE);
        ivPrev.setVisibility(View.GONE);
        mAlbumCoverView = coverView.findViewById(R.id.album_cover_view);
        mLrcViewSingle = coverView.findViewById(R.id.lrc_view_single);
        mLrcViewFull = lrcView.findViewById(R.id.lrc_view_full);
        sbVolume = lrcView.findViewById(R.id.sb_volume);

        visualView = findView(R.id.visualview);

        ivBack.setOnClickListener(this);
        ivMode.setOnClickListener(this);
        ivPlay.setOnClickListener(this);
        ivPrev.setOnClickListener(this);
        ivNext.setOnClickListener(this);
        sbProgress.setOnSeekBarChangeListener(this);
        sbVolume.setOnSeekBarChangeListener(this);
        vpPlay.addOnPageChangeListener(this);

//        mAlbumCoverView.initNeedle(getPlayService().isPlaying());
        mLrcViewFull.setOnPlayClickListener(this);

        mViewPagerContent = new ArrayList<>(2);
        mViewPagerContent.add(coverView);
        mViewPagerContent.add(lrcView);
        vpPlay.setAdapter(new PlayPagerAdapter(mViewPagerContent));

        ilIndicator.create(mViewPagerContent.size());
    }

    private void toStartService() {
        Intent intent = new Intent(this, PlayService.class);
        startService(intent);
    }

    private void toBindService() {
        Intent intent = new Intent();
        intent.setClass(this, PlayService.class);
        mPlayServiceConnection = new PlayServiceConnection();
        bindService(intent, mPlayServiceConnection, Context.BIND_AUTO_CREATE);
    }

    private void toStopService() {
        Intent stopIntent = new Intent(this, PlayService.class);
        stopService(stopIntent);
    }

    private class PlayServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            final PlayService playService = ((PlayService.PlayBinder) service).getService();
            MusicCache.get().setPlayService(playService);
            getPlayService().setOnPlayEventListener(MusicActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    }

    protected PlayService getPlayService() {
        PlayService playService = MusicCache.get().getPlayService();
        if (playService == null) {
            throw new NullPointerException("play service is null");
        }
        return playService;
    }

    private void initVolume() {
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        sbVolume.setMax(mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        sbVolume.setProgress(mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
    }

    private void initPlayMode() {
        int mode = Preferences.getPlayMode();
        ivMode.setImageLevel(mode);
    }

    @Override
    public void onChange(Music music) {
        onChangeImpl(music);
    }

    @Override
    public void onPlayerStart() {
        ivPlay.setSelected(true);
        mAlbumCoverView.start();
    }

    @Override
    public void onPlayerPause() {
        ivPlay.setSelected(false);
        mAlbumCoverView.pause();
    }

    /**
     * 更新播放进度
     */
    @Override
    public void onPublish(int progress) {
        if (!isDraggingProgress) {
            sbProgress.setProgress(progress);
        }

        if (mLrcViewSingle.hasLrc()) {
            mLrcViewSingle.updateTime(progress);
            mLrcViewFull.updateTime(progress);
        }
    }

    @Override
    public void onBufferingUpdate(int percent) {
        sbProgress.setSecondaryProgress(sbProgress.getMax() * 100 / percent);
    }

    @Override
    public void onTimer(long remain) {
    }

    @Override
    public void onMusicListUpdate() {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.iv_mode:
                switchPlayMode();
                break;
            case R.id.iv_play:
                play();
                break;
            case R.id.iv_next:
                next();
                break;
            case R.id.iv_prev:
                prev();
                break;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        ilIndicator.setCurrent(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (seekBar == sbProgress) {
            if (Math.abs(progress - mLastProgress) >= DateUtils.SECOND_IN_MILLIS) {
                tvCurrentTime.setText(formatTime(progress));
                mLastProgress = progress;
            }
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        if (seekBar == sbProgress) {
            isDraggingProgress = true;
        }
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (seekBar == sbProgress) {
            isDraggingProgress = false;
            if (getPlayService().isPlaying() || getPlayService().isPausing()) {
                int progress = seekBar.getProgress();
                getPlayService().seekTo(progress);

                if (mLrcViewSingle.hasLrc()) {
                    mLrcViewSingle.updateTime(progress);
                    mLrcViewFull.updateTime(progress);
                }
            } else {
                seekBar.setProgress(0);
            }
        } else if (seekBar == sbVolume) {
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, seekBar.getProgress(),
                    AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
        }
    }

    @Override
    public boolean onPlayClick(long time) {
        if (getPlayService().isPlaying() || getPlayService().isPausing()) {
            getPlayService().seekTo((int) time);
            if (getPlayService().isPausing()) {
                getPlayService().playPause();
            }
            return true;
        }
        return false;
    }

    @Override
    public void changeMusicProgress(long time) {
        if (getPlayService().isPlaying() || getPlayService().isPausing()) {
            getPlayService().seekTo((int) time);
//            if (getPlayService().isPausing()) {
//                getPlayService().playPause();
//            }
        }
    }

    private void onChangeImpl(Music music) {
        if (music == null) {
            return;
        }

        tvTitle.setText(music.getTitle());
        tvArtist.setText(music.getArtist());
        sbProgress.setProgress((int) getPlayService().getCurrentPosition());
        sbProgress.setSecondaryProgress(0);
        sbProgress.setMax((int) music.getDuration());
        mLastProgress = 0;
        tvCurrentTime.setText("00:00");
        tvTotalTime.setText(formatTime(music.getDuration()));
        setCoverAndBg(music);
        setLrc(music);
        if (getPlayService().isPlaying() || getPlayService().isPreparing()) {
            ivPlay.setSelected(true);
            mAlbumCoverView.start();
        } else {
            ivPlay.setSelected(false);
            mAlbumCoverView.pause();
        }
    }

    private void play() {
        getPlayService().playPause();
    }

    private void next() {
        getPlayService().next();
    }

    private void prev() {
        getPlayService().prev();
    }

    private void switchPlayMode() {
        PlayModeEnum mode = PlayModeEnum.valueOf(Preferences.getPlayMode());
        switch (mode) {
            case LOOP:
                mode = PlayModeEnum.SHUFFLE;
                ToastUtils.show("随机播放");
                break;
            case SHUFFLE:
                mode = PlayModeEnum.SINGLE;
                ToastUtils.show("单曲循环");
                break;
            case SINGLE:
                mode = PlayModeEnum.LOOP;
                ToastUtils.show("列表循环");
                break;
        }
        Preferences.savePlayMode(mode.value());
        initPlayMode();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ivBack.setEnabled(false);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ivBack.setEnabled(true);
            }
        }, 300);
    }

    private void setCoverAndBg(Music music) {
        mAlbumCoverView.setCoverBitmap(CoverLoader.getInstance().loadRound(music));
        ivPlayingBg.setImageBitmap(CoverLoader.getInstance().loadBlur(music));
    }

    private void setLrc(final Music music) {
        if (music.getType() == Music.Type.LOCAL) {
            String lrcPath = FileUtils.getLrcFilePath(music);
            if (!TextUtils.isEmpty(lrcPath)) {
                loadLrc(lrcPath);
            } else {
                new SearchLrc(music.getArtist(), music.getTitle()) {
                    @Override
                    public void onPrepare() {
                        // 设置tag防止歌词下载完成后已切换歌曲
                        vpPlay.setTag(music);
                        loadLrc("");
                        setLrcLabel("正在搜索歌词");
                    }

                    @Override
                    public void onExecuteSuccess(@NonNull String lrcPath) {
                        if (vpPlay.getTag() != music) {
                            return;
                        }
                        // 清除tag
                        vpPlay.setTag(null);
                        loadLrc(lrcPath);
                        setLrcLabel("暂无歌词");
                    }

                    @Override
                    public void onExecuteFail(Exception e) {
                        if (vpPlay.getTag() != music) {
                            return;
                        }
                        // 清除tag
                        vpPlay.setTag(null);
                        setLrcLabel("暂无歌词");
                    }
                }.execute();
            }
        } else {
            String lrcPath = FileUtils.getLrcDir() + FileUtils.getLrcFileName(music.getArtist(), music.getTitle());
            loadLrc(lrcPath);
        }
    }

    private void loadLrc(String path) {
        File file = new File(path);
        mLrcViewSingle.loadLrc(file);
        mLrcViewFull.loadLrc(file);
    }

    private void setLrcLabel(String label) {
        mLrcViewSingle.setLabel(label);
        mLrcViewFull.setLabel(label);
    }

    private String formatTime(long time) {
        return SystemUtils.formatTime("mm:ss", time);
    }

    private BroadcastReceiver mVolumeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            sbVolume.setProgress(mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(Actions.VOLUME_CHANGED_ACTION);
        registerReceiver(mVolumeReceiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mVolumeReceiver != null) {
            unregisterReceiver(mVolumeReceiver);
        }
        if (mPlayServiceConnection != null) {
            unbindService(mPlayServiceConnection);
        }
        PlayService service = getPlayService();
        if (service != null) {
            service.setOnPlayEventListener(null);
        }
        toStopService();

        if (captureVisualizer != null) {
            captureVisualizer.onDestroy();
        }
    }

}
