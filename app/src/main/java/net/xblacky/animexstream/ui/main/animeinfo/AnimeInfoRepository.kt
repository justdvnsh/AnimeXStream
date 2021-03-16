package net.xblacky.animexstream.ui.main.animeinfo

import androidx.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.xblacky.animexstream.utils.model.FavouriteModel
import net.xblacky.animexstream.utils.realm.InitalizeRealm
import net.xblacky.animexstream.utils.rertofit.NetworkInterface
import net.xblacky.animexstream.utils.rertofit.RetrofitHelper
import okhttp3.ResponseBody
import retrofit2.Retrofit
import javax.inject.Inject

class AnimeInfoRepository @Inject constructor(
    val realm: Realm,
    val fetchAnimeInfo: NetworkInterface.FetchAnimeInfo,
    val fetchEpisodeList: NetworkInterface.FetchEpisodeList
){

    suspend fun fetchAnimeInfo(categoryUrl: String): ResponseBody = fetchAnimeInfo.get(categoryUrl)

    suspend fun fetchEpisodeList(id: String, endEpisode: String, alias: String): ResponseBody =
        fetchEpisodeList.get(id = id, endEpisode = endEpisode, alias = alias)

    fun isFavourite(id: String): Boolean {
        val result = realm.where(FavouriteModel::class.java).equalTo("ID", id).findFirst()
        result?.let {
            return true
        } ?: return false
    }

    fun addToFavourite(favouriteModel: FavouriteModel) = CoroutineScope(Dispatchers.IO).launch {
        realm.executeTransaction {
            it.insertOrUpdate(favouriteModel)
        }
    }

    fun removeFromFavourite(id: String) = CoroutineScope(Dispatchers.IO).launch {
        realm.executeTransaction {
            it.where(FavouriteModel::class.java).equalTo("ID", id).findAll().deleteAllFromRealm()
        }

    }

}