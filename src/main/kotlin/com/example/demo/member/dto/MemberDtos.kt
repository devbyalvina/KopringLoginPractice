package com.example.demo.member.dto

import com.example.demo.common.annotation.ValidEnum
import com.example.demo.common.status.Gender
import com.example.demo.member.entity.Member
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import java.time.LocalDate
import java.time.format.DateTimeFormatter

// [Note 2.3]
// validation을 추가 하기 위해서는 DTO에 일단 데이터를 담아야 함
// => 1. 다 Null 허용하는 타입으로 변경
// => 2. 날짜나 Gender도 일단 String?로 스트링으로 받음
// => 3. Validation 체크 후 Custom Getter로 LocalDate 타입이나 Gender 타입으로 내보냄

/*
// [Note 2.3] 2.2 수업내용 주석
data class MemberDtoRequest(
        var id: Long?,
        val loginId: String,
        val password: String,
        val name: String,
        val birthDate: LocalDate,
        val gender: Gender,
        val email: String,
)
*/


data class MemberDtoRequest(
        var id: Long?,


        @field:NotBlank             // [Note 2.3] DTO로 받은 값 Not Null Validation 체크를 위한 Annotation
        @JsonProperty("loginId")    // [Note 2.3] Client로 부터는 _loginId가 아니라 loginId로 받기 위해 연결시킴
        private val _loginId: String?,

        @field:NotBlank
        @field:Pattern(
                // [Note 2.3] 정규식
                regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#\$%^&*])[a-zA-Z0-9!@#\$%^&*]{8,20}\$",
                // [Note 2.3] 정규식에 맞지 않았을 때 메세지
                message = "영문, 숫자, 특수문자를 포함한 8~20자리로 입력해주세요"
        )
        @JsonProperty("password")
        private val _password: String?,

        @field:NotBlank
        @JsonProperty("name")
        private val _name: String?,

        @field:NotBlank
        @field:Pattern(
                regexp = "^([12]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01]))$",
                message = "날짜형식(YYYY-MM-DD)을 확인해주세요"
        )
        @JsonProperty("birthDate")
        private val _birthDate: String?,

        @field:NotBlank
        // [Note 2.3]
        // enumClass에 Gender을 넣어줌
        // message: Validation 만족 못했을 때 남기는 메세지
        @field:ValidEnum(enumClass = Gender::class, message = "MAN 이나 WOMAN 중 하나를 선택해주세요.")
        @JsonProperty("gender")
        private val _gender: String?,

        @field:NotBlank
        @field:Email    // [Note 2.3] Email 형식 체크
        @JsonProperty("email")
        private val _email: String?,
) {
    // [Note 2.3] Custom Getter
    val loginId: String
        get() = _loginId!!
    val password: String
        get() = _password!!
    val name: String
        get() = _name!!
    val birthDate: LocalDate
        get() = _birthDate!!.toLocalDate()  // [Note 2.3] 확장함수 String.toLocalDate()로 String > LocalDate로 변경
    val gender: Gender
        get() = Gender.valueOf(_gender!!)
    val email: String
        get() = _email!!


    // [Note 2.3] String을 LocalDate 타입으로 리턴하는 확장함수
    private fun String.toLocalDate(): LocalDate =
            // this는 String을 의미함
            LocalDate.parse(this, DateTimeFormatter.ofPattern("yyyy-MM-dd"))

    // [Note 2.5] ExceptionHandler에서 BaseResponse의 결과 값을 담아서 전달할 수 있도록
    // 'Member' DTO를 Request 클래스에 Entity로 변환해서 반환하는 function 추가
    fun toEntity(): Member =
            Member(id, loginId, password, name, birthDate, gender, email)
}