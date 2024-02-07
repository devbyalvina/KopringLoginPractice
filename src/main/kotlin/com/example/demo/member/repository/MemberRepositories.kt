package com.example.demo.member.repository

import com.example.demo.member.entity.Member
import org.springframework.data.jpa.repository.JpaRepository

interface MemberRepository : JpaRepository<Member, Long> {  // [Note 2.2] JpaRepository 상속 받음
    fun findByLoginId(loginId: String): Member?
}