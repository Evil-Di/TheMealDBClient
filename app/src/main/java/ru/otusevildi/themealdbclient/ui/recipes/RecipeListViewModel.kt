package ru.otusevildi.themealdbclient.ui.recipes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.otusevildi.themealdbclient.data.DataCache
import javax.inject.Inject

@HiltViewModel
class RecipeListViewModel @Inject constructor(private val dataCache: DataCache) : ViewModel(){
    private var _state = MutableLiveData<RecipeListViewState>(RecipeListViewState.Loading)
    val state: LiveData<RecipeListViewState> get() = _state

    init {
        viewModelScope.launch {
            dataCache.recipesByCategory.collect {
                _state.value = RecipeListViewState.SetData(it)
            }
        }
    }

    fun selectList(category: String) {
        viewModelScope.launch {
            dataCache.selectCategory(viewModelScope, category)
        }
    }
}