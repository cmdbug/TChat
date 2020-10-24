package com.netease.nim.demo.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.netease.nim.demo.R;
import com.netease.nim.demo.config.preference.Preferences;
import com.netease.nim.demo.contact.ContactHttpClient;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.common.ui.dialog.CustomAlertDialog;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.netease.nim.uikit.common.util.sys.NetworkUtil;
import com.netease.nim.uikit.model.ToolBarOptions;


public class RegisterActivity extends UI {

    private EditText et_usernick;
    private EditText et_usertel;
    private EditText et_password;
    private Button btn_register;
    private TextView tv_protocol;
    private ImageView iv_hide;
    private ImageView iv_show;
    private ImageView iv_photo;

    private String imageName;
    private static final int PHOTO_REQUEST_TAKEPHOTO = 1;// 拍照
    private static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择
    private static final int PHOTO_REQUEST_CUT = 3;// 结果

    public static void start(Context context) {
        start(context, null);
    }

    public static void start(Context context, Intent extras) {
        Intent intent = new Intent();
        intent.setClass(context, RegisterActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        if (extras != null) {
            intent.putExtras(extras);
        }
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ToolBarOptions options = new ToolBarOptions();
        options.titleId = R.string.register;
        setToolBar(R.id.toolbar, options);

        et_usernick = findView(R.id.et_usernick);
        et_usertel = findView(R.id.et_usertel);
        et_password = findView(R.id.et_password);

        // 监听多个输入框
        et_usernick.addTextChangedListener(new TextChange());
        et_usertel.addTextChangedListener(new TextChange());
        et_password.addTextChangedListener(new TextChange());
        btn_register = findView(R.id.btn_register);
        tv_protocol = findView(R.id.tv_protocol);
        iv_hide = findView(R.id.iv_hide);
        iv_show = findView(R.id.iv_show);
        iv_photo = findView(R.id.iv_avatar);

        String protocol = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" +
                "<font color=" + "\"" + "#AAAAAA" + "\">" + "点击上面的"
                + "\"" + "注册" + "\"" + "按钮,即表示你同意" + "</font>" + "<a>"
                + "<font color=" + "\"" + "#576B95" + "\">" + "《藤信软件许可及服务协议》"
                + "</font>" + "</a>。";

        tv_protocol.setText(Html.fromHtml(protocol));
        iv_hide.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                iv_hide.setVisibility(View.GONE);
                iv_show.setVisibility(View.VISIBLE);
                et_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                // 切换后将EditText光标置于末尾
                CharSequence charSequence = et_password.getText();
                if (charSequence instanceof Spannable) {
                    Spannable spanText = (Spannable) charSequence;
                    Selection.setSelection(spanText, charSequence.length());
                }
            }
        });
        iv_show.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                iv_show.setVisibility(View.GONE);
                iv_hide.setVisibility(View.VISIBLE);
                et_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                // 切换后将EditText光标置于末尾
                CharSequence charSequence = et_password.getText();
                if (charSequence instanceof Spannable) {
                    Spannable spanText = (Spannable) charSequence;
                    Selection.setSelection(spanText, charSequence.length());
                }
            }
        });
        iv_photo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //弹出一个列表选择拍照或相册
                CustomAlertDialog alertDialog = new CustomAlertDialog(RegisterActivity.this);
                alertDialog.setTitle("选项");
                String title = "拍照";
                alertDialog.addItem(title, new CustomAlertDialog.onSeparateItemClickListener() {
                    @Override
                    public void onClick() {
                        //拍照
                        Toast.makeText(RegisterActivity.this, "拍照", Toast.LENGTH_SHORT).show();
                    }
                });
                title = "从相册选择";
                alertDialog.addItem(title, new CustomAlertDialog.onSeparateItemClickListener() {
                    @Override
                    public void onClick() {
                        //相册
                        Toast.makeText(RegisterActivity.this, "相册", Toast.LENGTH_SHORT).show();
                    }
                });
                alertDialog.show();
            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (!NetworkUtil.isNetAvailable(RegisterActivity.this)) {
                    Toast.makeText(RegisterActivity.this, R.string.network_is_not_available, Toast.LENGTH_SHORT).show();
                    return;
                }

                DialogMaker.showProgressDialog(RegisterActivity.this, getString(R.string.registering), false);

                final String nickName = et_usernick.getText().toString().trim();
                final String password = et_password.getText().toString().trim();
                final String account = et_usertel.getText().toString().trim();
                ContactHttpClient.getInstance().register(account, nickName,
                        password, new ContactHttpClient.ContactHttpCallback<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(RegisterActivity.this, R.string.register_success, Toast.LENGTH_SHORT).show();
                                DialogMaker.dismissProgressDialog();

                                Preferences.saveUserAccount(account);//teng
                            }

                            @Override
                            public void onFailed(int code, String errorMsg) {
                                Toast.makeText(RegisterActivity.this, getString(R.string.register_failed,
                                        String.valueOf(code), errorMsg), Toast.LENGTH_LONG).show();
                                DialogMaker.dismissProgressDialog();
                            }
                        });

            }
        });
    }

    // EditText监听器
    class TextChange implements TextWatcher {

        @Override
        public void afterTextChanged(Editable arg0) {

        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

        }

        @Override
        public void onTextChanged(CharSequence cs, int start, int before, int count) {

            boolean sign1 = et_usernick.getText().length() > 0;
            boolean sign2 = et_usertel.getText().length() > 0;
            boolean sign3 = et_password.getText().length() > 0;

            if (sign1 & sign2 & sign3) {
                btn_register.setTextColor(0xFFFFFFFF);
                btn_register.setEnabled(true);
            }
            // 在layout文件中，对Button的text属性应预先设置默认值，否则刚打开程序的时候Button是无显示的
            else {
                btn_register.setTextColor(0xFFD0EFC6);
                btn_register.setEnabled(false);
            }
        }

    }

    private boolean checkRegisterContentValid() {

        // 帐号检查tel
        String account = et_usertel.getText().toString().trim();
        if (account.length() <= 0 || account.length() > 20) {
            Toast.makeText(this, R.string.register_account_tip, Toast.LENGTH_SHORT).show();
            return false;
        }

        // 昵称检查
        String nick = et_usernick.getText().toString().trim();
        if (nick.length() <= 0 || nick.length() > 10) {
            Toast.makeText(this, R.string.register_nick_name_tip, Toast.LENGTH_SHORT).show();
            return false;
        }

        // 密码检查
        String password = et_password.getText().toString().trim();
        if (password.length() < 6 || password.length() > 20) {
            Toast.makeText(this, R.string.register_password_tip, Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

}
