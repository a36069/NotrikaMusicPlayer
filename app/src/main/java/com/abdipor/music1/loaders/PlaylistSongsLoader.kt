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

package com.abdipor.music1.loaders

import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import android.provider.MediaStore.Audio.AudioColumns
import com.abdipor.music1.Constants.BASE_SELECTION
import com.abdipor.music1.model.AbsCustomPlaylist
import com.abdipor.music1.model.Playlist
import com.abdipor.music1.model.PlaylistSong
import com.abdipor.music1.model.Song
import java.util.*

/**
 * Created by hemanths on 16/08/17.
 */

object PlaylistSongsLoader {

    fun getPlaylistSongList(
        context: Context,
        playlist: Playlist
    ): ArrayList<Song> {
        return (playlist as? AbsCustomPlaylist)?.getSongs(context)
            ?: getPlaylistSongList(context, playlist.id)
    }

    @JvmStatic
    fun getPlaylistSongList(context: Context, playlistId: Int): ArrayList<Song> {
        val songs = arrayListOf<Song>()
        val cursor = makePlaylistSongCursor(context, playlistId)

        if (cursor != null && cursor.moveToFirst()) {
            do {
                songs.add(getPlaylistSongFromCursorImpl(cursor, playlistId))
            } while (cursor.moveToNext())
        }
        cursor?.close()
        return songs
    }

    private fun getPlaylistSongFromCursorImpl(cursor: Cursor, playlistId: Int): PlaylistSong {
        val id = cursor.getInt(0)
        val title = cursor.getString(1)
        val trackNumber = cursor.getInt(2)
        val year = cursor.getInt(3)
        val duration = cursor.getLong(4)
        val data = cursor.getString(5)
        val dateModified = cursor.getLong(6)
        val albumId = cursor.getInt(7)
        val albumName = cursor.getString(8)
        val artistId = cursor.getInt(9)
        val artistName = cursor.getString(10)
        val idInPlaylist = cursor.getInt(11)
        val composer = cursor.getString(12)

        return PlaylistSong(
            id,
            title,
            trackNumber,
            year,
            duration,
            data,
            dateModified,
            albumId,
            albumName,
            artistId,
            artistName,
            playlistId,
            idInPlaylist,
            composer
        )
    }

    private fun makePlaylistSongCursor(context: Context, playlistId: Int): Cursor? {
        try {
            return context.contentResolver.query(
                MediaStore.Audio.Playlists.Members.getContentUri("external", playlistId.toLong()),
                arrayOf(
                    MediaStore.Audio.Playlists.Members.AUDIO_ID, // 0
                    AudioColumns.TITLE, // 1
                    AudioColumns.TRACK, // 2
                    AudioColumns.YEAR, // 3
                    AudioColumns.DURATION, // 4
                    AudioColumns.DATA, // 5
                    AudioColumns.DATE_MODIFIED, // 6
                    AudioColumns.ALBUM_ID, // 7
                    AudioColumns.ALBUM, // 8
                    AudioColumns.ARTIST_ID, // 9
                    AudioColumns.ARTIST, // 10
                    MediaStore.Audio.Playlists.Members._ID,//11
                    AudioColumns.COMPOSER
                )// 12
                , BASE_SELECTION, null,
                MediaStore.Audio.Playlists.Members.DEFAULT_SORT_ORDER
            )
        } catch (e: SecurityException) {
            return null
        }
    }
}
