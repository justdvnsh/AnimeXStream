package net.xblacky.animexstream.ui.main.search

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import net.xblacky.animexstream.utils.rertofit.NetworkInterface
import net.xblacky.animexstream.utils.rertofit.RetrofitHelper
import okhttp3.Response
import okhttp3.ResponseBody
import javax.inject.Inject

class SearchRepository @Inject constructor(
    private val fetchSearchData: NetworkInterface.FetchSearchData
){

    suspend fun fetchSearchList(keyWord: String, pageNumber: Int): ResponseBody =
        fetchSearchData.get(keyWord, pageNumber)

}