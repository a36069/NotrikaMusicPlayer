package com.abdipor.music1.fragments.genres

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.abdipor.music1.Result.Success
import com.abdipor.music1.model.Genre
import com.abdipor.music1.providers.RepositoryImpl
import kotlinx.coroutines.launch

class GenreViewModel(application: Application) : AndroidViewModel(application) {
    var genres = MutableLiveData<List<Genre>>()

    init {
        loadGenre()
    }

    fun loadGenre() = viewModelScope.launch {
        val result = RepositoryImpl(getApplication()).allGenres()
        if (result is Success) {
            genres.value = result.data
        }else {
            genres.value = listOf()
        }
    }
}