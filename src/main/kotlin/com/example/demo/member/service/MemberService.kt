package com.example.demo.member.service

import com.example.demo.common.authority.JwtTokenProvider
import com.example.demo.common.authority.TokenInfo
import com.example.demo.common.exception.InvalidInputException
import com.example.demo.common.status.ROLE
import com.example.demo.member.dto.LoginDto
import com.example.demo.member.dto.MemberDtoRequest
import com.example.demo.member.dto.MemberDtoResponse
import com.example.demo.member.entity.Member
import com.example.demo.member.entity.MemberRole
import com.example.demo.member.repository.MemberRepository
import com.example.demo.member.repository.MemberRoleRepository
import jakarta.transaction.Transactional
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.stereotype.Service

@Transactional
@Service
class MemberService(
        private val memberRepository: MemberRepository,
        // [Note 4.1] 회원가입 시 DB에 권한정보도 같이 저장
        private val memberRoleRepository: MemberRoleRepository,
        // [Note 4.2] 로그인 시 AuthenticationManagerBuilder & JwtTokenProvider 사용해 로그인 진행
        private val authenticationManagerBuilder:AuthenticationManagerBuilder ,
        private val jwtTokenProvider: JwtTokenProvider,
) {
    /**
     * 회원가입
     */
    fun signUp(memberDtoRequest: MemberDtoRequest): String {
        // ID 중복 검사
        var member: Member? = memberRepository.findByLoginId(memberDtoRequest.loginId)
        if (member != null) {
            // [Note 2.5] id 중복 검사 후 stirng으로 결과를 리턴해줬는데 이부분을 Exception 발생시켜서 처리
            // return "이미 등록된 ID 입니다."
            throw InvalidInputException("loginId", "이미 등록된 ID 입니다.")
        }

        // [Note 2.5] toEntity로 정리
        /*
        member = Member(
                null,
                memberDtoRequest.loginId,
                memberDtoRequest.password,
                memberDtoRequest.name,
                memberDtoRequest.birthDate,
                memberDtoRequest.gender,
                memberDtoRequest.email
        )
        */
        member = memberDtoRequest.toEntity()
        memberRepository.save(member)

        // [Note 4.1] 회원가입 시 DB에 권한정보도 같이 저장 => 회원가입하면 member & member_role 테이블 두 개 생성
        val memberRole: MemberRole = MemberRole(null, ROLE.MEMBER, member)
        memberRoleRepository.save(memberRole)

        return "회원가입이 완료되었습니다."
    }

    /**
     * 로그인 -> 토큰 발행
     * [Note 4.2] LoginDto(loginId, password)를 받아서 TokenInfo로 토큰 정보 전달
     */
    fun login(loginDto: LoginDto): TokenInfo {
        // [Note 4.2] loginId & password로 authenticationToken 발행
        val authenticationToken = UsernamePasswordAuthenticationToken(loginDto.loginId, loginDto.password)
        // [Note 4.2] authenticate가 실행될 때 CustomUserDetailsService에 loadUserByUsername이 호출되면서 DB에 있는 멤버 정보와 비교
        val authentication = authenticationManagerBuilder.`object`.authenticate(authenticationToken)

        // [Note 4.2] 아무 문제 없으면 이 정보로 토큰 발행해서 리턴
        return jwtTokenProvider.createToken(authentication)
    }

    /**
     * 내 정보 조회
     */
    fun searchMyInfo(id: Long): MemberDtoResponse {
        val member: Member = memberRepository.findByIdOrNull(id) ?: throw InvalidInputException("id", "회원번호(${id})가 존재하지 않는 유저입니다.")
        return member.toDto()
    }

    /**
     * 내 정보 수정
     */
    fun saveMyInfo(memberDtoRequest: MemberDtoRequest): String {
        val member: Member = memberDtoRequest.toEntity()
        memberRepository.save(member)
        return "수정이 완료되었습니다."
    }
}