package com.niyaj.popos.features.addon_item.domain.repository

import com.niyaj.popos.features.common.util.ValidationResult

interface ValidationRepository {

    fun validateItemName(name: String, addOnItemId: String?): ValidationResult

    fun validateItemPrice(price: Int): ValidationResult
}