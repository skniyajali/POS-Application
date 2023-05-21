package com.niyaj.popos.features.charges.domain.use_cases

import com.niyaj.popos.features.charges.domain.model.Charges
import com.niyaj.popos.features.charges.domain.model.filterCharges
import com.niyaj.popos.features.charges.domain.repository.ChargesRepository
import com.niyaj.popos.features.common.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.withContext

class GetAllCharges(
    private val chargesRepository: ChargesRepository
) {
    suspend operator fun invoke(searchText : String = ""): Flow<Resource<List<Charges>>>{
        return channelFlow {
            withContext(Dispatchers.IO) {
                chargesRepository.getAllCharges().collectLatest { result ->
                    when(result){
                        is Resource.Loading -> {
                            send(Resource.Loading(result.isLoading))
                        }
                        is Resource.Success -> {
                            val data = result.data?.let { data ->
                                data.filter { chargesItem ->
                                    chargesItem.filterCharges(searchText)
                                }
                            }

                            send(Resource.Success(data))
                        }
                        is Resource.Error -> {
                            send(Resource.Error(result.message ?: "Unable to get data from repository"))
                        }
                    }
                }
            }
        }
    }
}