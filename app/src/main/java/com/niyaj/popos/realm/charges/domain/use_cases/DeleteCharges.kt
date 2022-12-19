package com.niyaj.popos.realm.charges.domain.use_cases

import com.niyaj.popos.realm.charges.domain.repository.ChargesRepository
import com.niyaj.popos.domain.util.Resource

class DeleteCharges(
    private val chargesRepository: ChargesRepository
) {

    suspend operator fun invoke(chargesId: String): Resource<Boolean>{
        return chargesRepository.deleteCharges(chargesId)
    }
}