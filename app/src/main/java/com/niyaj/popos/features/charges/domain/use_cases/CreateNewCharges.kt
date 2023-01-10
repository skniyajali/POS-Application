package com.niyaj.popos.features.charges.domain.use_cases

import com.niyaj.popos.features.charges.domain.model.Charges
import com.niyaj.popos.features.charges.domain.repository.ChargesRepository
import com.niyaj.popos.features.common.util.Resource

class CreateNewCharges(
    private val chargesRepository: ChargesRepository
) {
    suspend operator fun invoke(newCharges: Charges): Resource<Boolean> {
        return chargesRepository.createNewCharges(newCharges)
    }
}