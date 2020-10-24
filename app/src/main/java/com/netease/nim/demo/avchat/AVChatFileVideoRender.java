package com.netease.nim.demo.avchat;


import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;

import com.netease.nimlib.sdk.avchat.model.AVChatExternalVideoRender;
import com.netease.nimlib.sdk.avchat.model.AVChatI420Frame;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

/**
 * Created by liuqijun on 2/27/17.
 */
public class AVChatFileVideoRender extends AVChatExternalVideoRender {

    private OutputStream outputStream;
    private int width;
    private int height;

    @Override
    public void onFrame(AVChatI420Frame i420Frame, int rotation) {
        int size = i420Frame.calcBufferSize(AVChatI420Frame.AVChatVideoFormat.kVideoI420);
        ByteBuffer i420Buffer = ByteBuffer.allocateDirect(size);
        if (i420Frame.convertFrame(AVChatI420Frame.AVChatVideoFormat.kVideoI420, 0, i420Buffer)) {
            if (needCreateFile(i420Frame.width(), i420Frame.height())) {
                createFile(i420Frame.width(), i420Frame.height());
            }

            if (outputStream != null) {
                try {
                    outputStream.write("FRAME\n".getBytes());
                    byte[] data = new byte[size];
                    i420Buffer.get(data);
                    outputStream.write(data);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        i420Frame.release();
    }

    @Override
    public boolean initialize() {
        return true;
    }

    @Override
    public void release() {
        super.release();
        if (outputStream != null) {
            try {
                outputStream.close();
                outputStream = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean needCreateFile(int w, int h) {
        return outputStream == null || width != w || height != h;
    }

    private boolean createFile(int w, int h) {
        String fileName = getAttachedSession() + "_" + w + "x" + h + "_" + SystemClock.elapsedRealtime() + ".y4m";
        Log.i("AVChatFileVideoRender", "create file -> " + fileName);
        if (outputStream != null) {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        final String videoOutPath = Environment.getExternalStorageDirectory().getPath() + "/" + fileName;
        try {
            outputStream = new BufferedOutputStream(new FileOutputStream(videoOutPath));
            outputStream.write(("YUV4MPEG2 C420 W" + w + " H" + h + " Ip F30:1 A1:1\n").getBytes());
            width = w;
            height = h;
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }


}
