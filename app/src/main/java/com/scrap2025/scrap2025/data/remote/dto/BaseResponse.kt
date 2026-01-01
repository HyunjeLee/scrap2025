package com.scrap2025.scrap2025.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class Meta(
    val totalElemnt: Int, // todo: 서버에서 오타 수정 시 같이 수정할 것
    val numOfElement: Int,
    val isEnd: Boolean
)

@Serializable
data class BaseResponse<T>(
    val code: String,
    val message: String,
    val result: T? = null
)