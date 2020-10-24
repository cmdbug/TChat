package com.netease.nim.demo.main.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.netease.nim.demo.R;
import com.netease.nim.demo.session.SessionHelper;
import com.netease.nim.uikit.cache.NimUserInfoCache;
import com.netease.nim.uikit.cache.TeamDataCache;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.contact.core.item.AbsContactItem;
import com.netease.nim.uikit.contact.core.item.ItemTypes;
import com.netease.nim.uikit.contact.core.item.MsgItem;
import com.netease.nim.uikit.contact.core.model.ContactDataAdapter;
import com.netease.nim.uikit.contact.core.provider.ContactDataProvider;
import com.netease.nim.uikit.contact.core.query.IContactDataProvider;
import com.netease.nim.uikit.contact.core.query.TextQuery;
import com.netease.nim.uikit.contact.core.viewholder.LabelHolder;
import com.netease.nim.uikit.contact.core.viewholder.MsgHolder;
import com.netease.nim.uikit.model.ToolBarOptions;
import com.netease.nimlib.sdk.search.model.MsgIndexRecord;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;

/**
 * 消息全文检索详细页面
 * Created by huangjun on 2016/6/28.
 */
public class GlobalSearchDetailActivity extends UI implements OnItemClickListener {

    private static final String EXTRA_SESSION_TYPE = "EXTRA_SESSION_TYPE";

    private static final String EXTRA_SESSION_ID = "EXTRA_SESSION_ID";

    private static final String EXTRA_QUERY = "EXTRA_QUERY";

    private static final String EXTRA_RESULT_COUNT = "EXTRA_RESULT_COUNT";

    private ContactDataAdapter adapter;

    private ListView lvContacts;

    private String sessionId;

    private SessionTypeEnum sessionType;

    private String query;

    private int resultCount;

    public static final void start(Context context, MsgIndexRecord record) {
        Intent intent = new Intent();
        intent.setClass(context, GlobalSearchDetailActivity.class);
        intent.putExtra(EXTRA_SESSION_TYPE, record.getSessionType().getValue());
        intent.putExtra(EXTRA_SESSION_ID, record.getSessionId());
        intent.putExtra(EXTRA_QUERY, record.getQuery());
        intent.putExtra(EXTRA_RESULT_COUNT, record.getCount());

        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        parseIntent();

        setContentView(R.layout.global_search_detail);

        // title name
        ToolBarOptions options = new ToolBarOptions();
        if (sessionType == SessionTypeEnum.P2P) {
            options.titleString = NimUserInfoCache.getInstance().getUserDisplayName(sessionId);
        } else if (sessionType == SessionTypeEnum.Team) {
            options.titleString = TeamDataCache.getInstance().getTeamName(sessionId);
        }
        setToolBar(R.id.toolbar, options);

        // textView tip
        String tip = String.format("共%d条与\"%s\"相关的聊天记录", resultCount, query);
        TextView tipTextView = findView(R.id.search_result_tip);
        tipTextView.setText(tip);

        // listView adapter
        lvContacts = findView(R.id.search_result_list);
        IContactDataProvider dataProvider = new ContactDataProvider(ItemTypes.MSG);

        adapter = new ContactDataAdapter(this, null, dataProvider);
        adapter.addViewHolder(ItemTypes.LABEL, LabelHolder.class);
        adapter.addViewHolder(ItemTypes.MSG, MsgHolder.class);

        lvContacts.setAdapter(adapter);
        lvContacts.setOnItemClickListener(this);

        // query data
        TextQuery textQuery = new TextQuery(query);
        textQuery.extra = new Object[]{sessionType, sessionId};
        adapter.query(textQuery);
    }

    private void parseIntent() {
        sessionType = SessionTypeEnum.typeOfValue(getIntent().getIntExtra(EXTRA_SESSION_TYPE, 0));
        sessionId = getIntent().getStringExtra(EXTRA_SESSION_ID);
        query = getIntent().getStringExtra(EXTRA_QUERY);
        resultCount = getIntent().getIntExtra(EXTRA_RESULT_COUNT, 0);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        AbsContactItem item = (AbsContactItem) adapter.getItem(position - lvContacts.getHeaderViewsCount());
        switch (item.getItemType()) {
            case ItemTypes.MSG: {
                MsgIndexRecord msgIndexRecord = ((MsgItem) item).getRecord();
                if (msgIndexRecord.getSessionType() == SessionTypeEnum.P2P) {
                    SessionHelper.startP2PSession(this, msgIndexRecord.getSessionId(), msgIndexRecord.getMessage());
                } else if (msgIndexRecord.getSessionType() == SessionTypeEnum.Team) {
                    SessionHelper.startTeamSession(this, msgIndexRecord.getSessionId(), msgIndexRecord.getMessage());
                }
                break;
            }

            default:
                break;
        }
    }

}
