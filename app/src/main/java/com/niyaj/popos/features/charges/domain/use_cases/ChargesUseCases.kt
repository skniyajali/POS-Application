package com.niyaj.popos.features.charges.domain.use_cases

data class ChargesUseCases(
    val getAllCharges: GetAllCharges,
    val getChargesById: GetChargesById,
    val findChargesByName: FindChargesByName,
    val createNewCharges: CreateNewCharges,
    val updateCharges: UpdateCharges,
    val deleteCharges: DeleteCharges,
)
