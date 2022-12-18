package com.niyaj.popos.realm.address.domain.use_cases

import com.niyaj.popos.domain.util.Resource
import com.niyaj.popos.realm.address.domain.model.Address
import com.niyaj.popos.realm.address.domain.repository.AddressRepository

class CreateNewAddress(
    private val addressRepository: AddressRepository
) {

    suspend operator fun invoke(address: Address): Resource<Boolean> {
        return addressRepository.addNewAddress(address)
    }
}