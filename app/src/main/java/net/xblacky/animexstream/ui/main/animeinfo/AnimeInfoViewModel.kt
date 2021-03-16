package net.xblacky.animexstream.ui.main.animeinfo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import net.xblacky.animexstream.ui.main.home.HomeRepository
import net.xblacky.animexstream.utils.CommonViewModel
import net.xblacky.animexstream.utils.constants.C
import net.xblacky.animexstream.utils.model.AnimeInfoModel
import net.xblacky.animexstream.utils.model.EpisodeModel
import net.xblacky.animexstream.utils.model.FavouriteModel
import net.xblacky.animexstream.utils.parser.HtmlParser
import okhttp3.Response
import okhttp3.ResponseBody
import timber.log.Timber
import javax.inject.Inject

class AnimeInfoViewModel(
    val categoryUrl: String,
    val animeInfoRepository: AnimeInfoRepository
) : CommonViewModel() {

    private var _animeInfoModel: MutableLiveData<AnimeInfoModel> = MutableLiveData()
    private var _episodeList: MutableLiveData<ArrayList<EpisodeModel>> = MutableLiveData()
    var episodeList: LiveData<ArrayList<EpisodeModel>> = _episodeList
    var animeInfoModel: LiveData<AnimeInfoModel> = _animeInfoModel
    private var _isFavourite: MutableLiveData<Boolean> = MutableLiveData(false)
    var isFavourite: LiveData<Boolean> = _isFavourite

    init {
        fetchAnimeInfo()
    }

    fun fetchAnimeInfo() = viewModelScope.launch {
        updateLoading(loading = true)
        updateErrorModel(false, null, false)
        categoryUrl?.let {
            val resp = animeInfoRepository.fetchAnimeInfo(it)
            getAnimeInfoObserver(resp, C.TYPE_ANIME_INFO)
        }
    }

    private fun getAnimeInfoObserver(response: ResponseBody, typeValue: Int) : Job = viewModelScope.launch{
        if (typeValue == C.TYPE_ANIME_INFO) {
            val animeInfoModel = HtmlParser.parseAnimeInfo(response = response.string())
            _animeInfoModel.value = animeInfoModel
            val res = animeInfoRepository.fetchEpisodeList(
                id = animeInfoModel.id,
                endEpisode = animeInfoModel.endEpisode,
                alias = animeInfoModel.alias
            )
            getAnimeInfoObserver(
                response = res,
                typeValue = C.TYPE_EPISODE_LIST
            )
            _isFavourite.value = animeInfoRepository.isFavourite(animeInfoModel.id)

        } else if (typeValue == C.TYPE_EPISODE_LIST) {
            _episodeList.value = HtmlParser.fetchEpisodeList(response = response.string())
            updateLoading(loading = false)
        }
    }

    fun toggleFavourite() {
        if (_isFavourite.value!!) {
            animeInfoModel.value?.id?.let { animeInfoRepository.removeFromFavourite(it) }
            _isFavourite.value = false
        } else {
            saveFavourite()
        }
    }

    private fun saveFavourite() {
        val model = animeInfoModel.value
        animeInfoRepository.addToFavourite(
            FavouriteModel(
                ID = model?.id,
                categoryUrl = categoryUrl,
                animeName = model?.animeTitle,
                releasedDate = model?.releasedTime,
                imageUrl = model?.imageUrl
            )
        )
        _isFavourite.value = true
    }

//    fun setUrl(url: String) {
//        this.categoryUrl = url
//    }

    override fun onCleared() {
        if (isFavourite.value!!) {
            saveFavourite()
        }
        super.onCleared()
    }
}