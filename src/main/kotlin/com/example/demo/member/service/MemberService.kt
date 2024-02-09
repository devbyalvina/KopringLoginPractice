package com.example.demo.member.service

import com.example.demo.common.exception.InvalidInputException
import com.example.demo.common.status.ROLE
import com.example.demo.member.dto.MemberDtoRequest
import com.example.demo.member.entity.Member
import com.example.demo.member.entity.MemberRole
import com.example.demo.member.repository.MemberRepository
import com.example.demo.member.repository.MemberRoleRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Transactional
@Service
class MemberService(
        private val memberRepository: MemberRepository,
        // [Note 4.1] 회원가입 시 DB에 권한정보도 같이 저장
        private val memberRoleRepository: MemberRoleRepository
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
}