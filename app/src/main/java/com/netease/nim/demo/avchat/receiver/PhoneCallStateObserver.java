package com.netease.nim.demo.avchat.receiver;

import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.avchat.AVChatCallback;
import com.netease.nimlib.sdk.avchat.AVChatManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huangjun on 2015/5/13.
 */
public class PhoneCallStateObserver {

    public enum PhoneCallStateEnum {
        IDLE,           // 空闲
        INCOMING_CALL,  // 有来电
        DIALING_OUT,    // 呼出电话已经接通
        DIALING_IN      // 来电已接通
    }

    private final String TAG = "PhoneCallStateObserver";

    private int phoneState = TelephonyManager.CALL_STATE_IDLE;
    private PhoneCallStateEnum  stateEnum = PhoneCallStateObserver.PhoneCallStateEnum.IDLE;;
    private List<Observer<Integer>> autoHangUpObservers = new ArrayList<>(1); // 与本地电话互斥的挂断监听

    private static class InstanceHolder {
        public final static PhoneCallStateObserver instance = new PhoneCallStateObserver();
    }

    public static PhoneCallStateObserver getInstance() {
        return InstanceHolder.instance;
    }

    public void onCallStateChanged(String state) {
        Log.i(TAG, "onCallStateChanged, now state =" + state);

        stateEnum = PhoneCallStateEnum.IDLE;
        if (TelephonyManager.EXTRA_STATE_IDLE.equals(state)) {
            phoneState = TelephonyManager.CALL_STATE_IDLE;
            stateEnum = PhoneCallStateEnum.IDLE;
        } else if (TelephonyManager.EXTRA_STATE_RINGING.equals(state)) {
            phoneState = TelephonyManager.CALL_STATE_RINGING;
            stateEnum = PhoneCallStateEnum.INCOMING_CALL;
        } else if (TelephonyManager.EXTRA_STATE_OFFHOOK.equals(state)) {
            int lastPhoneState = phoneState;
            phoneState = TelephonyManager.CALL_STATE_OFFHOOK;
            if (lastPhoneState == TelephonyManager.CALL_STATE_IDLE) {
                stateEnum = PhoneCallStateEnum.DIALING_OUT;
            } else if (lastPhoneState == TelephonyManager.CALL_STATE_RINGING) {
                stateEnum = PhoneCallStateEnum.DIALING_IN;
            }
        }

        handleLocalCall();
    }
    /**
     * 处理本地电话与网络通话的互斥
     */
    public void handleLocalCall() {
        LogUtil.i(TAG, "notify phone state changed, state=" + stateEnum.name());

        if (stateEnum != PhoneCallStateEnum.IDLE) {
            AVChatManager.getInstance().hangUp2(AVChatManager.getInstance().getCurrentChatId(), new HandleLocalCallCallback(1));
        }
    }

    public PhoneCallStateEnum getPhoneCallState() {
        return stateEnum;
    }

    private class HandleLocalCallCallback implements AVChatCallback<Void> {
        private int reason;
        private String log;

        public HandleLocalCallCallback(int reason) {
            this.reason = reason;
            this.log = "handle local call";
        }

        @Override
        public void onSuccess(Void param) {
            notifyObservers(autoHangUpObservers, reason);
        }

        @Override
        public void onFailed(int code) {
            notifyObservers(autoHangUpObservers, -1 * reason);
        }

        @Override
        public void onException(Throwable exception) {
            notifyObservers(autoHangUpObservers, 0);

            if (!TextUtils.isEmpty(log)) {
                LogUtil.i(TAG, log + " throws exception, e=" + exception.getMessage());
            }
        }
    }

    private <T> void notifyObservers(List<Observer<T>> observers, T result) {
        if (observers == null || observers.isEmpty()) {
            return;
        }

        // 创建副本，为了使得回调到app后，app如果立即注销观察者，会造成List异常。
        List<Observer<T>> copy = new ArrayList<>(observers.size());
        copy.addAll(observers);

        for (Observer<T> o : copy) {
            o.onEvent(result);
        }
    }

    private <T> void registerObservers(List<Observer<T>> observers, final Observer<T> observer, boolean register) {
        if (observers == null || observer == null) {
            return;
        }

        if (register) {
            observers.add(observer);
        } else {
            observers.remove(observer);
        }
    }

    /**
     * 监听网络通话发起，接听或正在进行时有本地来电的通知
     * 网络通话发起或者正在接通时，需要监听是否有本地来电（用户接通本地来电）。
     * 若有本地来电，目前Demo中示例代码的处理是网络通话自动拒绝或者挂断，开发者可以自行灵活处理。
     */
    public void observeAutoHangUpForLocalPhone(Observer<Integer> observer, boolean register) {
        LogUtil.i(TAG, "observeAutoHangUpForLocalPhone->" + observer + "#" + register);
        registerObservers(this.autoHangUpObservers, observer, register);
    }
}
