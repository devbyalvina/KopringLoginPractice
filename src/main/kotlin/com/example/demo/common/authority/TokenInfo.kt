package com.example.demo.common.authority

// [Note 3.2] 토큰정보를 담아 두는 클래스
data class TokenInfo(
            val grantType: String,      // [Note 3.2] 권한인증 타입
        val accessToken: String,        // [Note 3.2] 실제로 검증할 토큰
)