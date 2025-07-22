package ru.otusevildi.themealdbclient.ui.container.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.otusevildi.themealdbclient.data.DataCache
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(private val dataCache: DataCache) : ViewModel() {
    private var _recipes = MutableStateFlow<FavoritesViewState>(FavoritesViewState.Loading)
    val recipes: StateFlow<FavoritesViewState> get() = _recipes

    init {
        viewModelScope.launch {
            dataCache.favorites.collect {
                _recipes.value = FavoritesViewState.ListReceived(it)
            }
        }
    }




}