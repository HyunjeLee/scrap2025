package com.scrap2025.scrap2025.data.local

import com.scrap2025.scrap2025.data.local.dao.CategoryDao
import com.scrap2025.scrap2025.data.local.entity.CategoryEntity
import kotlinx.coroutines.flow.first
import java.util.UUID
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
                    id = UUID.randomUUID().toString(),
                    name = "분류되지 않음",
                    scrapCount = 0,
                    isDefault = true,
                )
            )
        }
    }
}
