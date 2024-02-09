package com.example.demo.common.dto

import com.example.demo.common.status.ResultCode

// [Note 2.4] ResultCode 클래스를 사용하기 위해 생성
data class BaseResponse<T>(
        // 결과코드
        val resultCode: String = ResultCode.SUCCESS.name,
        // 조회나 처리 시에 결과를 반환해 줄 값
        val data: T? = null,
        // 메세지
        val message: String = ResultCode.SUCCESS.msg,
)