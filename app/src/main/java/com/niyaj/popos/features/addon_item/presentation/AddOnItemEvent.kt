package com.niyaj.popos.features.addon_item.presentation

sealed class AddOnItemEvent {

    data class SelectAddOnItem(val addOnItemId: String) : AddOnItemEvent()

    object SelectAllAddOnItem : AddOnItemEvent()

    object DeselectAddOnItem : AddOnItemEvent()

    data class DeleteAddOnItem(val addOnItems: List<String>) : AddOnItemEvent()

    data class OnSearchAddOnItem(val searchText: String): AddOnItemEvent()

    object ToggleSearchBar : AddOnItemEvent()

    object RefreshAddOnItem : AddOnItemEvent()
}
