package com.scrap2025.scrap2025.data.local

import com.scrap2025.scrap2025.model.CategoryItem

object CategoryDummyData {
    val dummyCategories = mutableListOf(
        CategoryItem(id = "1", name = "분류되지 않음", scrapCount = 322),
        CategoryItem(id = "2", name = "데이트", scrapCount = 32),
        CategoryItem(id = "3", name = "맛집", scrapCount = 22),
        CategoryItem(
            id = "4",
            name = "제목제목제목제목제목제목고고고고고고제제제제들물음",
            scrapCount = 5
        )
    )
}
