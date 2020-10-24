package com.netease.nim.demo.chatroom.fragment;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.TextView;

import com.netease.nim.demo.R;
import com.netease.nim.demo.chatroom.activity.ChatRoomActivity;
import com.netease.nim.demo.chatroom.adapter.ChatRoomTabPagerAdapter;
import com.netease.nim.demo.chatroom.fragment.tab.ChatRoomTabFragment;
import com.netease.nim.demo.chatroom.helper.ChatRoomHelper;
import com.netease.nim.demo.common.infra.Handlers;
import com.netease.nim.demo.common.ui.viewpager.FadeInOutPageTransformer;
import com.netease.nim.demo.common.ui.viewpager.PagerSlidingTabStrip;
import com.netease.nim.uikit.common.ui.barrage.BarrageConfig;
import com.netease.nim.uikit.common.ui.barrage.BarrageSurfaceView;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.chatroom.ChatRoomService;

/**
 * 聊天室顶层fragment
 * Created by hzxuwen on 2015/12/14.
 */
public class ChatRoomFragment extends ChatRoomTabFragment implements ViewPager.OnPageChangeListener {
    private PagerSlidingTabStrip tabs;
    private ViewPager viewPager;
    private ChatRoomTabPagerAdapter adapter;
    private int scrollState;
    private ImageView imageView;
    private TextView statusText;
    private static final boolean SHOW_BARRAGE = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onInit() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.chat_room_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        findViews();
        setupPager();
        setupTabs();
    }

    public void updateOnlineStatus(boolean isOnline) {
        statusText.setVisibility(isOnline ? View.GONE : View.VISIBLE);
    }

    public void updateView() {
        ChatRoomHelper.setCoverImage(((ChatRoomActivity) getActivity()).getRoomInfo().getRoomId(), imageView, true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void findViews() {
        imageView = findView(R.id.chat_room_view);
        statusText = findView(R.id.online_status);
        final ImageView backImage = findView(R.id.back_arrow);
        tabs = findView(R.id.chat_room_tabs);
        viewPager = findView(R.id.chat_room_viewpager);

        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NIMClient.getService(ChatRoomService.class).exitChatRoom(((ChatRoomActivity) getActivity()).getRoomInfo().getRoomId());
                ((ChatRoomActivity) getActivity()).clearChatRoom();
            }
        });

        // 是否演示弹幕控件
        if (SHOW_BARRAGE) {
            ViewStub barrageViewStub = findView(R.id.barrage_view_stub);
            barrageViewStub.inflate();

            View barrageViewRoot = findView(R.id.barrage_view_after_inflate);
            final BarrageSurfaceView barrageView = (BarrageSurfaceView) barrageViewRoot.findViewById(R.id.barrage_view);

            final String barrageText1 = "欢迎进入直播间";
            final String barrageText2 = "Welcome to live room";
            Handlers.sharedHandler(getActivity()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    BarrageConfig config = new BarrageConfig();
                    config.setDuration(4500);
                    // 初始化弹幕控件
                    barrageView.init(config);
                    for (int i = 1; i <= 200; i++) {
                        barrageView.addTextBarrage((i % 2 == 0 ? barrageText1 : barrageText2) + i);
                    }
                }
            }, 1000);
        }
    }

    private void setupPager() {
        // CACHE COUNT
        adapter = new ChatRoomTabPagerAdapter(getFragmentManager(), getActivity(), viewPager);
        viewPager.setOffscreenPageLimit(adapter.getCacheCount());
        // page swtich animation
        viewPager.setPageTransformer(true, new FadeInOutPageTransformer());
        // ADAPTER
        viewPager.setAdapter(adapter);
        // TAKE OVER CHANGE
        viewPager.setOnPageChangeListener(this);
    }

    private void setupTabs() {
        tabs.setOnCustomTabListener(new PagerSlidingTabStrip.OnCustomTabListener() {
            @Override
            public int getTabLayoutResId(int position) {
                return R.layout.chat_room_tab_layout;
            }

            @Override
            public boolean screenAdaptation() {
                return true;
            }
        });
        tabs.setViewPager(viewPager);
        tabs.setOnTabClickListener(adapter);
        tabs.setOnTabDoubleTapListener(adapter);
    }

    /********************
     * OnPageChangeListener
     **************************/

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        // TO TABS
        tabs.onPageScrolled(position, positionOffset, positionOffsetPixels);
        // TO ADAPTER
        adapter.onPageScrolled(position);
    }

    @Override
    public void onPageSelected(int position) {
        // TO TABS
        tabs.onPageSelected(position);

        selectPage(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        // TO TABS
        tabs.onPageScrollStateChanged(state);

        scrollState = state;

        selectPage(viewPager.getCurrentItem());
    }

    private void selectPage(int page) {
        // TO PAGE
        if (scrollState == ViewPager.SCROLL_STATE_IDLE) {
            adapter.onPageSelected(viewPager.getCurrentItem());
        }
    }
}
