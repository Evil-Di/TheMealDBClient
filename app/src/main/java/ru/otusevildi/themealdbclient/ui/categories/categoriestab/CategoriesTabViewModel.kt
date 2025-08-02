package ru.otusevildi.themealdbclient.ui.categories.categoriestab

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.otusevildi.themealdbclient.data.DataCache
import javax.inject.Inject

@HiltViewModel
class CategoriesTabViewModel @Inject constructor(private val dataCache: DataCache) : ViewModel() {
    private var _state = MutableLiveData<CategoriesTabViewState>(CategoriesTabViewState.Loading)
    val state: LiveData<CategoriesTabViewState> get() = _state
    var pageSelected = 0

    init {
        viewModelScope.launch {
            dataCache.categories.collect {
                _state.value = CategoriesTabViewState.SetData(it)
            }
        }
    }
}
