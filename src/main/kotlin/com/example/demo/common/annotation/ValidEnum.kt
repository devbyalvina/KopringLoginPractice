package com.example.demo.common.annotation

import jakarta.validation.Constraint
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import jakarta.validation.Payload
import kotlin.reflect.KClass

// [Note 2.3] Enum Class를 체크할 수 있는 Annotation

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Constraint(validatedBy = [ValidEnumValidator::class])  // [Note 2.3] ValidEnumValidator를 ValidEnum의 Annotation에 추가
annotation class ValidEnum(
        // [Note 2.3]
        // Gender Enum Class가 enumClass 프로퍼티에 들어가게 되고
        // 유효성검사에 통화 못하면 message를 내뱉음
        val message: String = "Invalid enum value",
        val groups: Array<KClass<*>> = [],
        val payload: Array<KClass<out Payload>> = [],
        val enumClass: KClass<out Enum<*>>
)

class ValidEnumValidator : ConstraintValidator<ValidEnum, Any> {
    private lateinit var enumValues: Array<out Enum<*>>

    override fun initialize(annotation: ValidEnum) {
        enumValues = annotation.enumClass.java.enumConstants
    }

    // [Note 2.3] 유효성 검사하는 로직
    override fun isValid(value: Any?, context: ConstraintValidatorContext): Boolean {
        if (value == null) {
            return true
        }
        return enumValues.any { it.name == value.toString()}
    }
}