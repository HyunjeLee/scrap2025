package com.scrap2025.scrap2025.repository

import com.scrap2025.scrap2025.model.enums.SnsType

interface AuthRepository {
    suspend fun loginToServer(snsType: SnsType, socialToken: String): Result<Unit>
    suspend fun logoutToServer(): Result<Unit>
    suspend fun withdrawToServer(): Result<Unit>
}
