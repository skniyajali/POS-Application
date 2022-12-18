package com.niyaj.popos.realm.address.domain.use_cases

import com.niyaj.popos.domain.util.Resource
import com.niyaj.popos.realm.address.domain.repository.AddressRepository

class DeleteAddress(
    private val addressRepository: AddressRepository
) {

    suspend operator fun invoke(addressId: String): Resource<Boolean> {
        return addressRepository.deleteAddress(addressId)
    }
}