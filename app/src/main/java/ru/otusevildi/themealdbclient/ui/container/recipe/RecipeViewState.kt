package ru.otusevildi.themealdbclient.ui.container.recipe

import ru.otusevildi.themealdbclient.data.Recipe

sealed class RecipeViewState {
    data object Loading : RecipeViewState()
    data class Received(val recipe: Recipe?) : RecipeViewState()
}
