package com.example.demo.common.authority

import com.example.demo.common.dto.CustomUser
import io.jsonwebtoken.*
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.util.*
import kotlin.RuntimeException

const val EXPIRATION_MILLISECONDS: Long = 1000 * 60 * 30    // 30분

// [Note 3.2] 토큰을 생성하고 토큰 정보를 추출하는 클래스
@Component
class JwtTokenProvider {
    @Value("\${jwt.secret}")
    lateinit var secretKey: String

    private val key by lazy { Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey)) }

    /**
     * Token 생성
     */
    fun createToken(authentication: Authentication): TokenInfo {
        // [Note 3.2] param으로 받은 authentication 안에 권한들을 뽑아서 ,로 연결
        val authorities: String = authentication
                .authorities
                .joinToString(",", transform = GrantedAuthority::getAuthority)

        val now = Date()
        val accessExpiration = Date(now.time + EXPIRATION_MILLISECONDS)

        // [Note 3.2] 실제 Access Token 생성하는 부분
        val accessToken = Jwts
                .builder()
                .setSubject(authentication.name)
                .claim("auth", authorities)    // [Note 3.2] 위에서 뽑은 권한들을 "auth"라는 이름으로 담음
                .claim("userId", (authentication.principal as CustomUser).userId)   // [Note 5.2] 토큰 생성 시 userId도 저장
                .setIssuedAt(now)    // [Note 3.2] 발행시간
                .setExpiration(accessExpiration)    //[Note 3.2]  유효시간(~까지)
                .signWith(key, SignatureAlgorithm.HS256)    // [Note 3.2] 서명 키에 적용한 알고리즘
                .compact()

        // [Note 3.2] TokenInfo 안에 accessToken를 넣어서 return
        return TokenInfo("Bearer", accessToken)
    }

    /**
     * Token 정보 추출
     */
    // [Note 3.2] param으로 받는 token은 access token
    fun getAuthentication(token: String): Authentication {
        // [Note 3.2] 토큰 안에서 클래임 가져오는 함수 호출
        val claims: Claims = getClaims(token)
        // [Note 3.2] claims 안에 "auth"가 없으면 잘못된 토큰
        val auth = claims["auth"] ?: throw RuntimeException("잘못된 토큰입니다.")
        // [Note 5.2] Token 정보 추출 시 "userId"도 체크
        val userId = claims["userId"] ?: throw RuntimeException("잘못된 토큰입니다.")

        // 권한 정보 추출
        // [Note 3.2] auth를 ','로 파싱해서 Collection에 담음
        val authorities: Collection<GrantedAuthority> = (auth as String)
                .split(",")
                .map { SimpleGrantedAuthority(it) }

        // [Note 3.2] UsernamePasswordAuthenticationToken 만들어서 리턴
        // [Note 5.2] User > CustomUser로 변경
        val principal: UserDetails = CustomUser(userId.toString().toLong(), claims.subject, "", authorities)

        return UsernamePasswordAuthenticationToken(principal, "", authorities)
    }

    /**
     * Token 검증
     */
    fun validateToken(token: String): Boolean {
        try {
            // [Note 3.2] 토큰 안에서 클래임 가져오는 함수 호출해서 Exception 안 떨어지면 성공
            getClaims(token)
            return true
        } catch (e: Exception) {
            when (e) {
                is SecurityException -> {}  // Invalid JWT Token
                is MalformedJwtException -> {}  // Invalid JWT Token
                is ExpiredJwtException -> {}    // Expired JWT Token
                is UnsupportedJwtException -> {}    // Unsupported JWT Token
                is IllegalArgumentException -> {}   // JWT claims string is empty
                else -> {}  // else
            }
            println(e.message)
        }
        return false
    }

    // [Note 3.2] 토큰 안에서 클래임 가져오는 함수
    private fun getClaims(token: String): Claims =
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .body
}