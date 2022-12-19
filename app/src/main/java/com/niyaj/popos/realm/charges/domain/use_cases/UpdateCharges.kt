package com.niyaj.popos.realm.charges.domain.use_cases

import com.niyaj.popos.realm.charges.domain.model.Charges
import com.niyaj.popos.realm.charges.domain.repository.ChargesRepository
import com.niyaj.popos.domain.util.Resource

class UpdateCharges(
    private val chargesRepository: ChargesRepository
) {

    suspend operator fun invoke(newCharges: Charges, chargesId: String): Resource<Boolean>{
        return chargesRepository.updateCharges(newCharges, chargesId)
    }
}