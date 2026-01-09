package com.scrap2025.scrap2025.repository

import com.scrap2025.scrap2025.data.remote.datasource.UserRemoteDataSource
import com.scrap2025.scrap2025.data.remote.dto.MyPageResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MyPageRepositoryImpl
@Inject
constructor(
    private val userRemoteDataSource: UserRemoteDataSource,
) : MyPageRepository {
    // DB 대신 최신 상태를 메모리에 저장 (SSOT 역할)
    private val _myPageData = MutableStateFlow<MyPageResponse?>(null)
    override val myPageData = _myPageData.asStateFlow()

    override suspend fun fetchMyPage() {
        val response = userRemoteDataSource.getMyPage()
        _myPageData.value = response // 메모리 업데이트 -> 관찰 중인 모든 곳에 통지
    }
}
