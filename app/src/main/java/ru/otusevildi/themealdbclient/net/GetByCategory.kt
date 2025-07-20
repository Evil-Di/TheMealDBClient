package ru.otusevildi.themealdbclient.net

import ru.otusevildi.themealdbclient.data.RecipesShort
import java.io.IOException
import javax.inject.Inject

interface GetByCategory {
    suspend operator fun invoke(name: String ): RecipesShort

    class Impl @Inject constructor(private val api: TheMealDbApi) : GetByCategory {
        override suspend fun invoke(name: String): RecipesShort {
            val response = api.getByCategory(name)
            if (!response.isSuccessful) {
                throw IOException("Unexpected code $response")
            }
            return response.body() ?: throw IOException("Empty body $response")
        }
    }
}