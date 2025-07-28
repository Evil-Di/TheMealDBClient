package ru.otusevildi.themealdbclient.ui.recipes

import ru.otusevildi.themealdbclient.data.RecipeShort

sealed class RecipeListViewState {
    data object Loading : RecipeListViewState()
    data class SetData(val list: List<RecipeShort>) : RecipeListViewState()
}
