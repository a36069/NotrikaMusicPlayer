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

package com.abdipor.music1.dagger.module

import android.content.Context
import com.abdipor.music1.mvp.presenter.*
import com.abdipor.music1.mvp.presenter.AlbumDetailsPresenter.AlbumDetailsPresenterImpl
import com.abdipor.music1.mvp.presenter.AlbumsPresenter.AlbumsPresenterImpl
import com.abdipor.music1.mvp.presenter.ArtistDetailsPresenter.ArtistDetailsPresenterImpl
import com.abdipor.music1.mvp.presenter.ArtistsPresenter.ArtistsPresenterImpl
import com.abdipor.music1.mvp.presenter.GenreDetailsPresenter.GenreDetailsPresenterImpl
import com.abdipor.music1.mvp.presenter.GenresPresenter.GenresPresenterImpl
import com.abdipor.music1.mvp.presenter.HomePresenter.HomePresenterImpl
import com.abdipor.music1.mvp.presenter.PlaylistSongsPresenter.PlaylistSongsPresenterImpl
import com.abdipor.music1.mvp.presenter.PlaylistsPresenter.PlaylistsPresenterImpl
import com.abdipor.music1.mvp.presenter.SearchPresenter.SearchPresenterImpl
import com.abdipor.music1.mvp.presenter.SongPresenter.SongPresenterImpl
import com.abdipor.music1.providers.RepositoryImpl
import com.abdipor.music1.providers.interfaces.Repository
import dagger.Module
import dagger.Provides

/**
 * Created by hemanths on 2019-12-30.
 */

@Module
class PresenterModule {

    @Provides
    fun providesRepository(context: Context): Repository {
        return RepositoryImpl(context)
    }

    @Provides
    fun providesAlbumsPresenter(presenter: AlbumsPresenterImpl): AlbumsPresenter {
        return presenter
    }

    @Provides
    fun providesAlbumDetailsPresenter(presenter: AlbumDetailsPresenterImpl): AlbumDetailsPresenter {
        return presenter
    }

    @Provides
    fun providesArtistDetailsPresenter(presenter: ArtistDetailsPresenterImpl): ArtistDetailsPresenter {
        return presenter
    }

    @Provides
    fun providesArtistsPresenter(presenter: ArtistsPresenterImpl): ArtistsPresenter {
        return presenter
    }

    @Provides
    fun providesGenresPresenter(presenter: GenresPresenterImpl): GenresPresenter {
        return presenter
    }

    @Provides
    fun providesGenreDetailsPresenter(presenter: GenreDetailsPresenterImpl): GenreDetailsPresenter {
        return presenter
    }

    @Provides
    fun providesHomePresenter(presenter: HomePresenterImpl): HomePresenter {
        return presenter
    }

    @Provides
    fun providesPlaylistSongPresenter(presenter: PlaylistSongsPresenterImpl): PlaylistSongsPresenter {
        return presenter
    }

    @Provides
    fun providesPlaylistsPresenter(presenter: PlaylistsPresenterImpl): PlaylistsPresenter {
        return presenter
    }

    @Provides
    fun providesSearchPresenter(presenter: SearchPresenterImpl): SearchPresenter {
        return presenter
    }

    @Provides
    fun providesSongPresenter(presenter: SongPresenterImpl): SongPresenter {
        return presenter
    }
}