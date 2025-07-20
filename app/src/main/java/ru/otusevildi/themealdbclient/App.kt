package ru.otusevildi.themealdbclient

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import ru.otusevildi.themealdbclient.data.DataStoreProvider

@HiltAndroidApp
class App() : Application() {
    override fun onCreate() {
        super.onCreate()
        DataStoreProvider.init(this)
    }
}
