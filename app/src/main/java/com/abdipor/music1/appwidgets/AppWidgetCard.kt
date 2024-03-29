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

package com.abdipor.music1.appwidgets

import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.view.View
import android.widget.RemoteViews
import com.abdipor.appthemehelper.util.MaterialValueHelper
import com.abdipor.music1.R
import com.abdipor.music1.activities.MainActivity
import com.abdipor.music1.appwidgets.base.BaseAppWidget
import com.abdipor.music1.glide.SongGlideRequest
import com.abdipor.music1.glide.palette.BitmapPaletteWrapper
import com.abdipor.music1.service.MusicService
import com.abdipor.music1.service.MusicService.*
import com.abdipor.music1.util.ImageUtil
import com.abdipor.music1.util.NotrikaUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.request.animation.GlideAnimation
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.target.Target

class AppWidgetCard : BaseAppWidget() {
    private var target: Target<BitmapPaletteWrapper>? = null // for cancellation

    /**
     * Initialize given widgets to default state, where we launch Music on default click and hide
     * actions if service not running.
     */
    override fun defaultAppWidget(context: Context, appWidgetIds: IntArray) {
        val appWidgetView = RemoteViews(context.packageName, R.layout.app_widget_card)

        appWidgetView.setViewVisibility(R.id.media_titles, View.INVISIBLE)
        appWidgetView.setImageViewResource(R.id.image, R.drawable.default_audio_art)
        appWidgetView.setImageViewBitmap(
            R.id.button_next, createBitmap(
                NotrikaUtil.getTintedVectorDrawable(
                    context,
                    R.drawable.ic_skip_next_white_24dp,
                    MaterialValueHelper.getSecondaryTextColor(
                        context, true
                    )
                )!!, 1f
            )
        )
        appWidgetView.setImageViewBitmap(
            R.id.button_prev, createBitmap(
                NotrikaUtil.getTintedVectorDrawable(
                    context,
                    R.drawable.ic_skip_previous_white_24dp,
                    MaterialValueHelper.getSecondaryTextColor(
                        context, true
                    )
                )!!, 1f
            )
        )
        appWidgetView.setImageViewBitmap(
            R.id.button_toggle_play_pause, createBitmap(
                NotrikaUtil.getTintedVectorDrawable(
                    context,
                    R.drawable.ic_play_arrow_white_32dp,
                    MaterialValueHelper.getSecondaryTextColor(
                        context, true
                    )
                )!!, 1f
            )
        )

        linkButtons(context, appWidgetView)
        pushUpdate(context, appWidgetIds, appWidgetView)
    }

    /**
     * Update all active widget instances by pushing changes
     */
    override fun performUpdate(service: MusicService, appWidgetIds: IntArray?) {
        val appWidgetView = RemoteViews(service.packageName, R.layout.app_widget_card)

        val isPlaying = service.isPlaying
        val song = service.currentSong

        // Set the titles and artwork
        if (TextUtils.isEmpty(song.title) && TextUtils.isEmpty(song.artistName)) {
            appWidgetView.setViewVisibility(R.id.media_titles, View.INVISIBLE)
        } else {
            appWidgetView.setViewVisibility(R.id.media_titles, View.VISIBLE)
            appWidgetView.setTextViewText(R.id.title, song.title)
            appWidgetView.setTextViewText(R.id.text, getSongArtistAndAlbum(song))
        }

        // Set correct drawable for pause state
        val playPauseRes =
            if (isPlaying) R.drawable.ic_pause_white_24dp else R.drawable.ic_play_arrow_white_32dp
        appWidgetView.setImageViewBitmap(
            R.id.button_toggle_play_pause, createBitmap(
                NotrikaUtil.getTintedVectorDrawable(
                    service,
                    playPauseRes,
                    MaterialValueHelper.getSecondaryTextColor(service, true)
                )!!, 1f
            )
        )

        // Set prev/next button drawables
        appWidgetView.setImageViewBitmap(
            R.id.button_next, createBitmap(
                NotrikaUtil.getTintedVectorDrawable(
                    service,
                    R.drawable.ic_skip_next_white_24dp,
                    MaterialValueHelper.getSecondaryTextColor(service, true)
                )!!, 1f
            )
        )
        appWidgetView.setImageViewBitmap(
            R.id.button_prev, createBitmap(
                NotrikaUtil.getTintedVectorDrawable(
                    service,
                    R.drawable.ic_skip_previous_white_24dp,
                    MaterialValueHelper.getSecondaryTextColor(service, true)
                )!!, 1f
            )
        )

        // Link actions buttons to intents
        linkButtons(service, appWidgetView)

        if (imageSize == 0) {
            imageSize =
                service.resources.getDimensionPixelSize(com.abdipor.music1.R.dimen.app_widget_card_image_size)
        }
        if (cardRadius == 0f) {
            cardRadius =
                service.resources.getDimension(com.abdipor.music1.R.dimen.app_widget_card_radius)
        }

        // Load the album cover async and push the update on completion
        service.runOnUiThread {
            if (target != null) {
                Glide.clear(target)
            }
            target = SongGlideRequest.Builder.from(Glide.with(service), song)
                .checkIgnoreMediaStore(service).generatePalette(service).build().centerCrop()
                .into(object : SimpleTarget<BitmapPaletteWrapper>(imageSize, imageSize) {
                    override fun onResourceReady(
                        resource: BitmapPaletteWrapper,
                        glideAnimation: GlideAnimation<in BitmapPaletteWrapper>
                    ) {
                        val palette = resource.palette
                        update(
                            resource.bitmap, palette.getVibrantColor(
                                palette.getMutedColor(
                                    MaterialValueHelper.getSecondaryTextColor(
                                        service, true
                                    )
                                )
                            )
                        )
                    }

                    override fun onLoadFailed(e: Exception?, errorDrawable: Drawable?) {
                        super.onLoadFailed(e, errorDrawable)
                        update(null, MaterialValueHelper.getSecondaryTextColor(service, true))
                    }

                    private fun update(bitmap: Bitmap?, color: Int) {
                        // Set correct drawable for pause state
                        appWidgetView.setImageViewBitmap(
                            R.id.button_toggle_play_pause, ImageUtil.createBitmap(
                                ImageUtil.getTintedVectorDrawable(
                                    service, playPauseRes, color
                                )
                            )
                        )

                        // Set prev/next button drawables
                        appWidgetView.setImageViewBitmap(
                            R.id.button_next, ImageUtil.createBitmap(
                                ImageUtil.getTintedVectorDrawable(
                                    service, R.drawable.ic_skip_next_white_24dp, color
                                )
                            )
                        )
                        appWidgetView.setImageViewBitmap(
                            R.id.button_prev, ImageUtil.createBitmap(
                                ImageUtil.getTintedVectorDrawable(
                                    service, R.drawable.ic_skip_previous_white_24dp, color
                                )
                            )
                        )

                        val image = getAlbumArtDrawable(service.resources, bitmap)
                        val roundedBitmap = createRoundedBitmap(
                            image, imageSize, imageSize, cardRadius, 0F, cardRadius, 0F
                        )
                        appWidgetView.setImageViewBitmap(R.id.image, roundedBitmap)

                        pushUpdate(service, appWidgetIds, appWidgetView)
                    }
                })
        }
    }

    /**
     * Link up various button actions using [PendingIntent].
     */
    private fun linkButtons(context: Context, views: RemoteViews) {
        val action: Intent = Intent(context, MainActivity::class.java).putExtra("expand", true)
        var pendingIntent: PendingIntent

        val serviceName = ComponentName(context, MusicService::class.java)

        // Home
        action.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        pendingIntent = PendingIntent.getActivity(context, 0, action, 0)
        views.setOnClickPendingIntent(R.id.image, pendingIntent)
        views.setOnClickPendingIntent(R.id.media_titles, pendingIntent)

        // Previous track
        pendingIntent = buildPendingIntent(context, ACTION_REWIND, serviceName)
        views.setOnClickPendingIntent(R.id.button_prev, pendingIntent)

        // Play and pause
        pendingIntent = buildPendingIntent(context, ACTION_TOGGLE_PAUSE, serviceName)
        views.setOnClickPendingIntent(R.id.button_toggle_play_pause, pendingIntent)

        // Next track
        pendingIntent = buildPendingIntent(context, ACTION_SKIP, serviceName)
        views.setOnClickPendingIntent(R.id.button_next, pendingIntent)
    }

    companion object {

        const val NAME = "app_widget_card"

        private var mInstance: AppWidgetCard? = null
        private var imageSize = 0
        private var cardRadius = 0f

        val instance: AppWidgetCard
            @Synchronized get() {
                if (mInstance == null) {
                    mInstance = AppWidgetCard()
                }
                return mInstance!!
            }
    }
}
