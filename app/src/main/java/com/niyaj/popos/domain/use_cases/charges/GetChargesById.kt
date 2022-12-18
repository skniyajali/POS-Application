package com.niyaj.popos.domain.use_cases.charges

import com.niyaj.popos.domain.model.Charges
import com.niyaj.popos.domain.repository.ChargesRepository
import com.niyaj.popos.domain.util.Resource

class GetChargesById(
    private val chargesRepository: ChargesRepository
) {

    suspend operator fun invoke(chargesId: String): Resource<Charges?>{
        return chargesRepository.getChargesById(chargesId)
    }
}