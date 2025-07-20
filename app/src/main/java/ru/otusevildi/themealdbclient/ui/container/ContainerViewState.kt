package ru.otusevildi.themealdbclient.ui.container

import ru.otusevildi.themealdbclient.data.TAB_CATEGORIES

sealed class ContainerViewState {
    data object None : ContainerViewState()
    data class SelectTab(val tab: Int = TAB_CATEGORIES) : ContainerViewState()
}
