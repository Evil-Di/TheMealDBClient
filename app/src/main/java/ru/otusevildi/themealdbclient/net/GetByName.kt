package ru.otusevildi.themealdbclient.net

import ru.otusevildi.themealdbclient.data.Recipes
import java.io.IOException
import javax.inject.Inject

interface GetByName {
    suspend operator fun invoke(name: String ): Recipes

    class Impl @Inject constructor(private val api: TheMealDbApi) : GetByName {
        override suspend fun invoke(name: String): Recipes {
            val response = api.getByName(name)
            if (!response.isSuccessful) {
                throw IOException("Unexpected code $response")
            }
            return response.body() ?: throw IOException("Empty body $response")
        }
    }
}