package com.netease.nim.demo.wzteng.friends.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.Toast;

import com.cjt2325.cameralibrary.FileUtil;
import com.cjt2325.cameralibrary.JCameraView;
import com.netease.nim.demo.R;
import com.netease.nim.uikit.common.activity.UI;


public class CameraActivity extends UI {
    private JCameraView mJCameraView;

    public static void start(Context context) {
        start(context, null);
    }

    public static void start(Context context, Intent extras) {
        Intent intent = new Intent();
        intent.setClass(context, CameraActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        if (extras != null) {
            intent.putExtras(extras);
        }
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        mJCameraView = findView(R.id.cameraview);
        mJCameraView.setAutoFoucs(true);
//        mJCameraView.setSaveVideoPath(Environment.getExternalStorageDirectory().getPath());
        mJCameraView.setCameraViewListener(new JCameraView.CameraViewListener() {
            @Override
            public void quit() {
                CameraActivity.this.finish();
            }

            @Override
            public void captureSuccess(Bitmap bitmap) {
                String path = FileUtil.saveBitmap(bitmap, CameraActivity.this);
//                Toast.makeText(CameraActivity.this, "获取到照片路径:" + path, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void recordSuccess(String url) {
//                Toast.makeText(CameraActivity.this, "获取到视频路径:" + url, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mJCameraView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mJCameraView.onPause();
    }
}
