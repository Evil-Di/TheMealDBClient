package ru.otusevildi.themealdbclient.net

import ru.otusevildi.themealdbclient.data.RecipeCategories
import java.io.IOException
import javax.inject.Inject

interface GetCategories {
    suspend operator fun invoke(): RecipeCategories

    class Impl @Inject constructor(private val api: TheMealDbApi) : GetCategories {
        override suspend fun invoke(): RecipeCategories {
            val response = api.getCategories()
            if (!response.isSuccessful) {
                throw IOException("Unexpected code $response")
            }
            return response.body() ?: throw IOException("Empty body $response")
        }
    }
}