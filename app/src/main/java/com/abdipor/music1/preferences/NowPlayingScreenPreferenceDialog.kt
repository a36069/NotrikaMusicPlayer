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

package com.abdipor.music1.preferences

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat.SRC_IN
import androidx.preference.PreferenceDialogFragmentCompat
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.abdipor.appthemehelper.common.prefs.supportv7.ATEDialogPreference
import com.abdipor.music1.App
import com.abdipor.music1.R
import com.abdipor.music1.extensions.colorControlNormal
import com.abdipor.music1.fragments.NowPlayingScreen
import com.abdipor.music1.fragments.NowPlayingScreen.*
import com.abdipor.music1.util.NavigationUtil
import com.abdipor.music1.util.PreferenceUtil
import com.abdipor.music1.util.ViewUtil
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.bumptech.glide.Glide

class NowPlayingScreenPreference @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = -1,
    defStyleRes: Int = -1
) : ATEDialogPreference(context, attrs, defStyleAttr, defStyleRes) {

    private val mLayoutRes = R.layout.preference_dialog_now_playing_screen

    override fun getDialogLayoutResource(): Int {
        return mLayoutRes
    }

    init {
        icon?.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
            colorControlNormal(context),
            SRC_IN
        )
    }
}

class NowPlayingScreenPreferenceDialog : PreferenceDialogFragmentCompat(),
    ViewPager.OnPageChangeListener {

    private var viewPagerPosition: Int = 0

    override fun onPageScrollStateChanged(state: Int) {
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
    }

    override fun onPageSelected(position: Int) {
        this.viewPagerPosition = position
    }

    override fun onDialogClosed(positiveResult: Boolean) {
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = LayoutInflater.from(requireContext())
            .inflate(R.layout.preference_dialog_now_playing_screen, null)
        val viewPager = view.findViewById<ViewPager>(R.id.now_playing_screen_view_pager)
            ?: throw  IllegalStateException("Dialog view must contain a ViewPager with id 'now_playing_screen_view_pager'")
        viewPager.adapter = NowPlayingScreenAdapter(requireContext())
        viewPager.addOnPageChangeListener(this)
        viewPager.pageMargin = ViewUtil.convertDpToPixel(32f, resources).toInt()
        viewPager.currentItem =
            PreferenceUtil.getInstance(requireContext()).nowPlayingScreen.ordinal


        return MaterialDialog(requireContext()).show {
            title(R.string.pref_title_now_playing_screen_appearance)
            positiveButton(R.string.set) {
                val nowPlayingScreen = values()[viewPagerPosition]
                if (isNowPlayingThemes(nowPlayingScreen)) {
                    val result =
                        getString(nowPlayingScreen.titleRes) + " theme is Pro version feature."
                    Toast.makeText(context, result, Toast.LENGTH_SHORT).show()
                    NavigationUtil.goToProVersion(requireContext())
                } else {
                    PreferenceUtil.getInstance(requireContext()).nowPlayingScreen = nowPlayingScreen
                }
            }
            cornerRadius(PreferenceUtil.getInstance(requireContext()).dialogCorner)
            negativeButton(android.R.string.cancel)
            customView(view = view, scrollable = false, noVerticalPadding = false)
        }
    }

    companion object {
        fun newInstance(key: String): NowPlayingScreenPreferenceDialog {
            val bundle = Bundle()
            bundle.putString(ARG_KEY, key)
            val fragment = NowPlayingScreenPreferenceDialog()
            fragment.arguments = bundle
            return fragment
        }
    }
}

private class NowPlayingScreenAdapter(private val context: Context) : PagerAdapter() {

    override fun instantiateItem(collection: ViewGroup, position: Int): Any {
        val nowPlayingScreen = values()[position]

        val inflater = LayoutInflater.from(context)
        val layout = inflater.inflate(
            R.layout.preference_now_playing_screen_item,
            collection,
            false
        ) as ViewGroup
        collection.addView(layout)

        val image = layout.findViewById<ImageView>(R.id.image)
        val title = layout.findViewById<TextView>(R.id.title)
        val proText = layout.findViewById<TextView>(R.id.proText)
        Glide.with(context).load(nowPlayingScreen.drawableResId).into(image)
        title.setText(nowPlayingScreen.titleRes)
        if (isNowPlayingThemes(nowPlayingScreen)) {
            proText.setText(R.string.pro)
        } else {
            proText.setText(R.string.free)
        }
        return layout
    }

    override fun destroyItem(
        collection: ViewGroup,
        position: Int,
        view: Any
    ) {
        collection.removeView(view as View)
    }

    override fun getCount(): Int {
        return values().size
    }

    override fun isViewFromObject(view: View, instance: Any): Boolean {
        return view === instance
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return context.getString(values()[position].titleRes)
    }
}

private fun isNowPlayingThemes(screen: NowPlayingScreen): Boolean {
    return (screen == FULL ||
            screen == CARD ||
            screen == PLAIN ||
            screen == BLUR ||
            screen == COLOR ||
            screen == SIMPLE ||
            screen == BLUR_CARD ||
            screen == CIRCLE ||
            screen == ADAPTIVE)
            && !App.isProVersion()
}