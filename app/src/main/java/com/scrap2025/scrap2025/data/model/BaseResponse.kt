package com.scrap2025.scrap2025.data.model

data class BaseResponse<T>(val code: String, val message: String, val result: T?)
