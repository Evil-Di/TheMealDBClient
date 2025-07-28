package ru.otusevildi.themealdbclient.ui.recipes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.otusevildi.themealdbclient.data.DataCache
import ru.otusevildi.themealdbclient.data.Recipe
import javax.inject.Inject

@HiltViewModel
class RecipeListViewModel @Inject constructor(private val dataCache: DataCache) : ViewModel(){
    private var _state = MutableStateFlow<RecipeListViewState>(RecipeListViewState.Loading)
    val state: StateFlow<RecipeListViewState> get() = _state

    private var _searchSuggestions = MutableStateFlow<List<Recipe>>(emptyList())
    val searchSuggestions: StateFlow<List<Recipe>> get() = _searchSuggestions

    init {
        viewModelScope.launch {
            dataCache.recipesByCategory.collect {
                _state.value = RecipeListViewState.SetData(it)
            }
        }

        viewModelScope.launch {
            dataCache.suggestions.collect {
                _searchSuggestions.value = it
            }
        }
    }

    fun selectList(category: String) {
        viewModelScope.launch {
            dataCache.selectCategory(viewModelScope, category)
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