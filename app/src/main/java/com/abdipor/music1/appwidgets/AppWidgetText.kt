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
import android.text.TextUtils
import android.view.View
import android.widget.RemoteViews
import androidx.core.content.ContextCompat
import com.abdipor.music1.App
import com.abdipor.music1.R
import com.abdipor.music1.activities.MainActivity
import com.abdipor.music1.appwidgets.base.BaseAppWidget
import com.abdipor.music1.service.MusicService
import com.abdipor.music1.service.MusicService.*
import com.abdipor.music1.util.NotrikaUtil

class AppWidgetText : BaseAppWidget() {
    override fun defaultAppWidget(context: Context, appWidgetIds: IntArray) {
        val appWidgetView = RemoteViews(context.packageName, R.layout.app_widget_text)

        appWidgetView.setImageViewBitmap(
            R.id.button_next, createBitmap(
                NotrikaUtil.getTintedVectorDrawable(
                    context, R.drawable.ic_skip_next_white_24dp, ContextCompat.getColor(
                        context, R.color.md_white_1000
                    )
                )!!, 1f
            )
        )
        appWidgetView.setImageViewBitmap(
            R.id.button_prev, createBitmap(
                NotrikaUtil.getTintedVectorDrawable(
                    context, R.drawable.ic_skip_previous_white_24dp, ContextCompat.getColor(
                        context, R.color.md_white_1000
                    )
                )!!, 1f
            )
        )
        appWidgetView.setImageViewBitmap(
            R.id.button_toggle_play_pause, createBitmap(
                NotrikaUtil.getTintedVectorDrawable(
                    context, R.drawable.ic_play_arrow_white_32dp, ContextCompat.getColor(
                        context, R.color.md_white_1000
                    )
                )!!, 1f
            )
        )

        appWidgetView.setTextColor(
            R.id.title, ContextCompat.getColor(context, R.color.md_white_1000)
        )
        appWidgetView.setTextColor(
            R.id.text, ContextCompat.getColor(context, R.color.md_white_1000)
        )

        linkButtons(context, appWidgetView)
        pushUpdate(context, appWidgetIds, appWidgetView)
    }

    /**
     * Link up various button actions using [PendingIntent].
     */
    private fun linkButtons(context: Context, views: RemoteViews) {
        val action = Intent(context, MainActivity::class.java).putExtra("expand", true)
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

    override fun performUpdate(service: MusicService, appWidgetIds: IntArray?) {
        val appWidgetView = RemoteViews(service.packageName, R.layout.app_widget_text)

        val isPlaying = service.isPlaying
        val song = service.currentSong

        // Set the titles and artwork
        if (TextUtils.isEmpty(song.title) && TextUtils.isEmpty(song.artistName)) {
            appWidgetView.setViewVisibility(R.id.media_titles, View.INVISIBLE)
        } else {
            appWidgetView.setViewVisibility(R.id.media_titles, View.VISIBLE)
            appWidgetView.setTextViewText(R.id.title, song.title)
            appWidgetView.setTextViewText(R.id.text, song.artistName)
        }
        // Link actions buttons to intents
        linkButtons(service, appWidgetView)

        // Set correct drawable for pause state
        val playPauseRes = if (isPlaying) R.drawable.ic_pause_white_24dp
        else R.drawable.ic_play_arrow_white_32dp
        appWidgetView.setImageViewBitmap(
            R.id.button_toggle_play_pause, createBitmap(
                NotrikaUtil.getTintedVectorDrawable(
                    App.getContext(), playPauseRes, ContextCompat.getColor(
                        App.getContext(), R.color.md_white_1000
                    )
                )!!, 1f
            )
        )
        appWidgetView.setImageViewBitmap(
            R.id.button_next, createBitmap(
                NotrikaUtil.getTintedVectorDrawable(
                    App.getContext(),
                    R.drawable.ic_skip_next_white_24dp,
                    ContextCompat.getColor(
                        App.getContext(), R.color.md_white_1000
                    )
                )!!, 1f
            )
        )
        appWidgetView.setImageViewBitmap(
            R.id.button_prev, createBitmap(
                NotrikaUtil.getTintedVectorDrawable(
                    App.getContext(),
                    R.drawable.ic_skip_previous_white_24dp,
                    ContextCompat.getColor(
                        App.getContext(), R.color.md_white_1000
                    )
                )!!, 1f
            )
        )



        pushUpdate(service.applicationContext, appWidgetIds, appWidgetView)

    }

    companion object {

        const val NAME: String = "app_widget_text"

        private var mInstance: AppWidgetText? = null

        val instance: AppWidgetText
            @Synchronized get() {
                if (mInstance == null) {
                    mInstance = AppWidgetText()
                }
                return mInstance!!
            }
    }
}
