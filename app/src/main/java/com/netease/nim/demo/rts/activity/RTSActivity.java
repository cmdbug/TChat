package com.netease.nim.demo.rts.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.netease.nim.demo.R;
import com.netease.nim.demo.rts.ActionTypeEnum;
import com.netease.nim.demo.rts.doodle.DoodleView;
import com.netease.nim.demo.rts.doodle.SupportActionType;
import com.netease.nim.demo.rts.doodle.TransactionCenter;
import com.netease.nim.demo.rts.doodle.action.MyPath;
import com.netease.nim.demo.session.extension.RTSAttachment;
import com.netease.nim.uikit.cache.NimUserInfoCache;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialog;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialogHelper;
import com.netease.nim.uikit.common.ui.imageview.HeadImageView;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.common.util.sys.ScreenUtil;
import com.netease.nim.uikit.model.ToolBarOptions;
import com.netease.nim.uikit.session.helper.MessageListPanelHelper;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.StatusCode;
import com.netease.nimlib.sdk.auth.AuthServiceObserver;
import com.netease.nimlib.sdk.auth.ClientType;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.constant.MsgDirectionEnum;
import com.netease.nimlib.sdk.msg.constant.MsgStatusEnum;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.rts.RTSCallback;
import com.netease.nimlib.sdk.rts.RTSChannelStateObserver;
import com.netease.nimlib.sdk.rts.RTSManager;
import com.netease.nimlib.sdk.rts.constant.RTSEventType;
import com.netease.nimlib.sdk.rts.constant.RTSTimeOutEvent;
import com.netease.nimlib.sdk.rts.constant.RTSTunnelType;
import com.netease.nimlib.sdk.rts.model.RTSCalleeAckEvent;
import com.netease.nimlib.sdk.rts.model.RTSCommonEvent;
import com.netease.nimlib.sdk.rts.model.RTSControlEvent;
import com.netease.nimlib.sdk.rts.model.RTSData;
import com.netease.nimlib.sdk.rts.model.RTSNotifyOption;
import com.netease.nimlib.sdk.rts.model.RTSOnlineAckEvent;
import com.netease.nimlib.sdk.rts.model.RTSOptions;
import com.netease.nimlib.sdk.rts.model.RTSTunData;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * 发起/接受会话界面
 * <p/>
 * Created by huangjun on 2015/7/27.
 */
public class RTSActivity extends UI implements View.OnClickListener {

    public static final int FROM_BROADCAST_RECEIVER = 0; // 来自广播
    public static final int FROM_INTERNAL = 1; // 来自发起方
    private static final String KEY_RTS_DATA = "KEY_RTS_DATA";
    private static final String KEY_INCOMING = "KEY_INCOMING";
    private static final String KEY_SOURCE = "KEY_SOURCE";
    private static final String KEY_UID = "KEY_UID";

    // data
    private boolean isIncoming = false;
    private String account;      // 对方帐号
    private String sessionId;    // 会话的唯一标识
    private RTSData sessionInfo; // 本次会话的信息
    private boolean audioOpen = false; // 语音默认
    private boolean finishFlag = false; // 结束标记，避免多次回调onFinish
    private static boolean needFinish = true; // Activity销毁后，从最近任务列表恢复，则finish

    private static boolean isBusy = false;

    // 发起会话布局
    private View startSessionLayout;
    private TextView sessionStepText;
    private HeadImageView headImage;
    private TextView nameText;
    private View calleeAckLayout;
    private Button acceptBtn;
    private Button rejectBtn;
    private Button endSessionBtn;
    private Button audioSwitchBtn;

    // 白板布局
    private View sessionLayout;
    private DoodleView doodleView;
    private Button backBtn;
    private Button clearBtn;

    public static void incomingSession(Context context, RTSData data, int source) {

        if (isBusy) {
            RTSManager.getInstance().close(data.getLocalSessionId(), null);
            Toast.makeText(context, "close session", Toast.LENGTH_SHORT).show();
            return;
        }

        needFinish = false;
        Intent intent = new Intent();
        intent.setClass(context, RTSActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(KEY_RTS_DATA, data);
        intent.putExtra(KEY_INCOMING, true);
        intent.putExtra(KEY_SOURCE, source);
        context.startActivity(intent);
    }

    public static void startSession(Context context, String account, int source) {
        needFinish = false;
        Intent intent = new Intent();
        intent.setClass(context, RTSActivity.class);
        intent.putExtra(KEY_UID, account);
        intent.putExtra(KEY_INCOMING, false);
        intent.putExtra(KEY_SOURCE, source);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (needFinish) {
            finish();
            return;
        }

        isBusy = true;

        setContentView(R.layout.rts_activity);

        ToolBarOptions options = new ToolBarOptions();
        options.isNeedNavigate = false;
        setToolBar(R.id.toolbar, options);

        isIncoming = getIntent().getBooleanExtra(KEY_INCOMING, false);
        findViews();
        initActionBarButton();

        if (isIncoming) {
            incoming();
            registerInComingObserver(true);
        } else {
            outgoing();
            registerOutgoingObserver(true);
        }

        initAudioSwitch();
        registerCommonObserver(true);

        //放到所有UI的基类里面注册，所有的UI实现onKickOut接口
        NIMClient.getService(AuthServiceObserver.class).observeOnlineStatus(userStatusObserver, true);
    }

    private void initActionBarButton() {
        TextView closeSessionBtn = findView(R.id.action_bar_right_clickable_textview);
        closeSessionBtn.setText(R.string.close);
        closeSessionBtn.setBackgroundResource(R.drawable.nim_message_button_bottom_send_selector);
        closeSessionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EasyAlertDialogHelper.OnDialogActionListener listener = new EasyAlertDialogHelper.OnDialogActionListener() {

                    @Override
                    public void doCancelAction() {
                    }

                    @Override
                    public void doOkAction() {
                        endSession(); // 挂断
                    }
                };
                final EasyAlertDialog dialog = EasyAlertDialogHelper.createOkCancelDiolag(RTSActivity.this,
                        getString(R.string.end_session_tip_head),
                        getString(R.string.end_session_tip_content),
                        getString(R.string.ok), getString(R.string.cancel), true, listener);
                dialog.show();
            }
        });
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    protected boolean displayHomeAsUpEnabled() {
        return false;
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

        // 这里需要重绘
        doodleView.onResume();
    }

    @Override
    protected void onDestroy() {
        if (doodleView != null) {
            doodleView.end();
        }

        super.onDestroy();
        NIMClient.getService(AuthServiceObserver.class).observeOnlineStatus(userStatusObserver, false);
        registerInComingObserver(false);
        registerOutgoingObserver(false);
        registerCommonObserver(false);

        needFinish = true;

        isBusy = false;
    }

    Observer<StatusCode> userStatusObserver = new Observer<StatusCode>() {

        @Override
        public void onEvent(StatusCode code) {
            if (code.wontAutoLogin()) {
                finish();
            }
        }
    };

    private void findViews() {
        startSessionLayout = findViewById(R.id.start_session_layout);
        sessionLayout = findViewById(R.id.session_layout);

        headImage = (HeadImageView) findViewById(R.id.head_image);
        sessionStepText = (TextView) findViewById(R.id.session_step_text);
        nameText = (TextView) findViewById(R.id.name);
        calleeAckLayout = findViewById(R.id.callee_ack_layout);
        acceptBtn = (Button) findViewById(R.id.accept);
        rejectBtn = (Button) findViewById(R.id.reject);
        endSessionBtn = (Button) findViewById(R.id.end_session);
        doodleView = (DoodleView) findViewById(R.id.doodle_view);
        backBtn = (Button) findViewById(R.id.doodle_back);
        clearBtn = (Button) findViewById(R.id.doodle_clear);
        audioSwitchBtn = (Button) findViewById(R.id.audio_switch);

        acceptBtn.setOnClickListener(this);
        rejectBtn.setOnClickListener(this);
        endSessionBtn.setOnClickListener(this);
        audioSwitchBtn.setOnClickListener(this);
        backBtn.setOnClickListener(this);
        clearBtn.setOnClickListener(this);

        float screenWidth = ScreenUtil.screenWidth * 1.0f;
        ViewGroup.LayoutParams params = doodleView.getLayoutParams();
        params.width = ((int) (screenWidth / 100)) * 100; // 比屏幕小的100的整数
        params.height = params.width; // 保证宽高比为1:1
        doodleView.setLayoutParams(params);
    }

    private void incoming() {
        sessionInfo = (RTSData) getIntent().getSerializableExtra(KEY_RTS_DATA);
        account = sessionInfo.getAccount();
        sessionId = sessionInfo.getLocalSessionId();

        Toast.makeText(RTSActivity.this, "incoming session, extra=" + sessionInfo.getExtra(),
                Toast.LENGTH_SHORT)
                .show();
        initIncomingSessionViews();
    }

    private void outgoing() {
        account = getIntent().getStringExtra(KEY_UID);

        initStartSessionViews();
        startSession();
    }

    private void initStartSessionViews() {
        initAccountInfoView();
        sessionStepText.setText(R.string.start_session);
        calleeAckLayout.setVisibility(View.GONE);
        endSessionBtn.setVisibility(View.VISIBLE);
        startSessionLayout.setVisibility(View.VISIBLE);
    }

    private void initIncomingSessionViews() {
        initAccountInfoView();
        sessionStepText.setText(R.string.receive_session);
        calleeAckLayout.setVisibility(View.VISIBLE);
        endSessionBtn.setVisibility(View.GONE);
        startSessionLayout.setVisibility(View.VISIBLE);
    }

    private void initAccountInfoView() {
        nameText.setText(NimUserInfoCache.getInstance().getUserDisplayName(account));
        headImage.loadBuddyAvatar(account);
    }

    private void registerOutgoingObserver(boolean register) {
        RTSManager.getInstance().observeCalleeAckNotification(sessionId, calleeAckEventObserver, register);
    }

    private void registerInComingObserver(boolean register) {
        RTSManager.getInstance().observeOnlineAckNotification(sessionId, onlineAckObserver, register);
    }

    private void registerCommonObserver(boolean register) {
        RTSManager.getInstance().observeChannelState(sessionId, channelStateObserver, register);
        RTSManager.getInstance().observeHangUpNotification(sessionId, endSessionObserver, register);
        RTSManager.getInstance().observeReceiveData(sessionId, receiveDataObserver, register);
        RTSManager.getInstance().observeTimeoutNotification(sessionId, timeoutObserver, register);
        RTSManager.getInstance().observeControlNotification(sessionId, controlObserver, register);
    }

    /**
     * 主叫方监听被叫方的接受or拒绝会话的响应
     */
    private Observer<RTSCalleeAckEvent> calleeAckEventObserver = new Observer<RTSCalleeAckEvent>() {
        @Override
        public void onEvent(RTSCalleeAckEvent rtsCalleeAckEvent) {
            if (rtsCalleeAckEvent.getEvent() == RTSEventType.CALLEE_ACK_AGREE) {
                // 判断SDK自动开启通道是否成功
                if (!rtsCalleeAckEvent.isTunReady()) {
                    Toast.makeText(RTSActivity.this, "通道开启失败!请查看LOG", Toast.LENGTH_SHORT).show();
                    return;
                }
                acceptView(); // 进入会话界面
            } else if (rtsCalleeAckEvent.getEvent() == RTSEventType.CALLEE_ACK_REJECT) {
                Toast.makeText(RTSActivity.this, R.string.callee_reject, Toast.LENGTH_SHORT).show();
                onFinish(false);
            }
        }
    };

    /**
     * 监听对方挂断
     */
    private Observer<RTSCommonEvent> endSessionObserver = new Observer<RTSCommonEvent>() {
        @Override
        public void onEvent(RTSCommonEvent rtsCommonEvent) {
            Toast.makeText(RTSActivity.this, R.string.target_has_end_session, Toast.LENGTH_SHORT).show();
            onFinish(false);
        }
    };

    /**
     * 监听收到对方发送的通道数据
     */
    private Observer<RTSTunData> receiveDataObserver = new Observer<RTSTunData>() {
        @Override
        public void onEvent(RTSTunData rtsTunData) {
            String data = "[parse bytes error]";
            try {
                data = new String(rtsTunData.getData(), 0, rtsTunData.getLength(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            TransactionCenter.getInstance().onReceive(sessionId, data);
        }
    };

    /**
     * 被叫方监听在线其他端的接听响应
     */
    private Observer<RTSOnlineAckEvent> onlineAckObserver = new Observer<RTSOnlineAckEvent>() {
        @Override
        public void onEvent(RTSOnlineAckEvent rtsOnlineAckEvent) {
            if (rtsOnlineAckEvent.getClientType() != ClientType.Android) {
                String client = null;
                switch (rtsOnlineAckEvent.getClientType()) {
                    case ClientType.Web:
                        client = "Web";
                        break;
                    case ClientType.Windows:
                        client = "Windows";
                        break;
                    default:
                        break;
                }
                if (client != null) {
                    String option = rtsOnlineAckEvent.getEvent() == RTSEventType.CALLEE_ONLINE_CLIENT_ACK_AGREE ?
                            "接受" : "拒绝";
                    Toast.makeText(RTSActivity.this, "白板演示已在" + client + "端被" + option, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(RTSActivity.this, "白板演示已在其他端处理", Toast.LENGTH_SHORT).show();
                }
                onFinish();
            }
        }
    };

    /**
     * 监听控制消息
     */
    private Observer<RTSControlEvent> controlObserver = new Observer<RTSControlEvent>() {
        @Override
        public void onEvent(RTSControlEvent rtsControlEvent) {
            Toast.makeText(RTSActivity.this, rtsControlEvent.getCommandInfo(), Toast.LENGTH_SHORT).show();
        }
    };

    /**
     * 监听当前会话的状态
     */
    private RTSChannelStateObserver channelStateObserver = new RTSChannelStateObserver() {

        @Override
        public void onConnectResult(String sessionId, RTSTunnelType tunType, long channelId, int code, String file) {
            Toast.makeText(RTSActivity.this, "onConnectResult, tunType=" + tunType.toString() +
                    ", channelId=" + channelId +
                    ", code=" + code + ", file=" + file, Toast.LENGTH_SHORT).show();
        }


        @Override
        public void onChannelEstablished(String sessionId, RTSTunnelType tunType) {
            Toast.makeText(RTSActivity.this, "onCallEstablished,tunType=" + tunType.toString(), Toast
                    .LENGTH_SHORT).show();

            if (tunType == RTSTunnelType.AUDIO) {
                RTSManager.getInstance().setSpeaker(sessionId, true); // 默认开启扬声器
            }
        }

        @Override
        public void onUserJoin(String sessionId, RTSTunnelType tunType, String account) {

        }

        @Override
        public void onUserLeave(String sessionId, RTSTunnelType tunType, String account, int event) {

        }

        @Override
        public void onDisconnectServer(String sessionId, RTSTunnelType tunType) {
            Toast.makeText(RTSActivity.this, "onDisconnectServer, tunType=" + tunType.toString(), Toast
                    .LENGTH_SHORT).show();
            if (tunType == RTSTunnelType.DATA) {
                // 如果数据通道断了，那么关闭会话
                Toast.makeText(RTSActivity.this, "TCP通道断开，自动结束会话", Toast.LENGTH_SHORT).show();
                endSession();
            } else if (tunType == RTSTunnelType.AUDIO) {
                // 如果音频通道断了，那么UI变换
                if (audioOpen) {
                    audioSwitch();
                }
            }
        }

        @Override
        public void onError(String sessionId, RTSTunnelType tunType, int code) {
            Toast.makeText(RTSActivity.this, "onError, tunType=" + tunType.toString() + ", error=" + code,
                    Toast.LENGTH_LONG).show();
            endSession();
        }

        @Override
        public void onNetworkStatusChange(String sessionId, RTSTunnelType tunType, int value) {
            // 网络信号强弱
        }
    };

    private Observer<RTSTimeOutEvent> timeoutObserver = new Observer<RTSTimeOutEvent>() {
        @Override
        public void onEvent(RTSTimeOutEvent rtsTimeOutEvent) {
            Toast.makeText(RTSActivity.this,
                    (rtsTimeOutEvent == RTSTimeOutEvent.OUTGOING_TIMEOUT) ? getString(R.string.callee_ack_timeout) :
                            "超时未处理，自动结束", Toast.LENGTH_SHORT).show();
            onFinish();
        }
    };

    private void startSession() {
        List<RTSTunnelType> types = new ArrayList<>(1);
        types.add(RTSTunnelType.AUDIO);
        types.add(RTSTunnelType.DATA);

        String pushContent = account + "发起一个会话";
        String extra = "extra_data";
        RTSOptions options = new RTSOptions().setRecordAudioTun(false)
                .setRecordDataTun(true);
        RTSNotifyOption notifyOption = new RTSNotifyOption();
        notifyOption.apnsContent = pushContent;
        notifyOption.extendMessage = extra;

        sessionId = RTSManager.getInstance().start(account, types, options, notifyOption, new RTSCallback<RTSData>() {
            @Override
            public void onSuccess(RTSData rtsData) {
                RTSAttachment attachment = new RTSAttachment((byte) 0);
                IMMessage msg = MessageBuilder.createCustomMessage(account, SessionTypeEnum.P2P, attachment.getContent(), attachment);
                MessageListPanelHelper.getInstance().notifyAddMessage(msg); // 界面上add一条
                NIMClient.getService(MsgService.class).sendMessage(msg, false); // 发送给对方
            }

            @Override
            public void onFailed(int code) {
                if (code == 11001) {
                    Toast.makeText(RTSActivity.this, "无可送达的被叫方", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(RTSActivity.this, "发起会话失败,code=" + code, Toast.LENGTH_SHORT).show();
                }
                onFinish();
            }

            @Override
            public void onException(Throwable exception) {
                Toast.makeText(RTSActivity.this, "发起会话异常,e=" + exception.toString(), Toast.LENGTH_SHORT).show();
                onFinish();
            }
        });

        if (sessionId == null) {
            Toast.makeText(RTSActivity.this, "发起会话失败!", Toast.LENGTH_SHORT).show();
            onFinish();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.accept:
                acceptSession();
                break;
            case R.id.reject:
                endSession();
                break;
            case R.id.end_session:
                endSession();
                break;
            case R.id.doodle_back:
                doodleBack();
                break;
            case R.id.doodle_clear:
                clear();
                break;
            case R.id.audio_switch:
                audioSwitch();
                break;
            default:
                break;
        }
    }

    private void acceptSession() {
        RTSOptions options = new RTSOptions().setRecordAudioTun(false).setRecordDataTun(true);
        RTSManager.getInstance().accept(sessionId, options, new RTSCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean success) {
                // 判断开启通道是否成功
                if (success) {
                    acceptView();
                } else {
                    Toast.makeText(RTSActivity.this, "通道开启失败!请查看LOG", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailed(int code) {
                if (code == -1) {
                    Toast.makeText(RTSActivity.this, "接受会话失败,音频通道同时只能有一个会话开启", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(RTSActivity.this, "接受会话失败, code=" + code, Toast.LENGTH_SHORT).show();
                }
                onFinish();
            }

            @Override
            public void onException(Throwable exception) {
                Toast.makeText(RTSActivity.this, "接受会话异常, e=" + exception.toString(), Toast.LENGTH_SHORT).show();
                onFinish();
            }
        });
    }

    private void acceptView() {
        startSessionLayout.setVisibility(View.GONE);
        sessionLayout.setVisibility(View.VISIBLE);
        initDoodleView();


    }

    private void endSession() {
        RTSManager.getInstance().close(sessionId, new RTSCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }

            @Override
            public void onFailed(int code) {
                Toast.makeText(RTSActivity.this, "挂断请求错误，code：" + code, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onException(Throwable exception) {

            }
        });

        onFinish();
    }

    private void onFinish() {
        onFinish(true);
    }

    private void onFinish(boolean selfFinish) {
        if (finishFlag) {
            return;
        }
        finishFlag = true;

        RTSAttachment attachment = new RTSAttachment((byte) 1);

        IMMessage msg = MessageBuilder.createCustomMessage(account, SessionTypeEnum.P2P, attachment.getContent(), attachment);
        if (!selfFinish) {
            // 被结束会话，在这里模拟一条接收的消息
            msg.setFromAccount(account);
            msg.setDirect(MsgDirectionEnum.In);
        }

        msg.setStatus(MsgStatusEnum.success);

        NIMClient.getService(MsgService.class).saveMessageToLocal(msg, true);

        finish();
    }

    /**
     * ***************************** 画板 ***********************************
     */
    private void initDoodleView() {
        // add support ActionType
        SupportActionType.getInstance().addSupportActionType(ActionTypeEnum.Path.getValue(), MyPath.class);

        doodleView.init(sessionId, account, DoodleView.Mode.BOTH, Color.WHITE, this);

        doodleView.setPaintSize(10);
        doodleView.setPaintType(ActionTypeEnum.Path.getValue());

        // adjust paint offset
        new Handler(getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                Rect frame = new Rect();
                getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
                int statusBarHeight = frame.top;
                Log.i("Doodle", "statusBarHeight =" + statusBarHeight);

                int marginTop = doodleView.getTop();
                Log.i("Doodle", "doodleView marginTop =" + marginTop);

                int marginLeft = doodleView.getLeft();
                Log.i("Doodle", "doodleView marginLeft =" + marginLeft);

                float offsetX = marginLeft;
                float offsetY = statusBarHeight + marginTop;

                doodleView.setPaintOffset(offsetX, offsetY);
                Log.i("Doodle", "client1 offsetX = " + offsetX + ", offsetY = " + offsetY);
            }
        }, 50);
    }

    /**
     * 撤销一步
     */
    private void doodleBack() {
        doodleView.paintBack();
    }

    /**
     * 清屏
     */
    private void clear() {
        doodleView.clear();
    }

    /**
     * 语音开关
     */
    private void audioSwitch() {
        audioOpen = !audioOpen;
        RTSManager.getInstance().setMute(sessionId, !audioOpen);
        audioSwitchBtn.setBackgroundResource(audioOpen ? R.drawable.icon_audio_open : R.drawable.icon_audio_close);

        // 通过控制协议通知对方
        String content = "对方静音" + (audioOpen ? "关闭" : "开启");
        RTSManager.getInstance().sendControlCommand(sessionId, content, new RTSCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                String tip = "静音" + (audioOpen ? "关闭" : "开启");
                Toast.makeText(RTSActivity.this, tip, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailed(int code) {
                Toast.makeText(RTSActivity.this, "控制协议发送失败, code =" + code, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onException(Throwable exception) {
                Toast.makeText(RTSActivity.this, "控制协议发送异常, e=" + exception.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 初始化语音开关(默认关闭)
     */
    private void initAudioSwitch() {
        RTSManager.getInstance().setMute(sessionId, true);
        audioSwitchBtn.setBackgroundResource(R.drawable.icon_audio_close);
    }
}
