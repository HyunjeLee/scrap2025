package com.scrap2025.scrap2025.repository

import com.scrap2025.scrap2025.data.remote.datasource.UserRemoteDataSource
import com.scrap2025.scrap2025.data.remote.dto.MyPageResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/** MyPageRepository의 구현체. [UserRemoteDataSource]를 통해 사용자 정보를 가져오고, 메모리상의 StateFlow로 상태를 유지합니다. */
@Singleton
class MyPageRepositoryImpl
@Inject
constructor(
    private val userRemoteDataSource: UserRemoteDataSource,
) : MyPageRepository {

    // DB 대신 최신 상태를 메모리에 저장하여 SSOT(Single Source of Truth) 역할 수행
    private val _myPageData = MutableStateFlow<MyPageResponse?>(null)
    override val myPageData = _myPageData.asStateFlow()

    override suspend fun fetchMyPage() {
        try {
            val response = userRemoteDataSource.getMyPage()
            _myPageData.value = response // 메모리 업데이트 -> 관찰 중인 UI에 즉시 통지
        } catch (e: Exception) {
            // 에러 핸들링 로직 추가 가능 (현재는 기존 상태 유지 또는 로그 출력)
            _myPageData.value = null
        }
    }
}
