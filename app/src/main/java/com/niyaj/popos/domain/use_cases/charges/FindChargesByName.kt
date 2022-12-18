package com.niyaj.popos.domain.use_cases.charges

import com.niyaj.popos.domain.repository.ChargesRepository

class FindChargesByName(
    private val chargesRepository: ChargesRepository
) {

    operator fun invoke(chargesName: String, chargesId: String?) : Boolean {
        return chargesRepository.findChargesByName(chargesName, chargesId)
    }
}