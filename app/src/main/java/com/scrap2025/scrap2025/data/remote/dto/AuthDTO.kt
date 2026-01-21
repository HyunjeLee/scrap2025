@file:Suppress("ktlint:standard:filename")

package com.scrap2025.scrap2025.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable data class LoginResponse(val accessToken: String, val refreshToken: String)
