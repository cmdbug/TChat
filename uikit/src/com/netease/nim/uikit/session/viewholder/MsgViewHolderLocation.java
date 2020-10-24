package com.netease.nim.uikit.session.viewholder;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.TextView;

import com.netease.nim.uikit.NimUIKit;
import com.netease.nim.uikit.R;
import com.netease.nim.uikit.common.ui.imageview.MsgThumbImageView;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseMultiItemFetchLoadAdapter;
import com.netease.nim.uikit.common.util.media.ImageUtil;
import com.netease.nim.uikit.common.util.sys.ScreenUtil;
import com.netease.nim.uikit.wzteng.database.greendao.LocationSrcDao;
import com.netease.nim.uikit.wzteng.database.manager.GetDaoMaster;
import com.netease.nim.uikit.wzteng.database.model.LocationSrc;
import com.netease.nim.uikit.wzteng.locationscreenshot.AMapScreenShotUtils;
import com.netease.nimlib.sdk.msg.attachment.LocationAttachment;

import java.util.List;

/**
 * Created by zhoujianghua on 2015/8/7.
 */
public class MsgViewHolderLocation extends MsgViewHolderBase {

    public MsgViewHolderLocation(BaseMultiItemFetchLoadAdapter adapter) {
        super(adapter);
    }

    public MsgThumbImageView mapView;

    public TextView addressText;

    @Override
    protected int getContentResId() {
        return R.layout.nim_message_item_location;
    }

    @Override
    protected void inflateContentView() {
        mapView = (MsgThumbImageView) view.findViewById(R.id.message_item_location_image);
        addressText = (TextView) view.findViewById(R.id.message_item_location_address);
    }

    @Override
    protected void bindContentView() {
        final LocationAttachment location = (LocationAttachment) message.getAttachment();
        addressText.setText(location.getAddress());

        int[] bound = ImageUtil.getBoundWithLength(getLocationDefEdge(), R.drawable.nim_location_bk, true);
        int width = bound[0];
        int height = bound[1];

        setLayoutParams(width, height, mapView);
        setLayoutParams(width, (int) (0.30 * height), addressText);

        List<LocationSrc> list = GetDaoMaster.getDaoSession(context).getLocationSrcDao()
                .queryBuilder()
                .where(LocationSrcDao.Properties.AddrTxt.eq(location.getAddress()))
                .build()
                .list();
        Bitmap locationBmp = null;
        if (list.size() > 0) {
            locationBmp = AMapScreenShotUtils.getBitmapFromData(context, list.get(0).getImagePath() + ".png");
            float p = height / (float) width;
            int lBmpWidth = locationBmp.getWidth();
            int lBmpHeight = locationBmp.getHeight();
            int tBmpHeight = (int) (lBmpWidth * p + 0.5);

            try {
                locationBmp = Bitmap.createBitmap(locationBmp,
                        0,
                        (lBmpHeight - tBmpHeight) / 2,
                        lBmpWidth,
                        tBmpHeight);
            } catch (Exception e) {
                e.printStackTrace();
                locationBmp = null;
            }
        }

//        mapView.loadAsResource(R.drawable.nim_location_bk, R.drawable.nim_message_item_round_bg);
        if (locationBmp == null) {
            mapView.loadAsResource(R.drawable.nim_location_bk, R.drawable.wzt_message_location_item_map_bg);//teng
        } else {
            mapView.loadAsResource(locationBmp, R.drawable.wzt_message_location_item_map_bg);//teng
        }
//        mapView.loadAsResource(R.drawable.nim_location_bk, R.drawable.wzt_message_location_item_map_bg);
//        mapView.loadAsResource(locationBmp, R.drawable.wzt_message_location_item_map_bg);//teng
    }

    //teng
    @Override
    protected int leftBackground() {
        return R.drawable.chat_from_location_bg_normal;
    }

    //teng
    @Override
    protected int rightBackground() {
        return R.drawable.chat_to_location_bg_normal;
    }

    @Override
    protected void onItemClick() {
        if (NimUIKit.getLocationProvider() != null) {
            LocationAttachment location = (LocationAttachment) message.getAttachment();
            NimUIKit.getLocationProvider().openMap(context, location.getLongitude(), location.getLatitude(), location.getAddress());
        }
    }

    public static int getLocationDefEdge() {
        return (int) (0.5 * ScreenUtil.screenWidth);
    }
}
