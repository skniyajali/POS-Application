package com.niyaj.popos.domain.util.filter_items

import com.niyaj.popos.domain.util.SortType

sealed class FilterPartner(val sortType: SortType){

    class ByPartnerId(sortType: SortType): FilterPartner(sortType)

    class ByPartnerName(sortType: SortType): FilterPartner(sortType)

    class ByPartnerPhone(sortType: SortType): FilterPartner(sortType)

    class ByPartnerEmail(sortType: SortType): FilterPartner(sortType)

    class ByPartnerStatus(sortType: SortType): FilterPartner(sortType)

    class ByPartnerType(sortType: SortType): FilterPartner(sortType)

    class ByPartnerDate(sortType: SortType): FilterPartner(sortType)


    fun copy(sortType: SortType): FilterPartner {
        return when(this){
            is ByPartnerId -> ByPartnerId(sortType)
            is ByPartnerName -> ByPartnerName(sortType)
            is ByPartnerPhone -> ByPartnerPhone(sortType)
            is ByPartnerEmail -> ByPartnerEmail(sortType)
            is ByPartnerStatus -> ByPartnerStatus(sortType)
            is ByPartnerType -> ByPartnerType(sortType)
            is ByPartnerDate -> ByPartnerDate(sortType)
        }
    }
}
