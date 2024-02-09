package com.example.demo.common.authority

import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.util.StringUtils
import org.springframework.web.filter.GenericFilterBean

// [Note 3.2] 토큰을 검사하는 클래스
// GenericFilterBean를 상속 받음
// 생성자로 JwtTokenProvider를 받음
class JwtAuthenticationFilter(
        private val jwtTokenProvider: JwtTokenProvider
) : GenericFilterBean() {

    override fun doFilter(request: ServletRequest?, response: ServletResponse?, chain: FilterChain?) {
        // [Note 3.2] 이 토큰이 access token
        val token = resolveToken(request as HttpServletRequest)

        // [Note 3.2] 토큰이 정상인지 validation 체크
        if (token != null && jwtTokenProvider.validateToken(token)) {
            val authentication = jwtTokenProvider.getAuthentication(token)
            // [Note 3.2] 토큰이 정상인 경우 SecurityContextHolder에 저장
            SecurityContextHolder.getContext().authentication = authentication
        }

        chain?.doFilter(request, response)
    }

    // [Note 3.2] Request Header에 Authorization에 있는 값을 가져오고 Bearer로 시작하는게 있으면 뒤에 값만 뽑아옴
    private fun resolveToken(request: HttpServletRequest): String? {
        val bearerToken = request.getHeader("Authorization")

        return if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer")) {
            bearerToken.substring(7)
        } else {
            null
        }
    }
}