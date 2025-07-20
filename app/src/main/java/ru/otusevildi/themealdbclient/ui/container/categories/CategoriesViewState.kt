package ru.otusevildi.themealdbclient.ui.container.categories

import ru.otusevildi.themealdbclient.data.RecipeCategory

sealed class CategoriesViewState {
    data object None : CategoriesViewState()
    data class ListReceived(val list: List<RecipeCategory>) : CategoriesViewState()
}
