package com.netease.nim.demo.wzteng.qrcode;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.sumimakito.awesomeqr.AwesomeQRCode;
import com.netease.nim.demo.DemoCache;
import com.netease.nim.demo.R;
import com.netease.nim.uikit.cache.NimUserInfoCache;
import com.netease.nim.uikit.common.ui.imageview.HeadImageView;
import com.netease.nimlib.sdk.uinfo.constant.GenderEnum;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

/**
 * Created by WZTENG on 2017/05/16 0010.
 */

public class QrCodeDialog extends Dialog {

    public QrCodeDialog(Context context) {
        super(context);
    }

    public QrCodeDialog(Context context, int theme) {
        super(context, theme);
    }

    public static class Builder {
        private Context context;
        private HeadImageView ivAvatar;
        private TextView tvName;
        private ImageView ivClose;
        private ImageView ivSex;
        private TextView tvAbout;
        private ImageView ivQrCodeImg;

        public Builder(Context context) {
            this.context = context;
        }

        public QrCodeDialog create() {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            final QrCodeDialog dialog = new QrCodeDialog(context, R.style.dialog_qrcode);
            View layout = inflater.inflate(R.layout.wzt_qrcode_dialog, null);
            dialog.addContentView(layout, new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            ivClose = (ImageView) layout.findViewById(R.id.iv_qrcode_close);
            ivClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            ivAvatar = (HeadImageView) layout.findViewById(R.id.iv_qrcode_avatar);
            ivAvatar.loadBuddyAvatar(DemoCache.getAccount());
            tvName = (TextView) layout.findViewById(R.id.tv_qrcode_name);
            tvName.setText(DemoCache.getAccount());
            ivSex = (ImageView) layout.findViewById(R.id.iv_qrcode_sex);
            NimUserInfo userInfo;
            userInfo = NimUserInfoCache.getInstance().getUserInfo(DemoCache.getAccount());
            if (userInfo != null && userInfo.getGenderEnum() != null) {
                if (userInfo.getGenderEnum() == GenderEnum.MALE) {
                    ivSex.setImageResource(R.drawable.ic_sex_male);
                } else if (userInfo.getGenderEnum() == GenderEnum.FEMALE) {
                    ivSex.setImageResource(R.drawable.ic_sex_female);
                } else {
                    ivSex.setVisibility(View.GONE);
                }
            }

            tvAbout = (TextView) layout.findViewById(R.id.tv_qrcode_about);
            ivQrCodeImg = (ImageView) layout.findViewById(R.id.iv_qrcode_img);
            Bitmap qrBitmap = AwesomeQRCode.create(
                    "https://github.com/WZTENG/TChat",
                    1600,
                    0,
                    0.5f,
                    Color.BLACK,
                    Color.WHITE,
                    BitmapFactory.decodeResource(context.getResources(), R.drawable.head_icon_1),
                    false,
                    true,
                    false,
                    128,
                    false,
                    null,
                    10,
                    20,
                    0.2f);
            ivQrCodeImg.setImageBitmap(qrBitmap);

            dialog.setContentView(layout);
            dialog.setCanceledOnTouchOutside(true);
            return dialog;
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
