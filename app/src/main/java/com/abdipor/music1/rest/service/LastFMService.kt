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

package com.abdipor.music1.rest.service

import com.abdipor.music1.rest.model.LastFmAlbum
import com.abdipor.music1.rest.model.LastFmArtist
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

/**
 * Created by hemanths on 2019-11-26.
 */

interface LastFMService {
    companion object {
        private const val API_KEY = "c679c8d3efa84613dc7dcb2e8d42da4c"
        const val BASE_QUERY_PARAMETERS = "?format=json&autocorrect=1&api_key=$API_KEY"
    }

    @GET("$BASE_QUERY_PARAMETERS&method=artist.getinfo")
    suspend fun artistInfo(
        @Query("artist") artistName: String,
        @Query("lang") language: String?,
        @Header("Cache-Control") cacheControl: String?
    ): LastFmArtist

    @GET("$BASE_QUERY_PARAMETERS&method=album.getinfo")
    suspend fun albumInfo(
        @Query("artist") artistName: String,
        @Query("album") albumName: String
    ): LastFmAlbum
}