package com.example.demo.member.repository

import com.example.demo.member.entity.Member
import com.example.demo.member.entity.MemberRole
import org.springframework.data.jpa.repository.JpaRepository

interface MemberRepository : JpaRepository<Member, Long> {  // [Note 2.2] JpaRepository 상속 받음
    fun findByLoginId(loginId: String): Member?
}

// [Note 4.1] 회원가입 시 DB에 권한정보도 같이 저장
interface MemberRoleRepository : JpaRepository<MemberRole, Long>