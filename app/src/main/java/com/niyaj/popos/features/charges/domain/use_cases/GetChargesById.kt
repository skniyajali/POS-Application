package com.niyaj.popos.features.charges.domain.use_cases

import com.niyaj.popos.features.charges.domain.model.Charges
import com.niyaj.popos.features.charges.domain.repository.ChargesRepository
import com.niyaj.popos.features.common.util.Resource

class GetChargesById(
    private val chargesRepository: ChargesRepository
) {

    suspend operator fun invoke(chargesId: String): Resource<Charges?> {
        return chargesRepository.getChargesById(chargesId)
    }
}