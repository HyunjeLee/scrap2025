package com.scrap2025.scrap2025.data.remote.auth.social

import android.content.Context
import com.navercorp.nid.NidOAuth
import com.navercorp.nid.oauth.util.NidOAuthCallback
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine

class NaverLoginProvider
@Inject
constructor() : SocialLoginProvider {
    override suspend fun login(context: Context): Result<String> =
        suspendCancellableCoroutine { continuation ->
            val callback =
                object : NidOAuthCallback {
                    override fun onSuccess() {
                        val token = NidOAuth.getAccessToken()
                        if (token != null) {
                            continuation.resume(Result.success(token))
                        } else {
                            continuation.resume(
                                Result.failure(Exception("Naver Access Token is null"))
                            )
                        }
                    }

                    override fun onFailure(errorCode: String, errorDesc: String) {
                        continuation.resume(
                            Result.failure(
                                Exception(
                                    "Naver Login Failed: $errorDesc ($errorCode)"
                                )
                            )
                        )
                    }
                }
            NidOAuth.requestLogin(context, callback)
        }

    override suspend fun logout(): Result<Unit> = suspendCancellableCoroutine { continuation ->
        val callback =
            object : NidOAuthCallback {
                override fun onSuccess() {
                    continuation.resume(Result.success(Unit))
                }

                override fun onFailure(errorCode: String, errorDesc: String) {
                    continuation.resume(
                        Result.failure(
                            Exception("Naver Logout Failed: $errorDesc ($errorCode)")
                        )
                    )
                }
            }
        NidOAuth.logout(callback)
    }

    override suspend fun disconnect(): Result<Unit> = suspendCancellableCoroutine { continuation ->
        val callback =
            object : NidOAuthCallback {
                override fun onSuccess() {
                    continuation.resume(Result.success(Unit))
                }

                override fun onFailure(errorCode: String, errorDesc: String) {
                    continuation.resume(
                        Result.failure(
                            Exception("Naver Disconnect Failed: $errorDesc ($errorCode)")
                        )
                    )
                }
            }
        NidOAuth.disconnect(callback)
    }
}
