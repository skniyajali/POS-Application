package com.niyaj.popos.domain.model

data class DeliveryPartner(
    val deliveryPartnerId: String = "",
    val deliveryPartnerName: String = "",
    val deliveryPartnerEmail: String = "",
    val deliveryPartnerPhone: String = "",
    val deliveryPartnerPassword: String = "",
    val deliveryPartnerStatus: String = "",
    val deliveryPartnerType: String = "",
    val createdAt: String? = null,
    val updatedAt: String? = null,
)


sealed class PartnerStatus(val partnerStatus: String){
    object Active: PartnerStatus("Active")
    object InActive: PartnerStatus("Inactive")
    object Suspended: PartnerStatus("Suspended")
}

sealed class PartnerType(val partnerType: String){
    object PartTime: PartnerType("PartTime")
    object FullTime: PartnerType("FullTime")
}