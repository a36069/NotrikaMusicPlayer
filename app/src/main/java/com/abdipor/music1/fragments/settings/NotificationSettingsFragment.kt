/*
 * Copyright (c) 2019 mohamamd hussin abdi.
 *
 * Licensed under the GNU General Public License v3
 *
 * This is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by
 *  the Free Software Foundation either version 3 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 */

package com.abdipor.music1.fragments.settings

import android.content.SharedPreferences
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.TwoStatePreference
import com.abdipor.music1.R
import com.abdipor.music1.util.PreferenceUtil


/**
 * @author Hemanth S (a36069).
 */

class NotificationSettingsFragment : AbsSettingsFragment(),
    SharedPreferences.OnSharedPreferenceChangeListener {
    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (key == PreferenceUtil.CLASSIC_NOTIFICATION) {
            if (VERSION.SDK_INT >= VERSION_CODES.O) {
                findPreference<Preference>("colored_notification")?.isEnabled =
                    sharedPreferences?.getBoolean(key, false)!!
            }
        }
    }

    override fun invalidateSettings() {

        val classicNotification: TwoStatePreference? = findPreference("classic_notification")
        if (VERSION.SDK_INT < VERSION_CODES.N) {
            classicNotification?.isVisible = false
        } else {
            classicNotification?.apply {
                isChecked = PreferenceUtil.getInstance(requireContext()).classicNotification()
                setOnPreferenceChangeListener { _, newValue ->
                    // Save preference
                    PreferenceUtil.getInstance(requireContext())
                        .setClassicNotification(newValue as Boolean)
                    invalidateSettings()
                    true
                }
            }
        }

        val coloredNotification: TwoStatePreference? = findPreference("colored_notification")
        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            coloredNotification?.isEnabled =
                PreferenceUtil.getInstance(requireContext()).classicNotification()
        } else {
            coloredNotification?.apply {
                isChecked = PreferenceUtil.getInstance(requireContext()).coloredNotification()
                setOnPreferenceChangeListener { _, newValue ->
                    PreferenceUtil.getInstance(requireContext())
                        .setColoredNotification(newValue as Boolean)
                    true
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        PreferenceUtil.getInstance(requireContext()).registerOnSharedPreferenceChangedListener(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        PreferenceUtil.getInstance(requireContext())
            .unregisterOnSharedPreferenceChangedListener(this)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.pref_notification)
    }
}
