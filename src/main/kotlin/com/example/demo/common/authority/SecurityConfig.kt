package com.example.demo.common.authority

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

// [Note 3.3] 권한 관리 Config 클래스
@Configuration
@EnableWebSecurity
class SecurityConfig(
        private val jwtTokenProvider: JwtTokenProvider
) {
    // [Note 3.3] http 안에 하나씩 설정 & 권한 관리하는 빈
    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .httpBasic { it.disable() } // [Note 3.3] httpBasic 사용X
            .csrf { it.disable() }      // [Note 3.3] csrf 사용X
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }    // [Note 3.3] JWT를 사용하므로 세션 사용X
            // [Note 3.3] authorizeHttpRequests 안에 권한 관리를 넣어주면 됨
            .authorizeHttpRequests {
                // [Note 3.3] "/api/member/signup" URL을 호출하는 사람은 인증되지 않은 사용자여야하고
                // [Note 5.1] "/api/member/login"도 추가
                it.requestMatchers("/api/member/signup", "/api/member/login").anonymous()
                    // [Note 5.1] "/api/member/~"로 호출되는 모든 API는 MEMBER 권한이 있어야 함
                    .requestMatchers("/api/member/**").hasRole("MEMBER")
                    // [Note 3.3] 그 외의 요청은 아무 권한 없이 모두가 접근이 가능
                    .anyRequest().permitAll()
            }
            // [Note 3.3] 앞에 있는 필터가 뒤에 있는 필터보다 먼저 실행되도록 함
            // 앞에 있는 필터를 통과하면 뒤에 있는 필터는 실행X
            .addFilterBefore(
                    JwtAuthenticationFilter(jwtTokenProvider),      // [Note 3.3] 앞 filter
                    UsernamePasswordAuthenticationFilter::class.java    // [Note 3.3] 뒤 filter
            )

        return http.build()
    }

    // [Note 3.3] 코드를 암호화하는 빈
    @Bean
    fun passwordEncoder(): PasswordEncoder =
            PasswordEncoderFactories.createDelegatingPasswordEncoder()
}