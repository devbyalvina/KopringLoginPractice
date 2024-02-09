package com.example.demo.common.exception

// [Note 2.5]
// BaseResponse 클래스를 활용해 Exception 처리하기 위해
// RuntimeException를 상속한 InvalidInputException 생성
// 파라미터 'message'를 RuntimeException의 파라미터로 넘김
class InvalidInputException(
        val fieldName: String = "",
        message: String = "Invalid Input"
) : RuntimeException(message)