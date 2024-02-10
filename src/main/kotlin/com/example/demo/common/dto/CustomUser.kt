package com.example.demo.common.dto

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.User

// [Note 5.2]
// User를 상속 + userId 프로퍼티 추가
class CustomUser(
        val userId: Long,
        userName: String,
        password: String,
        authorities: Collection<GrantedAuthority>
) : User(userName, password, authorities)