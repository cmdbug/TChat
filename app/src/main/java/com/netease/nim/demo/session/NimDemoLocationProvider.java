package com.netease.nim.demo.session;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.netease.nim.demo.location.activity.LocationAmapActivity;
import com.netease.nim.demo.location.activity.LocationExtras;
import com.netease.nim.demo.location.activity.NavigationAmapActivity;
import com.netease.nim.demo.location.helper.NimLocationManager;
import com.netease.nim.uikit.LocationProvider;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialog;
import com.netease.nim.uikit.common.util.log.LogUtil;

/**
 * Created by zhoujianghua on 2015/8/11.
 */
public class NimDemoLocationProvider implements LocationProvider {
    @Override
    public void requestLocation(final Context context, Callback callback) {
        if (!NimLocationManager.isLocationEnable(context)) {
            final EasyAlertDialog alertDialog = new EasyAlertDialog(context);
            alertDialog.setMessage("位置服务未开启");
            alertDialog.addNegativeButton("取消", EasyAlertDialog.NO_TEXT_COLOR, EasyAlertDialog.NO_TEXT_SIZE,
                    new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            alertDialog.dismiss();
                        }
                    });
            alertDialog.addPositiveButton("设置", EasyAlertDialog.NO_TEXT_COLOR, EasyAlertDialog.NO_TEXT_SIZE,
                    new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            alertDialog.dismiss();
                            Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            try {
                                context.startActivity(intent);
                            } catch (Exception e) {
                                LogUtil.e("LOC", "start ACTION_LOCATION_SOURCE_SETTINGS error");
                            }

                        }
                    });
            alertDialog.show();
            return;
        }

        LocationAmapActivity.start(context, callback);
    }

    @Override
    public void openMap(Context context, double longitude, double latitude, String address) {
        Intent intent = new Intent(context, NavigationAmapActivity.class);
        intent.putExtra(LocationExtras.LONGITUDE, longitude);
        intent.putExtra(LocationExtras.LATITUDE, latitude);
        intent.putExtra(LocationExtras.ADDRESS, address);
        context.startActivity(intent);
    }
}
