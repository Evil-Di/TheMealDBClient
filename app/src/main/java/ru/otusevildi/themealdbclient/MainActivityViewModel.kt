package ru.otusevildi.themealdbclient

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.otusevildi.themealdbclient.data.DataCache
import ru.otusevildi.themealdbclient.data.Recipe
import ru.otusevildi.themealdbclient.net.NetService
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(private val dataCache: DataCache,
                                                private val service: NetService): ViewModel() {

    private var _searchSuggestions = MutableStateFlow<List<Recipe>>(emptyList())
    val searchSuggestions: StateFlow<List<Recipe>> get() = _searchSuggestions

    private var _ingredients = MutableStateFlow<Recipe?>(null)
    val ingredients: StateFlow<Recipe?> get() = _ingredients


    init {
        dataCache.init(viewModelScope, service)

        viewModelScope.launch {
            dataCache.suggestions.collect {
                _searchSuggestions.value = it
            }
        }

        viewModelScope.launch {
            dataCache.recipeById.collect {
                _ingredients.value = it
            }
        }
    }

    fun onSearch(text: String) {
        if (text.isEmpty()) {
            //dataCache.getRecentSearch()
        }
        else {
            dataCache.getSuggestions(viewModelScope, text)
        }
    }
}