package com.scrap2025.scrap2025.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.scrap2025.scrap2025.model.SortDirection
import com.scrap2025.scrap2025.model.SortType
import com.scrap2025.scrap2025.model.ViewMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "scrap_preferences")

class PreferencesManager(private val context: Context) {
    private val dataStore: DataStore<Preferences> = context.dataStore

    companion object {
        private val SORT_TYPE_KEY = stringPreferencesKey("sort_type")
        private val SORT_DIRECTION_KEY = stringPreferencesKey("sort_direction")
        private val VIEW_MODE_KEY = stringPreferencesKey("view_mode")
    }

    // 정렬 타입 Flow (기본값: DATE)
    val sortType: Flow<SortType> = dataStore.data.map { preferences ->
        val sortTypeString = preferences[SORT_TYPE_KEY] ?: SortType.DATE.name
        try {
            SortType.valueOf(sortTypeString)
        } catch (e: IllegalArgumentException) {
            SortType.DATE
        }
    }

    // 정렬 방향 Flow (기본값: ASCENDING)
    val sortDirection: Flow<SortDirection> = dataStore.data.map { preferences ->
        val sortDirectionString = preferences[SORT_DIRECTION_KEY] ?: SortDirection.ASCENDING.name
        try {
            SortDirection.valueOf(sortDirectionString)
        } catch (e: IllegalArgumentException) {
            SortDirection.ASCENDING
        }
    }

    // 뷰모드 Flow (기본값: LIST)
    val viewMode: Flow<ViewMode> = dataStore.data.map { preferences ->
        val viewModeString = preferences[VIEW_MODE_KEY] ?: ViewMode.LIST.name
        try {
            ViewMode.valueOf(viewModeString)
        } catch (e: IllegalArgumentException) {
            ViewMode.LIST
        }
    }

    // 정렬 타입 저장
    suspend fun setSortType(sortType: SortType) {
        dataStore.edit { preferences ->
            preferences[SORT_TYPE_KEY] = sortType.name
        }
    }

    // 정렬 방향 저장
    suspend fun setSortDirection(sortDirection: SortDirection) {
        dataStore.edit { preferences ->
            preferences[SORT_DIRECTION_KEY] = sortDirection.name
        }
    }

    // 뷰모드 저장
    suspend fun setViewMode(viewMode: ViewMode) {
        dataStore.edit { preferences ->
            preferences[VIEW_MODE_KEY] = viewMode.name
        }
    }
}
