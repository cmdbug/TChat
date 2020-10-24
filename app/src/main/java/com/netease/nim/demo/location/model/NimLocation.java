package com.netease.nim.demo.location.model;

import android.location.Location;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.amap.api.location.AMapLocation;

public class NimLocation {
	
	public static final String AMap_Location = "AMap_location";

	public static final String System_Location = "system_location";
	
	public enum Status {
		INVALID(0), HAS_LOCATION(1), HAS_LOCATION_ADDRESS(2);
		Status(int value) {
			_value = value;
		}
		int _value;
		
		public static Status getStatus(int value) {
			if(value == HAS_LOCATION_ADDRESS._value) {
				return HAS_LOCATION_ADDRESS;
			}
			if(value == HAS_LOCATION._value) {
				return HAS_LOCATION;
			}
			return INVALID;
		}
	}
	public static final String Just_Point = "just_point";

	private static final double DEF_VALUE = -1000.0D;
	
	private double latitude = DEF_VALUE;

	private double longitude = DEF_VALUE;
	
	private Object location;

	private String type = "";
	
	private Status status = Status.INVALID;
	
	private transient boolean fromLocation = false;
	
	private String addrStr;
	
	private long updateTime;
	
	private NimAddress nimAddress = new NimAddress();
	
	public NimLocation(Object location, String type) {
		this.location = location;
		this.type = type;
		this.status = Status.HAS_LOCATION;
	}

	public NimLocation(double latitude, double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.type = Just_Point;
		this.status = Status.HAS_LOCATION;
	}

	public NimLocation() {
		this.status = Status.INVALID;
	}
	
	public void setStatus(Status status) {
		this.status = status;
	}
	
	public boolean isFromLocation() {
		return fromLocation;
	}

	public void setFromLocation(boolean fromLocation) {
		this.fromLocation = fromLocation;
	}
	
	public void setAddrStr(String addrStr) {
		this.addrStr = addrStr;
	}

	public String getAddrStr() {
		return addrStr;
	}
	
	public void setProvinceName(String mProvinceName) {
		this.nimAddress.provinceName = mProvinceName;
	}

	public String getProvinceCode() {
		return nimAddress.provinceCode;
	}
	
	public String getCityName() {
		return nimAddress.cityName;
	}

	public void setCityName(String mCityName) {
		this.nimAddress.cityName = mCityName;
	}

	public String getCityCode() {
		return nimAddress.cityCode;
	}

	public void setCityCode(String mCityCode) {
		this.nimAddress.cityCode = mCityCode;
	}

	public String getDistrictName() {
		return nimAddress.districtName;
	}

	public void setDistrictName(String mDistrictName) {
		this.nimAddress.districtName = mDistrictName;
	}

	public String getDistrictCode() {
		return nimAddress.districtCode;
	}

	public void setDistrictCode(String mDistrictCode) {
		this.nimAddress.districtCode = mDistrictCode;
	}

	public String getStreetName() {
		return nimAddress.streetName;
	}

	public void setStreetName(String mStreetName) {
		this.nimAddress.streetName = mStreetName;
	}

	public String getStreetCode() {
		return nimAddress.streetCode;
	}

	public void setStreetCode(String mStreetCode) {
		this.nimAddress.streetCode = mStreetCode;
	}

	public String getFeatureName() {
		return nimAddress.featureName;
	}

	public void setFeatureName(String mFeatureName) {
		this.nimAddress.featureName = mFeatureName;
	}

	public String getCountryName() {
		return nimAddress.countryName;
	}

	public void setCountryName(String mCountryName) {
		this.nimAddress.countryName = mCountryName;
	}

	public String getCountryCode() {
		return nimAddress.countryCode;
	}

	public void setCountryCode(String mCountryCode) {
		this.nimAddress.countryCode = mCountryCode;
	}
	
	public boolean hasCoordinates() {
		if (status != Status.INVALID)
			return true;
		return false;
	}

	public boolean hasAddress() {
		if (status == Status.HAS_LOCATION_ADDRESS)
			return true;
		return false;
	}
	
	public String getFullAddr() {
        if (!TextUtils.isEmpty(addrStr)) {
            return addrStr;
        } else {
            StringBuilder addr = new StringBuilder();
            if (!TextUtils.isEmpty(nimAddress.countryName))
                addr.append(nimAddress.countryName);
            if (!TextUtils.isEmpty(nimAddress.provinceName))
                addr.append(nimAddress.provinceName);
            if (!TextUtils.isEmpty(nimAddress.cityName))
                addr.append(nimAddress.cityName);
            if (!TextUtils.isEmpty(nimAddress.districtName))
                addr.append(nimAddress.districtName);
            if (!TextUtils.isEmpty(nimAddress.streetName))
                addr.append(nimAddress.streetName);
            return addr.toString();
        }
	}
	
	public double getLatitude() {
		if (location != null) {
			if (type.equals(AMap_Location))
				latitude = ((AMapLocation) location).getLatitude();
			else if (type.equals(System_Location))
				latitude = ((Location) location).getLatitude();
		}

		return latitude;
	}

	public double getLongitude() {
		if (location != null) {
			if (type.equals(AMap_Location))
				longitude = ((AMapLocation) location).getLongitude();
			else if (type.equals(System_Location))
				longitude = ((Location) location).getLongitude();
		}
		return longitude;
	}
	
	public class NimAddress {

		public String countryName;
		public String countryCode;
		public String provinceName;
		public String provinceCode;
		public String cityName;
		public String cityCode;
		public String districtName;
		public String districtCode;
		public String streetName;
		public String streetCode;
		public String featureName;
		
		public void fromJSON(JSONObject jsonObj) {
			if(jsonObj == null) {
				return;
			}
			countryName = jsonObj.getString(TAG.TAG_COUNTRYNAME);
			countryCode = jsonObj.getString(TAG.TAG_COUNTRYCODE);
			provinceName = jsonObj.getString(TAG.TAG_PROVINCENAME);
			provinceCode = jsonObj.getString(TAG.TAG_PROVINCECODE);
			cityName = jsonObj.getString(TAG.TAG_CITYNAME);
			cityCode = jsonObj.getString(TAG.TAG_CITYCODE);
			districtName = jsonObj.getString(TAG.TAG_DISTRICTNAME);
			districtCode = jsonObj.getString(TAG.TAG_DISTRICTCODE);
			streetName = jsonObj.getString(TAG.TAG_STREETNAME);
			streetCode = jsonObj.getString(TAG.TAG_STREETCODE);
			featureName = jsonObj.getString(TAG.TAG_FEATURENAME);
		}
		
		public JSONObject toJSONObject() {
			JSONObject jsonObj = new JSONObject();
			jsonObj.put(TAG.TAG_COUNTRYNAME, countryName);
			jsonObj.put(TAG.TAG_COUNTRYCODE, countryCode);
			jsonObj.put(TAG.TAG_PROVINCENAME, provinceName);
			jsonObj.put(TAG.TAG_PROVINCECODE, provinceCode);
			jsonObj.put(TAG.TAG_CITYNAME, cityName);
			jsonObj.put(TAG.TAG_CITYCODE, cityCode);
			jsonObj.put(TAG.TAG_DISTRICTNAME, districtName);
			jsonObj.put(TAG.TAG_DISTRICTCODE, districtCode);
			jsonObj.put(TAG.TAG_STREETNAME, streetName);
			jsonObj.put(TAG.TAG_STREETCODE, streetCode);
			jsonObj.put(TAG.TAG_FEATURENAME, featureName);
			return jsonObj;
		}
	}
	
	private static class TAG {
		public static final String TAG_LATITUDE = "latitude";
		public static final String TAG_LONGITUDE = "longitude";
		public static final String TAG_TYPE = "type";
		public static final String TAG_STATUS = "status";
		public static final String TAG_NIMADDRESS = "nimaddress";
		public static final String TAG_ADDRSTR = "addrstr";
		public static final String TAG_UPDATETIME = "updatetime";
		
		public static final String TAG_COUNTRYNAME = "countryname";
		public static final String TAG_COUNTRYCODE = "countrycode";
		public static final String TAG_PROVINCENAME = "provincename";
		public static final String TAG_PROVINCECODE = "provincecode";
		public static final String TAG_CITYNAME = "cityname";
		public static final String TAG_CITYCODE = "citycode";
		public static final String TAG_DISTRICTNAME = "districtname";
		public static final String TAG_DISTRICTCODE = "districtcode";
		public static final String TAG_STREETNAME = "streetname";
		public static final String TAG_STREETCODE = "streetcode";
		public static final String TAG_FEATURENAME = "featurename";
	}
	
	public String toJSONString() {
		JSONObject jsonObj = new JSONObject();
		jsonObj.put(TAG.TAG_LATITUDE, getLatitude());
		jsonObj.put(TAG.TAG_LONGITUDE, getLongitude());
		jsonObj.put(TAG.TAG_TYPE, type);
		jsonObj.put(TAG.TAG_STATUS, status._value);
		jsonObj.put(TAG.TAG_ADDRSTR, addrStr);
		jsonObj.put(TAG.TAG_UPDATETIME, updateTime);
		jsonObj.put(TAG.TAG_NIMADDRESS, nimAddress.toJSONObject());
		return jsonObj.toJSONString();
	}
}
