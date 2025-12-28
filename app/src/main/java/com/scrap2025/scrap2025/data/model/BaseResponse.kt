package com.scrap2025.scrap2025.data.model

import kotlinx.serialization.Serializable

@Serializable
data class BaseResponse<T>(val code: String, val message: String, val result: T?)
