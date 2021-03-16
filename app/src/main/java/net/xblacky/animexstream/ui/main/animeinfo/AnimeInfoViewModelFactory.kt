package net.xblacky.animexstream.ui.main.animeinfo

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import retrofit2.http.Url
import java.lang.IllegalArgumentException

class AnimeInfoViewModelFactory (
    private val categoryUrl: String,
    val animeInfoRepository: AnimeInfoRepository
    ) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(AnimeInfoViewModel::class.java)){
            return AnimeInfoViewModel(categoryUrl = categoryUrl, animeInfoRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }

}