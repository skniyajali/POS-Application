package com.niyaj.popos.realm.charges.domain.use_cases

import com.niyaj.popos.realm.charges.domain.repository.ChargesRepository

class FindChargesByName(
    private val chargesRepository: ChargesRepository
) {

    operator fun invoke(chargesName: String, chargesId: String?) : Boolean {
        return chargesRepository.findChargesByName(chargesName, chargesId)
    }
}