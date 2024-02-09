package com.example.demo.member.controller

import com.example.demo.common.authority.TokenInfo
import com.example.demo.common.dto.BaseResponse
import com.example.demo.member.dto.LoginDto
import com.example.demo.member.dto.MemberDtoRequest
import com.example.demo.member.service.MemberService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/api/member")
@RestController
class MemberController(
        private val memberService: MemberService
) {
    /**
     * 회원가입
     */
    // [Note 2.3] @Valid: DTO에 Validation 체크하기 위한 Annotation
    // [Note 2.5] 리턴타입 String > BaseResponse로 변경
    /* Note 2.3 내용 주석
    @PostMapping("/signup")
    fun signUp(@RequestBody @Valid memberDtoRequest: MemberDtoRequest): String {
        return memberService.signUp(memberDtoRequest)
    }
     */
    @PostMapping("/signup")
    // [Note 2.5] BaseResponse로 반환 시 데이터에 아무것도 전달할 게 없음: Unit => 비어있는 값 명시
    fun signUp(@RequestBody @Valid memberDtoRequest: MemberDtoRequest): BaseResponse<Unit> {
        // [Note 2.5] '회원가입이 완료되었습니다' 메세지 전달
        val resultMsg: String = memberService.signUp(memberDtoRequest)
        return BaseResponse(message = resultMsg)
    }

    /**
     * 로그인
     */
    @PostMapping("/login")
    fun login(@RequestBody @Valid loginDto: LoginDto): BaseResponse<TokenInfo> {
        val tokenInfo = memberService.login(loginDto)
        // [Note 4.2] 토큰을 Response에 담아서 리턴
        return BaseResponse(data = tokenInfo)
    }
}