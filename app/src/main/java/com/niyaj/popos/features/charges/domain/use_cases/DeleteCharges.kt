package com.niyaj.popos.features.charges.domain.use_cases

import com.niyaj.popos.features.charges.domain.repository.ChargesRepository
import com.niyaj.popos.features.common.util.Resource

class DeleteCharges(
    private val chargesRepository: ChargesRepository
) {

    suspend operator fun invoke(chargesId: String): Resource<Boolean> {
        return chargesRepository.deleteCharges(chargesId)
    }
}