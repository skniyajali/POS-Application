package com.niyaj.popos.realm.charges.domain.use_cases

import com.niyaj.popos.realm.charges.domain.model.Charges
import com.niyaj.popos.realm.charges.domain.repository.ChargesRepository
import com.niyaj.popos.domain.util.Resource

class GetChargesById(
    private val chargesRepository: ChargesRepository
) {

    suspend operator fun invoke(chargesId: String): Resource<Charges?>{
        return chargesRepository.getChargesById(chargesId)
    }
}