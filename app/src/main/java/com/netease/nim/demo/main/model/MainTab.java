package com.netease.nim.demo.main.model;

import com.netease.nim.demo.R;
import com.netease.nim.demo.main.fragment.ContactListFragment;
import com.netease.nim.demo.main.fragment.FindListFragment;
import com.netease.nim.demo.main.fragment.MainTabFragment;
import com.netease.nim.demo.main.fragment.ProfileListFragment;
import com.netease.nim.demo.main.fragment.SessionListFragment;
import com.netease.nim.demo.main.reminder.ReminderId;

public enum MainTab {
    RECENT_CONTACTS(0, ReminderId.SESSION, SessionListFragment.class, R.string.main_tab_session, R.drawable.weixin_normal, R.layout.session_list),
    CONTACT(1, ReminderId.CONTACT, ContactListFragment.class, R.string.main_tab_contact, R.drawable.contact_list_normal, R.layout.contacts_list),
//    CHAT_ROOM(2, ReminderId.INVALID, ChatRoomListFragment.class, R.string.chat_room, R.drawable.find_normal, R.layout.chat_room_tab),
    FIND(2, ReminderId.FIND, FindListFragment.class, R.string.main_tab_find, R.drawable.find_normal, R.layout.wzt_find_contacts),
    PROFILE(3, ReminderId.PROFILE, ProfileListFragment.class, R.string.main_tab_profile, R.drawable.profile_normal, R.layout.wzt_profile_contacts);

    public final int tabIndex;

    public final int reminderId;

    public final Class<? extends MainTabFragment> clazz;

    public final int resId;

    public final int resIconId;

    public final int fragmentId;

    public final int layoutId;

    MainTab(int index, int reminderId, Class<? extends MainTabFragment> clazz, int resId, int resIconId, int layoutId) {
        this.tabIndex = index;
        this.reminderId = reminderId;
        this.clazz = clazz;
        this.resId = resId;
        this.resIconId = resIconId;
        this.fragmentId = index;
        this.layoutId = layoutId;
    }

    public static final MainTab fromReminderId(int reminderId) {
        for (MainTab value : MainTab.values()) {
            if (value.reminderId == reminderId) {
                return value;
            }
        }

        return null;
    }

    public static final MainTab fromTabIndex(int tabIndex) {
        for (MainTab value : MainTab.values()) {
            if (value.tabIndex == tabIndex) {
                return value;
            }
        }

        return null;
    }
}
