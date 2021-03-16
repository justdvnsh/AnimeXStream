package net.xblacky.animexstream.ui.main.search

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import kotlinx.coroutines.launch
import net.xblacky.animexstream.utils.CommonViewModel
import net.xblacky.animexstream.utils.CommonViewModel2
import net.xblacky.animexstream.utils.constants.C
import net.xblacky.animexstream.utils.model.AnimeMetaModel
import net.xblacky.animexstream.utils.parser.HtmlParser
import okhttp3.ResponseBody
import javax.inject.Inject

class SearchViewModel @ViewModelInject constructor(
    private val searchRepository: SearchRepository
) : CommonViewModel2() {

    private var _searchList: MutableLiveData<ArrayList<AnimeMetaModel>> = MutableLiveData()
    private var pageNumber: Int = 1
    private lateinit var keyword: String
    private var _canNextPageLoaded = true
    var searchList: LiveData<ArrayList<AnimeMetaModel>> = _searchList

    fun fetchSearchList(keyword: String) = viewModelScope.launch {
        pageNumber = 1
        this@SearchViewModel.keyword = keyword
        val list = _searchList.value
        list?.clear()
        _searchList.value = list
        if (!super.isLoading()) {
            val resp = searchRepository.fetchSearchList(
                keyword,
                pageNumber
            )
            getSearchObserver(resp, C.TYPE_SEARCH_NEW)
            updateLoadingState(loading = Loading.LOADING, e = null, isListEmpty = isListEmpty())
        }
    }

    fun fetchNextPage() = viewModelScope.launch {
        if (_canNextPageLoaded && !super.isLoading()) {
            val resp = searchRepository.fetchSearchList(
                keyword,
                pageNumber
            )
            getSearchObserver(resp, C.TYPE_SEARCH_UPDATE)
            updateLoadingState(loading = Loading.LOADING, e = null, isListEmpty = isListEmpty())
        }


    }

    private fun getSearchObserver(response: ResponseBody, searchType: Int) = viewModelScope.launch{
        val list =
            HtmlParser.parseMovie(response = response.string(), typeValue = C.TYPE_DEFAULT)
        if (list.isNullOrEmpty() || list.size < 20) {
            _canNextPageLoaded = false
        }
        if (searchType == C.TYPE_SEARCH_NEW) {
            _searchList.value = list
        } else if (searchType == C.TYPE_SEARCH_UPDATE) {
            val updatedList = _searchList.value
            updatedList?.addAll(list)
            _searchList.value = updatedList
        }
        pageNumber++
    }


    private fun isListEmpty(): Boolean{
        return _searchList.value.isNullOrEmpty()
    }

}