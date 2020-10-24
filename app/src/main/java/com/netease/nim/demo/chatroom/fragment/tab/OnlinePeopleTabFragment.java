package com.netease.nim.demo.chatroom.fragment.tab;

import com.netease.nim.demo.R;
import com.netease.nim.demo.chatroom.fragment.OnlinePeopleFragment;

/**
 * 在线成员基类fragment
 * Created by hzxuwen on 2015/12/14.
 */
public class OnlinePeopleTabFragment extends ChatRoomTabFragment {
    private OnlinePeopleFragment fragment;

    @Override
    protected void onInit() {
        findViews();
    }

    @Override
    public void onCurrent() {
        super.onCurrent();
        if (fragment != null) {
            fragment.onCurrent();
        }
    }

    private void findViews() {
        fragment = (OnlinePeopleFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.online_people_fragment);
    }
}
