package com.netease.nim.demo.location.helper;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.widget.Toast;

import com.netease.nim.demo.R;
import com.netease.nim.demo.location.model.NimLocation;
import com.netease.nim.uikit.common.util.log.LogUtil;

public class MapHelper {
	private static final String Autonavi_Map = "com.autonavi.minimap";
	
	private static List<PackageInfo> initComponentInfo(Context context) {
		List<String> maps = new ArrayList<String>();
		maps.add(Autonavi_Map);
		return getComponentInfo(context, maps);
	}
	
	private static List<PackageInfo> getComponentInfo(Context context,
			List<String> maps) {
		PackageManager pm = context.getPackageManager();
		List<PackageInfo> infos = pm.getInstalledPackages(0);
		List<PackageInfo> available = new ArrayList<PackageInfo>();
		if ((infos != null) && (infos.size() > 0))
			for (PackageInfo info : infos) {
				String packName = info.packageName;
				if (!TextUtils.isEmpty(packName) && maps.contains(packName)) {
					if (packName.equals(Autonavi_Map)) {
						if (info.versionCode >= 161)
							available.add(info);
					} else {
						available.add(info);
					}
				}
			}
		return available;
	}
	
	public static void navigate(Context context, PackageInfo info, NimLocation origin, NimLocation des) {
		Intent intent = null;
		if (info == null) {
			intent = intentForAmap(origin, des);
		}
		if (intent != null) {
			try {
				context.startActivity(intent);
			} catch (Exception e) {
				LogUtil.e("mapHelper", "navigate error");
				Toast.makeText(context, R.string.location_open_map_error, Toast.LENGTH_LONG).show();
			}
		}
	}
	
	private static Intent intentForAmap(NimLocation origin, NimLocation des) {
		Intent intentForAmap;
		Object[] arrayOfObject = new Object[4];
		arrayOfObject[0] = Double.valueOf(origin.getLatitude());
		arrayOfObject[1] = Double.valueOf(origin.getLongitude());
		arrayOfObject[2] = Double.valueOf(des.getLatitude());
		arrayOfObject[3] = Double.valueOf(des.getLongitude());
		String str = String.format("androidamap://route?sourceApplication=yixin&slat=%f&slon=%f&sname=起点&dlat=%f&dlon=%f&dname=终点&dev=0&m=0&t=0&showType=1", arrayOfObject);
		Intent intent;
		try {
			intent = Intent.parseUri(str, 0);
			intent.setPackage(Autonavi_Map);
			intentForAmap = intent;
		} catch (URISyntaxException e) {
			e.printStackTrace();
			intentForAmap = null;
		}
		return intentForAmap;
	}
	
	public static List<PackageInfo> getAvailableMaps(Context context) {
		return initComponentInfo(context);
	}
	
}
