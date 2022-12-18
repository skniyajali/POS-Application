package com.niyaj.popos.domain.use_cases.delivery_partner

import com.niyaj.popos.domain.model.DeliveryPartner
import com.niyaj.popos.domain.repository.PartnerRepository
import com.niyaj.popos.domain.util.Resource
import com.niyaj.popos.domain.util.SortType
import com.niyaj.popos.domain.util.filter_items.FilterPartner
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber

class GetAllPartners(
    private val partnerRepository: PartnerRepository
) {

    suspend operator fun invoke(
        filterPartner: FilterPartner = FilterPartner.ByPartnerId(SortType.Descending),
        searchText: String = ""
    ): Flow<Resource<List<DeliveryPartner>>>{

        return flow {
            partnerRepository.getAllPartner().collect{ result ->
                when(result){
                    is Resource.Loading -> {
                        emit(Resource.Loading(result.isLoading))
                    }
                    is Resource.Success -> {
                        emit(Resource.Success(
                            result.data?.let { data ->
                                when(filterPartner.sortType){
                                    is SortType.Ascending -> {
                                        when(filterPartner){
                                            is FilterPartner.ByPartnerId -> { data.sortedBy { it.deliveryPartnerId } }
                                            is FilterPartner.ByPartnerName -> { data.sortedBy { it.deliveryPartnerName } }
                                            is FilterPartner.ByPartnerPhone -> { data.sortedBy { it.deliveryPartnerPhone } }
                                            is FilterPartner.ByPartnerEmail -> { data.sortedBy { it.deliveryPartnerEmail } }
                                            is FilterPartner.ByPartnerStatus -> { data.sortedBy { it.deliveryPartnerStatus } }
                                            is FilterPartner.ByPartnerType -> { data.sortedBy { it.deliveryPartnerType } }
                                            is FilterPartner.ByPartnerDate -> { data.sortedBy { it.createdAt } }
                                        }
                                    }
                                    is SortType.Descending -> {
                                        when(filterPartner){
                                            is FilterPartner.ByPartnerId -> { data.sortedByDescending { it.deliveryPartnerId } }
                                            is FilterPartner.ByPartnerName -> { data.sortedByDescending { it.deliveryPartnerName } }
                                            is FilterPartner.ByPartnerPhone -> { data.sortedByDescending { it.deliveryPartnerPhone } }
                                            is FilterPartner.ByPartnerEmail -> { data.sortedByDescending { it.deliveryPartnerEmail } }
                                            is FilterPartner.ByPartnerStatus -> { data.sortedByDescending { it.deliveryPartnerStatus } }
                                            is FilterPartner.ByPartnerType -> { data.sortedByDescending { it.deliveryPartnerType } }
                                            is FilterPartner.ByPartnerDate -> { data.sortedByDescending { it.createdAt } }
                                        }
                                    }
                                }.filter { partner ->
                                    partner.deliveryPartnerName.contains(searchText, true) ||
                                    partner.deliveryPartnerEmail.contains(searchText, true) ||
                                    partner.deliveryPartnerPhone.contains(searchText, true) ||
                                    partner.deliveryPartnerStatus.contains(searchText, true) ||
                                    partner.deliveryPartnerType.contains(searchText, true) ||
                                    partner.createdAt?.contains(searchText, true) == true ||
                                    partner.updatedAt?.contains(searchText, true) == true
                                }
                            }
                        ))
                    }
                    is Resource.Error -> {
                        Timber.d("Unable to get partner data from repository")
                        emit(Resource.Error(result.message ?: "Unable to get partner data from repository"))
                    }
                }
            }
        }

    }
}