package com.niyaj.popos.realm.delivery_partner.domain.model

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class DeliveryPartner: RealmObject {

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
}