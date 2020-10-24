package com.netease.nim.demo.jsbridge;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.netease.nim.demo.R;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.common.media.picker.PickImageHelper;
import com.netease.nim.uikit.model.ToolBarOptions;
import com.netease.nimlib.jsbridge.core.NIMJsBridge;
import com.netease.nimlib.jsbridge.core.NIMJsBridgeBuilder;
import com.netease.nimlib.jsbridge.extension.ImageInfo;
import com.netease.nimlib.jsbridge.interact.ResponseCode;
import com.netease.nimlib.jsbridge.interfaces.IJavaReplyToJsImageInfo;
import com.netease.nimlib.jsbridge.util.Base64;
import com.netease.nimlib.jsbridge.util.WebViewConfig;

/**
 * Created by hzliuxuanlin on 2016/10/21.
 */

public class JsBridgeActivity extends UI {

    /**
     * 本地资源页面
     */
    private static final String LOCAL_ASSET_HTML = "file:///android_asset/js/page.html";
    private static final int IMAGE_PICKER_REQUEST_ID = 2233;
    private NIMJsBridge jsBridge;
    private WebView webView;
    private IJavaReplyToJsImageInfo pickPictureCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.js_bridge_activity);

        ToolBarOptions options = new ToolBarOptions();
        options.titleId = R.string.js_bridge_demonstration;
        setToolBar(R.id.toolbar, options);

        initWebView();
        initData();
    }

    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.destroy();
        }

        super.onDestroy();
    }

    public static final void start(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, JsBridgeActivity.class);
        context.startActivity(intent);
    }

    private void initWebView() {
        webView = findView(R.id.webView);

        WebSettings settings = webView.getSettings();
        WebViewConfig.setWebSettings(this, settings, this.getApplicationInfo().dataDir);
        WebViewConfig.removeJavascriptInterfaces(webView);
        WebViewConfig.setWebViewAllowDebug(false);
        WebViewConfig.setAcceptThirdPartyCookies(webView);

        webView.loadUrl(LOCAL_ASSET_HTML);
    }

    private void initData() {
        JavaInterfaces javaInterfaces = new JavaInterfaces(this);
        jsBridge = new NIMJsBridgeBuilder().addJavaInterfaceForJS(javaInterfaces)
                .setWebView(webView).create();
    }

    public void pickPicture(IJavaReplyToJsImageInfo cb) {
        this.pickPictureCallback = cb;
        PickImageHelper.PickImageOption option = new PickImageHelper.PickImageOption();
        option.titleResId = R.string.set_head_image;
        option.crop = true;
        option.multiSelect = false;
        option.cropOutputImageWidth = 720;
        option.cropOutputImageHeight = 720;
        PickImageHelper.pickImage(JsBridgeActivity.this, IMAGE_PICKER_REQUEST_ID, option);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICKER_REQUEST_ID) {
            ImageInfo pictureInfo = new ImageInfo();
            pictureInfo.path = data.getStringExtra(com.netease.nim.uikit.session.constant.Extras.EXTRA_FILE_PATH);

            if (!TextUtils.isEmpty(pictureInfo.path)) {
                pictureInfo.base64 = Base64.encodeFile(pictureInfo.path);
                Log.i("demo", "choose picture:" + pictureInfo.toString());
            }

            if (pickPictureCallback != null) {
                this.pickPictureCallback.replyToJs(ResponseCode.RES_SUCCESS, "success", pictureInfo);
            }
        }
    }
}
