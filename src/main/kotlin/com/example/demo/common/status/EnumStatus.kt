package com.example.demo.common.status

enum class Gender(val desc: String) {
    MAN("남"),
    WOMAN("여")
}

// [Note 2.4] 2.3에서 추가한 Validation 체크 시 메세지가 너무 길어서 알아보기 어려움
// 클라이언트에 리턴하는 메세지를 규격화해서 전달
enum class ResultCode(val msg: String) {
    SUCCESS("정상 처리 되었습니다."),
    ERROR("에러가 발생했습니다.")
}