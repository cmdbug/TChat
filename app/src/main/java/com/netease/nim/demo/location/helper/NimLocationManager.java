package com.netease.nim.demo.location.helper;


import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.netease.nim.demo.location.model.NimLocation;
import com.netease.nim.demo.common.infra.TaskExecutor;
import com.netease.nim.uikit.common.util.log.LogUtil;

public class NimLocationManager implements AMapLocationListener {
	private static final String TAG = "NimLocationManager";
	private Context mContext;
	
	/** msg handler */
	private static final int MSG_LOCATION_WITH_ADDRESS_OK = 1;
	private static final int MSG_LOCATION_POINT_OK = 2;
    private static final int MSG_LOCATION_ERROR = 3;
	
	private NimLocationListener mListener;
	
	Criteria criteria; // onResume 重新激活 if mProvider == null
	
	/** AMap location */
//    private LocationManagerProxy aMapLocationManager;
    private AMapLocationClient mLocationClient;
    private AMapLocationClientOption mLocationOption;

	 /** google api */
//    private LocationManager mSysLocationMgr = null;
    private String mProvider;
    private Geocoder mGeocoder;
    
    private MsgHandler mMsgHandler = new MsgHandler();
    private TaskExecutor executor = new TaskExecutor(TAG, TaskExecutor.defaultConfig, true);
	
	public NimLocationManager(Context context, NimLocationListener oneShotListener) {
		mContext = context;
        mGeocoder = new Geocoder(mContext, Locale.getDefault());
		mListener = oneShotListener;
	}
	
	public static boolean isLocationEnable(Context context) {
		LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		Criteria cri = new Criteria();
		cri.setAccuracy(Criteria.ACCURACY_COARSE);
		cri.setAltitudeRequired(false);
		cri.setBearingRequired(false);
		cri.setCostAllowed(false);
		String bestProvider = locationManager.getBestProvider(cri, true);
		return !TextUtils.isEmpty(bestProvider);
		
	}

    @Override
    public void onLocationChanged(final AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    getAMapLocationAddress(aMapLocation);
                }
            });
        }else {
            LogUtil.i(TAG, "receive system location failed");
            // 真的拿不到了
            onLocation(null, MSG_LOCATION_ERROR);
        }
    }

//    @Override
//    public void onLocationChanged(Location location) {
//
//    }
//
//    @Override
//    public void onStatusChanged(String provider, int status, Bundle extras) {
//
//    }
//
//    @Override
//    public void onProviderEnabled(String provider) {
//
//    }
//
//    @Override
//    public void onProviderDisabled(String provider) {
//
//    }

    public interface NimLocationListener {
		public void onLocationChanged(NimLocation location);
	}
	
	public Location getLastKnownLocation() {
        try {
            if(criteria == null) {
                criteria = new Criteria();
                criteria.setAccuracy(Criteria.ACCURACY_COARSE);
                criteria.setAltitudeRequired(false);
                criteria.setBearingRequired(false);
                criteria.setCostAllowed(false);
            }
            if(mProvider == null) {
//                mProvider = aMapLocationManager.getBestProvider(criteria, true);
                mProvider = null;
            }
//            return aMapLocationManager.getLastKnownLocation(mProvider);
            return null;
		} catch (Exception e) {
			LogUtil.i(TAG, "get lastknown location failed: " + e.toString());
		}
        return null;
    }
	
	private void onLocation(NimLocation location, int what) {
        Message msg = mMsgHandler.obtainMessage();
        msg.what = what;
        msg.obj = location;
        mMsgHandler.sendMessage(msg);
    }
	
	private class MsgHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_LOCATION_WITH_ADDRESS_OK:
				if (mListener != null && msg.obj != null) {
                    if(msg.obj != null) {
                        NimLocation loc = (NimLocation) msg.obj;
                        loc.setStatus(NimLocation.Status.HAS_LOCATION_ADDRESS);
                        
                        // 记录地址信息
                        loc.setFromLocation(true);
                        
                        mListener.onLocationChanged(loc);
                    } else {
                    	NimLocation loc = new NimLocation();
                        mListener.onLocationChanged(loc);
                    }
                }
				break;
			case MSG_LOCATION_POINT_OK:
				if (mListener != null) {
                    if(msg.obj != null) {
                    	NimLocation loc = (NimLocation) msg.obj;
                        loc.setStatus(NimLocation.Status.HAS_LOCATION);
                        mListener.onLocationChanged(loc);
                    } else {
                    	NimLocation loc = new NimLocation();
                        mListener.onLocationChanged(loc);
                    }
                }
				break;
            case MSG_LOCATION_ERROR:
                if(mListener != null) {
                	NimLocation loc = new NimLocation();
                    mListener.onLocationChanged(loc);
                }
                break;
			default:
				break;
			}
			super.handleMessage(msg);
		}

	}
	
	private void getAMapLocationAddress(final AMapLocation loc) {
        if (TextUtils.isEmpty(loc.getAddress())) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    getLocationAddress(new NimLocation(loc, NimLocation.AMap_Location));
                }
            });
        } else {
            NimLocation location = new NimLocation(loc, NimLocation.AMap_Location);
            location.setAddrStr(loc.getAddress());
            location.setProvinceName(loc.getProvince());
            location.setCityName(loc.getCity());
            location.setCityCode(loc.getCityCode());
            location.setDistrictName(loc.getDistrict());
            location.setStreetName(loc.getStreet());
            location.setStreetCode(loc.getAdCode());

            onLocation(location, MSG_LOCATION_WITH_ADDRESS_OK);
        }
    }
	
	private boolean getLocationAddress(NimLocation location) {
        List<Address> list;
        boolean ret = false;
        try {
            list = mGeocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 2);
            if (list != null && list.size() > 0) {
                Address address = list.get(0);
                if (address != null) {
                    location.setCountryName(address.getCountryName());
                    location.setCountryCode(address.getCountryCode());
                    location.setProvinceName(address.getAdminArea());
                    location.setCityName(address.getLocality());
                    location.setDistrictName(address.getSubLocality());
                    location.setStreetName(address.getThoroughfare());
                    location.setFeatureName(address.getFeatureName());
                }
                ret = true;
            }
        } catch (IOException e) {
            LogUtil.e(TAG, e + "");
        }

        int what = ret ? MSG_LOCATION_WITH_ADDRESS_OK : MSG_LOCATION_POINT_OK;
        onLocation(location, what);

        return ret;
    }
	
	public void deactive() {
		stopAMapLocation();
	}
	
	private void stopAMapLocation() {
//        if (aMapLocationManager != null) {
//            aMapLocationManager.removeUpdates(this);
//            aMapLocationManager.destory();
//        }
//        aMapLocationManager = null;

        if (mLocationClient != null) {
            mLocationClient.stopLocation();
            mLocationClient = null;
        }
	}
	
	public void activate() {
		requestAmapLocation();
	}
	
	private void requestAmapLocation() {
//        if (aMapLocationManager == null) {
//            aMapLocationManager = LocationManagerProxy.getInstance(mContext);
//            aMapLocationManager.setGpsEnable(false);
//            aMapLocationManager.requestLocationData(
//                    LocationProviderProxy.AMapNetwork, 30 * 1000, 10, this);
//        }

        if (mLocationClient == null) {
            mLocationClient = new AMapLocationClient(mContext);
        }
        mLocationClient.setLocationListener(this);
        mLocationOption = new AMapLocationClientOption();
        //设置定位场景，目前支持三种场景（签到、出行、运动，默认无场景）
//        mLocationOption.setLocationPurpose(AMapLocationClientOption.AMapLocationPurpose.SignIn);

        //设置定位模式为AMapLocationMode
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //获取一次定位结果：
        //该方法默认为false。
        mLocationOption.setOnceLocation(true);

        //获取最近3s内精度最高的一次定位结果：
        //设置setOnceLocationLatest(boolean b)接口为true，启动定位时SDK会返回最近3s内精度最高的一次定位结果。如果设置其为true，setOnceLocation(boolean b)接口也会被设置为true，反之不会，默认为false。
        mLocationOption.setOnceLocationLatest(true);
        //设置定位间隔,单位毫秒,默认为2000ms，最低1000ms。
        mLocationOption.setInterval(5000);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //单位是毫秒，默认30000毫秒，建议超时时间不要低于8000毫秒。
        mLocationOption.setHttpTimeOut(30000);
        //缓存机制
        mLocationOption.setLocationCacheEnable(true);
        //启动定位
        if (null != mLocationClient) {
            mLocationClient.setLocationOption(mLocationOption);
            //设置场景模式后最好调用一次stop，再调用start以保证场景模式生效
            mLocationClient.stopLocation();
            mLocationClient.startLocation();
        }
    }
}
