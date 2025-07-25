package ru.otusevildi.themealdbclient.ui.container.recipe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.otusevildi.themealdbclient.data.DataCache
import javax.inject.Inject

@HiltViewModel
class RecipeViewModel @Inject constructor(private val dataCache: DataCache) : ViewModel() {
    private var _recipe = MutableStateFlow<RecipeViewState>(RecipeViewState.Loading)
    val recipe: StateFlow<RecipeViewState> get() = _recipe

    private var _favorite = MutableStateFlow(false)
    val favorite: StateFlow<Boolean> get() = _favorite

    private var recipeId = ""

    init {
        viewModelScope.launch {
            dataCache.recipeById.collect {
                if (it != null) {
                    _recipe.value = RecipeViewState.Received(it)
                }
            }
        }
    }

    fun selectRecipe(id: String) {
        recipeId = id
        viewModelScope.launch {
            dataCache.selectRecipe(viewModelScope, id)
        }

        viewModelScope.launch {
            dataCache.favorites.collect { list ->
                list.forEach {
                    if (it.id == recipeId) {
                        _favorite.value = true
                    }
                }
            }
        }
    }

    fun setFavorite(id: String, set: Boolean) {
        viewModelScope.launch {
            if (set) dataCache.addFavorite(viewModelScope, id)
            else dataCache.removeFavorite(viewModelScope, id)
        }
    }
}