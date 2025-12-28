package com.scrap2025.scrap2025.data.model

import kotlinx.serialization.Serializable

@Serializable
enum class SyncStatus {
    PENDING,
    SYNCED
}
