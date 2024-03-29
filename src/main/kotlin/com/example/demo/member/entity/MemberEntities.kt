package com.example.demo.member.entity

import com.example.demo.common.status.Gender
import com.example.demo.common.status.ROLE
import com.example.demo.member.dto.MemberDtoResponse
import jakarta.persistence.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter


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
) {
    // [Note 4.1] 멤버 엔티티에서 멤버 룰 조회 가능하도록 연결
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "member")
    val memberRole: List<MemberRole>? = null

    // [Note 5.1] 내 정보 조회 용 DTO 변경 함수(toDto) 추가
    private fun LocalDate.formatDate(): String =
            this.format(DateTimeFormatter.ofPattern("yyyyMMdd"))

    fun toDto(): MemberDtoResponse =
            MemberDtoResponse(id!!, loginId, name, birthDate.formatDate(), gender.desc, email)  // [Note 5.1] birthDate & gender는 String으로 변환
}

// [Note 4.1] 권한(Role)을 가지고 있는 엔티티 추가 & 기존 멤버 엔티티에 연결
@Entity
class MemberRole(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null,

    // [Note 4.1] 권한(Role) 정보를 갖고 있음
    @Column(nullable = false, length = 30)
    @Enumerated(EnumType.STRING)
    val role: ROLE,

    // [Note 4.1] 멤버 엔티티에 다대일로 연결
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(foreignKey = ForeignKey(name = "fk_member_role_member_id"))
    val member: Member,

    )