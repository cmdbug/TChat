package com.netease.nim.demo.wzteng.qrcode;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Vibrator;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.jude.swipbackhelper.SwipeBackHelper;
import com.netease.nim.demo.R;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.model.ToolBarOptions;

import cn.bingoogolapple.qrcode.core.BGAQRCodeUtil;
import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zxing.ZXingView;

public class QrCodeActivity extends UI implements QRCodeView.Delegate {
    private static final String TAG = QrCodeActivity.class.getSimpleName();

    private QRCodeView mQRCodeView;

    private ImageView ivSpot;
    private ImageView ivCover;
    private ImageView ivStreet;
    private ImageView ivTranslation;

    public static void start(Context context) {
        start(context, null);
    }

    public static void start(Context context, Intent extras) {
        Intent intent = new Intent();
        intent.setClass(context, QrCodeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        if (extras != null) {
            intent.putExtras(extras);
        }
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code);

        ToolBarOptions options = new ToolBarOptions();
        options.titleString = "扫一扫";
        setToolBar(R.id.toolbar, options);

        SwipeBackHelper.getCurrentPage(this).setSwipeBackEnable(false);//禁止滑动退出

        mQRCodeView = (ZXingView) findViewById(R.id.zxingview);
        mQRCodeView.setDelegate(this);

        ivSpot = findView(R.id.start_spot);
        ivCover = findView(R.id.start_cover);
        ivStreet = findView(R.id.start_street);
        ivTranslation = findView(R.id.start_translation);
        ivSpot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int wd = (BGAQRCodeUtil.getScreenResolution(QrCodeActivity.this).x - BGAQRCodeUtil.dp2px(QrCodeActivity.this, 200)) / 2;
                mQRCodeView.setScanBoxViewFramingRect(
                        new Rect(wd,
                                BGAQRCodeUtil.dp2px(QrCodeActivity.this, 146),
                                wd + BGAQRCodeUtil.dp2px(QrCodeActivity.this, 200),
                                BGAQRCodeUtil.dp2px(QrCodeActivity.this, 146 + 200)
                        )
                );
                mQRCodeView.setMyTipText("将二维码/条形码放入框内，即可自动扫描");
                mQRCodeView.setShowScanLine(true);
                ivSpot.setImageResource(R.drawable.scan_qr_hl);
                ivCover.setImageResource(R.drawable.scan_book);
                ivStreet.setImageResource(R.drawable.scan_street);
                ivTranslation.setImageResource(R.drawable.scan_word);
            }
        });
        ivCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int wd = (BGAQRCodeUtil.getScreenResolution(QrCodeActivity.this).x - BGAQRCodeUtil.dp2px(QrCodeActivity.this, 250)) / 2;
                mQRCodeView.setScanBoxViewFramingRect(
                        new Rect(wd,
                                BGAQRCodeUtil.dp2px(QrCodeActivity.this, 146),
                                wd + BGAQRCodeUtil.dp2px(QrCodeActivity.this, 250),
                                BGAQRCodeUtil.dp2px(QrCodeActivity.this, 146 + 300)
                        )
                );
                mQRCodeView.setMyTipText("将书、CD、电影海报放入框内，即可自动扫描");
                mQRCodeView.setShowScanLine(true);
                ivSpot.setImageResource(R.drawable.scan_qr);
                ivCover.setImageResource(R.drawable.scan_book_hl);
                ivStreet.setImageResource(R.drawable.scan_street);
                ivTranslation.setImageResource(R.drawable.scan_word);
            }
        });
        ivStreet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int wd = (BGAQRCodeUtil.getScreenResolution(QrCodeActivity.this).x - BGAQRCodeUtil.dp2px(QrCodeActivity.this, 300)) / 2;
                mQRCodeView.setScanBoxViewFramingRect(
                        new Rect(wd,
                                BGAQRCodeUtil.dp2px(QrCodeActivity.this, 146),
                                wd + BGAQRCodeUtil.dp2px(QrCodeActivity.this, 300),
                                BGAQRCodeUtil.dp2px(QrCodeActivity.this, 146 + 350)
                        )
                );
                mQRCodeView.setMyTipText("扫一扫周围环境，寻找附近街景");
                mQRCodeView.setShowScanLine(true);
                ivSpot.setImageResource(R.drawable.scan_qr);
                ivCover.setImageResource(R.drawable.scan_book);
                ivStreet.setImageResource(R.drawable.scan_street_hl);
                ivTranslation.setImageResource(R.drawable.scan_word);
            }
        });
        ivTranslation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int wd = (BGAQRCodeUtil.getScreenResolution(QrCodeActivity.this).x - BGAQRCodeUtil.dp2px(QrCodeActivity.this, 200)) / 2;
                mQRCodeView.setScanBoxViewFramingRect(
                        new Rect(wd,
                                BGAQRCodeUtil.dp2px(QrCodeActivity.this, 146),
                                wd + BGAQRCodeUtil.dp2px(QrCodeActivity.this, 200),
                                BGAQRCodeUtil.dp2px(QrCodeActivity.this, 146 + 50)
                        )
                );
                mQRCodeView.setMyTipText("将英文单词放入框内");
                mQRCodeView.setShowScanLine(false);
                ivSpot.setImageResource(R.drawable.scan_qr);
                ivCover.setImageResource(R.drawable.scan_book);
                ivStreet.setImageResource(R.drawable.scan_street);
                ivTranslation.setImageResource(R.drawable.scan_word_hl);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mQRCodeView.startCamera();
//        mQRCodeView.startCamera(Camera.CameraInfo.CAMERA_FACING_FRONT);
        mQRCodeView.showScanRect();
        mQRCodeView.startSpot();

    }

    @Override
    protected void onStop() {
        mQRCodeView.stopCamera();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mQRCodeView.onDestroy();
        super.onDestroy();
    }

    private void vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(150);
    }

    @Override
    public void onScanQRCodeSuccess(String result) {
        Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
//        vibrate();
//        mQRCodeView.setScanBoxViewFramingRect(new Rect(110,110,300,400));
        mQRCodeView.startSpot();
    }

    @Override
    public void onScanQRCodeOpenCameraError() {
        Log.e(TAG, "打开相机出错");
    }
}
