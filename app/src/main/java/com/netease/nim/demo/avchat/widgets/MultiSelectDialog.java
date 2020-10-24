package com.netease.nim.demo.avchat.widgets;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.netease.nim.demo.R;
import com.netease.nim.uikit.common.adapter.TAdapter;
import com.netease.nim.uikit.common.adapter.TAdapterDelegate;
import com.netease.nim.uikit.common.adapter.TViewHolder;
import com.netease.nim.uikit.common.util.sys.ScreenUtil;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 多选对话框.
 */

public class MultiSelectDialog extends Dialog {
    private ListView listView;
    private int itemSize = 0;
    private List<Pair<String, Boolean>> itemTextList = new LinkedList<Pair<String, Boolean>>();
    private BaseAdapter listAdapter;
    private AdapterView.OnItemClickListener itemListener;

    private Context context;

    public static final int NO_TEXT_COLOR = -99999999;

    public static final int NO_TEXT_SIZE = -99999999;

    private View titleView;

    private ImageButton titleBtn;

    private TextView titleTV;

    private TextView messageTV;

    private TextView message2TV;

    private Button positiveBtn, negativeBtn;

    private View btnDivideView;

    private CharSequence title = "", message = "", message2 = "", positiveBtnTitle = "", negativeBtnTitle = "";

    private int titleTextColor = NO_TEXT_COLOR, msgTextColor = NO_TEXT_COLOR,
            positiveBtnTitleTextColor = NO_TEXT_COLOR, negativeBtnTitleTextColor = NO_TEXT_COLOR;

    private float titleTextSize = NO_TEXT_SIZE, msgTextSize = NO_TEXT_SIZE, positiveBtnTitleTextSize = NO_TEXT_SIZE,
            negativeBtnTitleTextSize = NO_TEXT_SIZE;

    private int resourceId;

    private boolean isPositiveBtnVisible = true, isNegativeBtnVisible = false;

    private boolean isTitleVisible = false , isMessageVisble = true, isTitleBtnVisible = false;

    private View.OnClickListener positiveBtnListener, negativeBtnListener;

    private HashMap<Integer, View.OnClickListener> mViewListener = new HashMap<Integer, View.OnClickListener>();

    public MultiSelectDialog(Context context, int resourceId) {
        super(context, R.style.dialog_default_style);
        this.context = context;
        if (-1 != resourceId) {
            setContentView(resourceId);
            this.resourceId = resourceId;
        }
        WindowManager.LayoutParams Params = getWindow().getAttributes();
        Params.width = WindowManager.LayoutParams.MATCH_PARENT;
        Params.height = WindowManager.LayoutParams.MATCH_PARENT;
        getWindow().setAttributes((WindowManager.LayoutParams) Params);
        initAdapter();
    }
    public void setTitle(CharSequence title) {
        isTitleVisible = TextUtils.isEmpty(title) ? false : true;
        setTitleVisible(isTitleVisible);
        if (null != title) {
            this.title = title;
            if (null != titleTV)
                titleTV.setText(title);
        }
    }

    public void setTitleVisible(boolean visible){
        isTitleVisible = visible;
        if(titleView != null){
            titleView.setVisibility(isTitleVisible ? View.VISIBLE : View.GONE);
        }
    }

    public void setTitleBtnVisible(boolean visible) {
        isTitleBtnVisible = visible;
        if (titleBtn != null) {
            titleBtn.setVisibility(isTitleBtnVisible ? View.VISIBLE : View.GONE);
        }
    }

    public void setTitleTextColor(int color) {
        titleTextColor = color;
        if (null != titleTV && NO_TEXT_COLOR != color)
            titleTV.setTextColor(color);
    }

    public void setMessageTextColor(int color) {
        msgTextColor = color;
        if (null != messageTV && NO_TEXT_COLOR != color)
            messageTV.setTextColor(color);

    }

    public void setMessageTextSize(float size) {
        msgTextSize = size;
        if (null != messageTV && NO_TEXT_SIZE != size)
            messageTV.setTextSize(size);
    }

    public void setTitleTextSize(float size) {
        titleTextSize = size;
        if (null != titleTV && NO_TEXT_SIZE != size)
            titleTV.setTextSize(size);
    }

    public void setMessageVisible(boolean visible){
        isMessageVisble = visible;
        if(messageTV != null){
            messageTV.setVisibility(visible ? View.VISIBLE : View.GONE);
        }
    }

    public void setMessage(CharSequence message) {
        if (null != message) {
            this.message = message;
            if (null != messageTV)
                messageTV.setText(message);
        }
    }

    public void setMessage2(CharSequence message) {
        if(!TextUtils.isEmpty(message)) {
            this.message2 = message;
            if(null != message2TV) {
                message2TV.setText(message);
            }
        }
    }

    public void addPositiveButton(CharSequence title, int color, float size,
                                  View.OnClickListener positiveBtnListener) {
        isPositiveBtnVisible = true;
        positiveBtnTitle = TextUtils.isEmpty(title) ? context
                .getString(R.string.ok) : title;
        positiveBtnTitleTextColor = color;
        positiveBtnTitleTextSize = size;
        this.positiveBtnListener = positiveBtnListener;

        if (positiveBtn != null) {
            positiveBtn.setText(positiveBtnTitle);
            positiveBtn.setTextColor(positiveBtnTitleTextColor);
            positiveBtn.setTextSize(positiveBtnTitleTextSize);
            positiveBtn.setOnClickListener(positiveBtnListener);
        }
    }

    public void addNegativeButton(CharSequence title, int color, float size,
                                  View.OnClickListener negativeBtnListener) {
        isNegativeBtnVisible = true;
        negativeBtnTitle = TextUtils.isEmpty(title) ? context
                .getString(R.string.cancel) : title;
        negativeBtnTitleTextColor = color;
        negativeBtnTitleTextSize = size;
        this.negativeBtnListener = negativeBtnListener;

        if (negativeBtn != null) {
            negativeBtn.setText(negativeBtnTitle);
            negativeBtn.setTextColor(negativeBtnTitleTextColor);
            negativeBtn.setTextSize(negativeBtnTitleTextSize);
            negativeBtn.setOnClickListener(negativeBtnListener);
        }
    }

    public void addPositiveButton(CharSequence title,
                                  View.OnClickListener positiveBtnListener) {
        addPositiveButton(title, NO_TEXT_COLOR, NO_TEXT_SIZE,
                positiveBtnListener);
    }

    public void addNegativeButton(CharSequence title,
                                  View.OnClickListener negativeBtnListener) {
        addNegativeButton(title, NO_TEXT_COLOR, NO_TEXT_SIZE,
                negativeBtnListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(resourceId);
        try {
            ViewGroup root = (ViewGroup) findViewById(R.id.multi_select_dialog_layout);
            if (root != null) {
                ViewGroup.LayoutParams params = root.getLayoutParams();
                params.width = (int) ScreenUtil.getDialogWidth();
                root.setLayoutParams(params);
            }

            titleView = findViewById(R.id.multi_select_dialog_title_view);
            if (titleView != null) {
                setTitleVisible(isTitleVisible);
            }
            titleBtn = (ImageButton) findViewById(R.id.multi_select_dialog_title_button);
            if (titleBtn != null) {
                setTitleBtnVisible(isTitleBtnVisible);
            }
            titleTV = (TextView) findViewById(R.id.multi_select_dialog_title_text_view);
            if (titleTV != null) {
                titleTV.setText(title);
                if (NO_TEXT_COLOR != titleTextColor)
                    titleTV.setTextColor(titleTextColor);
                if (NO_TEXT_SIZE != titleTextSize)
                    titleTV.setTextSize(titleTextSize);
            }

            messageTV = (TextView) findViewById(R.id.multi_select_dialog_message_text_view);
            if (messageTV != null) {
                messageTV.setText(message);
                setMessageVisible(isMessageVisble);
                if (NO_TEXT_COLOR != msgTextColor)
                    messageTV.setTextColor(msgTextColor);
                if (NO_TEXT_SIZE != msgTextSize)
                    messageTV.setTextSize(msgTextSize);
            }

            message2TV = (TextView) findViewById(R.id.multi_select_dialog_message_2);
            if(message2TV != null && !TextUtils.isEmpty(message2)) {
                message2TV.setVisibility(View.VISIBLE);
                message2TV.setText(message2);
            }

            positiveBtn = (Button) findViewById(R.id.multi_select_dialog_positive_btn);
            if (isPositiveBtnVisible && positiveBtn != null) {
                positiveBtn.setVisibility(View.VISIBLE);
                if (NO_TEXT_COLOR != positiveBtnTitleTextColor) {
                    positiveBtn.setTextColor(positiveBtnTitleTextColor);
                }
                if (NO_TEXT_SIZE != positiveBtnTitleTextSize) {
                    positiveBtn.setTextSize(positiveBtnTitleTextSize);
                }
                positiveBtn.setText(positiveBtnTitle);
                positiveBtn.setOnClickListener(positiveBtnListener);
            }

            boolean hasChecked = false;
            for(Pair<String, Boolean> pair:itemTextList) {
                if(pair.second == true) {
                    hasChecked = true;
                }
            }
            positiveBtn.setEnabled(hasChecked);


            negativeBtn = (Button) findViewById(R.id.multi_select_dialog_negative_btn);
            btnDivideView = findViewById(R.id.multi_select_dialog_btn_divide_view);
            if (isNegativeBtnVisible) {
                negativeBtn.setVisibility(View.VISIBLE);
                btnDivideView.setVisibility(View.VISIBLE);
                if (NO_TEXT_COLOR != this.negativeBtnTitleTextColor) {
                    negativeBtn.setTextColor(negativeBtnTitleTextColor);
                }
                if (NO_TEXT_SIZE != this.negativeBtnTitleTextSize) {
                    negativeBtn.setTextSize(negativeBtnTitleTextSize);
                }
                negativeBtn.setText(negativeBtnTitle);
                negativeBtn.setOnClickListener(negativeBtnListener);
            }

            if (mViewListener != null && mViewListener.size() != 0) {
                Iterator iter = mViewListener.entrySet().iterator();
                View view = null;
                while (iter.hasNext()) {
                    Map.Entry<Integer, View.OnClickListener> entry = (Map.Entry) iter.next();
                    view = findViewById(entry.getKey());
                    if(view != null && entry.getValue() != null) {
                        view.setOnClickListener(entry.getValue());
                    }
                }
            }
            listView = (ListView) findViewById(R.id.multi_select_dialog_list_view);
            if (itemSize > 0) {
                updateListView();
            }
        } catch (Exception e) {

        }
    }

    public int getResourceId() {
        return resourceId;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }

    public Button getPositiveBtn() {
        return positiveBtn;
    }

    public Button getNegativeBtn() {
        return negativeBtn;
    }

    public void setViewListener(int viewId, View.OnClickListener listener) {
        mViewListener.put(viewId, listener);
    }



    private void initAdapter() {
        listAdapter = new TAdapter<>(context, itemTextList, new TAdapterDelegate() {
            @Override
            public int getViewTypeCount() {
                return itemTextList.size();
            }

            @Override
            public Class<? extends TViewHolder> viewHolderAtPosition(int position) {
                return MultiSelectDialogViewHolder.class;
            }

            @Override
            public boolean enabled(int position) {
                return true;
            }
        });
        itemListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                itemTextList.set(position,new Pair<String, Boolean>(itemTextList.get(position).first,!itemTextList.get(position).second));
                boolean hasChecked = false;
                for(Pair<String, Boolean> pair:itemTextList) {
                    if(pair.second == true) {
                        hasChecked = true;
                    }
                }
                positiveBtn.setEnabled(hasChecked);
                listAdapter.notifyDataSetChanged();
            }
        };
    }

    public MultiSelectDialog(Context context) {
        this(context, R.layout.multi_select_dialog_default_layout);
    }

    private void updateListView() {
        listAdapter.notifyDataSetChanged();
        if (listView != null) {
            listView.setAdapter(listAdapter);
            listView.setOnItemClickListener(itemListener);
        }
    }

    public void addItem(String itemText, boolean isChecked) {
        itemTextList.add(new Pair<String, Boolean>(itemText, isChecked));
        itemSize = itemTextList.size();
    }

    public void clearData() {
        itemTextList.clear();
        itemSize = 0;
    }

    @Override
    public void show() {
        if (itemSize <= 0) {
            return;
        }
        updateListView();
        super.show();
    }

    public List<Pair<String, Boolean>> getItemTextList() {
        return itemTextList;
    }
}
