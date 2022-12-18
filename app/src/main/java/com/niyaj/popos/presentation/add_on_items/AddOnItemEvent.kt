package com.niyaj.popos.presentation.add_on_items

import com.niyaj.popos.domain.util.filter_items.FilterAddOnItem

sealed class AddOnItemEvent {

    data class SelectAddOnItem(val addOnItemId: String) : AddOnItemEvent()

    object SelectAllAddOnItem : AddOnItemEvent()

    object DeselectAddOnItem : AddOnItemEvent()

    data class DeleteAddOnItem(val addOnItems: List<String>) : AddOnItemEvent()

    data class OnFilterAddOnItem(val filterAddOnItem: FilterAddOnItem): AddOnItemEvent()

    data class OnSearchAddOnItem(val searchText: String): AddOnItemEvent()

    object ToggleSearchBar : AddOnItemEvent()

    object RefreshAddOnItem : AddOnItemEvent()
}
