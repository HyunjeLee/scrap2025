package com.scrap2025.scrap2025.data.local

import com.scrap2025.scrap2025.model.ScrapItem
import java.time.LocalDateTime

object ScrapDummyData {
    val dummyScrapItems = listOf(
        ScrapItem(
            id = "1",
            title = "제목제목",
            url = "주소주소주소주소",
            imageUrl = null,
            createdDate = LocalDateTime.of(2024, 2, 20, 10, 30),
            isFavorite = true,
            categoryId = "1"
        ),
        ScrapItem(
            id = "2",
            title = "제목제목제목제목제목제목제목제목제목제목제목제끝",
            url = "주소주소주소주소",
            imageUrl = "https://images.unsplash.com/photo-1506905925346-21bda4d32df4",
            createdDate = LocalDateTime.of(2024, 2, 22, 14, 15),
            isFavorite = true,
            categoryId = "1"
        ),
        ScrapItem(
            id = "3",
            title = "제목제목",
            url = "주소주소주소주소",
            imageUrl = null,
            createdDate = LocalDateTime.of(2024, 2, 22, 16, 45),
            isFavorite = false,
            categoryId = "2"
        ),
        ScrapItem(
            id = "4",
            title = "제목제목제제목제목제목제목제목제목제목제목제목제목제목제목제목제목제목끝",
            url = "주소주소주소주소",
            imageUrl = "https://images.unsplash.com/photo-1472214103451-9374bd1c798e",
            createdDate = LocalDateTime.of(2024, 2, 24, 9, 0),
            isFavorite = false,
            categoryId = "2"
        ),
        ScrapItem(
            id = "5",
            title = "제목제목제목",
            url = "주소주소주소주소",
            imageUrl = "https://images.unsplash.com/photo-1501594907352-04cda38ebc29",
            createdDate = LocalDateTime.of(2024, 2, 12, 11, 20),
            isFavorite = false,
            categoryId = "3"
        ),
        ScrapItem(
            id = "6",
            title = "제목제목제목제목",
            url = "주소주소주소주소",
            imageUrl = null,
            createdDate = LocalDateTime.of(2024, 2, 15, 13, 10),
            isFavorite = true,
            categoryId = "1"
        ),
        ScrapItem(
            id = "7",
            title = "제목제목제목제목제목제목제목",
            url = "주소주소주소주소",
            imageUrl = "https://images.unsplash.com/photo-1518791841217-8f162f1e1131",
            createdDate = LocalDateTime.of(2024, 2, 18, 15, 30),
            isFavorite = false,
            categoryId = "2"
        ),
        ScrapItem(
            id = "8",
            title = "제목",
            url = "주소주소주소주소",
            imageUrl = null,
            createdDate = LocalDateTime.of(2024, 2, 25, 17, 50),
            isFavorite = true,
            categoryId = "3"
        ),
        ScrapItem(
            id = "9",
            title = "제목제목제목제목제목제목제목제목제목제목",
            url = "주소주소주소주소",
            imageUrl = "https://images.unsplash.com/photo-1507525428034-b723cf961d3e",
            createdDate = LocalDateTime.of(2024, 2, 28, 8, 40),
            isFavorite = false,
            categoryId = "1"
        ),
        ScrapItem(
            id = "10",
            title = "제목제목제목제목제목",
            url = "주소주소주소주소",
            imageUrl = "https://images.unsplash.com/photo-1441974231531-c6227db76b6e",
            createdDate = LocalDateTime.of(2024, 3, 1, 12, 0),
            isFavorite = true,
            categoryId = "2"
        )
    )
}
