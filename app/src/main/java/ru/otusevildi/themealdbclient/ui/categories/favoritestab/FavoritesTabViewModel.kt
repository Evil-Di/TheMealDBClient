package ru.otusevildi.themealdbclient.ui.categories.favoritestab

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.otusevildi.themealdbclient.data.DataCache
import javax.inject.Inject

@HiltViewModel
class FavoritesTabViewModel @Inject constructor(private val dataCache: DataCache) : ViewModel() {
    private var _state = MutableLiveData<FavoritesTabViewState>(FavoritesTabViewState.Loading)
    val state: LiveData<FavoritesTabViewState> get() = _state
    var pageSelected = 0

    init {
        viewModelScope.launch {
            dataCache.favorites.collect {
                _state.value = FavoritesTabViewState.SetData(it ?: emptyList())
            }
        }
    }
}
