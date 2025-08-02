package ru.otusevildi.themealdbclient.ui.categories

sealed class CategoriesHostViewState {
    data object Loading : CategoriesHostViewState()
    data class SelectCategoriesTab(val hideFavoritesTab: Boolean) : CategoriesHostViewState()
    data object SelectFavoritesTab : CategoriesHostViewState()
}
