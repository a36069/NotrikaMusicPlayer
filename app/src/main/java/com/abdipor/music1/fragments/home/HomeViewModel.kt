package com.abdipor.music1.fragments.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.abdipor.music1.Result
import com.abdipor.music1.model.Home
import com.abdipor.music1.providers.RepositoryImpl
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    var sections = MutableLiveData<List<Home>>()
    var repository: RepositoryImpl = RepositoryImpl(getApplication())

    init {
        loadHome()
    }

    fun loadHome() = viewModelScope.launch {
        val list = mutableListOf<Home>()
        val result = listOf(
            repository.topArtists(),
            repository.topAlbums(),
            repository.recentArtists(),
            repository.recentAlbums(),
            repository.favoritePlaylist()
        )
        for (r in result) {
            when (r) {
                is Result.Success -> list.add(r.data)
            }
        }
        sections.value = list
    }
}