package com.scrap2025.scrap2025.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class Meta(
    val totalElement: Int,
    val numOfElement: Int,
    val isEnd: Boolean
)

@Serializable
data class BaseResponse<T>(
    val code: String,
    val message: String,
    val result: T? = null
)