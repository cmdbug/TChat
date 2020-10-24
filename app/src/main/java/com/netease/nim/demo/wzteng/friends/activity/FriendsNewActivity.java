package com.netease.nim.demo.wzteng.friends.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.luck.picture.lib.model.FunctionConfig;
import com.luck.picture.lib.model.LocalMediaLoader;
import com.luck.picture.lib.model.PictureConfig;
import com.netease.nim.demo.R;
import com.netease.nim.demo.wzteng.others.pictureselector.DefaultItemTouchHelpCallback;
import com.netease.nim.demo.wzteng.others.pictureselector.DefaultItemTouchHelper;
import com.netease.nim.demo.wzteng.others.pictureselector.FullyGridLayoutManager;
import com.netease.nim.demo.wzteng.others.pictureselector.GridImageAdapter;
import com.netease.nim.demo.wzteng.others.pictureselector.TouchHelpCallbackListenerImpl;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.model.ToolBarOptions;
import com.yalantis.ucrop.entity.LocalMedia;

import java.util.ArrayList;
import java.util.List;

public class FriendsNewActivity extends UI {

    private RecyclerView recyclerView;
    private GridImageAdapter adapter;
    private int maxSelectNum = 9;// 图片最大可选数量
    private List<LocalMedia> selectMedia = new ArrayList<>();
    private int compressFlag = 2;// 1 系统自带压缩 2 luban压缩

    private static Bundle bundle;

    private Button btnSend;
    private EditText etMessage;

    private DefaultItemTouchHelpCallback defaultItemTouchHelpCallback;
    private DefaultItemTouchHelpCallback.OnItemTouchCallbackListener onItemTouchCallbackListener;

    LinearLayout llFriendsBox;

    private LinearLayout llDeleteBox;
    private TextView tvDeleteleTip;
    private ImageView ivDeleteTip;
    private boolean touchUp;
    private boolean toDelete;
    private boolean cleaning;

    public static void start(Context context) {
        start(context, null);
    }

    public static void start(Context context, Intent extras) {
        Intent intent = new Intent();
        intent.setClass(context, FriendsNewActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        if (extras != null) {
            intent.putExtras(extras);
            bundle = intent.getExtras();
        }
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friends_new_message);

        ToolBarOptions options = new ToolBarOptions();
        options.titleString = "编辑";
        setToolBar(R.id.toolbar, options);

        btnSend = findView(R.id.btn_send);
        etMessage = findView(R.id.etNewMessage);
        recyclerView = findView(R.id.recycler);

        llFriendsBox = findView(R.id.ll_friends_box);

        llDeleteBox = findView(R.id.ll_del_tip);
        tvDeleteleTip = findView(R.id.tv_del_tip);
        ivDeleteTip = findView(R.id.iv_del_tip);

        if (bundle != null) {
            initRecycleView();
            initLongTouchtoMove();//长按滑动
            selectMedia = (List<LocalMedia>) bundle.get("resultList");
            adapter.setList(selectMedia);
            adapter.notifyDataSetChanged();
            ((TouchHelpCallbackListenerImpl) onItemTouchCallbackListener).setSelectList(selectMedia);
            bundle.clear();
            bundle = null;
            refreshLayout();
        }

        initUI();
    }

    private void initRecycleView() {
        FullyGridLayoutManager manager = new FullyGridLayoutManager(FriendsNewActivity.this, 4, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        adapter = new GridImageAdapter(FriendsNewActivity.this, onAddPicClickListener);
        adapter.setSelectMax(maxSelectNum);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new GridImageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                // 这里可预览图片
                PictureConfig.getPictureConfig().externalPicturePreview(FriendsNewActivity.this, position, selectMedia);
            }

            @Override
            public boolean onItemLongClick(int position, View v) {
//                Log.d("wzt", "position:" + position + " ," + selectList.size());
                defaultItemTouchHelpCallback.setDragEnable(true);
                defaultItemTouchHelpCallback.setSwipeEnable(true);
                return false;
            }
        });
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                if (selectMedia.size() > 0 || etMessage.getText().length() > 0) {
                    btnSend.setEnabled(true);
                } else {
                    btnSend.setEnabled(false);
                }
            }
        });
    }

    private void initLongTouchtoMove() {
        onItemTouchCallbackListener = new TouchHelpCallbackListenerImpl(this, selectMedia, adapter,
                new TouchHelpCallbackListenerImpl.ITouchHelpCallback() {
                    @Override
                    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
//                        Log.d("wzt", "onSelectedChanged:" + actionState);
                        if (toDelete) {
                            touchUp = actionState == ItemTouchHelper.ACTION_STATE_IDLE;
                        }
                        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
                            showDeleteBox();
                        } else {
                            hideDeleteBox();
                        }
                    }

                    @Override
                    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                        cleaning = true;
                        toDelete = false;
                        hideDeleteBox();
//                        Log.d("wzt", "clearView");
                    }

                    @Override
                    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                            float dX, float dY, int actionState, boolean isCurrentlyActive) {

                        toChangeViewByOnChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                    }
                });
        defaultItemTouchHelpCallback = new DefaultItemTouchHelpCallback(onItemTouchCallbackListener);
        defaultItemTouchHelpCallback.setDragEnable(true);
        defaultItemTouchHelpCallback.setSwipeEnable(true);
        DefaultItemTouchHelper defaultItemTouchHelper = new DefaultItemTouchHelper(defaultItemTouchHelpCallback);
        defaultItemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void initUI() {
        etMessage.addTextChangedListener(new MessageChange());
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(FriendsNewActivity.this, "点击发表", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // EditText监听器
    class MessageChange implements TextWatcher {

        @Override
        public void afterTextChanged(Editable arg0) {
        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
        }

        @Override
        public void onTextChanged(CharSequence cs, int start, int before, int count) {
            boolean messageFlag = etMessage.getText().length() > 0;
            if (messageFlag || selectMedia.size() > 0) {
                btnSend.setEnabled(true);
            } else {
                btnSend.setEnabled(false);
            }
        }

    }

    /**
     * 增加按钮图片回调接口
     */
    private GridImageAdapter.onAddPicClickListener onAddPicClickListener = new GridImageAdapter.onAddPicClickListener() {
        @Override
        public void onAddPicClick(int type, int position) {
            switch (type) {
                case 0:
                    // 进入相册
//                    /**
//                     * type --> 1图片 or 2视频
//                     * copyMode -->裁剪比例，默认、1:1、3:4、3:2、16:9
//                     * maxSelectNum --> 可选择图片的数量
//                     * selectMode         --> 单选 or 多选
//                     * isShow       --> 是否显示拍照选项 这里自动根据type 启动拍照或录视频
//                     * isPreview    --> 是否打开预览选项
//                     * isCrop       --> 是否打开剪切选项
//                     * isPreviewVideo -->是否预览视频(播放) mode or 多选有效
//                     * ThemeStyle -->主题颜色
//                     * CheckedBoxDrawable -->图片勾选样式
//                     * cropW-->裁剪宽度 值不能小于100  如果值大于图片原始宽高 将返回原图大小
//                     * cropH-->裁剪高度 值不能小于100
//                     * isCompress -->是否压缩图片
//                     * setEnablePixelCompress 是否启用像素压缩
//                     * setEnableQualityCompress 是否启用质量压缩
//                     * setRecordVideoSecond 录视频的秒数，默认不限制
//                     * setRecordVideoDefinition 视频清晰度  Constants.HIGH 清晰  Constants.ORDINARY 低质量
//                     * setImageSpanCount -->每行显示个数
//                     * setCheckNumMode 是否显示QQ选择风格(带数字效果)
//                     * setPreviewColor 预览文字颜色
//                     * setCompleteColor 完成文字颜色
//                     * setPreviewBottomBgColor 预览界面底部背景色
//                     * setBottomBgColor 选择图片页面底部背景色
//                     * setCompressQuality 设置裁剪质量，默认无损裁剪
//                     * setSelectMedia 已选择的图片
//                     * setCompressFlag 1为系统自带压缩  2为第三方luban压缩
//                     * 注意-->type为2时 设置isPreview or isCrop 无效
//                     * 注意：Options可以为空，默认标准模式
//                     */

                    int selector = R.drawable.picselector_select_cb;
                    FunctionConfig config = new FunctionConfig();
                    config.setType(LocalMediaLoader.TYPE_IMAGE);
                    config.setCopyMode(FunctionConfig.CROP_MODEL_DEFAULT);
                    config.setCompress(false);
                    config.setEnablePixelCompress(true);
                    config.setEnableQualityCompress(true);
                    config.setMaxSelectNum(maxSelectNum);
                    config.setSelectMode(FunctionConfig.MODE_MULTIPLE);
                    config.setShowCamera(true);
                    config.setEnablePreview(true);
                    config.setEnableCrop(false);
                    config.setPreviewVideo(true);
                    config.setRecordVideoDefinition(FunctionConfig.HIGH);// 视频清晰度
                    config.setRecordVideoSecond(60);// 视频秒数
//                    config.setCropW(cropW);
//                    config.setCropH(cropH);
                    config.setCheckNumMode(true);
                    config.setCompressQuality(100);
                    config.setImageSpanCount(4);
                    config.setSelectMedia(selectMedia);
                    config.setCompressFlag(compressFlag);
//                    config.setCompressW(compressW);
//                    config.setCompressH(compressH);
                    if (true) {
                        config.setThemeStyle(ContextCompat.getColor(FriendsNewActivity.this, R.color.color_blue_3a9efb));
                        // 可以自定义底部 预览 完成 文字的颜色和背景色
                        if (true) {
                            // QQ 风格模式下 这里自己搭配颜色，使用蓝色可能会不好看
                            config.setPreviewColor(ContextCompat.getColor(FriendsNewActivity.this, R.color.white));
                            config.setCompleteColor(ContextCompat.getColor(FriendsNewActivity.this, R.color.white));
                            config.setPreviewBottomBgColor(ContextCompat.getColor(FriendsNewActivity.this, R.color.color_blue_3a9efb));
                            config.setBottomBgColor(ContextCompat.getColor(FriendsNewActivity.this, R.color.color_blue_3a9efb));
                        }
                    }
                    if (false) {
                        config.setCheckedBoxDrawable(selector);
                    }

                    // 先初始化参数配置，在启动相册
                    PictureConfig.init(config);
                    PictureConfig.getPictureConfig().openPhoto(FriendsNewActivity.this, resultCallback);

                    // 只拍照
                    //PictureConfig.getPictureConfig().startOpenCamera(mContext, resultCallback);
                    break;
                case 1:
                    // 删除图片
                    selectMedia.remove(position);
                    adapter.notifyItemRemoved(position);
                    adapter.notifyItemRangeChanged(position, selectMedia.size());
//                    adapter.notifyDataSetChanged();//teng 修正点击删除图标没有触发观察者但是没了删除动画效果
                    refreshLayout();
                    break;
            }
        }

        @Override
        public boolean onLongClick(View view) {
            defaultItemTouchHelpCallback.setDragEnable(false);
            defaultItemTouchHelpCallback.setSwipeEnable(false);
            return false;
        }
    };

    /**
     * 图片回调方法
     */
    private PictureConfig.OnSelectResultCallback resultCallback = new PictureConfig.OnSelectResultCallback() {
        @Override
        public void onSelectSuccess(List<LocalMedia> resultList) {
            selectMedia = resultList;
//            Log.i("wzt", "selectMedia回调个数:" + selectMedia.size());
            if (selectMedia != null) {
                adapter.setList(selectMedia);
                adapter.notifyDataSetChanged();
                ((TouchHelpCallbackListenerImpl) onItemTouchCallbackListener).setSelectList(selectMedia);
                refreshLayout();
            }
        }
    };

    /**
     * 刷新地理位置等布局
     */
    private void refreshLayout() {
        touchUp = false;
        toDelete = false;
        //判断提醒谁布局看是否需要下移
        int row = (selectMedia.size() + 1) / 4;
        row = 0 == (selectMedia.size() + 1) % 4 ? row : row + 1;
        row = 4 == row ? 3 : row;//row最多为三行
        int marginTop = (getResources().getDimensionPixelSize(R.dimen.article_img_margin_top)
                + getResources().getDimensionPixelSize(R.dimen.article_img_dimens)) * row
                + getResources().getDimensionPixelSize(R.dimen.article_post_et_h)
                + 10;
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) llFriendsBox.getLayoutParams();
        params.setMargins(0, marginTop, 0, 0);
        llFriendsBox.setLayoutParams(params);
    }

    private void showDeleteBox() {
        llDeleteBox.setVisibility(View.VISIBLE);
        llDeleteBox.setBackgroundColor(0xDDFF4444);
        tvDeleteleTip.setText("拖到此处删除");
        ivDeleteTip.setImageResource(R.drawable.friends_del_nor);
    }

    private void hideDeleteBox() {
        llDeleteBox.setVisibility(View.GONE);
    }

    private void sureToDeleteBox() {
        llDeleteBox.setVisibility(View.VISIBLE);
        llDeleteBox.setBackgroundColor(0xDDD7110A);
        tvDeleteleTip.setText("松手即可删除");
        ivDeleteTip.setImageResource(R.drawable.friends_del_sure);
    }

    private void toChangeViewByOnChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                           float dX, float dY, int actionState, boolean isCurrentlyActive) {

        if (actionState == ItemTouchHelper.ACTION_STATE_IDLE && !toDelete) {
            return;
        }
        if (dY >= (recyclerView.getHeight()
                - viewHolder.itemView.getBottom()//item底部距离recyclerView顶部高度
                - getPixelById(R.dimen.article_post_delete))) {//拖到删除处

            toDelete = true;
            sureToDeleteBox();
            if (touchUp) {//在删除处放手，则删除item
                viewHolder.itemView.setVisibility(View.INVISIBLE);//先设置不可见，如果不设置的话，
                // 会看到viewHolder返回到原位置时才消失，因为remove会在viewHolder动画执行完成后才将viewHolder删除
                selectMedia.remove(viewHolder.getAdapterPosition());
                adapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                hideDeleteBox();
                refreshLayout();
                cleaning = true;
            }
        } else {//没有到删除处
            if (toDelete) {
                showDeleteBox();
            }
            toDelete = false;
        }
    }

    private int getPixelById(int dimensionId) {
        return this.getResources().getDimensionPixelSize(dimensionId);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bundle != null) {
            bundle.clear();
            bundle = null;
        }
        if (adapter != null) {
            adapter = null;
        }
        if (recyclerView != null) {
            recyclerView = null;
        }
        if (selectMedia != null) {
            selectMedia = null;
        }
    }
}
