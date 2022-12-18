
package com.niyaj.popos.domain.model

import com.niyaj.popos.presentation.components.components.autocomplete.AutoCompleteEntity

data class Address(
    val addressId: String = "",
    val shortName: String = "",
    val addressName: String = "",
    val created_at: String? = null,
    val updated_at: String? = null,
): AutoCompleteEntity {
    override fun filter(query: String): Boolean {
        return addressName.contains(query, true) // || shortName.contains(query, true)
    }
}