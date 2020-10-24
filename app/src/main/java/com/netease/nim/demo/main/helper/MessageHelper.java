package com.netease.nim.demo.main.helper;

import android.text.TextUtils;

import com.netease.nim.uikit.cache.NimUserInfoCache;
import com.netease.nim.uikit.cache.TeamDataCache;
import com.netease.nimlib.sdk.friend.model.AddFriendNotify;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.constant.SystemMessageStatus;
import com.netease.nimlib.sdk.msg.constant.SystemMessageType;
import com.netease.nimlib.sdk.msg.model.SystemMessage;
import com.netease.nimlib.sdk.team.model.Team;

/**
 * Created by huangjun on 2015/4/9.
 */
public class MessageHelper {
    public static String getName(String account, SessionTypeEnum sessionType) {
        if (sessionType == SessionTypeEnum.P2P) {
            return NimUserInfoCache.getInstance().getUserDisplayName(account);
        } else if (sessionType == SessionTypeEnum.Team) {
            return TeamDataCache.getInstance().getTeamName(account);
        }
        return account;
    }

    public static String getVerifyNotificationText(SystemMessage message) {
        StringBuilder sb = new StringBuilder();
        String fromAccount = NimUserInfoCache.getInstance().getUserDisplayNameYou(message.getFromAccount());
        Team team = TeamDataCache.getInstance().getTeamById(message.getTargetId());
        if (team == null && message.getAttachObject() instanceof Team) {
            team = (Team) message.getAttachObject();
        }
        String teamName = team == null ? message.getTargetId() : team.getName();

        if (message.getType() == SystemMessageType.TeamInvite) {
            sb.append("邀请").append("你").append("加入群 ").append(teamName);
        } else if (message.getType() == SystemMessageType.DeclineTeamInvite) {
            sb.append(fromAccount).append("拒绝了群 ").append(teamName).append(" 邀请");
        } else if (message.getType() == SystemMessageType.ApplyJoinTeam) {
            sb.append("申请加入群 ").append(teamName);
        } else if (message.getType() == SystemMessageType.RejectTeamApply) {
            sb.append(fromAccount).append("拒绝了你加入群 ").append(teamName).append("的申请");
        } else if (message.getType() == SystemMessageType.AddFriend) {
            AddFriendNotify attachData = (AddFriendNotify) message.getAttachObject();
            if (attachData != null) {
                if (attachData.getEvent() == AddFriendNotify.Event.RECV_ADD_FRIEND_DIRECT) {
                    sb.append("已添加你为好友");
                } else if (attachData.getEvent() == AddFriendNotify.Event.RECV_AGREE_ADD_FRIEND) {
                    sb.append("通过了你的好友请求");
                } else if (attachData.getEvent() == AddFriendNotify.Event.RECV_REJECT_ADD_FRIEND) {
                    sb.append("拒绝了你的好友请求");
                } else if (attachData.getEvent() == AddFriendNotify.Event.RECV_ADD_FRIEND_VERIFY_REQUEST) {
                    sb.append("请求添加好友" + (TextUtils.isEmpty(message.getContent()) ? "" : "：" + message.getContent()));
                }
            }
        }

        return sb.toString();
    }

    /**
     * 是否验证消息需要处理（需要有同意拒绝的操作栏）
     */
    public static boolean isVerifyMessageNeedDeal(SystemMessage message) {
        if (message.getType() == SystemMessageType.AddFriend) {
            if (message.getAttachObject() != null) {
                AddFriendNotify attachData = (AddFriendNotify) message.getAttachObject();
                if (attachData.getEvent() == AddFriendNotify.Event.RECV_ADD_FRIEND_DIRECT ||
                        attachData.getEvent() == AddFriendNotify.Event.RECV_AGREE_ADD_FRIEND ||
                        attachData.getEvent() == AddFriendNotify.Event.RECV_REJECT_ADD_FRIEND) {
                    return false; // 对方直接加你为好友，对方通过你的好友请求，对方拒绝你的好友请求
                } else if (attachData.getEvent() == AddFriendNotify.Event.RECV_ADD_FRIEND_VERIFY_REQUEST) {
                    return true; // 好友验证请求
                }
            }
            return false;
        } else if (message.getType() == SystemMessageType.TeamInvite || message.getType() == SystemMessageType.ApplyJoinTeam) {
            return true;
        } else {
            return false;
        }
    }

    public static String getVerifyNotificationDealResult(SystemMessage message) {
        if (message.getStatus() == SystemMessageStatus.passed) {
            return "已同意";
        } else if (message.getStatus() == SystemMessageStatus.declined) {
            return "已拒绝";
        } else if (message.getStatus() == SystemMessageStatus.ignored) {
            return "已忽略";
        } else if (message.getStatus() == SystemMessageStatus.expired) {
            return "已过期";
        } else {
            return "未处理";
        }
    }
}
