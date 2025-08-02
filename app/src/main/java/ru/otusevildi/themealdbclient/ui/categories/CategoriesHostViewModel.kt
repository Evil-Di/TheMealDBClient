package ru.otusevildi.themealdbclient.ui.categories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.otusevildi.themealdbclient.data.DataCache
import ru.otusevildi.themealdbclient.data.Recipe
import ru.otusevildi.themealdbclient.data.RecipeCategory
import ru.otusevildi.themealdbclient.data.TAB_CATEGORIES
import ru.otusevildi.themealdbclient.data.TAB_FAVORITES
import ru.otusevildi.themealdbclient.data.TAB_NOT_SELECTED
import javax.inject.Inject

@HiltViewModel
class CategoriesHostViewModel @Inject constructor(private val dataCache: DataCache) : ViewModel() {
    private var _state = MutableLiveData<CategoriesHostViewState>(CategoriesHostViewState.Loading)
    val state: LiveData<CategoriesHostViewState> get() = _state

    private var categories: List<RecipeCategory> = emptyList()
    var categoriesPage = 0
    private var favorites: List<Recipe> = emptyList()
    var favoritesPage = 0
    private var tab: Int = TAB_NOT_SELECTED

    init {
        viewModelScope.launch {
            val categoriesResultDeferred = async(Dispatchers.IO) {
                dataCache.categories.first {
                    categories = it
                    it.isNotEmpty()
                }
            }
            val favoritesResultDeferred = async(Dispatchers.IO) {
                dataCache.favorites.first {
                    if (it != null) {
                        favorites = it
                    }
                    it != null
                }
            }
            val tabResultDeferred = async(Dispatchers.IO) {
                dataCache.selectedTab.first {
                    tab = it
                    it != TAB_NOT_SELECTED
                }
            }
            try {
                categoriesResultDeferred.await()
                favoritesResultDeferred.await()
                tabResultDeferred.await()
                withContext(Dispatchers.Main) {
                    if (tab == TAB_FAVORITES && favorites.isNotEmpty()) {
                        _state.value = CategoriesHostViewState.SelectFavoritesTab
                    }
                    else {
                        tab == TAB_CATEGORIES
                        _state.value = CategoriesHostViewState.SelectCategoriesTab(favorites.isEmpty())
                    }
                }
            }
            catch (e: Exception) {
                TODO()
            }

            viewModelScope.launch {
                dataCache.favorites.collect {
                    if (it != favorites) {
                        favorites = it ?: emptyList()
                        if (tab == TAB_FAVORITES) {
                            if (favorites.isEmpty()) {
                                dataCache.setSelectedTab(viewModelScope, TAB_CATEGORIES)
                            }
                        }
                        else {
                            _state.value = CategoriesHostViewState.SelectCategoriesTab(favorites.isEmpty())
                        }
                    }
                }
            }

            viewModelScope.launch {
                dataCache.selectedTab.collect {
                    if (it == TAB_FAVORITES) {
                        if (favorites.isEmpty()) {
                            dataCache.setSelectedTab(viewModelScope, TAB_CATEGORIES)
                        }
                        else {
                            if (it != tab) {
                                tab = it
                                _state.value = CategoriesHostViewState.SelectFavoritesTab
                            }
                        }
                    }
                    else {
                        if (it != tab) {
                            tab = it
                            _state.value = CategoriesHostViewState.SelectCategoriesTab(favorites.isEmpty())
                        }
                    }
                }
            }
        }
    }

    fun onTabSelected(index: Int) {
        dataCache.setSelectedTab(viewModelScope, index)
    }
}
