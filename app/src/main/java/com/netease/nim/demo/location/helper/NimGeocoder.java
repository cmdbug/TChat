package com.netease.nim.demo.location.helper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Handler;
import android.text.TextUtils;

import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeAddress;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.netease.nim.demo.location.model.NimLocation;
import com.netease.nim.demo.common.infra.DefaultTaskManager;
import com.netease.nim.demo.common.infra.DefaultTaskWorker;
import com.netease.nim.demo.common.infra.ManagedTask;
import com.netease.nim.demo.common.infra.TaskExecutor;
import com.netease.nim.demo.common.infra.TaskManager;
import com.netease.nim.uikit.common.util.log.LogUtil;

public class NimGeocoder {

    public interface NimGeocoderListener {
        public void onGeoCoderResult(NimLocation location);
    }

    private static final String TAG = "YixinGeoCoder";

    private Context context;

    private NimGeocoderListener listener;

    private List<NimLocation> queryList;

    private Set<NimLocation> querying;

    private List<GeocoderProvider> providers;

    private TaskManager taskManager;

    private Handler callerHandler;

    public NimGeocoder(Context context, NimGeocoderListener listener) {
        this.context = context;
        this.listener = listener;

        queryList = new LinkedList<NimLocation>();
        querying = new HashSet<NimLocation>();
        querying = Collections.synchronizedSet(querying);
        callerHandler = new Handler();

        setupProviders();
    }

    public void queryAddress(double latitude, double longitude) {
    	queryAddress(latitude, longitude, false);
    }
    
    /**
     * 是否来次定位坐标（用于缓存）
     * @param latitude
     * @param longitude
     * @param fromLocation
     */
    public void queryAddress(double latitude, double longitude, boolean fromLocation) {
    	NimLocation location = new NimLocation(latitude, longitude);
        location.setFromLocation(fromLocation);
        queryList.add(location);
        query();
    }

    public void queryAddressNow(double latitude, double longitude) {
    	queryAddressNow(latitude, longitude, false);
    }
    
    /**
     * @param latitude
     * @param longitude
     * @param fromLocation 是否来次定位坐标（用于缓存）
     */
    public void queryAddressNow(double latitude, double longitude, boolean fromLocation) {
        // remove all
        queryList.clear();
        querying.clear();
        if (taskManager != null) {
            taskManager.cancelAll();
        }

        queryAddress(latitude, longitude, fromLocation);
    }

    public void destroy() {
        queryList.clear();
        querying.clear();
        if (taskManager != null) {
            taskManager.shutdown();
        }

        listener = null;
    }

    private void query() {
        if (queryList.size() == 0) {
            return;
        }

        if (taskManager == null) {
            TaskExecutor.Config config = new TaskExecutor.Config(0, 3, 30 * 1000, true);
            taskManager = new DefaultTaskManager(new DefaultTaskWorker(TAG, config));
        }

        final NimLocation location = queryList.remove(0);
        querying.add(location);

        taskManager.schedule(new ManagedTask() {
            @Override
            protected Object[] execute(Object[] params) {
                for (GeocoderProvider provider : providers) {
                    if (!querying.contains(location)) {
                        break;
                    }
                    if (provider.queryAddress(location)) {
                        break;
                    }
                }
                notifyListener(location);
                return null;
            }
        });
    }

    private void notifyListener(final NimLocation location) {
        callerHandler.post(new Runnable() {
            @Override
            public void run() {
                if (listener != null && querying.contains(location)) {
                    listener.onGeoCoderResult(location);
                    querying.remove(location);
                }

                // again to see if there are pending queries
                query();
            }
        });
    }

    private void setupProviders() {
        providers = new ArrayList<GeocoderProvider>();
        providers.add(new AMapGeocoder());
        providers.add(new GoogleGeocoder());
    }

    private interface GeocoderProvider {
        public boolean queryAddress(NimLocation location);
    }

    private class GoogleGeocoder implements GeocoderProvider {

        private Geocoder geocoder = new Geocoder(context, Locale.getDefault());

        @Override
        public boolean queryAddress(NimLocation location) {
            boolean ret = false;
            try {
                List<Address> list = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                if (list != null && list.size() > 0) {
                    Address address = list.get(0);
                    if (address != null) {
                        locationFromGoogleAddress(location, address);
                        ret = true;
                    }
                }
            } catch (IOException e) {
                LogUtil.e(TAG, e + "");
            }
            return ret;
        }
    }

    private class AMapGeocoder implements GeocoderProvider {

        private GeocodeSearch search = new GeocodeSearch(context);

        @Override
        public boolean queryAddress(NimLocation location) {
            boolean ret = false;
            LatLonPoint point = new LatLonPoint(location.getLatitude(), location.getLongitude());
            RegeocodeQuery query = new RegeocodeQuery(point, 100, GeocodeSearch.AMAP);
            try {
                RegeocodeAddress address = search.getFromLocation(query);
                if (address != null && !TextUtils.isEmpty(address.getFormatAddress())) {
                    locationFromAmapAddress(location, address);
                    ret = true;
                }
            } catch (AMapException e) {
                e.printStackTrace();
            }
            return ret;
        }
    }

    private static void locationFromGoogleAddress(NimLocation location, Address address) {
        location.setStatus(NimLocation.Status.HAS_LOCATION_ADDRESS);
        location.setCountryName(address.getCountryName());
        location.setCountryCode(address.getCountryCode());
        location.setProvinceName(address.getAdminArea());
        location.setCityName(address.getLocality());
        location.setDistrictName(address.getSubLocality());
        location.setStreetName(address.getThoroughfare());
        location.setFeatureName(address.getFeatureName());
    }

    private static void locationFromAmapAddress(NimLocation location, RegeocodeAddress address) {
        location.setStatus(NimLocation.Status.HAS_LOCATION_ADDRESS);
        location.setAddrStr(address.getFormatAddress());
        location.setProvinceName(address.getProvince());
        location.setCityName(address.getCity());
        location.setDistrictName(address.getDistrict());

        StringBuilder street = new StringBuilder();
        if (!TextUtils.isEmpty(address.getTownship())) {
            street.append(address.getTownship());
        }
        if (address.getStreetNumber() != null) {
            street.append(address.getStreetNumber().getStreet());
            if (!TextUtils.isEmpty(address.getStreetNumber().getNumber())) {
                street.append(address.getStreetNumber().getNumber());
                street.append("号");

            }
        }
        location.setStreetName(street.toString());
    }
}
