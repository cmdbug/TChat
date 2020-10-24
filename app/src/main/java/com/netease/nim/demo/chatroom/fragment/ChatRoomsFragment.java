package com.netease.nim.demo.chatroom.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.netease.nim.demo.R;
import com.netease.nim.demo.chatroom.activity.ChatRoomActivity;
import com.netease.nim.demo.chatroom.adapter.ChatRoomsAdapter;
import com.netease.nim.demo.chatroom.thridparty.ChatRoomHttpClient;
import com.netease.nim.uikit.common.fragment.TFragment;
import com.netease.nim.uikit.common.ui.ptr2.PullToRefreshLayout;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseQuickAdapter;
import com.netease.nim.uikit.common.ui.recyclerview.decoration.SpacingDecoration;
import com.netease.nim.uikit.common.ui.recyclerview.listener.OnItemClickListener;
import com.netease.nim.uikit.common.util.sys.ScreenUtil;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomInfo;

import java.util.List;

/**
 * 直播间列表fragment
 * <p>
 * Created by huangjun on 2015/12/11.
 */
public class ChatRoomsFragment extends TFragment {
    private static final String TAG = ChatRoomsFragment.class.getSimpleName();

    private ChatRoomsAdapter adapter;
    private PullToRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.chat_rooms, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        findViews();
    }

    public void onCurrent() {
        fetchData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void findViews() {
        // swipeRefreshLayout
        swipeRefreshLayout = findView(R.id.swipe_refresh);
        swipeRefreshLayout.setPullUpEnable(false);
        swipeRefreshLayout.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onPullDownToRefresh() {
                fetchData();
            }

            @Override
            public void onPullUpToRefresh() {

            }
        });

        // recyclerView
        recyclerView = findView(R.id.recycler_view);
        adapter = new ChatRoomsAdapter(recyclerView);
        adapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        recyclerView.addItemDecoration(new SpacingDecoration(ScreenUtil.dip2px(10), ScreenUtil.dip2px(10), true));
        recyclerView.addOnItemTouchListener(new OnItemClickListener<ChatRoomsAdapter>() {
            @Override
            public void onItemClick(ChatRoomsAdapter adapter, View view, int position) {
                ChatRoomInfo room = adapter.getItem(position);
                ChatRoomActivity.start(getActivity(), room.getRoomId());
            }
        });
    }

    private void fetchData() {
        ChatRoomHttpClient.getInstance().fetchChatRoomList(new ChatRoomHttpClient.ChatRoomHttpCallback<List<ChatRoomInfo>>() {
            @Override
            public void onSuccess(List<ChatRoomInfo> rooms) {
                onFetchDataDone(true, rooms);
            }

            @Override
            public void onFailed(int code, String errorMsg) {
                onFetchDataDone(false, null);
                if (getActivity() != null) {
                    Toast.makeText(getActivity(), "fetch chat room list failed, code=" + code, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void onFetchDataDone(final boolean success, final List<ChatRoomInfo> data) {
        Activity context = getActivity();
        if (context != null) {
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(false); // 刷新结束

                    if (success) {
                        adapter.setNewData(data); // 刷新数据源

                        postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                adapter.closeLoadAnimation();
                            }
                        });
                    }
                }
            });
        }
    }
}
