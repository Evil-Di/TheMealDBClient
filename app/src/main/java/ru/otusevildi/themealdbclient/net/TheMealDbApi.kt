package ru.otusevildi.themealdbclient.net

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query
import ru.otusevildi.themealdbclient.data.RecipeCategories
import ru.otusevildi.themealdbclient.data.Recipes
import ru.otusevildi.themealdbclient.data.RecipesShort
import java.util.concurrent.TimeUnit

private const val baseUrl = "https://www.themealdb.com/api/json/v1/"

interface TheMealDbApi {
    @GET("categories.php")
    suspend fun getCategories(): Response<RecipeCategories>

    @GET("random.php")
    suspend fun getRandom(): Response<Recipes>

    @GET("search.php")
    suspend fun getByName(@Query("s") name: String): Response<Recipes>

    @GET("lookup.php")
    suspend fun getById(@Query("i") id: String): Response<Recipes>

    @GET("filter.php")
    suspend fun getByCategory(@Query("c") name: String): Response<RecipesShort>
}

fun buildRetrofit(okHttpClient: OkHttpClient, sessionManager: SessionManager): Retrofit {
    val json = Json /*{ignoreUnknownKeys = true}*/ /*{ coerceInputValues = true }*/
    return Retrofit.Builder()
        .baseUrl(baseUrl + "${sessionManager.getToken()}/")
        .client(okHttpClient)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()
}

@Module
@InstallIn(ViewModelComponent::class)
class NetApiProvider {
    @Provides
    fun okHttp(): OkHttpClient = OkHttpClient.Builder()
        .callTimeout(10, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().apply {
            setLevel(HttpLoggingInterceptor.Level.BASIC)
        })
        .build()

    private fun sessionManager(): SessionManager = SessionManager.Impl()

    @Provides
    fun retrofit(okHttp: OkHttpClient): Retrofit = buildRetrofit(okHttp, sessionManager())

    @Provides
    fun api(retrofit: Retrofit): TheMealDbApi =
        retrofit.create(TheMealDbApi::class.java)
}