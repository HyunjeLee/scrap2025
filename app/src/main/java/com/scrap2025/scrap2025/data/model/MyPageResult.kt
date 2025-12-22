package com.scrap2025.scrap2025.data.model

data class MyPageResult(
    val memberInfo: MemberInfo,
    val statistics: Statistics
)

data class MemberInfo(
    val name: String
)

data class Statistics(
    val totalCategory: Int,
    val totalScrap: Int
)
