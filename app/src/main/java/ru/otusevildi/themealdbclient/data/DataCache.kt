package ru.otusevildi.themealdbclient.data

import android.util.Log
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.otusevildi.themealdbclient.net.NetService
import javax.inject.Inject
import javax.inject.Singleton

interface DataCache {

    val selectedTab: Flow<Int>
    val categories: Flow<List<RecipeCategory>>
    val recipesByCategory: Flow<List<RecipeShort>>
    val recipeById: Flow<Recipe?>
    val favorites: Flow<List<Recipe>>
    fun init(scope: CoroutineScope, service: NetService)
    fun setSelectedTab(scope: CoroutineScope, index: Int)
    fun selectCategory(scope: CoroutineScope, category: String)
    fun selectRecipe(scope: CoroutineScope, id: String)
    fun addFavorite(scope: CoroutineScope, f: String)
    fun removeFavorite(scope: CoroutineScope, f: String)
    fun clear(scope: CoroutineScope)

    class Impl @Inject constructor(private val dataStoreProvider: DataStoreProvider

        ): DataCache {

        private var service: NetService? = null

        private val categoryRecipes: MutableMap<String, List<RecipeShort>> =
            emptyMap<String, List<RecipeShort>>().toMutableMap()

        private var favoriteIds: MutableSet<String> = emptySet<String>().toMutableSet()

        private var _selectedTab = MutableStateFlow(TAB_NOT_SELECTED)
        override val selectedTab: StateFlow<Int> get() = _selectedTab

        private var _categories = MutableStateFlow<List<RecipeCategory>>(emptyList())
        override val categories: StateFlow<List<RecipeCategory>> get() = _categories

        private var _recipesByCategory = MutableStateFlow<List<RecipeShort>>(emptyList())
        override val recipesByCategory: StateFlow<List<RecipeShort>> get() = _recipesByCategory

        private var _recipeById = MutableStateFlow<Recipe?>(null)
        override val recipeById: StateFlow<Recipe?> get() = _recipeById

        private var _favorites = MutableStateFlow<List<Recipe>>(emptyList())
        override val favorites: StateFlow<List<Recipe>> get() = _favorites

        override fun init(scope: CoroutineScope, service: NetService) {
            this.service = service
            scope.launch {
                dataStoreProvider.getSelectedTab().collect {
                    _selectedTab.value = it
                }
            }
            scope.launch {
                dataStoreProvider.getFavorites().collect { set ->
                    if (set != favoriteIds) {
                        favoriteIds = set.toMutableSet()
                        val resultList = mutableListOf<Deferred<Recipe>>()
                        Log.i("favorites", "get ${favoriteIds.size} favorites")
                        favoriteIds.forEach {
                            resultList.add(async(Dispatchers.IO) {
                                Log.i("get favorite", it)
                                service.getById(it).list[0]
                            })
                        }
                        _favorites.value = resultList.awaitAll()
                    }
                }
            }
            scope.launch {
                _categories.value = service.getCategories().list
            }
        }

        override fun setSelectedTab(scope: CoroutineScope, index: Int) {
            if (_selectedTab.value != index) {
                _selectedTab.value = index
                scope.launch {
                    dataStoreProvider.setSelectedTab(index)
                }
            }
        }

        override fun selectRecipe(scope: CoroutineScope, id: String) {
            if (_recipeById.value == null || _recipeById.value?.id != id) {
                scope.launch {
                    val recipe: List<Recipe?> = withContext(Dispatchers.IO) {
                        service?.getById(id)?.list!!
                    }
                    _recipeById.value = recipe[0]
                }
            }
        }

        override fun selectCategory(scope: CoroutineScope, category: String) {
            if (categoryRecipes[category] == null) {
                scope.launch {
                    val list: List<RecipeShort> = withContext(Dispatchers.IO) {
                        service?.getByCategory(category)?.list ?: emptyList()
                    }
                    categoryRecipes[category] = list
                    _recipesByCategory.value = list
                }
            }
            else _recipesByCategory.value = categoryRecipes[category]!!
        }

        override fun addFavorite(scope: CoroutineScope, f: String) {
            if (!favoriteIds.contains(f)) {
                if (favoriteIds.add(f)) {
                    Log.i("favorites", "add $f")
                    scope.launch {
                        dataStoreProvider.setFavorites(favoriteIds)
                    }
                }
            }
        }

        override fun removeFavorite(scope: CoroutineScope, f: String) {
            if (favoriteIds.contains(f)) {
                if (favoriteIds.remove(f)) {
                    Log.i("favorite", "remove $f")
                    scope.launch {
                        dataStoreProvider.setFavorites(favoriteIds.toSet())
                    }
                }
            }
        }

        override fun clear(scope: CoroutineScope) {
            favoriteIds.clear()
            scope.launch {
                dataStoreProvider.clear()
            }
        }
    }
}

@Module
@InstallIn(SingletonComponent::class)
object DataCacheModule {
    @Provides
    @Singleton
    fun dataCache(): DataCache = DataCache.Impl(DataStoreProvider)
}
