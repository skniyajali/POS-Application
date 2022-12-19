package com.niyaj.popos.realm.delivery_partner.domain.use_cases

import com.niyaj.popos.domain.util.Resource
import com.niyaj.popos.domain.util.SortType
import com.niyaj.popos.realm.delivery_partner.domain.model.DeliveryPartner
import com.niyaj.popos.realm.delivery_partner.domain.repository.PartnerRepository
import com.niyaj.popos.realm.delivery_partner.domain.util.FilterPartner
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

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
                                            is FilterPartner.ByPartnerId -> { data.sortedBy { it.partnerId } }
                                            is FilterPartner.ByPartnerName -> { data.sortedBy { it.partnerName } }
                                            is FilterPartner.ByPartnerPhone -> { data.sortedBy { it.partnerPhone } }
                                            is FilterPartner.ByPartnerEmail -> { data.sortedBy { it.partnerEmail } }
                                            is FilterPartner.ByPartnerStatus -> { data.sortedBy { it.partnerStatus } }
                                            is FilterPartner.ByPartnerType -> { data.sortedBy { it.partnerType } }
                                            is FilterPartner.ByPartnerDate -> { data.sortedBy { it.createdAt } }
                                        }
                                    }
                                    is SortType.Descending -> {
                                        when(filterPartner){
                                            is FilterPartner.ByPartnerId -> { data.sortedByDescending { it.partnerId } }
                                            is FilterPartner.ByPartnerName -> { data.sortedByDescending { it.partnerName } }
                                            is FilterPartner.ByPartnerPhone -> { data.sortedByDescending { it.partnerPhone } }
                                            is FilterPartner.ByPartnerEmail -> { data.sortedByDescending { it.partnerEmail } }
                                            is FilterPartner.ByPartnerStatus -> { data.sortedByDescending { it.partnerStatus } }
                                            is FilterPartner.ByPartnerType -> { data.sortedByDescending { it.partnerType } }
                                            is FilterPartner.ByPartnerDate -> { data.sortedByDescending { it.createdAt } }
                                        }
                                    }
                                }.filter { partner ->
                                    partner.partnerName.contains(searchText, true) ||
                                    partner.partnerEmail.contains(searchText, true) ||
                                    partner.partnerPhone.contains(searchText, true) ||
                                    partner.partnerStatus.contains(searchText, true) ||
                                    partner.partnerType.contains(searchText, true) ||
                                    partner.createdAt.contains(searchText, true) ||
                                    partner.updatedAt?.contains(searchText, true) == true
                                }
                            }
                        ))
                    }
                    is Resource.Error -> {
                        emit(Resource.Error(result.message ?: "Unable to get partner data from repository"))
                    }
                }
            }
        }

    }
}