package com.scrap2025.scrap2025.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.scrap2025.scrap2025.model.enums.SortDirection
import com.scrap2025.scrap2025.model.enums.SortType
import com.scrap2025.scrap2025.model.enums.ViewMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "scrap_preferences")

class PreferencesManager(context: Context) {
    private val dataStore: DataStore<Preferences> = context.dataStore

    companion object {
        private val SORT_TYPE_KEY = stringPreferencesKey("sort_type")
        private val SORT_DIRECTION_KEY = stringPreferencesKey("sort_direction")
        private val VIEW_MODE_KEY = stringPreferencesKey("view_mode")
        private val IS_DATABASE_INITIALIZED_KEY =
            androidx.datastore.preferences.core.booleanPreferencesKey("is_database_initialized")
    }

    // 정렬 타입 Flow (기본값: DATE)
    val sortType: Flow<SortType> =
        dataStore.data.map { preferences ->
            val sortTypeString = preferences[SORT_TYPE_KEY] ?: SortType.SCRAP_DATE.name
            try {
                SortType.valueOf(sortTypeString)
            } catch (e: IllegalArgumentException) {
                SortType.SCRAP_DATE
            }
        }

    // 정렬 방향 Flow (기본값: ASCENDING)
    val sortDirection: Flow<SortDirection> =
        dataStore.data.map { preferences ->
            val sortDirectionString =
                preferences[SORT_DIRECTION_KEY] ?: SortDirection.ASC.name
            try {
                SortDirection.valueOf(sortDirectionString)
            } catch (e: IllegalArgumentException) {
                SortDirection.ASC
            }
        }

    // 뷰모드 Flow (기본값: LIST)
    val viewMode: Flow<ViewMode> =
        dataStore.data.map { preferences ->
            val viewModeString = preferences[VIEW_MODE_KEY] ?: ViewMode.LIST.name
            try {
                ViewMode.valueOf(viewModeString)
            } catch (e: IllegalArgumentException) {
                ViewMode.LIST
            }
        }

    // 정렬 타입 저장
    suspend fun setSortType(sortType: SortType) {
        dataStore.edit { preferences -> preferences[SORT_TYPE_KEY] = sortType.name }
    }

    // 정렬 방향 저장
    suspend fun setSortDirection(sortDirection: SortDirection) {
        dataStore.edit { preferences -> preferences[SORT_DIRECTION_KEY] = sortDirection.name }
    }

    // 뷰모드 저장
    suspend fun setViewMode(viewMode: ViewMode) {
        dataStore.edit { preferences -> preferences[VIEW_MODE_KEY] = viewMode.name }
    }

    // 데이터베이스 초기화 여부 확인
    val isDatabaseInitialized: Flow<Boolean> =
        dataStore.data.map { preferences -> preferences[IS_DATABASE_INITIALIZED_KEY] ?: false }

    // 데이터베이스 초기화 완료 설정
    suspend fun setDatabaseInitialized(initialized: Boolean) {
        dataStore.edit { preferences -> preferences[IS_DATABASE_INITIALIZED_KEY] = initialized }
    }
}
