package ru.otusevildi.themealdbclient.ui.container.recipes

import ru.otusevildi.themealdbclient.data.RecipeShort

sealed class RecipeListViewState {
    data object Loading : RecipeListViewState()
    data class ListReceived(val list: List<RecipeShort>) : RecipeListViewState()
}
