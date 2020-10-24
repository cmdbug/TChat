package com.netease.nim.demo.avchat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.SoundPool;

import com.netease.nim.demo.DemoCache;
import com.netease.nim.demo.R;
import com.netease.nim.uikit.common.util.log.LogUtil;


/**
 * SoundPool 铃声尽量不要超过1M
 * 在不同的系统下 SoundPool 表现可能存在不一致
 */
public class AVChatSoundPlayer {

    private static final String TAG = "AVChatSoundPlayer";

    public enum RingerTypeEnum {
        CONNECTING,
        NO_RESPONSE,
        PEER_BUSY,
        PEER_REJECT,
        RING,
        ;
    }
    private Context context;

    private SoundPool soundPool;
    private AudioManager audioManager;
    private int streamId;
    private int soundId;
    private boolean loop;
    private RingerTypeEnum ringerTypeEnum;
    private boolean isRingModeRegister = false;
    private int ringMode = -1;

    private static AVChatSoundPlayer instance = null;
    private RingModeChangeReceiver ringModeChangeReceiver;

    public static AVChatSoundPlayer instance() {
        if(instance == null) {
            synchronized (AVChatSoundPlayer.class) {
                if(instance == null) {
                    instance = new AVChatSoundPlayer();
                }
            }
        }
        return instance;
    }

    public AVChatSoundPlayer() {
        this.context = DemoCache.getContext();
    }

    public synchronized void play(RingerTypeEnum type) {
        LogUtil.d(TAG, "play type->" + type.name());
        this.ringerTypeEnum = type;
        int ringId = 0;
        switch (type) {
        case NO_RESPONSE:
            ringId = R.raw.avchat_no_response;
            loop = false;
            break;
        case PEER_BUSY:
            ringId = R.raw.avchat_peer_busy;
            loop = false;
            break;
        case PEER_REJECT:
            ringId = R.raw.avchat_peer_reject;
            loop = false;
            break;
        case CONNECTING:
            ringId = R.raw.avchat_connecting;
            loop = true;
            break;
        case RING:
            ringId = R.raw.avchat_ring;
            loop = true;
            break;
        }

        if(ringId != 0) {
            play(ringId);
        }

    }

    public void stop() {
        LogUtil.d(TAG, "stop");
        if (soundPool != null) {
            if (streamId != 0) {
                soundPool.stop(streamId);
                streamId = 0;
            }
            if (soundId != 0) {
                soundPool.unload(soundId);
                soundId = 0;
            }
        }
        if (isRingModeRegister) {
            registerVolumeReceiver(false);
        }
    }

    private void play(int ringId) {
        initSoundPool();
        if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL) {
            soundId = soundPool.load(context, ringId, 1);
        }
    }

    private void initSoundPool() {
        stop();
        if (soundPool == null) {
            soundPool = new SoundPool(1, AudioManager.STREAM_RING, 0);
            soundPool.setOnLoadCompleteListener(onLoadCompleteListener);

            audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            ringMode = audioManager.getRingerMode();
        }
        registerVolumeReceiver(true);
    }

    SoundPool.OnLoadCompleteListener onLoadCompleteListener = new SoundPool.OnLoadCompleteListener() {
        @Override
        public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
            if (soundId != 0 && status == 0) {
                if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL) {
                    int curVolume = audioManager.getStreamVolume(AudioManager.STREAM_RING);
                    streamId = soundPool.play(soundId, curVolume, curVolume, 1, loop ? -1 : 0, 1f);
                }
            }
        }
    };

    private void registerVolumeReceiver(boolean register){
        if (ringModeChangeReceiver == null) {
            ringModeChangeReceiver = new RingModeChangeReceiver() ;
        }

        if (register) {
            isRingModeRegister = true;
            IntentFilter filter = new IntentFilter() ;
            filter.addAction(AudioManager.RINGER_MODE_CHANGED_ACTION) ;
            context.registerReceiver(ringModeChangeReceiver, filter) ;
        } else {
            context.unregisterReceiver(ringModeChangeReceiver);
            isRingModeRegister = false;
        }
    }

    private class RingModeChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ringMode != -1 && ringMode != audioManager.getRingerMode()
                    && intent.getAction().equals(AudioManager.RINGER_MODE_CHANGED_ACTION)) {
                ringMode = audioManager.getRingerMode();
                play(ringerTypeEnum);
            }
        }
    }
}
