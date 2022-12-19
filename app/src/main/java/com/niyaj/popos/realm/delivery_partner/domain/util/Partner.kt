package com.niyaj.popos.realm.delivery_partner.domain.util

sealed class PartnerStatus(val partnerStatus: String){
    object Active: PartnerStatus("Active")
    object InActive: PartnerStatus("Inactive")
    object Suspended: PartnerStatus("Suspended")
}

sealed class PartnerType(val partnerType: String){
    object PartTime: PartnerType("PartTime")
    object FullTime: PartnerType("FullTime")
}