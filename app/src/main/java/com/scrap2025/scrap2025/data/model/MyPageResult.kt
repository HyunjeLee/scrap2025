package com.scrap2025.scrap2025.data.model

import kotlinx.serialization.Serializable

@Serializable data class MyPageResult(val memberInfo: MemberInfo, val statistics: Statistics)

@Serializable data class MemberInfo(val name: String)

@Serializable data class Statistics(val totalCategory: Int, val totalScrap: Int)
