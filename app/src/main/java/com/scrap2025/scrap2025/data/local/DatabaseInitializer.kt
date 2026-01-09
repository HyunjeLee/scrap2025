package com.scrap2025.scrap2025.data.local


// LOCAL-FIRST -> SERVER-FIRST
//@Singleton
//class DatabaseInitializer
//@Inject
//constructor(private val categoryDao: CategoryDao) {
//    suspend fun init() {
//        if (categoryDao.getCategoryCount().first() == 0) {  // 카테고리가 없을 때만 초기화
//            categoryDao.insertCategory(
//                CategoryEntity(
//                    id = CategoryItem.DEFAULT_ID,
//                    name = CategoryItem.DEFAULT_NAME,
//                    scrapCount = 0,
//                    orderIndex = 0,
//                    isDefault = true,
//                )
//            )
//        }
//    }
//}
