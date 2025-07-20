package ru.otusevildi.themealdbclient.ui.welcome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.otusevildi.themealdbclient.data.Recipes
import ru.otusevildi.themealdbclient.net.NetService
import javax.inject.Inject

@HiltViewModel
class WelcomeViewModel @Inject constructor(private val service: NetService): ViewModel() {

    private val _state = MutableStateFlow<WelcomeViewState>(WelcomeViewState.Done())
    val state: StateFlow<WelcomeViewState> get() = _state

    init {
        viewModelScope.launch {
            runDataFlow().collect {
                _state.value = it
            }
        }
    }

    private fun runDataFlow(): Flow<WelcomeViewState> = flow {
        try {
            emit(WelcomeViewState.Loading)
            val recipes: Recipes = withContext(Dispatchers.IO) {
                service.getRandom()
            }
            emit(WelcomeViewState.Done(recipes.list[0].tLink))
            delay(10000) //время отображения приветствия
            emit(WelcomeViewState.TimedOut)
        }
        catch (exception: Exception) {
            emit(WelcomeViewState.Done(error = exception))
        }
    }
}