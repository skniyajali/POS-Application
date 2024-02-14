package com.niyaj.data.repository.validation

import com.niyaj.common.utils.ValidationResult
import com.niyaj.model.PaymentMode
import com.niyaj.model.PaymentType

interface PaymentValidationRepository {

    fun validateEmployee(employeeId: String): ValidationResult

    fun validateGivenDate(givenDate: String): ValidationResult

    fun validatePaymentType(paymentType: PaymentType) : ValidationResult

    fun validatePaymentAmount(paymentAmount: String): ValidationResult

    fun validatePaymentMode(paymentMode: PaymentMode): ValidationResult
    
    fun validatePaymentNote(paymentNote: String, isRequired: Boolean = false): ValidationResult

}