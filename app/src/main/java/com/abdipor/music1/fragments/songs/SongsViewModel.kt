package com.abdipor.music1.fragments.songs

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.abdipor.music1.Result.Success
import com.abdipor.music1.model.Song
import com.abdipor.music1.providers.RepositoryImpl
import kotlinx.coroutines.launch

class SongsViewModel(application: Application) : AndroidViewModel(application) {
    var songs = MutableLiveData<List<Song>>()

    init {
        loadSongs()
    }

    fun loadSongs() = viewModelScope.launch {
        val result = RepositoryImpl(getApplication()).allSongs()
        if (result is Success) {
            songs.value = result.data
        } else {
            songs.value = listOf()
        }
    }
}