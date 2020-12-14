package com.abdipor.music1.fragments.albums

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.abdipor.music1.Result
import com.abdipor.music1.model.Album
import com.abdipor.music1.providers.RepositoryImpl
import kotlinx.coroutines.launch

class AlbumViewModel(application: Application) : AndroidViewModel(application) {
    var albums = MutableLiveData<List<Album>>()

    init {
        getAlbums()
    }

    fun getAlbums() = viewModelScope.launch {
        val result = RepositoryImpl(getApplication()).allAlbums()
        if (result is Result.Success) {
            albums.value = result.data
        }else {
            albums.value = listOf()
        }
    }
}