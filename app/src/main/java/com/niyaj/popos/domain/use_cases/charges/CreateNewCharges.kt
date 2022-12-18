package com.niyaj.popos.domain.use_cases.charges

import com.niyaj.popos.domain.model.Charges
import com.niyaj.popos.domain.repository.ChargesRepository
import com.niyaj.popos.domain.util.Resource

class CreateNewCharges(
    private val chargesRepository: ChargesRepository
) {

    suspend operator fun invoke(newCharges: Charges): Resource<Boolean>{
        return chargesRepository.createNewCharges(newCharges)
    }
}