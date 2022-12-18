package com.niyaj.popos.domain.use_cases.charges

data class ChargesUseCases(
    val getAllCharges: GetAllCharges,
    val getChargesById: GetChargesById,
    val findChargesByName: FindChargesByName,
    val createNewCharges: CreateNewCharges,
    val updateCharges: UpdateCharges,
    val deleteCharges: DeleteCharges,
)
