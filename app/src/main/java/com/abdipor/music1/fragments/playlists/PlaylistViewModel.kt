package com.abdipor.music1.fragments.playlists

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.abdipor.music1.Result
import com.abdipor.music1.model.Playlist
import com.abdipor.music1.providers.RepositoryImpl
import kotlinx.coroutines.launch

class PlaylistViewModel(application: Application) : AndroidViewModel(application) {
    var playlists = MutableLiveData<List<Playlist>>()

    init {
        loadPlaylist()
    }

    fun loadPlaylist() = viewModelScope.launch {
        val result = RepositoryImpl(getApplication()).allPlaylists()
        if (result is Result.Success) {
            playlists.value = result.data
        } else {
            playlists.value = listOf()
        }
    }
}