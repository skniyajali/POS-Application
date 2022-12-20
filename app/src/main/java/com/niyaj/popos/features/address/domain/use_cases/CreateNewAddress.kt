package com.niyaj.popos.features.address.domain.use_cases

import com.niyaj.popos.features.address.domain.model.Address
import com.niyaj.popos.features.address.domain.repository.AddressRepository
import com.niyaj.popos.features.common.util.Resource

class CreateNewAddress(
    private val addressRepository: AddressRepository
) {

    suspend operator fun invoke(address: Address): Resource<Boolean> {
        return addressRepository.addNewAddress(address)
    }
}