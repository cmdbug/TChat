package com.netease.nim.demo.avchat.activity;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import com.netease.nim.demo.R;

/**
 * Created by liuqijun on 7/19/16.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class AVChatSettingsFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.nrtc_setting_pref);

        bindPreferenceSummaryToValue(findPreference(getString(R.string.nrtc_setting_vie_crop_ratio_key)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.nrtc_setting_vie_quality_key)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.nrtc_setting_vie_hw_encoder_key)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.nrtc_setting_vie_hw_decoder_key)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.nrtc_setting_voe_audio_aec_key)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.nrtc_setting_voe_audio_ns_key)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.nrtc_setting_vie_max_bitrate_key)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.nrtc_setting_other_device_default_rotation_key)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.nrtc_setting_other_device_rotation_fixed_offset_key)));

    }


    private void bindPreferenceSummaryToValue(Preference preference) {

        preference.setOnPreferenceChangeListener(this);

        onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String stringValue = newValue.toString();

        if (preference instanceof ListPreference) {
            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(stringValue);
            if (prefIndex >= 0) {
                preference.setSummary(listPreference.getEntries()[prefIndex]);
            }
        } else {
            preference.setSummary(stringValue);
        }
        return true;
    }

}
