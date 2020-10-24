package com.netease.nim.demo.session.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.jude.swipbackhelper.SwipeBackHelper;
import com.netease.nim.demo.R;
import com.netease.nim.demo.wzteng.checkpwd.PassValitationPopwindow;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.model.ToolBarOptions;

import java.util.regex.PatternSyntaxException;

public class SendRedPackedActivity extends UI {
    private EditText etMoney;
    private EditText etSay;
    private Button btnSend;

    private String sMoney;
    private String sSay;

    public static void start(Context context) {
        Intent intent = new Intent(context, SendRedPackedActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(intent);
    }

    public static void startActivityForResult(Activity activity, int requestCode) {
        Intent intent = new Intent(activity, SendRedPackedActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_red_packed);

        SwipeBackHelper.onCreate(this);
        SwipeBackHelper.getCurrentPage(this)//获取当前页面
                .setSwipeBackEnable(false);//设置是否可滑动

        ToolBarOptions options = new ToolBarOptions();
        options.titleString = "发红包";
        setToolBar(R.id.toolbar, options);
        etMoney = findView(R.id.edit_red_money);
        etSay = findView(R.id.edit_red_say);
        etMoney.addTextChangedListener(textWatcher);
//        etSay.addTextChangedListener(textWatcher);
        btnSend = findView(R.id.btn_red_send);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showKeyboard(false);
                new PassValitationPopwindow(SendRedPackedActivity.this, 1, findViewById(R.id.btn_red_send), new PassValitationPopwindow.OnInputNumberCodeCallback() {
                    @Override
                    public void onSuccess() {
                        sMoney = etMoney.getText().toString();
                        sSay = etSay.getText().toString();
                        if ("".equals(sSay)) {
                            sSay = "恭喜发财，大吉大利！";
                        }
                        Intent intent = SendRedPackedActivity.this.getIntent();
                        intent.putExtra("money", sMoney);
                        intent.putExtra("say", sSay);
                        setResult(Activity.RESULT_OK, intent);
                        SendRedPackedActivity.this.finish();
                    }
                });
            }
        });
//        btnSend.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showKeyboard(false);
//                sMoney = etMoney.getText().toString();
//                sSay = etSay.getText().toString();
//                if ("".equals(sSay)) {
//                    sSay = "恭喜发财，大吉大利！";
//                }
//                Intent intent = SendRedPackedActivity.this.getIntent();
//                intent.putExtra("money", sMoney);
//                intent.putExtra("say", sSay);
//                setResult(Activity.RESULT_OK, intent);
//                SendRedPackedActivity.this.finish();
//            }
//        });
    }

    public boolean stringFilter(String str) throws PatternSyntaxException {
        String regEx = "^(?!0+(?:\\.0+)?$)\\d+(?:\\.\\d{1,2})?$";
        return str.matches(regEx);
    }

    private TextWatcher textWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            boolean isEnable = etMoney.getText().length() > 0 && stringFilter(etMoney.getText().toString());
            btnSend.setEnabled(isEnable);
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        showKeyboard(false);
    }
}
