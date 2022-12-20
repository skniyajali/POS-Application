package com.niyaj.popos.features.address.domain.use_cases

import com.niyaj.popos.features.address.domain.repository.AddressRepository
import com.niyaj.popos.features.common.util.Resource

class DeleteAddress(
    private val addressRepository: AddressRepository
) {

    suspend operator fun invoke(addressId: String): Resource<Boolean> {
        return addressRepository.deleteAddress(addressId)
    }
}