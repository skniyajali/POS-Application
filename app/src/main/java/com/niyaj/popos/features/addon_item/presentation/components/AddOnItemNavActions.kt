package com.niyaj.popos.features.addon_item.presentation.components

import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Rule
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.niyaj.popos.features.addon_item.domain.util.AddOnConstants
import com.niyaj.popos.features.components.StandardIconButton
import com.niyaj.popos.features.components.StandardSearchBar

@Composable
fun AddOnItemNavActions(
    addOnItemsIsEmpty: Boolean = true,
    selectedAddOnItems:List<String> = emptyList(),
    onClickEdit: (String) -> Unit = {},
    onClickDelete: () -> Unit = {},
    onClickSelectAll: () -> Unit = {},
    showSearchBar: Boolean = false,
    searchText: String = "",
    onSearchTextChanged: (String) -> Unit = {},
    onClearClick: () -> Unit = {},
    onClickSearch: () -> Unit = {},
) {
    if(selectedAddOnItems.isNotEmpty()) {
        if(selectedAddOnItems.size == 1){
            StandardIconButton(
                modifier = Modifier.testTag(AddOnConstants.ADDON_EDIT_BUTTON),
                onClick = {
                    onClickEdit(selectedAddOnItems.first())
                },
                imageVector = Icons.Default.Edit,
                contentDescription = AddOnConstants.ADDON_EDIT_BUTTON,
                tint = MaterialTheme.colors.onPrimary,
            )
        }

        StandardIconButton(
            modifier = Modifier.testTag(AddOnConstants.ADDON_DELETE_BUTTON),
            onClick = onClickDelete,
            enabled = selectedAddOnItems.isNotEmpty(),
            imageVector = Icons.Default.Delete,
            contentDescription = AddOnConstants.ADDON_DELETE_BUTTON,
            tint = MaterialTheme.colors.onPrimary,
        )

        StandardIconButton(
            modifier = Modifier.testTag(AddOnConstants.ADDON_SELECT_ALL_BUTTON),
            onClick = onClickSelectAll,
            enabled = selectedAddOnItems.isNotEmpty(),
            imageVector = Icons.AutoMirrored.Filled.Rule,
            contentDescription = AddOnConstants.ADDON_SELECT_ALL_BUTTON,
            tint = MaterialTheme.colors.onPrimary,
        )
    }
    else if(showSearchBar){
        StandardSearchBar(
            modifier = Modifier.testTag(AddOnConstants.ADDON_SEARCH_BAR),
            searchText = searchText,
            placeholderText = AddOnConstants.ADDON_SEARCH_PLACEHOLDER,
            onSearchTextChanged = {
                onSearchTextChanged(it)
            },
            onClearClick = onClearClick,
        )
    }
    else {
        if (!addOnItemsIsEmpty){
            StandardIconButton(
                modifier = Modifier.testTag(AddOnConstants.ADDON_SEARCH_BUTTON),
                onClick = onClickSearch,
                imageVector = Icons.Default.Search,
                contentDescription = AddOnConstants.ADDON_SEARCH_BUTTON,
                tint = MaterialTheme.colors.onPrimary,
            )
        }
    }
}