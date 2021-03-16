package net.xblacky.animexstream.ui.main.player

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.exoplayer2.upstream.HttpDataSource
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import net.xblacky.animexstream.utils.CommonViewModel
import net.xblacky.animexstream.utils.constants.C
import net.xblacky.animexstream.utils.model.Content
import net.xblacky.animexstream.utils.parser.HtmlParser
import okhttp3.ResponseBody
import retrofit2.HttpException
import javax.inject.Inject

class VideoPlayerViewModel @ViewModelInject constructor(
    val episodeRepository: EpisodeRepository
) : ViewModel() {

    private var _content = MutableLiveData<Content>(Content())
    var liveContent: LiveData<Content> = _content

    init {
        episodeRepository.clearContent()
    }

    fun fetchEpisodeMediaUrl(fetchFromDb: Boolean = true) {
        liveContent.value?.episodeUrl?.let {
//            updateErrorModel(show = false, e = null, isListEmpty = false)
//            updateLoading(loading = true)
            val result = episodeRepository.fetchContent(it)
            val animeName = _content.value?.animeName
            if (fetchFromDb) {
                result?.let {
                    result.animeName = animeName ?: ""
                    _content.value = result
//                    updateLoading(false)
                } ?: kotlin.run {
                    fetchFromInternet(it)
                }
            } else {
                fetchFromInternet(it)
            }
        }
    }

    private fun fetchFromInternet(url: String) = viewModelScope.launch{
        val resp = episodeRepository.fetchEpisodeMediaUrl(url=url)
        getEpisodeUrlObserver(resp, C.TYPE_MEDIA_URL)
    }

    fun updateEpisodeContent(content: Content) {
        _content.value = content
    }

    private fun getEpisodeUrlObserver(response: ResponseBody, type: Int): Job = viewModelScope.launch{
        if (type == C.TYPE_MEDIA_URL) {
            val episodeInfo = HtmlParser.parseMediaUrl(response = response.string())
            Log.d("MYSELF - EPISODEINFO", episodeInfo.vidcdnUrl.toString()!!)
            val response = episodeRepository.fetchEpisodeStreamingUrl(episodeInfo.vidcdnUrl!!)
//            Log.d("MYSELF-EPISODEINFORESP", response.string())
            val url = HtmlParser.parseStreamingUrl(response.string())
            val content = _content.value
            content?.url = url
            _content.value = content
            saveContent(content!!)

//            episodeInfo.vidcdnUrl?.let {
//                val res = episodeRepository.fetchM3u8Url(episodeInfo.vidcdnUrl!!)
//                getEpisodeUrlObserver(res, C.TYPE_M3U8_URL)
//            }
            val watchedEpisode =
                episodeRepository.fetchWatchedDuration(_content.value?.episodeUrl.hashCode())
            _content.value?.watchedDuration = watchedEpisode?.watchedDuration ?: 0
            _content.value?.previousEpisodeUrl = episodeInfo.previousEpisodeUrl
            _content.value?.nextEpisodeUrl = episodeInfo.nextEpisodeUrl
        } else if (type == C.TYPE_M3U8_URL) {
            val m3u8Url = HtmlParser.parseM3U8Url(response = response.string())
            val content = _content.value
            content?.url = m3u8Url
            _content.value = content
            saveContent(content!!)
//            updateLoading(false)
        } else if (type == C.TYPE_STREAMING_URL) {
            Log.d("MYSELF-STREEEINFORESP", response.string())

//            updateLoading(false)
        }
    }

    fun saveContent(content: Content) {
        if (!content.url.isNullOrEmpty()) {
            episodeRepository.saveContent(content)
        }
    }
}