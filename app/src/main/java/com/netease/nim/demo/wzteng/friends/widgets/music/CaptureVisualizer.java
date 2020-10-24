package com.netease.nim.demo.wzteng.friends.widgets.music;

import android.media.MediaPlayer;
import android.media.audiofx.Equalizer;
import android.media.audiofx.Visualizer;

/**
 * Created by WZTENG on 2018/1/26 0026.
 */

public class CaptureVisualizer {

    private Visualizer visualizer;//频谱器
    private Equalizer equalizer; //均衡器
    private IDataCaptureListener dataCaptureListener;

    public CaptureVisualizer(MediaPlayer mediaPlayer, IDataCaptureListener iOnWaveFormDataCapture) {
        this.dataCaptureListener = iOnWaveFormDataCapture;
        if (mediaPlayer != null) {
            visualizer = new Visualizer(mediaPlayer.getAudioSessionId());
            equalizer = new Equalizer(0, mediaPlayer.getAudioSessionId());
        }
    }

    public void initVisualizer() {
        visualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[0]);//1:1024 0:128
        //第一个是监听者，第二个单位是毫赫兹，表示的是采集的频率，第三个是是否采集波形，第四个是是否采集频率
        visualizer.setDataCaptureListener(new Visualizer.OnDataCaptureListener() {

            //这个回调应该采集的是波形数据
            @Override
            public void onWaveFormDataCapture(Visualizer visualizer, byte[] waveform, int samplingRate) {
                if (dataCaptureListener != null) {
                    dataCaptureListener.onWaveFormDataCapture(visualizer, waveform, samplingRate);
                }
            }

            @Override
            public void onFftDataCapture(Visualizer visualizer, byte[] fft, int samplingRate) {
                if (dataCaptureListener != null) {
                    dataCaptureListener.onFftDataCapture(visualizer, fft, samplingRate);
                }
            }
        }, Visualizer.getMaxCaptureRate() / 2, false, true);
        visualizer.setEnabled(true);

        equalizer.setEnabled(true);// 启用均衡器

//        // 通过均衡器得到其支持的频谱引擎
//        short bands = equalizer.getNumberOfBands();
//
//        // 第一个下标为最低的限度范围
//        // 第二个下标为最大的上限,依次取出
//        final short minEqualizer = equalizer.getBandLevelRange()[0];
//        final short maxEqualizer = equalizer.getBandLevelRange()[1];
    }

    public interface IDataCaptureListener {
        void onWaveFormDataCapture(Visualizer visualizer, byte[] waveform, int samplingRate);
        void onFftDataCapture(Visualizer visualizer, byte[] fft, int samplingRate);
    }

    public void onDestroy() {
        if (visualizer != null) {
            visualizer.setEnabled(false);
            visualizer.release();
        }
        if (equalizer != null) {
            equalizer.setEnabled(false);
            equalizer.release();
        }
    }

}
