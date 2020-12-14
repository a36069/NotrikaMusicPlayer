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

package com.abdipor.music1

import android.provider.BaseColumns
import android.provider.MediaStore

object Constants {

    const val RATE_ON_GOOGLE_PLAY =
        "https://play.google.com/store/apps/details?id=com.abdipor.music1"
    const val GITHUB_PROJECT = "https://github.com/a36069/NotrikaMusicPlayer"
    const val TELEGRAM_CHANGE_LOG = "https://t.me/m36069"
    const val USER_PROFILE = "profile.jpg"
    const val USER_BANNER = "banner.jpg"
    const val APP_INSTAGRAM_LINK = "https://www.instagram.com/abdipor_developer/"
    const val APP_TELEGRAM_LINK = "https://t.me/m36069/"
    const val APP_TWITTER_LINK = "https://twitter.com/mohammadabdiDev"
    const val FAQ_LINK = "https://github.com/a36069/NotrikaMusicPlayer/blob/master/FAQ.md"
    const val MEDIUM = "https://mohammadabdipour.medium.com/about/"

    const val BASE_SELECTION =
        MediaStore.Audio.AudioColumns.IS_MUSIC + "=1" + " AND " + MediaStore.Audio.AudioColumns.TITLE + " != ''"

    val baseProjection = arrayOf(
        BaseColumns._ID, // 0
        MediaStore.Audio.AudioColumns.TITLE, // 1
        MediaStore.Audio.AudioColumns.TRACK, // 2
        MediaStore.Audio.AudioColumns.YEAR, // 3
        MediaStore.Audio.AudioColumns.DURATION, // 4
        MediaStore.Audio.AudioColumns.DATA, // 5
        MediaStore.Audio.AudioColumns.DATE_MODIFIED, // 6
        MediaStore.Audio.AudioColumns.ALBUM_ID, // 7
        MediaStore.Audio.AudioColumns.ALBUM, // 8
        MediaStore.Audio.AudioColumns.ARTIST_ID, // 9
        MediaStore.Audio.AudioColumns.ARTIST,// 10
        MediaStore.Audio.AudioColumns.COMPOSER
    )// 11
    const val NUMBER_OF_TOP_TRACKS = 99
}
