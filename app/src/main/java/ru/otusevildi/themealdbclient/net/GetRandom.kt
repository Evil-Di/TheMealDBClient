package ru.otusevildi.themealdbclient.net

import ru.otusevildi.themealdbclient.data.Recipes
import java.io.IOException
import javax.inject.Inject

interface GetRandom {
    suspend operator fun invoke(): Recipes

    class Impl @Inject constructor(private val api: TheMealDbApi) : GetRandom {
        override suspend fun invoke(): Recipes {
            val response = api.getRandom()
            if (!response.isSuccessful) {
                throw IOException("Unexpected code $response")
            }
            return response.body() ?: throw IOException("Empty body $response")
        }
    }
}