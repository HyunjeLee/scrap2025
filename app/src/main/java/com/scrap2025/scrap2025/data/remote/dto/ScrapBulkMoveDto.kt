package com.scrap2025.scrap2025.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ScrapBulkRequest(val scrapIds: List<Long>, val moveCategoryId: Long)