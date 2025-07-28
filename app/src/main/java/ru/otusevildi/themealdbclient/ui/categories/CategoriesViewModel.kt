package ru.otusevildi.themealdbclient.ui.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.otusevildi.themealdbclient.data.DataCache
import ru.otusevildi.themealdbclient.data.Recipe
import ru.otusevildi.themealdbclient.data.RecipeCategory
import ru.otusevildi.themealdbclient.data.TAB_NOT_SELECTED
import ru.otusevildi.themealdbclient.net.NetService
import javax.inject.Inject

@HiltViewModel
class CategoriesViewModel @Inject constructor(private val dataCache: DataCache,
                                              private val service: NetService
    ) : ViewModel() {
    private var _state = MutableStateFlow<CategoriesViewState>(CategoriesViewState.Loading)
    val state: StateFlow<CategoriesViewState> get() = _state

    private var categories: List<RecipeCategory> = emptyList()
    private var favorites: List<Recipe> = emptyList()
    private var tab: Int = TAB_NOT_SELECTED

    private var _searchSuggestions = MutableStateFlow<List<Recipe>>(emptyList())
    val searchSuggestions: StateFlow<List<Recipe>> get() = _searchSuggestions

    init {
        dataCache.init(viewModelScope, service)

        viewModelScope.launch {
            val categoriesResultDeferred = async(Dispatchers.IO) {
                dataCache.categories.first {
                    categories = it
                    it.isNotEmpty()
                }
            }
            val favoritesResultDeferred = async(Dispatchers.IO) {
                dataCache.favorites.first {
                    favorites = it
                    true
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
                    //_state.value = CategoriesViewState.SetData(categories, favorites)
                    //_state.value = CategoriesViewState.SelectTab(tab)
                    _state.value = CategoriesViewState.SetData(categories, favorites, tab)
                }
            }
            catch (e: Exception) {
                TODO()
            }

            viewModelScope.launch {
                dataCache.categories.collect {
                    if (it != categories) {
                        categories = it
                        //_state.value = CategoriesViewState.SetData(categories, favorites)
                        _state.value = CategoriesViewState.SetData(categories, favorites, tab)
                    }
                }
            }
            viewModelScope.launch {
                dataCache.favorites.collect {
                    if (it != favorites) {
                        favorites = it
                        //_state.value = CategoriesViewState.SetData(categories, favorites)
                        _state.value = CategoriesViewState.SetData(categories, favorites, tab)
                    }
                }
            }
            viewModelScope.launch {
                dataCache.selectedTab.collect {
                    if (it != tab) {
                        tab = it
                        //_state.value = CategoriesViewState.SelectTab(it)
                        _state.value = CategoriesViewState.SetData(categories, favorites, tab)
                    }
                }
            }
        }

        viewModelScope.launch {
            dataCache.suggestions.collect {
                _searchSuggestions.value = it
            }
        }
    }

    fun onTabSelected(index: Int) {
        dataCache.setSelectedTab(viewModelScope, index)
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
