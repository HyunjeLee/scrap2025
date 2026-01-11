package com.scrap2025.scrap2025.repository

import com.scrap2025.scrap2025.data.remote.dto.MyPageResponse
import kotlinx.coroutines.flow.StateFlow

/** 마이페이지(사용자 프로필 및 정보) 데이터 처리를 담당하는 리포지토리 인터페이스 */
interface MyPageRepository {
    /** 마이페이지 정보를 담고 있는 관찰 가능한 상태 흐름 */
    val myPageData: StateFlow<MyPageResponse?>

    /** 서버로부터 최신 마이페이지 정보를 불러와 [myPageData]를 갱신합니다. */
    suspend fun fetchMyPage()
}
