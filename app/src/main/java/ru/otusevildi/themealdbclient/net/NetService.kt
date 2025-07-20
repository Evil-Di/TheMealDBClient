package ru.otusevildi.themealdbclient.net

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import ru.otusevildi.themealdbclient.data.RecipeCategories
import ru.otusevildi.themealdbclient.data.Recipes
import ru.otusevildi.themealdbclient.data.RecipesShort
import javax.inject.Inject

interface NetService {
    suspend fun getCategories(): RecipeCategories
    suspend fun getRandom(): Recipes
    suspend fun getByName(name: String): Recipes
    suspend fun getById(id: String): Recipes
    suspend fun getByCategory(name: String): RecipesShort

    class Impl @Inject constructor(
        private val getCategoriesCmd: GetCategories,
        private val getRandomCmd: GetRandom,
        private val getByNameCmd: GetByName,
        private val getByIdCmd: GetById,
        private val getByCategoryCmd: GetByCategory
    ) : NetService {
        override suspend fun getCategories(): RecipeCategories = getCategoriesCmd()
        override suspend fun getRandom(): Recipes = getRandomCmd()
        override suspend fun getByName(name: String): Recipes = getByNameCmd(name)
        override suspend fun getById(id: String): Recipes = getByIdCmd(id)
        override suspend fun getByCategory(name: String): RecipesShort = getByCategoryCmd(name)
    }
}

@Module
@InstallIn(ViewModelComponent::class)
abstract class NetServiceProvider {
    @Binds
    abstract fun netService(impl: NetService.Impl) : NetService
    @Binds
    abstract fun getCategories(impl: GetCategories.Impl) : GetCategories
    @Binds
    abstract fun getRandom(impl: GetRandom.Impl) : GetRandom
    @Binds
    abstract fun getByName(impl: GetByName.Impl) : GetByName
    @Binds
    abstract fun getById(impl: GetById.Impl) : GetById
    @Binds
    abstract fun getByCategory(impl: GetByCategory.Impl) : GetByCategory
}
