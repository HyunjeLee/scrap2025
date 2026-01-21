package com.scrap2025.scrap2025.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable data class MyPageResponse(val statistics: Statistics, val memberInfo: MemberInfo)

@Serializable data class Statistics(val totalCategory: Int, val totalScrap: Int)

@Serializable data class MemberInfo(val name: String)
