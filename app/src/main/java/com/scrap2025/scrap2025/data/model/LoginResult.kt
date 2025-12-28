package com.scrap2025.scrap2025.data.model

import kotlinx.serialization.Serializable

@Serializable
data class LoginResult(val accessToken: String, val refreshToken: String)
