package com.niyaj.popos.domain.use_cases.charges

import com.niyaj.popos.domain.model.Charges
import com.niyaj.popos.domain.repository.ChargesRepository
import com.niyaj.popos.domain.util.Resource

class UpdateCharges(
    private val chargesRepository: ChargesRepository
) {

    suspend operator fun invoke(newCharges: Charges, chargesId: String): Resource<Boolean>{
        return chargesRepository.updateCharges(newCharges, chargesId)
    }
}