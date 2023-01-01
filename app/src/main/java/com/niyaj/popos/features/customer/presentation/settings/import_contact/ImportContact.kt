package com.niyaj.popos.features.customer.presentation.settings.import_contact


import com.niyaj.popos.features.customer.domain.model.Contact
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.mongodb.kbson.BsonObjectId

@JsonClass(generateAdapter = true)
class ImportContact(
    @Json(name = "Anniversary", ignore = true)
    val anniversary: String? = null,
    @Json(name = "Birthday", ignore = true)
    val birthday: String? = null,
    @Json(name = "Business Address", ignore = true)
    val businessAddress: String? = null,
    @Json(name = "Business Address 2", ignore = true)
    val businessAddress2: String? = null,
    @Json(name = "Business City", ignore = true)
    val businessCity: String? = null,
    @Json(name = "Business Country", ignore = true)
    val businessCountry: String? = null,
    @Json(name = "Business Fax", ignore = true)
    val businessFax: String? = null,
    @Json(name = "Business Phone", ignore = true)
    val businessPhone: String? = null,
    @Json(name = "Business Postal Code", ignore = true)
    val businessPostalCode: String? = null,
    @Json(name = "Business State", ignore = true)
    val businessState: String? = null,
    @Json(name = "Categories", ignore = true)
    val categories: String? = null,
    @Json(name = "Country Code", ignore = true)
    val countryCode: String? = null,
    @Json(name = "Department", ignore = true)
    val department: String? = null,
    @Json(name = "Display Name")
    val displayName: String? = null,
    @Json(name = "E-mail 2 Address", ignore = true)
    val eMail2Address: String? = null,
    @Json(name = "E-mail 3 Address", ignore = true)
    val eMail3Address: String? = null,
    @Json(name = "E-mail Address", ignore = true)
    val eMailAddress: String? = null,
    @Json(name = "First Name")
    val firstName: String? = null,
    @Json(name = "Gender", ignore = true)
    val gender: String? = null,
    @Json(name = "Home Address 2", ignore = true)
    val homeAddress2: String? = null,
    @Json(name = "Home City", ignore = true)
    val homeCity: String? = null,
    @Json(name = "Home Country", ignore = true)
    val homeCountry: String? = null,
    @Json(name = "Home Fax", ignore = true)
    val homeFax: String? = null,
    @Json(name = "Home Phone", ignore = true)
    val homePhone: String? = null,
    @Json(name = "Home Postal Code", ignore = true)
    val homePostalCode: String? = null,
    @Json(name = "Home State", ignore = true)
    val homeState: String? = null,
    @Json(name = "Home Street", ignore = true)
    val homeStreet: String? = null,
    @Json(name = "Job Title", ignore = true)
    val jobTitle: String? = null,
    @Json(name = "Last Name", ignore = true)
    val lastName: String? = null,
    @Json(name = "Mobile Phone")
    val mobilePhone: String? = null,
    @Json(name = "Nickname", ignore = true)
    val nickname: String? = null,
    @Json(name = "Notes", ignore = true)
    val notes: String? = null,
    @Json(name = "Organization", ignore = true)
    val organization: String? = null,
    @Json(name = "Pager", ignore = true)
    val pager: String? = null,
    @Json(name = "Related name", ignore = true)
    val relatedName: String? = null,
    @Json(name = "Web Page", ignore = true)
    val webPage: String? = null,
    @Json(name = "Web Page 2", ignore = true)
    val webPage2: String? = null
)


fun ImportContact.toContact(): Contact? {
    try {
        val phoneNo = if (!mobilePhone.isNullOrEmpty() && mobilePhone.length >= 10) mobilePhone.takeLast(10) else null

        if (phoneNo != null){
            return Contact(
                contactId = BsonObjectId().toHexString(),
                phoneNo = phoneNo,
                name = displayName,
                email = eMailAddress
            )
        }

        return null
    }catch (e: Exception) {
        return null
    }
}