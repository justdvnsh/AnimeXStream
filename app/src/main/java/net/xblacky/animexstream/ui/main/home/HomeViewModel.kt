package net.xblacky.animexstream.ui.main.home

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.realm.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.xblacky.animexstream.utils.Utils
import net.xblacky.animexstream.utils.constants.C
import net.xblacky.animexstream.utils.model.AnimeMetaModel
import net.xblacky.animexstream.utils.model.HomeScreenModel
import net.xblacky.animexstream.utils.model.UpdateModel
import net.xblacky.animexstream.utils.parser.HtmlParser
import net.xblacky.animexstream.utils.realm.InitalizeRealm
import okhttp3.ResponseBody
import timber.log.Timber
import java.lang.IndexOutOfBoundsException
import javax.inject.Inject
import kotlin.collections.ArrayList

class HomeViewModel @ViewModelInject constructor(
    private val homeRepository: HomeRepository,
    private val realm: Realm
) : ViewModel(){

    private var _animeList: MutableLiveData<ArrayList<HomeScreenModel>> = MutableLiveData(makeEmptyArrayList())
    var animeList : LiveData<ArrayList<HomeScreenModel>> = _animeList
    private var _updateModel: MutableLiveData<UpdateModel> = MutableLiveData()
    var updateModel : LiveData<UpdateModel> = _updateModel
    private val realmListenerList = ArrayList<RealmResults<AnimeMetaModel>>()
    private lateinit var database: DatabaseReference

    init {
        fetchHomeList()
        queryDB()
    }

    private fun fetchHomeList(){
        fetchRecentSub()
        fetchRecentDub()
        fetchPopular()
        fetchNewSeason()
        fetchMovies()
    }

    private fun queryDB() = CoroutineScope(Dispatchers.IO).launch {
        database = Firebase.database.reference
        val query: Query = database.child("appdata")
        query.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(ignored: DatabaseError) {
                Timber.e(ignored.message)
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                Timber.e(snapshot.toString())
                _updateModel.value = UpdateModel(
                    versionCode = snapshot.child("versionCode").value as Long,
                    whatsNew = snapshot.child("whatsNew").value.toString()
                )
            }

        })
    }
    private fun getHomeListObserver(response: ResponseBody, typeValue: Int) = viewModelScope.launch {
        val list = parseList(response = response.string(), typeValue = typeValue)
        homeRepository.addDataInRealm(list)
    }

    private fun updateError(e: Throwable){
        var isListEmpty = true
        animeList.value?.forEach {
            if(!it.animeList.isNullOrEmpty()){
                isListEmpty = false
            }
        }
//        super.updateErrorModel(true , e , isListEmpty)

    }



    private fun parseList(response: String, typeValue: Int): ArrayList<AnimeMetaModel>{
        return when(typeValue){
            C.TYPE_RECENT_DUB -> HtmlParser.parseRecentSubOrDub(response,typeValue)
            C.TYPE_RECENT_SUB -> HtmlParser.parseRecentSubOrDub(response,typeValue)
            C.TYPE_POPULAR_ANIME -> HtmlParser.parsePopular(response,typeValue)
            C.TYPE_MOVIE -> HtmlParser.parseMovie(response,typeValue)
            C.TYPE_NEW_SEASON ->HtmlParser.parseMovie(response,typeValue)
            else -> ArrayList()
        }
    }

    private fun updateList(list: ArrayList<AnimeMetaModel>, typeValue: Int){
        val homeScreenModel = HomeScreenModel(
            typeValue = typeValue,
            type = Utils.getTypeName(typeValue),
            animeList = list
        )

        val newList = animeList.value!!
            try{
//               val preHomeScreenModel = newList[getPositionByType(typeValue)]
//                if(preHomeScreenModel.typeValue == homeScreenModel.typeValue){
                    newList[getPositionByType(typeValue)] = homeScreenModel
//                }else{
//                    newList.add(getPositionByType(typeValue),homeScreenModel)
//                }

            }catch (iobe: IndexOutOfBoundsException){
//                newList.add(getPositionByType(typeValue),homeScreenModel)
            }


//        newList.sortedByDescending {
//            Utils.getPositionByType(it.typeValue)
//        }
        _animeList.value = newList
    }

    private fun addRealmListener(typeValue: Int){
        realm.use {
            val results = it.where(AnimeMetaModel::class.java).equalTo("typeValue",typeValue).sort("insertionOrder", Sort.ASCENDING)
                .findAll()

            results.addChangeListener { newResult :RealmResults<AnimeMetaModel> , _ ->
                    val newAnimeList = (it.copyFromRealm(newResult) as ArrayList<AnimeMetaModel>)
                    updateList(newAnimeList, typeValue)
            }
            realmListenerList.add(results)
        }
    }



    private fun getPositionByType(typeValue: Int): Int{
        val size = animeList.value!!.size
        return when(typeValue){
            C.TYPE_RECENT_SUB-> if(size >= C.RECENT_SUB_POSITION) C.RECENT_SUB_POSITION else size
            C.TYPE_RECENT_DUB-> if(size >= C.RECENT_DUB_POSITION) C.RECENT_DUB_POSITION else size
            C.TYPE_POPULAR_ANIME -> if(size >= C.POPULAR_POSITION) C.POPULAR_POSITION else size
            C.TYPE_MOVIE ->if(size >= C.MOVIE_POSITION) C.MOVIE_POSITION else size
            C.TYPE_NEW_SEASON ->if(size >= C.NEWEST_SEASON_POSITION) C.NEWEST_SEASON_POSITION else size
            else->size
        }
    }


    private fun makeEmptyArrayList(): ArrayList<HomeScreenModel>{
        var i = 1
        val arrayList: ArrayList<HomeScreenModel> = ArrayList()
        while (i<=6){
            arrayList.add(
                HomeScreenModel(
                    typeValue = i
                )
            )
            i++
        }
        return arrayList
    }

    private fun fetchRecentSub() = viewModelScope.launch{

        val list = homeRepository.fetchFromRealm(C.TYPE_RECENT_SUB)
        if(list.size >0){
            updateList(list,C.TYPE_RECENT_SUB)
        }
        val response = homeRepository.fetchRecentSubOrDub(1, C.RECENT_SUB)
        getHomeListObserver(response, C.TYPE_RECENT_SUB)
        addRealmListener(C.TYPE_RECENT_SUB)
    }

    private fun fetchRecentDub() = viewModelScope.launch{
        val list = homeRepository.fetchFromRealm(C.TYPE_RECENT_DUB)
        if(list.size >0){
            updateList(list,C.TYPE_RECENT_DUB)
        }
        val response = (homeRepository.fetchRecentSubOrDub(1, C.RECENT_DUB))
        getHomeListObserver(response, C.TYPE_RECENT_DUB)
        addRealmListener(C.TYPE_RECENT_DUB)
    }

    private fun fetchMovies() = viewModelScope.launch{
        val list = homeRepository.fetchFromRealm(C.TYPE_MOVIE)
        if(list.size>0){
            updateList(list,C.TYPE_MOVIE)
        }
        val response = homeRepository.fetchMovies(1)
        getHomeListObserver(response, C.TYPE_MOVIE)
        addRealmListener(C.TYPE_MOVIE)
    }

    private fun fetchPopular() = viewModelScope.launch{
        val list = homeRepository.fetchFromRealm(C.TYPE_POPULAR_ANIME)
        if(list.size>0){
            updateList(list,C.TYPE_POPULAR_ANIME)
        }
        val response = homeRepository.fetchPopularFromAjax(1)
        getHomeListObserver(response, C.TYPE_POPULAR_ANIME)
        addRealmListener(C.TYPE_POPULAR_ANIME)
    }

    private fun fetchNewSeason() = viewModelScope.launch{
        val resultList = homeRepository.fetchFromRealm(C.TYPE_NEW_SEASON)
        if(resultList.size>0){
            updateList(resultList,C.TYPE_NEW_SEASON)
        }
        val response = homeRepository.fetchNewestAnime(1)
        getHomeListObserver(response, C.TYPE_NEW_SEASON)
        addRealmListener(C.TYPE_NEW_SEASON)
    }

    override fun onCleared() {
        homeRepository.removeFromRealm()
        super.onCleared()
    }


}