package com.niyaj.popos.realm.charges.domain.use_cases

import com.niyaj.popos.domain.util.Resource
import com.niyaj.popos.domain.util.SortType
import com.niyaj.popos.realm.charges.domain.model.Charges
import com.niyaj.popos.realm.charges.domain.repository.ChargesRepository
import com.niyaj.popos.realm.charges.domain.util.FilterCharges
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetAllCharges(
    private val chargesRepository: ChargesRepository
) {
    suspend operator fun invoke(
        filterCharges: FilterCharges = FilterCharges.ByChargesId(SortType.Descending),
        searchText: String = "",
    ): Flow<Resource<List<Charges>>>{
        return flow {
            chargesRepository.getAllCharges().collect{ result ->
                when(result){
                    is Resource.Loading -> {
                        emit(Resource.Loading(result.isLoading))
                    }
                    is Resource.Success -> {
                        emit(Resource.Success(
                            result.data?.let { data ->
                                when(filterCharges.sortType){
                                    is SortType.Ascending -> {
                                        when(filterCharges){
                                            is FilterCharges.ByChargesId -> { data.sortedBy { it.chargesId } }
                                            is FilterCharges.ByChargesName -> { data.sortedBy { it.chargesName } }
                                            is FilterCharges.ByChargesPrice -> { data.sortedBy { it.chargesPrice } }
                                            is FilterCharges.ByChargesApplicable -> { data.sortedBy { it.isApplicable } }
                                            is FilterCharges.ByChargesDate -> { data.sortedBy { it.createdAt } }
                                        }
                                    }
                                    is SortType.Descending -> {
                                        when(filterCharges){
                                            is FilterCharges.ByChargesId -> { data.sortedByDescending { it.chargesId } }
                                            is FilterCharges.ByChargesName -> { data.sortedByDescending { it.chargesName } }
                                            is FilterCharges.ByChargesPrice -> { data.sortedByDescending { it.chargesPrice } }
                                            is FilterCharges.ByChargesApplicable -> { data.sortedByDescending { it.isApplicable } }
                                            is FilterCharges.ByChargesDate -> { data.sortedByDescending { it.createdAt } }
                                        }
                                    }
                                }.filter { chargesItem ->
                                    if (searchText.isNotEmpty()){
                                        chargesItem.chargesName.contains(searchText, true) ||
                                                chargesItem.chargesPrice.toString().contains(searchText, true) ||
                                                chargesItem.createdAt.contains(searchText, true) ||
                                                chargesItem.updatedAt?.contains(searchText, true) == true
                                    }else{
                                        true
                                    }
                                }
                            }
                        ))
                    }
                    is Resource.Error -> {
                        emit(Resource.Error(result.message ?: "Unable to get data from repository"))
                    }
                }
            }
        }
    }
}