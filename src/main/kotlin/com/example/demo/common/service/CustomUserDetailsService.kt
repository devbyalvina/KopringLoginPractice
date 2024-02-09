package com.example.demo.common.service

import com.example.demo.member.entity.Member
import com.example.demo.member.repository.MemberRepository
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

// [Note 4.2] UserDetailsService를 구현해서 CustomUserDetailsService 생성
@Service
class CustomUserDetailsService(
        private val memberRepository: MemberRepository,
        private val passwordEncoder: PasswordEncoder,
) : UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails =
            // [Note 4.2] Repository에서 loginId 기준으로 찾음
            memberRepository.findByLoginId(username)
                    // [Note 4.2] 조회되는게 있으면 회원 정보가 있다는 거니까 createUserDetails 호출해서 UserDetails로 반환
                    // 조회되는게 없으면 UsernameNotFoundException 발생 (실제로 테스트해보면 BadCredentialException 발생 => CustomExceptionHandler에 추가)
                    ?.let { createUserDetails(it) } ?: throw UsernameNotFoundException("해당 유저는 없습니다.")


    private fun createUserDetails(member: Member): UserDetails =
            User(
                member.loginId,
                passwordEncoder.encode(member.password),
                member.memberRole!!.map { SimpleGrantedAuthority("ROLE_${it.role}") }
            )
}