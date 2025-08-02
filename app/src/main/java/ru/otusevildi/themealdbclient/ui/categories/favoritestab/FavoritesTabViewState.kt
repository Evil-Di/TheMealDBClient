package ru.otusevildi.themealdbclient.ui.categories.favoritestab

import ru.otusevildi.themealdbclient.data.Recipe

sealed class FavoritesTabViewState {
    data object Loading : FavoritesTabViewState()
    data class SetData(val recipes: List<Recipe>) : FavoritesTabViewState()
}
