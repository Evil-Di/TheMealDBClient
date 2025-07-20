package ru.otusevildi.themealdbclient.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.mapNotNull

private const val DATASTORE_FILE = "datastore_file"

private const val KEY_FAVORITES_LIST = "key_favorites_list"
private val keyFavoritesList = stringSetPreferencesKey(KEY_FAVORITES_LIST)

private const val KEY_SELECTED_TAB = "key_selected_tab"
private val keySelectedTab = intPreferencesKey(KEY_SELECTED_TAB)

object DataStoreProvider {

    private var dataStore: DataStore<Preferences>? = null

    suspend fun setFavorites(set: Set<String>) {
        dataStore?.edit { preferences ->
            preferences[keyFavoritesList] = set
        }
    }

    fun getFavorites(): Flow<Set<String>> {
        return if (dataStore != null) {
            dataStore!!.data.mapNotNull { preferences ->
                preferences[keyFavoritesList] ?: emptySet()
            }
        } else {
            emptyFlow()
        }
    }

    suspend fun setSelectedTab(index: Int) {
        dataStore?.edit { preferences ->
            preferences[keySelectedTab] = index
        }
    }

    fun getSelectedTab(): Flow<Int> {
        return if (dataStore != null) {
            dataStore!!.data.mapNotNull { preferences ->
                preferences[keySelectedTab] ?: TAB_NOT_SELECTED
            }
        } else {
            emptyFlow()
        }
    }

    suspend fun clear() {
        dataStore?.edit {
            it.clear()
        }
    }

    fun init(context: Context) {
        dataStore = context.settingsDataStore
    }
}

val Context.settingsDataStore by preferencesDataStore(DATASTORE_FILE)
