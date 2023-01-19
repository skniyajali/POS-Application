package com.niyaj.popos.features.charges.domain.use_cases

import com.niyaj.popos.features.charges.domain.use_cases.validation.ValidateChargesName
import com.niyaj.popos.features.charges.domain.use_cases.validation.ValidateChargesPrice

data class ChargesUseCases(
    val validateChargesName: ValidateChargesName,
    val validateChargesPrice: ValidateChargesPrice,
    val getAllCharges: GetAllCharges,
    val getChargesById: GetChargesById,
    val createNewCharges: CreateNewCharges,
    val updateCharges: UpdateCharges,
    val deleteCharges: DeleteCharges,
)
