package com.netease.nim.demo.session.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.netease.nim.demo.R;
import com.netease.nim.demo.session.extension.SnapChatAttachment;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.common.ui.dialog.CustomAlertDialog;
import com.netease.nim.uikit.common.ui.imageview.BaseZoomableImageView;
import com.netease.nim.uikit.common.util.media.BitmapDecoder;
import com.netease.nim.uikit.common.util.media.ImageUtil;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.constant.AttachStatusEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;


/**
 * 查看阅后即焚消息原图
 */
public class WatchSnapChatPictureActivity extends UI {
    private static final String INTENT_EXTRA_IMAGE = "INTENT_EXTRA_IMAGE";

    private Handler handler;
    private IMMessage message;

    private View loadingLayout;
    private BaseZoomableImageView image;
    protected CustomAlertDialog alertDialog;

    private static WatchSnapChatPictureActivity instance;

    public static void start(Context context, IMMessage message) {
        Intent intent = new Intent();
        intent.putExtra(INTENT_EXTRA_IMAGE, message);
        intent.setClass(context, WatchSnapChatPictureActivity.class);
        context.startActivity(intent);
    }

    public static void destroy() {
        if (instance != null) {
            instance.finish();
            instance = null;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nim_watch_snapchat_activity);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        onParseIntent();
        findViews();

        handler = new Handler();
        registerObservers(true);
        requestOriImage();

        instance = this;
    }

    @Override
    protected void onDestroy() {
        registerObservers(false);
        super.onDestroy();
        instance = null;
    }

    private void onParseIntent() {
        this.message = (IMMessage) getIntent().getSerializableExtra(INTENT_EXTRA_IMAGE);
    }

    private void findViews() {
        alertDialog = new CustomAlertDialog(this);
        loadingLayout = findViewById(R.id.loading_layout);
        image = (BaseZoomableImageView) findViewById(R.id.watch_image_view);
    }

    private void requestOriImage() {
        if (isOriginImageHasDownloaded(message)) {
            onDownloadSuccess(message);
            return;
        }

        // async download original image
        onDownloadStart(message);
        NIMClient.getService(MsgService.class).downloadAttachment(message, false);
    }

    private boolean isOriginImageHasDownloaded(final IMMessage message) {
        if (message.getAttachStatus() == AttachStatusEnum.transferred &&
                !TextUtils.isEmpty(((SnapChatAttachment) message.getAttachment()).getPath())) {
            return true;
        }

        return false;
    }

    /**
     * ******************************** 设置图片 *********************************
     */

    private void setThumbnail() {
        String path = ((SnapChatAttachment) message.getAttachment()).getThumbPath();
        if (!TextUtils.isEmpty(path)) {
            Bitmap bitmap = BitmapDecoder.decodeSampledForDisplay(path);
            bitmap = ImageUtil.rotateBitmapInNeeded(path, bitmap);
            if (bitmap != null) {
                image.setImageBitmap(bitmap);
                return;
            }
        }

        image.setImageBitmap(ImageUtil.getBitmapFromDrawableRes(getImageResOnLoading()));
    }

    private void setImageView(final IMMessage msg) {
        String path = ((SnapChatAttachment) msg.getAttachment()).getPath();
        if (TextUtils.isEmpty(path)) {
            image.setImageBitmap(ImageUtil.getBitmapFromDrawableRes(getImageResOnLoading()));
            return;
        }

        Bitmap bitmap = BitmapDecoder.decodeSampledForDisplay(path, false);
        bitmap = ImageUtil.rotateBitmapInNeeded(path, bitmap);
        if (bitmap == null) {
            Toast.makeText(this, R.string.picker_image_error, Toast.LENGTH_LONG).show();
            image.setImageBitmap(ImageUtil.getBitmapFromDrawableRes(getImageResOnFailed()));
        } else {
            image.setImageBitmap(bitmap);
        }
    }

    private int getImageResOnLoading() {
        return R.drawable.nim_image_default;
    }

    private int getImageResOnFailed() {
        return R.drawable.nim_image_download_failed;
    }

    /**
     * ********************************* 下载 ****************************************
     */

    private void registerObservers(boolean register) {
        NIMClient.getService(MsgServiceObserve.class).observeMsgStatus(statusObserver, register);
    }

    private Observer<IMMessage> statusObserver = new Observer<IMMessage>() {
        @Override
        public void onEvent(IMMessage msg) {
            if (!msg.isTheSame(message) || isDestroyedCompatible()) {
                return;
            }
            if (msg.getAttachStatus() == AttachStatusEnum.transferred && isOriginImageHasDownloaded(msg)) {
                onDownloadSuccess(msg);
            } else if (msg.getAttachStatus() == AttachStatusEnum.fail) {
                onDownloadFailed();
            }
        }
    };

    private void onDownloadStart(final IMMessage msg) {
        setThumbnail();
        if(TextUtils.isEmpty(((SnapChatAttachment) msg.getAttachment()).getPath())){
            loadingLayout.setVisibility(View.VISIBLE);
        } else {
            loadingLayout.setVisibility(View.GONE);
        }

    }

    private void onDownloadSuccess(final IMMessage msg) {
        loadingLayout.setVisibility(View.GONE);
        handler.post(new Runnable() {

            @Override
            public void run() {
                setImageView(msg);
            }
        });
    }

    private void onDownloadFailed() {
        loadingLayout.setVisibility(View.GONE);
        image.setImageBitmap(ImageUtil.getBitmapFromDrawableRes(getImageResOnFailed()));
        Toast.makeText(this, R.string.download_picture_fail, Toast.LENGTH_LONG).show();
    }
}
