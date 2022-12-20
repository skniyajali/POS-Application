package com.niyaj.popos.features.delivery_partner.domain.model

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class DeliveryPartner() : RealmObject {

    @PrimaryKey
    var partnerId: String = ""

    var partnerName: String = ""

    var partnerEmail: String = ""

    var partnerPhone: String = ""

    var partnerPassword: String = ""

    var partnerStatus: String = ""

    var partnerType: String = ""

    var createdAt: String = ""

    var updatedAt: String? = null

    constructor(
        partnerId: String = "",
        partnerName: String = "",
        partnerEmail: String = "",
        partnerPhone: String = "",
        partnerPassword: String = "",
        partnerStatus: String = "",
        partnerType: String = "",
        createdAt: String = "",
        updatedAt: String? = null
    ) : this() {
        this.partnerId = partnerId
        this.partnerName = partnerName
        this.partnerEmail = partnerEmail
        this.partnerPhone = partnerPhone
        this.partnerPassword = partnerPassword
        this.partnerStatus = partnerStatus
        this.partnerType = partnerType
        this.createdAt = createdAt
        this.updatedAt = updatedAt
    }
}