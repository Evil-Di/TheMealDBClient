
package ru.otusevildi.themealdbclient.net

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import ru.otusevildi.themealdbclient.BuildConfig
import javax.inject.Singleton

interface SessionManager {
    fun getToken(): String

    class Impl @Inject constructor() : SessionManager {
        override fun getToken(): String = BuildConfig.theMealDbApiKey
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class sessionManagerProvider {
    @Binds
    @Singleton
    abstract fun sessionManager(impl: SessionManager.Impl): SessionManager
}
