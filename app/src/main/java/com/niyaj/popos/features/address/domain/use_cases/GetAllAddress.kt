package com.niyaj.popos.features.address.domain.use_cases

import com.niyaj.popos.features.address.domain.model.Address
import com.niyaj.popos.features.address.domain.model.filterAddress
import com.niyaj.popos.features.address.domain.repository.AddressRepository
import com.niyaj.popos.features.common.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow

class GetAllAddress(
    private val addressRepository: AddressRepository
) {
    operator fun invoke(
        searchText :String =  ""
    ): Flow<Resource<List<Address>>>{
        return channelFlow {
            addressRepository.getAllAddress().collect { result ->
                when (result){
                    is Resource.Loading -> {
                        send(Resource.Loading(result.isLoading))
                    }
                    is Resource.Success -> {
                        val data = result.data?.let { addresses ->
                            addresses.filter { address ->
                                address.filterAddress(searchText)
                            }
                        }

                        data?.let {
                            send(Resource.Success(it))
                        }
                    }
                    is Resource.Error -> {
                        send(Resource.Error(result.message ?: "Unable to get addresses from repository"))
                    }
                }
            }
        }
    }
}