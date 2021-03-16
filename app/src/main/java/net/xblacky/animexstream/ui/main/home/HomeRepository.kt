package net.xblacky.animexstream.ui.main.home

import androidx.lifecycle.LifecycleCoroutineScope
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import io.realm.Sort
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.xblacky.animexstream.utils.constants.C
import net.xblacky.animexstream.utils.rertofit.NetworkInterface
import net.xblacky.animexstream.utils.rertofit.RetrofitHelper
import net.xblacky.animexstream.utils.model.AnimeMetaModel
import net.xblacky.animexstream.utils.realm.InitalizeRealm
import okhttp3.ResponseBody
import retrofit2.Retrofit
import javax.inject.Inject

class HomeRepository @Inject constructor(
    private val realm: Realm,
    private val retrofit: Retrofit,
    private val fetchRecentSubOrDub: NetworkInterface.FetchRecentSubOrDub,
    private val fetchPopular: NetworkInterface.FetchPopularFromAjax,
    private val fetchMovies: NetworkInterface.FetchMovies,
    private val fetchNewestSeason: NetworkInterface.FetchNewestSeason
) {

    suspend fun fetchRecentSubOrDub(page: Int, type: Int): ResponseBody =
        retrofit.create(NetworkInterface.FetchRecentSubOrDub::class.java).get(page, type)

    suspend fun fetchPopularFromAjax(page: Int): ResponseBody = fetchPopular.get(page)

    suspend fun fetchMovies(page: Int): ResponseBody = fetchMovies.get(page)

    suspend fun fetchNewestAnime(page: Int): ResponseBody = fetchNewestSeason.get(page)

    fun addDataInRealm(animeList: ArrayList<AnimeMetaModel>) =
        CoroutineScope(Dispatchers.IO).launch {
            try {
                realm.executeTransaction { realm1: Realm ->
                    realm1.insertOrUpdate(animeList)
                }
            } catch (ignored: Exception) {
            }
        }

    fun removeFromRealm() = CoroutineScope(Dispatchers.IO).launch {
        realm.executeTransaction {
            val results = it.where(AnimeMetaModel::class.java)
                .lessThanOrEqualTo("timestamp", System.currentTimeMillis() - C.MAX_TIME_FOR_ANIME)
                .findAll()
            results.deleteAllFromRealm()
        }
    }

    fun fetchFromRealm(typeValue: Int): ArrayList<AnimeMetaModel> {
        val list: ArrayList<AnimeMetaModel> = ArrayList()
        try {
            val results =
                realm.where(AnimeMetaModel::class.java)?.equalTo("typeValue", typeValue)
                    ?.sort("insertionOrder", Sort.ASCENDING)?.findAll()
            results?.let {
                list.addAll(it)
            }


        } catch (ignored: Exception) {
        }
        return list
    }


}