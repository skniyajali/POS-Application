package com.niyaj.popos.features.address.domain.use_cases

import com.niyaj.popos.features.address.domain.model.Address
import com.niyaj.popos.features.address.domain.repository.AddressRepository
import com.niyaj.popos.features.common.util.Resource

class UpdateAddress(
    private val addressRepository: AddressRepository
) {

    suspend operator fun invoke(newAddress: Address, addressId: String): Resource<Boolean> {
        return addressRepository.updateAddress(newAddress, addressId)
    }
}