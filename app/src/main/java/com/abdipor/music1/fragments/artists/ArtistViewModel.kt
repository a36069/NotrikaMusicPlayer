package com.abdipor.music1.fragments.artists

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.abdipor.music1.Result
import com.abdipor.music1.model.Artist
import com.abdipor.music1.providers.RepositoryImpl
import kotlinx.coroutines.launch

class ArtistViewModel(application: Application) : AndroidViewModel(application) {
    var artists = MutableLiveData<List<Artist>>()

    init {
        loadArtists()
    }

    fun loadArtists() = viewModelScope.launch {
        val result = RepositoryImpl(getApplication()).allArtists()
        if (result is Result.Success) {
            artists.value = result.data
        } else {
            artists.value = listOf()
        }
    }
}