package com.example.demo.member.entity

import com.example.demo.common.status.Gender
import jakarta.persistence.*
import java.time.LocalDate

@Entity     // [Note 2.2] Member라는 Entity
@Table(
    // [Note 2.2] loginId 중복 발생 방지 UniqueKey로 잡아줌
    uniqueConstraints = [UniqueConstraint(name = "uk_member_login_id", columnNames = ["loginId"])]
)
class Member(
    @Id     // [Note 2.2] id가 JPA에서 키 값
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null,

    @Column(nullable = false, length = 30, updatable = false)   // [Note 2.2] loginId는 업데이트 불가
    val loginId: String,

    @Column(nullable = false, length = 100)
    val password: String,

    @Column(nullable = false, length = 10)
    val name: String,

    @Column(nullable = false)
    @Temporal(TemporalType.DATE)    // [Note 2.2] 날짜만 입력 가능
    val birthDate: LocalDate,

    @Column(nullable = false, length = 5)
    @Enumerated(EnumType.STRING)    // [Note 2.2] Gender의 이름 String으로 그대로 사용
    val gender: Gender,

    @Column(nullable = false, length = 30)
    val email: String,
)