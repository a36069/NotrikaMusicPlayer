/*
 * Copyright (c) 2020 mohamamd hussin abdi.
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

package com.abdipor.music1.activities

import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore.Images.Media
import android.view.MenuItem
import androidx.core.view.drawToBitmap
import com.abdipor.appthemehelper.ThemeStore
import com.abdipor.appthemehelper.util.ColorUtil
import com.abdipor.appthemehelper.util.MaterialValueHelper
import com.abdipor.music1.R
import com.abdipor.music1.activities.base.AbsBaseActivity
import com.abdipor.music1.glide.NotrikaMusicColoredTarget
import com.abdipor.music1.glide.SongGlideRequest
import com.abdipor.music1.model.Song
import com.abdipor.music1.util.Share
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_share_instagram.*

/**
 * Created by hemanths on 2020-02-02.
 */

class ShareInstagramStory : AbsBaseActivity() {

    companion object {
        const val EXTRA_SONG = "extra_song"
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setDrawUnderStatusBar()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share_instagram)
        setStatusbarColor(Color.TRANSPARENT)
        setNavigationbarColor(Color.BLACK)

        toolbar.setBackgroundColor(Color.TRANSPARENT)
        setSupportActionBar(toolbar)

        val song = intent.extras?.getParcelable<Song>(EXTRA_SONG)
        song?.let { songFinal ->
            SongGlideRequest.Builder.from(Glide.with(this), songFinal)
                .checkIgnoreMediaStore(this@ShareInstagramStory)
                .generatePalette(this@ShareInstagramStory)
                .build()
                .into(object : NotrikaMusicColoredTarget(image) {
                    override fun onColorReady(color: Int) {
                        val isColorLight = ColorUtil.isColorLight(color)
                        setColors(isColorLight, color)
                    }
                })

            shareTitle.text = songFinal.title
            shareText.text = songFinal.artistName
            shareButton.setOnClickListener {
                val path: String = Media.insertImage(
                    contentResolver,
                    mainContent.drawToBitmap(Bitmap.Config.ARGB_8888),
                    "Design", null
                )
                val uri = Uri.parse(path)
                Share.shareStoryToSocial(
                    this@ShareInstagramStory,
                    uri
                )
            }
        }
        shareButton.setTextColor(
            MaterialValueHelper.getPrimaryTextColor(
                this,
                ColorUtil.isColorLight(ThemeStore.accentColor(this))
            )
        )
        shareButton.backgroundTintList = ColorStateList.valueOf(ThemeStore.accentColor(this))
    }

    private fun setColors(colorLight: Boolean, color: Int) {
        setLightStatusbar(colorLight)
        toolbar.setTitleTextColor(
            MaterialValueHelper.getPrimaryTextColor(
                this@ShareInstagramStory,
                colorLight
            )
        )
        toolbar.navigationIcon?.setTintList(
            ColorStateList.valueOf(
                MaterialValueHelper.getPrimaryTextColor(
                    this@ShareInstagramStory,
                    colorLight
                )
            )
        )
        mainContent.background =
            GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                intArrayOf(color, Color.BLACK)
            )
    }
}
