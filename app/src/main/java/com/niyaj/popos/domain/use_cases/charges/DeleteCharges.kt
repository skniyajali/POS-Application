package com.niyaj.popos.domain.use_cases.charges

import com.niyaj.popos.domain.repository.ChargesRepository
import com.niyaj.popos.domain.util.Resource

class DeleteCharges(
    private val chargesRepository: ChargesRepository
) {

    suspend operator fun invoke(chargesId: String): Resource<Boolean>{
        return chargesRepository.deleteCharges(chargesId)
    }
}