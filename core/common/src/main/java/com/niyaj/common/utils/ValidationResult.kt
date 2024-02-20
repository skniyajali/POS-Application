package com.niyaj.common.utils

data class ValidationResult(
    val successful: Boolean,
    val errorMessage: String? = null
)


fun List<ValidationResult>.toError(): String {
    var message = ""
    this.filter { !it.successful }.forEach {
        message += it.errorMessage + " \n "
    }
    return message
}