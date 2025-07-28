package ru.otusevildi.themealdbclient.ui.categories

import ru.otusevildi.themealdbclient.data.Recipe
import ru.otusevildi.themealdbclient.data.RecipeCategory

sealed class CategoriesViewState {
    data object Loading : CategoriesViewState()
    //data class SetData(val categories: List<RecipeCategory>, val recipes: List<Recipe>) : CategoriesViewState()
    //data class SelectTab(val tab: Int) : CategoriesViewState()
    data class SetData(val categories: List<RecipeCategory>, val recipes: List<Recipe>, val tab: Int) : CategoriesViewState()
}
