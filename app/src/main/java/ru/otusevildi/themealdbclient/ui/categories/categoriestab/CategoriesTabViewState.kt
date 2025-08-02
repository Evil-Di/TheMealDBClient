package ru.otusevildi.themealdbclient.ui.categories.categoriestab

import ru.otusevildi.themealdbclient.data.RecipeCategory

sealed class CategoriesTabViewState {
    data object Loading : CategoriesTabViewState()
    data class SetData(val categories: List<RecipeCategory>) : CategoriesTabViewState()
}
