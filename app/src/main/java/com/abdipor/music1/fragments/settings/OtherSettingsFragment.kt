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

import android.os.Bundle
import android.view.View
import androidx.preference.Preference

import com.abdipor.music1.R
import com.abdipor.music1.preferences.MaterialListPreference

/**
 * @author Hemanth S (a36069).
 */

class OtherSettingsFragment : AbsSettingsFragment() {
    override fun invalidateSettings() {
        val languagePreference: MaterialListPreference = findPreference("language_name")!!
        languagePreference.setOnPreferenceChangeListener { _, _ ->
            requireActivity().recreate()
            return@setOnPreferenceChangeListener true
        }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.pref_advanced)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val preference: Preference = findPreference("last_added_interval")!!
        setSummary(preference)
        val languagePreference: Preference = findPreference("language_name")!!
        setSummary(languagePreference)
    }
}
