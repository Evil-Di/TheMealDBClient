package ru.otusevildi.themealdbclient.data

import android.content.ContentValues.TAG
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
    val suggestions: Flow<List<Recipe>>
    fun init(scope: CoroutineScope, service: NetService)
    fun setSelectedTab(scope: CoroutineScope, index: Int)
    fun selectCategory(scope: CoroutineScope, category: String)
    fun selectRecipe(scope: CoroutineScope, id: String)
    fun getSuggestions(scope: CoroutineScope, text: String)
    fun addFavorite(scope: CoroutineScope, f: String)
    fun removeFavorite(scope: CoroutineScope, f: String)
    fun clear(scope: CoroutineScope)

    class Impl @Inject constructor(private val dataStoreProvider: DataStoreProvider

        ): DataCache {

        private var service: NetService? = null

        private val categoryRecipes: MutableMap<String, List<RecipeShort>> =
            emptyMap<String, List<RecipeShort>>().toMutableMap()

        private var favoriteIds: MutableSet<String>? = null//emptySet<String>().toMutableSet()

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

        private var _suggestions = MutableStateFlow<List<Recipe>>(emptyList())
        override val suggestions: StateFlow<List<Recipe>> get() = _suggestions

        override fun init(scope: CoroutineScope, service: NetService) {
            this.service = service
            scope.launch {
                dataStoreProvider.getSelectedTab().collect {
                    _selectedTab.value = it
                }
            }
            scope.launch {
                dataStoreProvider.getFavorites().collect { set ->
                    Log.i(TAG, "new favorites: $set")
                    if (set != favoriteIds) {
                        favoriteIds = set.toMutableSet()
                        val resultList = mutableListOf<Deferred<Recipe>>()
                        val newList = _favorites.value.toMutableList()

                        /* здесь возможен RuntimeException.concurrencyModification
                        newList.forEach {
                            if (it.id != null) {
                                if (!checkInList(it.id, favoriteIds.toList())) {
                                    newList.remove(it)
                                }
                            }
                        } напишем иначе */
                        var i = newList.size-1
                        while(i >= 0) {
                            if (newList[i].id != null) {
                                if (!checkInList(newList[i].id!!, favoriteIds!!.toList())) {
                                    newList.removeAt(i)
                                }
                            }
                            i--
                        }

                        favoriteIds!!.forEach {
                            if (!checkLoaded(it)) {
                                resultList.add(async(Dispatchers.IO) {
                                    service.getById(it).list!![0]
                                })
                            }
                        }
                        newList.addAll(resultList.awaitAll())
                        _favorites.value = newList
                    }
                }
            }
            scope.launch {
                _categories.value = service.getCategories().list!!
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

        override fun getSuggestions(scope: CoroutineScope, text: String) {
            if (text.isNotEmpty()) {
                scope.launch {
                    val list: List<Recipe> = withContext(Dispatchers.IO) {
                        service?.getByName(text)?.list ?: emptyList()
                    }
                    _suggestions.value = list
                }
            }
        }

        override fun addFavorite(scope: CoroutineScope, f: String) {
            Log.i(TAG, "add favorite: $f")
            if (!favoriteIds!!.contains(f)) {
                val newList: MutableSet<String> = favoriteIds!!.toMutableSet()
                if (newList.add(f)) {
                    scope.launch {
                        dataStoreProvider.setFavorites(newList)
                    }
                }
            }
        }

        override fun removeFavorite(scope: CoroutineScope, f: String) {
            Log.i(TAG, "remove favorite: $f")
            if (favoriteIds!!.contains(f)) {
                val newList: MutableSet<String> = favoriteIds!!.toMutableSet()
                if (newList.remove(f)) {
                    scope.launch {
                        dataStoreProvider.setFavorites(newList)
                    }
                }
            }
        }

        override fun clear(scope: CoroutineScope) {
            favoriteIds!!.clear()
            scope.launch {
                dataStoreProvider.clear()
            }
        }

        private fun checkInList(id: String, list: List<String>): Boolean {
            list.forEach {
                if (it == id) {
                    return true
                }
            }
            return false
        }

        private fun checkLoaded(id: String): Boolean {
            _favorites.value.forEach {
                if (it.id == id) {
                    return true
                }
            }
            return false
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
