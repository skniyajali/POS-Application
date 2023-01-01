package com.niyaj.popos.features.customer.presentation.settings.import_contact

import com.niyaj.popos.features.customer.domain.model.Contact

sealed class ImportContactEvent {

    data class SelectContact(val contactId: String) : ImportContactEvent()

    data class SelectContacts(val contacts: List<String>) : ImportContactEvent()

    object SelectAllContact : ImportContactEvent()

    object DeselectContacts : ImportContactEvent()

    object OnChooseContact: ImportContactEvent()

    data class ImportContactsData(val contacts: List<Contact> = emptyList()): ImportContactEvent()

    object ClearImportedContacts: ImportContactEvent()

    object ImportContacts: ImportContactEvent()
}
