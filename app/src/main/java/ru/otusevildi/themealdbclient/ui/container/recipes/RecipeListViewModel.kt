package ru.otusevildi.themealdbclient.ui.container.recipes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.otusevildi.themealdbclient.data.DataCache
import javax.inject.Inject

@HiltViewModel
class RecipeListViewModel @Inject constructor(private val dataCache: DataCache) : ViewModel(){
    private var _recipes = MutableStateFlow<RecipeListViewState>(RecipeListViewState.Loading)
    val recipes: StateFlow<RecipeListViewState> get() = _recipes

    init {
        viewModelScope.launch {
            dataCache.recipesByCategory.collect {
                _recipes.value = RecipeListViewState.ListReceived(it)
            }
        }
    }

    fun selectList(category: String) {
        viewModelScope.launch {
            dataCache.selectCategory(viewModelScope, category)
        }
    }
}