package com.niyaj.popos.features.addon_item.presentation.add_edit

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Link
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.niyaj.popos.domain.util.safeString
import com.niyaj.popos.features.addon_item.domain.util.AddOnConstants.ADDON_ADD_EDIT_BUTTON
import com.niyaj.popos.features.addon_item.domain.util.AddOnConstants.ADDON_NAME_ERROR_TAG
import com.niyaj.popos.features.addon_item.domain.util.AddOnConstants.ADDON_NAME_FIELD
import com.niyaj.popos.features.addon_item.domain.util.AddOnConstants.ADDON_PRICE_ERROR_TAG
import com.niyaj.popos.features.addon_item.domain.util.AddOnConstants.ADDON_PRICE_FIELD
import com.niyaj.popos.features.addon_item.domain.util.AddOnConstants.ADD_EDIT_ADDON_SCREEN
import com.niyaj.popos.features.addon_item.domain.util.AddOnConstants.ADD_EDIT_SCREEN_CLOSE_BUTTON
import com.niyaj.popos.features.addon_item.domain.util.AddOnConstants.CREATE_NEW_ADD_ON
import com.niyaj.popos.features.addon_item.domain.util.AddOnConstants.EDIT_ADD_ON_ITEM
import com.niyaj.popos.features.common.ui.theme.SpaceMedium
import com.niyaj.popos.features.common.ui.theme.SpaceSmall
import com.niyaj.popos.features.common.util.UiEvent
import com.niyaj.popos.features.components.StandardButton
import com.niyaj.popos.features.components.StandardOutlinedTextField
import com.niyaj.popos.features.components.util.BottomSheetWithCloseDialog
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.ramcosta.composedestinations.spec.DestinationStyle
import kotlinx.coroutines.flow.collectLatest

@Destination(style = DestinationStyle.BottomSheet::class)
@Composable
fun AddEditAddOnItemScreen(
    addOnItemId: String? = "",
    navController: NavController,
    addEditAddOnItemViewModel: AddEditAddOnItemViewModel = hiltViewModel(),
    resultNavigator: ResultBackNavigator<String>
) {
    LaunchedEffect(key1 = true) {
        addEditAddOnItemViewModel.eventFlow.collectLatest { event ->
            when (event) {
                is UiEvent.OnSuccess -> {
                    resultNavigator.navigateBack(result = event.successMessage)
                }

                is UiEvent.OnError -> {
                    resultNavigator.navigateBack(result = event.errorMessage)
                }

                is UiEvent.IsLoading -> {}
            }
        }
    }

    BottomSheetWithCloseDialog(
        modifier = Modifier
            .testTag(ADD_EDIT_ADDON_SCREEN)
            .fillMaxWidth(),
        closeBtnModifier = Modifier.testTag(ADD_EDIT_SCREEN_CLOSE_BUTTON),
        text = if (!addOnItemId.isNullOrEmpty()) EDIT_ADD_ON_ITEM else CREATE_NEW_ADD_ON,
        icon = Icons.Default.Link,
        onClosePressed = {
            navController.navigateUp()
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            StandardOutlinedTextField(
                modifier = Modifier.testTag(ADDON_NAME_FIELD),
                text = addEditAddOnItemViewModel.addEditState.itemName,
                hint = ADDON_NAME_FIELD,
                leadingIcon = Icons.Default.Badge,
                error = addEditAddOnItemViewModel.addEditState.itemNameError,
                errorTag = ADDON_NAME_ERROR_TAG,
                onValueChange = {
                    addEditAddOnItemViewModel.onEvent(AddEditAddOnItemEvent.ItemNameChanged(it))
                },
            )

            Spacer(modifier = Modifier.height(SpaceSmall))

            StandardOutlinedTextField(
                modifier = Modifier.testTag(ADDON_PRICE_FIELD),
                text = addEditAddOnItemViewModel.addEditState.itemPrice,
                hint = ADDON_PRICE_FIELD,
                errorTag = ADDON_PRICE_ERROR_TAG,
                leadingIcon = Icons.Default.CurrencyRupee,
                keyboardType = KeyboardType.Number,
                error = addEditAddOnItemViewModel.addEditState.itemPriceError,
                onValueChange = {
                    addEditAddOnItemViewModel.onEvent(
                        AddEditAddOnItemEvent.ItemPriceChanged(safeString(it).toString())
                    )
                },
            )

            Spacer(modifier = Modifier.height(SpaceMedium))

            StandardButton(
                modifier = Modifier.testTag(ADDON_ADD_EDIT_BUTTON),
                text = if (!addOnItemId.isNullOrEmpty()) EDIT_ADD_ON_ITEM
                    else CREATE_NEW_ADD_ON,
                icon = if(!addOnItemId.isNullOrEmpty()) Icons.Default.Edit else Icons.Default.Add,
                onClick = {
                    if (!addOnItemId.isNullOrEmpty()) {
                        addEditAddOnItemViewModel.onEvent(
                            AddEditAddOnItemEvent.UpdateAddOnItem(addOnItemId)
                        )
                    } else {
                        addEditAddOnItemViewModel.onEvent(AddEditAddOnItemEvent.CreateNewAddOnItem)
                    }
                },
            )
        }
    }
}