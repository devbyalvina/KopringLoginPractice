package com.example.demo.member.controller

import com.example.demo.common.authority.TokenInfo
import com.example.demo.common.dto.BaseResponse
import com.example.demo.common.dto.CustomUser
import com.example.demo.member.dto.LoginDto
import com.example.demo.member.dto.MemberDtoRequest
import com.example.demo.member.dto.MemberDtoResponse
import com.example.demo.member.service.MemberService
import jakarta.validation.Valid
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*

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

    /**
     * 내 정보 보기
     */
    // [Note 5.1] 내 정보를 조회할 id를 URL로 받도록 추가
    // [Note 5.2] 내 정보를 조회할 id를 URL로 받으면 다른 회원꺼도 조회 가능하므로
    //            id를 URL이 아니라 토큰에서 뽑아오도록 변경
    //            - 호출방법: 로그인 하면 Response에 "data"에 "accessToken"으로 리턴되는 값을
    //                       info 호출 시 Headers에 "Authorization"의 Value에 넣어서 호출
    //            - 토큰 사용 주의점: 토큰에 너무 많은 정보 > API 호출 시 전송되는 데이터 양이 증가, 개인정보 보안 문제
    //                            => 토큰은 최소한으로 사용할 것
    /* [Note 5.2] 5.1 로직 주석
    @GetMapping("/info/{id}")
    fun searchMyInfo(@PathVariable id: Long): BaseResponse<MemberDtoResponse> {
        val response = memberService.searchMyInfo(id)
        return BaseResponse(data = response)
    }
     */
    @GetMapping("/info")
    fun searchMyInfo(): BaseResponse<MemberDtoResponse> {
        // [Note 5.2] SecurityContextHolder에서 토큰 정보 가져 옴
        val userId = (SecurityContextHolder.getContext().authentication.principal as CustomUser).userId
        val response = memberService.searchMyInfo(userId)
        return BaseResponse(data = response)
    }
}