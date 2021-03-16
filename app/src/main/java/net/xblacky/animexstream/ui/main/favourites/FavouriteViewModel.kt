package net.xblacky.animexstream.ui.main.favourites

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort
import kotlinx.coroutines.launch
import net.xblacky.animexstream.utils.model.FavouriteModel
import net.xblacky.animexstream.utils.realm.InitalizeRealm
import javax.inject.Inject

class FavouriteViewModel @ViewModelInject constructor(
    private val realm: Realm
) : ViewModel() {

    private lateinit var result: RealmResults<FavouriteModel>
    private val _favouriteLists: MutableLiveData<ArrayList<FavouriteModel>> =
        MutableLiveData(ArrayList())
    var favouriteList: LiveData<ArrayList<FavouriteModel>> = _favouriteLists

    init {
        favouriteListListener()
    }

    private fun favouriteListListener() = viewModelScope.launch{
        result = realm.where(FavouriteModel::class.java).sort("insertionTime", Sort.DESCENDING).findAll()
        _favouriteLists.value = realm.copyFromRealm(result) as ArrayList<FavouriteModel>?
        result.addChangeListener { newList ->
            _favouriteLists.value = realm.copyFromRealm(newList) as ArrayList<FavouriteModel>?
        }
    }
}

