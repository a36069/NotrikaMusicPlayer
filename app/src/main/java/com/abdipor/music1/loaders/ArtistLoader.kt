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
import android.provider.MediaStore.Audio.AudioColumns
import com.abdipor.music1.model.Album
import com.abdipor.music1.model.Artist
import com.abdipor.music1.util.PreferenceUtil

object ArtistLoader {
    private fun getSongLoaderSortOrder(context: Context): String {
        return PreferenceUtil.getInstance(context).artistSortOrder + ", " + PreferenceUtil.getInstance(
            context
        ).artistAlbumSortOrder + ", " + PreferenceUtil.getInstance(
            context
        ).albumSongSortOrder
    }

    fun getAllArtists(context: Context): ArrayList<Artist> {
        val songs = SongLoader.getSongs(
            SongLoader.makeSongCursor(
                context,
                null, null,
                getSongLoaderSortOrder(context)
            )
        )
        return splitIntoArtists(AlbumLoader.splitIntoAlbums(songs))
    }

    fun getArtists(context: Context, query: String): ArrayList<Artist> {
        val songs = SongLoader.getSongs(
            SongLoader.makeSongCursor(
                context,
                AudioColumns.ARTIST + " LIKE ?",
                arrayOf("%$query%"),
                getSongLoaderSortOrder(context)
            )
        )
        return splitIntoArtists(AlbumLoader.splitIntoAlbums(songs))
    }

    fun splitIntoArtists(albums: ArrayList<Album>?): ArrayList<Artist> {
        val artists = ArrayList<Artist>()
        if (albums != null) {
            for (album in albums) {
                getOrCreateArtist(artists, album.artistId).albums!!.add(album)
            }
        }
        return artists
    }

    private fun getOrCreateArtist(artists: ArrayList<Artist>, artistId: Int): Artist {
        for (artist in artists) {
            if (artist.albums!!.isNotEmpty() && artist.albums[0].songs!!.isNotEmpty() && artist.albums[0].songs!![0].artistId == artistId) {
                return artist
            }
        }
        val album = Artist()
        artists.add(album)
        return album
    }

    @JvmStatic
    fun getArtist(context: Context, artistId: Int): Artist {
        val songs = SongLoader.getSongs(
            SongLoader.makeSongCursor(
                context,
                AudioColumns.ARTIST_ID + "=?",
                arrayOf(artistId.toString()),
                getSongLoaderSortOrder(context)
            )
        )
        return Artist(AlbumLoader.splitIntoAlbums(songs))
    }
}
