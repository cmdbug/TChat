package com.netease.nim.demo.main.reminder;

public class ReminderSettings {
	/**
	 * 最大显示未读数
	 */
	public static final int MAX_UNREAD_SHOW_NUMBER = 99;

	public static int unreadMessageShowRule(int unread) {
		return Math.min(MAX_UNREAD_SHOW_NUMBER, unread);
	}
}
