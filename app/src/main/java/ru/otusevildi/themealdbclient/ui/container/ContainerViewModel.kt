package ru.otusevildi.themealdbclient.ui.container

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.otusevildi.themealdbclient.data.DataCache
import ru.otusevildi.themealdbclient.data.Recipe
import ru.otusevildi.themealdbclient.data.TAB_NOT_SELECTED
import ru.otusevildi.themealdbclient.net.NetService
import javax.inject.Inject

@HiltViewModel
class ContainerViewModel @Inject constructor(private val dataCache: DataCache,
                                             private val service: NetService): ViewModel() {

    private val _state = MutableStateFlow<ContainerViewState>(ContainerViewState.None)
    val state: StateFlow<ContainerViewState> get() = _state

    private var _suggestions = MutableStateFlow<List<Recipe>>(emptyList())
    val suggestions: StateFlow<List<Recipe>> get() = _suggestions

    //val recent
    //val suggestions

    init {
        viewModelScope.launch {
            dataCache.selectedTab.collect {
                if (it != TAB_NOT_SELECTED) {
                    _state.value = ContainerViewState.SelectTab(it)
                }
            }
        }
        viewModelScope.launch {
            dataCache.suggestions.collect {
                _suggestions.value = it
            }
        }
        viewModelScope.launch {
            dataCache.init(viewModelScope, service)
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