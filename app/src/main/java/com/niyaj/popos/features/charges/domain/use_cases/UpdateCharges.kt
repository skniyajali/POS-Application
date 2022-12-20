package com.niyaj.popos.features.charges.domain.use_cases

import com.niyaj.popos.features.charges.domain.model.Charges
import com.niyaj.popos.features.charges.domain.repository.ChargesRepository
import com.niyaj.popos.features.common.util.Resource

class UpdateCharges(
    private val chargesRepository: ChargesRepository
) {

    suspend operator fun invoke(newCharges: Charges, chargesId: String): Resource<Boolean> {
        return chargesRepository.updateCharges(newCharges, chargesId)
    }
}