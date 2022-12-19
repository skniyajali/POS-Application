package com.niyaj.popos.realm.charges.domain.use_cases

import com.niyaj.popos.realm.charges.domain.model.Charges
import com.niyaj.popos.realm.charges.domain.repository.ChargesRepository
import com.niyaj.popos.domain.util.Resource

class CreateNewCharges(
    private val chargesRepository: ChargesRepository
) {

    suspend operator fun invoke(newCharges: Charges): Resource<Boolean>{
        return chargesRepository.createNewCharges(newCharges)
    }
}