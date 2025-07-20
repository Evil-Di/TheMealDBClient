package ru.otusevildi.themealdbclient.net

import ru.otusevildi.themealdbclient.data.Recipes
import java.io.IOException
import javax.inject.Inject

interface GetById {
    suspend operator fun invoke(id: String ): Recipes

    class Impl @Inject constructor(private val api: TheMealDbApi) : GetById {
        override suspend fun invoke(id: String): Recipes {
            val response = api.getById(id)
            if (!response.isSuccessful) {
                throw IOException("Unexpected code $response")
            }
            return response.body() ?: throw IOException("Empty body $response")
        }
    }
}