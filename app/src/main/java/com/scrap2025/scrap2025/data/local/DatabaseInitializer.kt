package com.scrap2025.scrap2025.data.local

import com.scrap2025.scrap2025.data.local.dao.CategoryDao
import com.scrap2025.scrap2025.data.local.entity.CategoryEntity
import com.scrap2025.scrap2025.model.CategoryItem
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DatabaseInitializer
@Inject
constructor(private val categoryDao: CategoryDao) {
    suspend fun init() {
        if (categoryDao.getCategoryCount().first() == 0) {  // 카테고리가 없을 때만 초기화
            categoryDao.insertCategory(
                CategoryEntity(
                    id = CategoryItem.DEFAULT_ID,
                    name = CategoryItem.DEFAULT_NAME,
                    scrapCount = 0,
                    orderIndex = 0,
                    isDefault = true,
                )
            )
        }
    }
}
