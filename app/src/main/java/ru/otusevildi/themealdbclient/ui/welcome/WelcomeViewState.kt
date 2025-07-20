package ru.otusevildi.themealdbclient.ui.welcome

sealed class WelcomeViewState {
    data object Loading : WelcomeViewState()
    data class Done(val imgLink: String? = null, val error: Exception? = null) : WelcomeViewState()
    data object TimedOut : WelcomeViewState()
}