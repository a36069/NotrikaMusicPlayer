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
import android.os.Bundle
import android.view.View
import androidx.preference.Preference
import androidx.preference.TwoStatePreference
import com.abdipor.music1.App
import com.abdipor.music1.R
import com.abdipor.music1.util.PreferenceUtil.*

/**
 * @author Hemanth S (a36069).
 */

class NowPlayingSettingsFragment : AbsSettingsFragment(),
    SharedPreferences.OnSharedPreferenceChangeListener {

    override fun invalidateSettings() {
        updateNowPlayingScreenSummary()
        updateAlbumCoverStyleSummary()

        val carouselEffect: TwoStatePreference = findPreference("carousel_effect")!!
        carouselEffect.setOnPreferenceChangeListener { _, newValue ->
            if (newValue as Boolean && !App.isProVersion()) {
                showProToastAndNavigate(getString(R.string.pref_title_toggle_carousel_effect))
                return@setOnPreferenceChangeListener false
            }
            return@setOnPreferenceChangeListener true
        }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.pref_now_playing_screen)
    }

    private fun updateAlbumCoverStyleSummary() {
        val preference: Preference = findPreference(ALBUM_COVER_STYLE)!!
        preference.setSummary(getInstance(requireContext()).albumCoverStyle.titleRes)
    }

    private fun updateNowPlayingScreenSummary() {
        val preference: Preference = findPreference(NOW_PLAYING_SCREEN_ID)!!
        preference.setSummary(getInstance(requireContext()).nowPlayingScreen.titleRes)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getInstance(requireContext()).registerOnSharedPreferenceChangedListener(this)
        val preference: Preference = findPreference("album_cover_transform")!!
        setSummary(preference)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        getInstance(requireContext()).unregisterOnSharedPreferenceChangedListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        when (key) {
            NOW_PLAYING_SCREEN_ID -> updateNowPlayingScreenSummary()
            ALBUM_COVER_STYLE -> updateAlbumCoverStyleSummary()
            CIRCULAR_ALBUM_ART, CAROUSEL_EFFECT -> invalidateSettings()
        }
    }
}
