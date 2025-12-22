package com.scrap2025.scrap2025.data.local

import com.scrap2025.scrap2025.data.local.dao.CategoryDao
import com.scrap2025.scrap2025.data.local.entity.CategoryEntity
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DatabaseInitializer @Inject constructor(private val categoryDao: CategoryDao) {
    suspend fun init() {
        if (categoryDao.getCategoryCount().first() == 0) {
            CategoryDummyData.dummyCategories.forEach { item ->
                categoryDao.insertCategory(
                    CategoryEntity(id = item.id, name = item.name, scrapCount = item.scrapCount)
                )
            }
        }
    }
}
