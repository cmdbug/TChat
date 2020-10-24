package com.netease.nim.demo.wzteng.webview;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.netease.nim.demo.R;
import com.netease.nim.demo.wzteng.others.websharedialog.BottomDialog;
import com.netease.nim.demo.wzteng.others.websharedialog.Item;
import com.netease.nim.demo.wzteng.others.websharedialog.OnItemClickListener;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.model.ToolBarOptions;
import com.tencent.smtt.export.external.extension.interfaces.IX5WebViewExtension;
import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

public class WebViewActivity extends UI {
    private WebView tWebview;//tencentX5
    private ProgressBar progressBar;
    private ImageView ivMore;
    public static boolean isX5WebViewEnabled = false;
    private static String url = "";
    private static String title = "";

    private TextView tvTitle;
    private ImageView ivClose;
    private ImageView ivStop;

    public static void start(String url, String title, Context context) {
        WebViewActivity.url = url;
        WebViewActivity.title = title;
        start(context);
    }

    private static void start(Context context) {
        start(context, null);
    }

    private static void start(Context context, Intent extras) {
        Intent intent = new Intent();
        intent.setClass(context, WebViewActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        if (extras != null) {
            intent.putExtras(extras);
        }
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
//        ToolBarOptions options = new ToolBarOptions();
//        options.titleString = title;
//        options.navigateId = R.drawable.back;
//        setToolBar(R.id.toolbar, options);

        initUI();
    }

    private void initUI() {
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        progressBar = findView(R.id.pb_progressbar);
        ivMore = findView(R.id.iv_more);
        ivMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new BottomDialog(WebViewActivity.this)
                        .title(R.string.share_title)
                        .orientation(BottomDialog.HORIZONTAL)
                        .layout(BottomDialog.LINEAR)              //设置内容 layout,默认为线性(LinearLayout)
                        .inflateMenu(R.menu.websharedialog_menu_share, new OnItemClickListener() {
                            @Override
                            public void click(Item item) {
                                Toast.makeText(WebViewActivity.this, getString(R.string.share_title) + item.getTitle(), Toast.LENGTH_SHORT).show();
                            }
                        })
                        .inflateMenu(R.menu.websharedialog_menu_main, new OnItemClickListener() {
                            @Override
                            public void click(Item item) {
                                Toast.makeText(WebViewActivity.this, item.getTitle(), Toast.LENGTH_SHORT).show();
                            }
                        }).show();
            }
        });

        tvTitle = findView(R.id.tv_title);
        tvTitle.setText(title);
        ivClose = findView(R.id.iv_close);
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ivStop = findView(R.id.iv_stop);

        preinitX5WebCore();
        /**
         //预加载x5内核
         Intent intent = new Intent(this, AdvanceLoadX5Service.class);
         startService(intent);
         */
        tWebview = findView(R.id.tencent_webview);
        //去掉右侧滚动条
        IX5WebViewExtension ix5 = tWebview.getX5WebViewExtension();
        if (null != ix5) {
            ix5.setScrollBarFadingEnabled(false);
        }
        WebSettings webSettings = tWebview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setUseWideViewPort(false);
        webSettings.setSupportMultipleWindows(false);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setAppCacheEnabled(false);
        webSettings.setDatabaseEnabled(false);
        webSettings.setDomStorageEnabled(true);
        webSettings.setGeolocationEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setLoadsImagesAutomatically(true);
//        tWebview.setLayerType();
        tWebview.setDrawingCacheEnabled(true);
        tWebview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageStarted(WebView webView, String s, Bitmap bitmap) {
                super.onPageStarted(webView, s, bitmap);
                ivStop.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView webView, String s) {
                super.onPageFinished(webView, s);

//                ToolBarOptions options = new ToolBarOptions();
//                options.titleString = webView.getTitle();
//                options.navigateId = R.drawable.back;
//                setToolBar(R.id.toolbar, options);
                tvTitle.setText(webView.getTitle());
                ivStop.setVisibility(View.GONE);
            }
        });
        tWebview.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress == 100) {
                    progressBar.setProgress(newProgress);
                    progressBar.setVisibility(View.GONE);
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setProgress(newProgress);//设置加载进度
                }
            }
        });
        tWebview.loadUrl(url);
    }

    /**
     * X5内核在使用preinit接口之后，对于首次安装首次加载没有效果
     * 实际上，X5webview的preinit接口只是降低了webview的冷启动时间；
     * 因此，现阶段要想做到首次安装首次加载X5内核，必须要让X5内核提前获取到内核的加载条件
     */
    private void preinitX5WebCore() {
        if (!QbSdk.isTbsCoreInited()) {// preinit只需要调用一次，如果已经完成了初始化，那么就直接构造view
            QbSdk.preInit(WebViewActivity.this, myCallback);// 设置X5初始化完成的回调接口
            // 第三个参数为true：如果首次加载失败则继续尝试加载；
        }
    }

    private QbSdk.PreInitCallback myCallback = new QbSdk.PreInitCallback() {

        @Override
        public void onViewInitFinished(boolean b) {// 当X5webview 初始化结束后的回调
//            new WebView(WebViewActivity.this);
            isX5WebViewEnabled = true;
        }

        @Override
        public void onCoreInitFinished() {
        }
    };

    /**
     * 如果安装了微信或QQ一般是不需要下载X5内核的，如果需要下载一般在application中加载service先行下载
     */
    public class AdvanceLoadX5Service extends Service {
        @Nullable
        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

        @Override
        public void onCreate() {
            super.onCreate();
            initX5();
        }

        private void initX5() {
            //  预加载X5内核
            QbSdk.initX5Environment(getApplicationContext(), myCallback);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (tWebview != null) {
            tWebview.destroy();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && tWebview.canGoBack()) {
            tWebview.goBack();// 返回前一个页面
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
