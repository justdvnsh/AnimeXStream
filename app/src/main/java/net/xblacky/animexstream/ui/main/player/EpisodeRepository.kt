package net.xblacky.animexstream.ui.main.player

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.xblacky.animexstream.utils.constants.C
import net.xblacky.animexstream.utils.rertofit.NetworkInterface
import net.xblacky.animexstream.utils.rertofit.RetrofitHelper
import net.xblacky.animexstream.utils.model.Content
import net.xblacky.animexstream.utils.model.WatchedEpisode
import net.xblacky.animexstream.utils.realm.InitalizeRealm
import okhttp3.ResponseBody
import retrofit2.Retrofit
import timber.log.Timber
import javax.inject.Inject


class EpisodeRepository @Inject constructor(
    val realm: Realm,
    val fetchEpisodeMediaUrl: NetworkInterface.FetchEpisodeMediaUrl,
    val fetchEpisodemM3u8Url: NetworkInterface.FetchM3u8Url
){


    suspend fun fetchEpisodeMediaUrl(url: String): ResponseBody = fetchEpisodeMediaUrl.get(url)

    suspend fun fetchM3u8Url(url: String): ResponseBody = fetchEpisodemM3u8Url.get(url)


    fun fetchWatchedDuration(id: Int): WatchedEpisode?{
        return realm.where(WatchedEpisode::class.java).equalTo("id", id).findFirst()
    }

    fun fetchContent(episodeUrl: String): Content? {
        try {
            var content: Content? = null
            val result =  realm.where(Content::class.java).equalTo("episodeUrl", episodeUrl).findFirst()
            result?.let {
                content = realm.copyFromRealm(it)
            }
            Timber.e("ID : %s", content?.episodeUrl.hashCode())
            val watchedEpisode = fetchWatchedDuration(content?.episodeUrl.hashCode())
            content?.watchedDuration = watchedEpisode?.watchedDuration?.let { it
            } ?: 0
            return content


        } catch (ignored: Exception) {
        }
        return null
    }


    fun saveContent(content: Content) = CoroutineScope(Dispatchers.IO).launch{
        try {
            content.insertionTime = System.currentTimeMillis()
            realm.executeTransactionAsync { realm1: Realm ->
                realm1.insertOrUpdate(content)
            }

            val progressPercentage: Long = ((content.watchedDuration.toDouble()/(content.duration).toDouble()) * 100).toLong()
            val watchedEpisode = WatchedEpisode(
                id = content.episodeUrl.hashCode(),
                watchedDuration = content.watchedDuration,
                watchedPercentage = progressPercentage,
                animeName = content.animeName

            )
            realm.executeTransactionAsync {
                it.insertOrUpdate(watchedEpisode)
            }
        } catch (ignored: Exception) {
        }
    }


    fun clearContent() = CoroutineScope(Dispatchers.IO).launch{
            realm.executeTransactionAsync {
                val results = it.where(Content::class.java).lessThanOrEqualTo("insertionTime", System.currentTimeMillis() - C.MAX_TIME_M3U8_URL).findAll()
                results.deleteAllFromRealm()
            }
    }
}