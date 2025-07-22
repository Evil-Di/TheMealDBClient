package ru.otusevildi.themealdbclient.ui.container.favorites

import ru.otusevildi.themealdbclient.data.Recipe

sealed class FavoritesViewState {
    data object Loading : FavoritesViewState()
    data class ListReceived(val list: List<Recipe>) : FavoritesViewState()
}
