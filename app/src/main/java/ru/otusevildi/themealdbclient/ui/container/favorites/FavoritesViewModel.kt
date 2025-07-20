package ru.otusevildi.themealdbclient.ui.container.favorites

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.otusevildi.themealdbclient.data.DataCache
import ru.otusevildi.themealdbclient.data.Recipe
import ru.otusevildi.themealdbclient.net.NetService
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val dataCache: DataCache,
) : ViewModel() {
    var recentList = MutableLiveData<List<Recipe>>(null)

    init {
        viewModelScope.launch {
            dataCache.favorites.collect {
                val d = it
            }

        }
    }


    /*fun getRandom() {
        viewModelScope.launch {
            recentList.value = service.getRandom().list
            Log.i("TAG", recentList.toString())
        }
    }*/

    /*fun getByName() {
        dataCache.getFavorites()
        viewModelScope.launch {
            recentList.value = service.getByName("a").list
            Log.i("TAG", recentList.toString())
        }
    }*/

    /*
    fun getById() {
        viewModelScope.launch {
            recentList.value = service.getById("52772").list
            Log.i("TAG", recentList.toString())
        }
    }*/
}