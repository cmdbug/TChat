package com.netease.nim.demo.main.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.netease.nim.demo.DemoCache;
import com.netease.nim.demo.R;
import com.netease.nim.demo.main.model.SettingTemplate;
import com.netease.nim.demo.main.model.SettingType;
import com.netease.nim.uikit.cache.NimUserInfoCache;
import com.netease.nim.uikit.common.ui.imageview.HeadImageView;
import com.netease.nim.uikit.common.ui.widget.SwitchButton;

import java.util.List;

/**
 * Created by hzxuwen on 2015/6/30.
 */
public class SettingsAdapter extends BaseAdapter {
    protected List<SettingTemplate> items;
    protected Context context;
    private int layoutID;
    protected int itemHeight;

    private SwitchButton.OnChangedListener onchangeListener;
    private SwitchChangeListener switchChangeListener;

    public interface SwitchChangeListener {
        void onSwitchChange(SettingTemplate item, boolean checkState);
    }

    public SettingsAdapter(Context context, SwitchChangeListener switchChangeListener, List<SettingTemplate> items) {
        this(context, switchChangeListener, items, R.layout.setting_item_base);
    }

    public SettingsAdapter(Context context, SwitchChangeListener switchChangeListener, List<SettingTemplate> items, int layoutID) {
        this.context = context;
        this.switchChangeListener = switchChangeListener;
        this.items = items;
        this.layoutID = layoutID;
        itemHeight = context.getResources().getDimensionPixelSize(R.dimen.isetting_item_height);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if(convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(layoutID, parent, false);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if(viewHolder == null) {
            viewHolder = new ViewHolder();
            viewHolder.root = convertView;
            viewHolder.headImageView = (HeadImageView) convertView.findViewById(R.id.head_image);
            viewHolder.titleView = (TextView)  convertView.findViewById(R.id.title_label);
            viewHolder.detailView = (TextView) convertView.findViewById(R.id.detail_label);
            viewHolder.switchButton = (SwitchButton) convertView.findViewById(R.id.setting_item_toggle);
            viewHolder.line = convertView.findViewById(R.id.line);
            viewHolder.indicator = (ImageView) convertView.findViewById(R.id.setting_item_indicator);
            viewHolder.headTitleView = (TextView) convertView.findViewById(R.id.head_title_label);
            viewHolder.headDetailView = (TextView) convertView.findViewById(R.id.head_detail_label);
            convertView.setTag(viewHolder);
        }


        ViewGroup.LayoutParams lp = viewHolder.root.getLayoutParams();
        if(lp != null) {
            lp.height = itemHeight;
            viewHolder.root.setLayoutParams(lp);
        }
        viewHolder.root.setBackgroundResource(R.drawable.nim_semitransparency_selector);
        viewHolder.headImageView.setVisibility(View.GONE);
        viewHolder.titleView.setVisibility(View.GONE);
        viewHolder.detailView.setVisibility(View.GONE);
        viewHolder.switchButton.setVisibility(View.GONE);
        viewHolder.line.setVisibility(View.GONE);
        viewHolder.indicator.setVisibility(View.GONE);
        viewHolder.headTitleView.setVisibility(View.GONE);
        viewHolder.headDetailView.setVisibility(View.GONE);


        SettingTemplate item = items.get(position);
        if(item.getType() == SettingType.TYPE_TOGGLE) {
            updateToggleItem(viewHolder, item, position);
        } else if(item.getType() == SettingType.TYPE_HEAD) {
            updateHeadItem(viewHolder);
        } else if(item.getType() == SettingType.TYPE_SEPERATOR) {
            updateSeperatorItem(viewHolder);
        } else if(item.getType() == SettingType.TYPE_LINE) {
            addLineItem(viewHolder);
        } else {
            updateDefaultItem(viewHolder, item, position);
        }

        return convertView;
    }

    /**
     * 设置默认格式item
     * @param viewHolder
     * @param item
     * @param position
     */
    private void updateDefaultItem(ViewHolder viewHolder, SettingTemplate item, int position) {
        ViewGroup.LayoutParams lp = viewHolder.root.getLayoutParams();
        if(lp != null) {
            if (itemHeight > 0) {
                lp.height = itemHeight;
            } else {
                lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            }
            viewHolder.root.setLayoutParams(lp);
        }
        setTextView(viewHolder.titleView, item.getTitle());
        setTextView(viewHolder.detailView, item.getDetail());
    }

    /**
     * 设置带toggle button item
     * @param viewHolder
     * @param item
     * @param position
     */
    private void updateToggleItem(ViewHolder viewHolder, SettingTemplate item, int position) {
        setTextView(viewHolder.titleView, item.getTitle());
        setToggleView(viewHolder, item);
    }

    /**
     * 设置头像和名字item
     * @param viewHolder
     */
    private void updateHeadItem(ViewHolder viewHolder) {
        ViewGroup.LayoutParams lp = viewHolder.root.getLayoutParams();
        if(lp != null) {
            lp.height = 200;
            viewHolder.root.setLayoutParams(lp);
            viewHolder.root.setBackgroundColor(Color.TRANSPARENT);
        }
        viewHolder.headImageView.setVisibility(View.VISIBLE);
        viewHolder.headTitleView.setVisibility(View.VISIBLE);
        viewHolder.headTitleView.setText(NimUserInfoCache.getInstance().getUserDisplayName(DemoCache.getAccount()));
        viewHolder.headDetailView.setVisibility(View.VISIBLE);
        viewHolder.headDetailView.setText(String.format("帐号:%s", DemoCache.getAccount()));
        viewHolder.titleView.setVisibility(View.GONE);
        viewHolder.headImageView.loadBuddyAvatar(DemoCache.getAccount());
        viewHolder.indicator.setImageResource(R.drawable.nim_arrow_right);
        viewHolder.indicator.setVisibility(View.VISIBLE);
    }

    /**
     * 设置空的item
     */
    private void updateSeperatorItem(ViewHolder viewHolder) {
        ViewGroup.LayoutParams lp = viewHolder.root.getLayoutParams();
        if(lp != null) {
            lp.height = 50;
            viewHolder.root.setLayoutParams(lp);
            viewHolder.root.setBackgroundColor(Color.TRANSPARENT);
        }
        viewHolder.headImageView.setVisibility(View.GONE);
        viewHolder.titleView.setVisibility(View.GONE);
        viewHolder.detailView.setVisibility(View.GONE);
        viewHolder.switchButton.setVisibility(View.GONE);
    }

    /**
     * 添加分割线
     * @param viewHolder
     */
    private void addLineItem(ViewHolder viewHolder) {
        ViewGroup.LayoutParams lp = viewHolder.root.getLayoutParams();
        if(lp != null) {
            lp.height = 2;
            viewHolder.root.setLayoutParams(lp);
        }
        viewHolder.headImageView.setVisibility(View.GONE);
        viewHolder.titleView.setVisibility(View.GONE);
        viewHolder.detailView.setVisibility(View.GONE);
        viewHolder.switchButton.setVisibility(View.GONE);
        viewHolder.line.setVisibility(View.VISIBLE);
    }

    private void setToggleView(ViewHolder viewHolder, SettingTemplate item) {
        if(viewHolder.switchButton != null) {
            viewHolder.switchButton.setVisibility(View.VISIBLE);
            viewHolder.switchButton.setCheck(item.getChekced());
            createSwitchListener(item);
            viewHolder.switchButton.setOnChangedListener(onchangeListener);
        }
    }

    private void setTextView(TextView textView, String value) {
        if(textView == null || TextUtils.isEmpty(value)) {
            return;
        }
        if(textView.getVisibility() != View.VISIBLE) {
            textView.setVisibility(View.VISIBLE);
        }
        textView.setText(value);
    }

    private void createSwitchListener(final SettingTemplate item) {
        onchangeListener = new SwitchButton.OnChangedListener() {
            @Override
            public void OnChanged(View v, boolean checkState) {
                switchChangeListener.onSwitchChange(item, checkState);
            }
        };
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder {
        private View root;
        private HeadImageView headImageView;
        private TextView titleView;
        private TextView detailView;
        private SwitchButton switchButton;
        private View line;
        private ImageView indicator;
        private TextView headTitleView;
        private TextView headDetailView;
    }

}
