package ru.otusevildi.themealdbclient.ui.container.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.otusevildi.themealdbclient.data.DataCache
import javax.inject.Inject

@HiltViewModel
class CategoriesViewModel @Inject constructor(private val dataCache: DataCache) : ViewModel() {
    private var _categories = MutableStateFlow<CategoriesViewState>(CategoriesViewState.None)
    val categories: StateFlow<CategoriesViewState> get() = _categories

    init {
        viewModelScope.launch {
            dataCache.categories.collect {
                _categories.value = CategoriesViewState.ListReceived(it)
            }
        }
    }
}
