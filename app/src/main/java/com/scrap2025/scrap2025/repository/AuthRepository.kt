package com.scrap2025.scrap2025.repository

import com.scrap2025.scrap2025.model.enums.SnsType

/** 인증 관련 데이터 처리를 담당하는 리포지토리 인터페이스 */
interface AuthRepository {

    /** 운영계를 통한 테스트 토큰 */
    suspend fun testLogin(testAccessToken: String, testRefreshToken: String): Result<Unit>

    /**
     * 서버에 로그인을 요청하고 토큰을 저장합니다.
     * @param snsType 로그인하는 SNS 종류 (KAKAO, NAVER 등)
     * @param socialToken 소셜 플랫폼에서 발급받은 액세스 토큰
     * @return 성공 여부 (Result<Unit>)
     */
    suspend fun loginToServer(snsType: SnsType, socialToken: String): Result<Unit>

    /**
     * 서버에 로그아웃을 요청하고 로컬 토큰을 삭제합니다.
     * @return 성공 여부 (Result<Unit>)
     */
    suspend fun logoutToServer(): Result<Unit>

    /**
     * 서버에 회원 탈퇴를 요청하고 모든 로컬 데이터를 초기화합니다.
     * @return 성공 여부 (Result<Unit>)
     */
    suspend fun withdrawToServer(): Result<Unit>
}
