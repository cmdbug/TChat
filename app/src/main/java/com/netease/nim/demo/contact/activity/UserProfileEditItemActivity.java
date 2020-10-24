package com.netease.nim.demo.contact.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.netease.nim.demo.R;
import com.netease.nim.demo.contact.constant.UserConstant;
import com.netease.nim.demo.contact.helper.UserUpdateHelper;
import com.netease.nim.uikit.cache.FriendDataCache;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.netease.nim.uikit.common.ui.widget.ClearableEditTextWithIcon;
import com.netease.nim.uikit.common.util.sys.NetworkUtil;
import com.netease.nim.uikit.common.util.sys.TimeUtil;
import com.netease.nim.uikit.model.ToolBarOptions;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.friend.FriendService;
import com.netease.nimlib.sdk.friend.constant.FriendFieldEnum;
import com.netease.nimlib.sdk.friend.model.Friend;
import com.netease.nimlib.sdk.uinfo.constant.GenderEnum;
import com.netease.nimlib.sdk.uinfo.constant.UserInfoFieldEnum;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hzxuwen on 2015/9/14.
 */
public class UserProfileEditItemActivity extends UI implements View.OnClickListener {

    private static final String EXTRA_KEY = "EXTRA_KEY";
    private static final String EXTRA_DATA = "EXTRA_DATA";
    public static final int REQUEST_CODE = 1000;

    // data
    private int key;
    private String data;
    private int birthYear = 1990;
    private int birthMonth = 10;
    private int birthDay = 10;
    private Map<Integer, UserInfoFieldEnum> fieldMap;

    // VIEW
    private ClearableEditTextWithIcon editText;

    // gender layout
    private RelativeLayout maleLayout;
    private RelativeLayout femaleLayout;
    private RelativeLayout otherLayout;
    private ImageView maleCheck;
    private ImageView femaleCheck;
    private ImageView otherCheck;

    // birth layout
    private RelativeLayout birthPickerLayout;
    private TextView birthText;
    private int gender;

    public static final void startActivity(Context context, int key, String data) {
        Intent intent = new Intent();
        intent.setClass(context, UserProfileEditItemActivity.class);
        intent.putExtra(EXTRA_KEY, key);
        intent.putExtra(EXTRA_DATA, data);
        ((Activity) context).startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parseIntent();
        if (key == UserConstant.KEY_NICKNAME || key == UserConstant.KEY_PHONE || key == UserConstant.KEY_EMAIL
                || key == UserConstant.KEY_SIGNATURE || key == UserConstant.KEY_ALIAS) {
            setContentView(R.layout.user_profile_edittext_layout);
            findEditText();
        } else if (key == UserConstant.KEY_GENDER) {
            setContentView(R.layout.user_profile_gender_layout);
            findGenderViews();
        } else if (key == UserConstant.KEY_BIRTH) {
            setContentView(R.layout.user_profile_birth_layout);
            findBirthViews();
        }
        ToolBarOptions options = new ToolBarOptions();
        setToolBar(R.id.toolbar, options);
        initActionbar();
        setTitles();
    }

    @Override
    public void onBackPressed() {
        showKeyboard(false);
        super.onBackPressed();
    }

    private void parseIntent() {
        key = getIntent().getIntExtra(EXTRA_KEY, -1);
        data = getIntent().getStringExtra(EXTRA_DATA);
    }

    private void setTitles() {
        switch (key) {
            case UserConstant.KEY_NICKNAME:
                setTitle(R.string.nickname);
                break;
            case UserConstant.KEY_PHONE:
                setTitle(R.string.phone_number);
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                break;
            case UserConstant.KEY_EMAIL:
                setTitle(R.string.email);
                break;
            case UserConstant.KEY_SIGNATURE:
                setTitle(R.string.signature);
                break;
            case UserConstant.KEY_GENDER:
                setTitle(R.string.gender);
                break;
            case UserConstant.KEY_BIRTH:
                setTitle(R.string.birthday);
                break;
            case UserConstant.KEY_ALIAS:
                setTitle(R.string.alias);
                break;
        }
    }

    private void findEditText() {
        editText = findView(R.id.edittext);
        if (key == UserConstant.KEY_NICKNAME) {
            editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
        } else if (key == UserConstant.KEY_PHONE) {
            editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(13)});
        } else if (key == UserConstant.KEY_EMAIL || key == UserConstant.KEY_SIGNATURE) {
            editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(30)});
        } else if (key == UserConstant.KEY_ALIAS) {
            editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(16)});
        }
        if (key == UserConstant.KEY_ALIAS) {
            Friend friend = FriendDataCache.getInstance().getFriendByAccount(data);
            if (friend != null && !TextUtils.isEmpty(friend.getAlias())) {
                editText.setText(friend.getAlias());
            } else {
                editText.setHint("请输入备注名...");
            }
        } else {
            editText.setText(data);
        }
        editText.setDeleteImage(R.drawable.nim_grey_delete_icon);
    }

    private void initActionbar() {
        TextView toolbarView = findView(R.id.action_bar_right_clickable_textview);
        toolbarView.setText(R.string.save);
        toolbarView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!NetworkUtil.isNetAvailable(UserProfileEditItemActivity.this)) {
                    Toast.makeText(UserProfileEditItemActivity.this, R.string.network_is_not_available, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (key == UserConstant.KEY_NICKNAME && TextUtils.isEmpty(editText.getText().toString().trim())) {
                    Toast.makeText(UserProfileEditItemActivity.this, R.string.nickname_empty, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (key == UserConstant.KEY_BIRTH) {
                    update(birthText.getText().toString());
                } else if (key == UserConstant.KEY_GENDER) {
                    update(Integer.valueOf(gender));
                } else {
                    update(editText.getText().toString().trim());
                }
            }
        });
    }

    private void findGenderViews() {
        maleLayout = findView(R.id.male_layout);
        femaleLayout = findView(R.id.female_layout);
        otherLayout = findView(R.id.other_layout);

        maleCheck = findView(R.id.male_check);
        femaleCheck = findView(R.id.female_check);
        otherCheck = findView(R.id.other_check);

        maleLayout.setOnClickListener(this);
        femaleLayout.setOnClickListener(this);
        otherLayout.setOnClickListener(this);

        initGender();
    }

    private void initGender() {
        gender = Integer.parseInt(data);
        genderCheck(gender);
    }

    private void findBirthViews() {
        birthPickerLayout = findView(R.id.birth_picker_layout);
        birthText = findView(R.id.birth_value);

        birthPickerLayout.setOnClickListener(this);
        birthText.setText(data);

        if (!TextUtils.isEmpty(data)) {
            Date date = TimeUtil.getDateFromFormatString(data);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            if (date != null) {
                birthYear = cal.get(Calendar.YEAR);
                birthMonth = cal.get(Calendar.MONTH);
                birthDay = cal.get(Calendar.DATE);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.male_layout:
                gender = GenderEnum.MALE.getValue();
                genderCheck(gender);
                break;
            case R.id.female_layout:
                gender = GenderEnum.FEMALE.getValue();
                genderCheck(gender);
                break;
            case R.id.other_layout:
                gender = GenderEnum.UNKNOWN.getValue();
                genderCheck(gender);
                break;
            case R.id.birth_picker_layout:
                openTimePicker();
                break;
        }
    }

    private void genderCheck(int selected) {
        otherCheck.setBackgroundResource(selected == GenderEnum.UNKNOWN.getValue() ? R.drawable.nim_contact_checkbox_checked_green : R.drawable.nim_checkbox_not_checked);
        maleCheck.setBackgroundResource(selected == GenderEnum.MALE.getValue() ? R.drawable.nim_contact_checkbox_checked_green : R.drawable.nim_checkbox_not_checked);
        femaleCheck.setBackgroundResource(selected == GenderEnum.FEMALE.getValue() ? R.drawable.nim_contact_checkbox_checked_green : R.drawable.nim_checkbox_not_checked);
    }

    private void openTimePicker() {
        MyDatePickerDialog datePickerDialog = new MyDatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                birthYear = year;
                birthMonth = monthOfYear;
                birthDay = dayOfMonth;
                updateDate();

            }
        }, birthYear, birthMonth, birthDay);
        datePickerDialog.show();
    }

    private void updateDate() {
        birthText.setText(TimeUtil.getFormatDatetime(birthYear, birthMonth, birthDay));
    }

    private void update(Serializable content) {
        RequestCallbackWrapper callback = new RequestCallbackWrapper() {
            @Override
            public void onResult(int code, Object result, Throwable exception) {
                DialogMaker.dismissProgressDialog();
                if (code == ResponseCode.RES_SUCCESS) {
                    onUpdateCompleted();
                } else if (code == ResponseCode.RES_ETIMEOUT) {
                    Toast.makeText(UserProfileEditItemActivity.this, R.string.user_info_update_failed, Toast.LENGTH_SHORT).show();
                }
            }
        };
        if (key == UserConstant.KEY_ALIAS) {
            DialogMaker.showProgressDialog(this, null, true);
            Map<FriendFieldEnum, Object> map = new HashMap<>();
            map.put(FriendFieldEnum.ALIAS, content);
            NIMClient.getService(FriendService.class).updateFriendFields(data, map).setCallback(callback);
        } else {
            if (fieldMap == null) {
                fieldMap = new HashMap<>();
                fieldMap.put(UserConstant.KEY_NICKNAME, UserInfoFieldEnum.Name);
                fieldMap.put(UserConstant.KEY_PHONE, UserInfoFieldEnum.MOBILE);
                fieldMap.put(UserConstant.KEY_SIGNATURE, UserInfoFieldEnum.SIGNATURE);
                fieldMap.put(UserConstant.KEY_EMAIL, UserInfoFieldEnum.EMAIL);
                fieldMap.put(UserConstant.KEY_BIRTH, UserInfoFieldEnum.BIRTHDAY);
                fieldMap.put(UserConstant.KEY_GENDER, UserInfoFieldEnum.GENDER);
            }
            DialogMaker.showProgressDialog(this, null, true);
            UserUpdateHelper.update(fieldMap.get(key), content, callback);
        }
    }

    private void onUpdateCompleted() {
        showKeyboard(false);
        Toast.makeText(UserProfileEditItemActivity.this, R.string.user_info_update_success, Toast.LENGTH_SHORT).show();
        finish();
    }

    private class MyDatePickerDialog extends DatePickerDialog {
        private int maxYear = 2015;
        private int minYear = 1900;
        private int currYear;
        private int currMonthOfYear;
        private int currDayOfMonth;

        public MyDatePickerDialog(Context context, OnDateSetListener callBack, int year, int monthOfYear, int dayOfMonth) {
            super(context, callBack, year, monthOfYear, dayOfMonth);
            currYear = year;
            currMonthOfYear = monthOfYear;
            currDayOfMonth = dayOfMonth;
        }

        @Override
        public void onDateChanged(DatePicker view, int year, int month, int day) {
            if(year >= minYear && year <= maxYear){
                currYear = year;
                currMonthOfYear = month;
                currDayOfMonth = day;
            } else {
                if (currYear > maxYear) {
                    currYear = maxYear;
                } else if (currYear < minYear) {
                    currYear = minYear;
                }
                updateDate(currYear, currMonthOfYear, currDayOfMonth);
            }
        }

        public void setMaxYear(int year){
            maxYear = year;
        }

        public void setMinYear(int year){
            minYear = year;
        }

        public void setTitle(CharSequence title) {
            super.setTitle("生 日");
        }
    }
}
