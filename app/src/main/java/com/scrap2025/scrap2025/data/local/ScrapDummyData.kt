package com.scrap2025.scrap2025.data.local

import com.scrap2025.scrap2025.model.ScrapItem

object ScrapDummyData {
    val dummyScrapItems = listOf(
        ScrapItem(
            id = "1",
            title = "제목제목",
            url = "주소주소주소주소",
            imageUrl = null,
            createdDate = "2024.02.20",
            isFavorite = true,
            categoryId = "1"
        ),
        ScrapItem(
            id = "2",
            title = "제목제목제목제목제목제목제목제목제목제목제목제끝",
            url = "주소주소주소주소",
            imageUrl = "https://images.unsplash.com/photo-1506905925346-21bda4d32df4",
            createdDate = "2024.02.22",
            isFavorite = true,
            categoryId = "1"
        ),
        ScrapItem(
            id = "3",
            title = "제목제목",
            url = "주소주소주소주소",
            imageUrl = null,
            createdDate = "2024.02.22",
            isFavorite = false,
            categoryId = "2"
        ),
        ScrapItem(
            id = "4",
            title = "제목제목제제목제목제목제목제목제목제목제목제목제목제목제목제목제목제목끝",
            url = "주소주소주소주소",
            imageUrl = "https://images.unsplash.com/photo-1472214103451-9374bd1c798e",
            createdDate = "2024.02.24",
            isFavorite = false,
            categoryId = "2"
        ),
        ScrapItem(
            id = "5",
            title = "제목제목제목",
            url = "주소주소주소주소",
            imageUrl = "https://images.unsplash.com/photo-1501594907352-04cda38ebc29",
            createdDate = "2024.02.12",
            isFavorite = false,
            categoryId = "3"
        )
    )
}
