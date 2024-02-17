package com.niyaj.feature.addonitem.add_edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Link
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.niyaj.common.tags.AddOnConstants.ADDON_ADD_EDIT_BUTTON
import com.niyaj.common.tags.AddOnConstants.ADDON_NAME_ERROR_TAG
import com.niyaj.common.tags.AddOnConstants.ADDON_NAME_FIELD
import com.niyaj.common.tags.AddOnConstants.ADDON_PRICE_ERROR_TAG
import com.niyaj.common.tags.AddOnConstants.ADDON_PRICE_FIELD
import com.niyaj.common.tags.AddOnConstants.ADD_EDIT_ADDON_SCREEN
import com.niyaj.common.tags.AddOnConstants.ADD_EDIT_SCREEN_CLOSE_BUTTON
import com.niyaj.common.tags.AddOnConstants.CREATE_NEW_ADD_ON
import com.niyaj.common.tags.AddOnConstants.EDIT_ADD_ON_ITEM
import com.niyaj.common.tags.ChargesTestTags
import com.niyaj.common.utils.safeString
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.ui.components.StandardButtonFW
import com.niyaj.ui.components.StandardOutlinedTextField
import com.niyaj.ui.event.UiEvent
import com.niyaj.ui.util.BottomSheetWithCloseDialog
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.ramcosta.composedestinations.spec.DestinationStyleBottomSheet
import kotlinx.coroutines.flow.collectLatest

@Destination(style = DestinationStyleBottomSheet::class)
@Composable
fun AddEditAddOnItemScreen(
    addOnItemId: String? = "",
    navController: NavController,
    viewModel: AddEditAddOnItemViewModel = hiltViewModel(),
    resultNavigator: ResultBackNavigator<String>
) {
    val nameError = viewModel.nameError.collectAsStateWithLifecycle().value
    val priceError = viewModel.priceError.collectAsStateWithLifecycle().value

    val enableBtn = nameError == null && priceError == null

    val title = if (addOnItemId.isNullOrEmpty()) CREATE_NEW_ADD_ON else EDIT_ADD_ON_ITEM

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is UiEvent.Success -> {
                    resultNavigator.navigateBack(result = event.successMessage)
                }

                is UiEvent.Error -> {
                    resultNavigator.navigateBack(result = event.errorMessage)
                }
            }
        }
    }

    BottomSheetWithCloseDialog(
        modifier = Modifier
            .testTag(ADD_EDIT_ADDON_SCREEN)
            .fillMaxWidth(),
        closeBtnModifier = Modifier.testTag(ADD_EDIT_SCREEN_CLOSE_BUTTON),
        text = title,
        icon = Icons.Default.Link,
        onClosePressed = {
            navController.navigateUp()
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceSmall),
            verticalArrangement = Arrangement.spacedBy(SpaceSmall)
        ) {
            StandardOutlinedTextField(
                modifier = Modifier.testTag(ADDON_NAME_FIELD),
                text = viewModel.addEditState.itemName,
                label = ADDON_NAME_FIELD,
                leadingIcon = Icons.Default.Badge,
                error = nameError,
                errorTag = ADDON_NAME_ERROR_TAG,
                onValueChange = {
                    viewModel.onEvent(AddOnEvent.ItemNameChanged(it))
                },
            )

            StandardOutlinedTextField(
                modifier = Modifier.testTag(ADDON_PRICE_FIELD),
                text = viewModel.addEditState.itemPrice.safeString,
                label = ADDON_PRICE_FIELD,
                errorTag = ADDON_PRICE_ERROR_TAG,
                leadingIcon = Icons.Default.CurrencyRupee,
                keyboardType = KeyboardType.Number,
                error = priceError,
                onValueChange = {
                    viewModel.onEvent(AddOnEvent.ItemPriceChanged(it))
                },
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Switch(
                    modifier = Modifier.testTag(ChargesTestTags.CHARGES_APPLIED_SWITCH),
                    checked = viewModel.addEditState.isApplicable,
                    onCheckedChange = {
                        viewModel.onEvent(AddOnEvent.ItemApplicableChanged)
                    }
                )
                Spacer(modifier = Modifier.width(SpaceSmall))
                Text(
                    text = if (viewModel.addEditState.isApplicable)
                        "Marked as applied"
                    else
                        "Marked as not applied",
                    style = MaterialTheme.typography.overline
                )
            }

            Spacer(modifier = Modifier.height(SpaceMini))

            StandardButtonFW(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(ADDON_ADD_EDIT_BUTTON),
                enabled = enableBtn,
                text = title,
                icon = if (!addOnItemId.isNullOrEmpty()) Icons.Default.Edit else Icons.Default.Add,
                onClick = {
                    viewModel.onEvent(AddOnEvent.CreateUpdateAddOnItem)
                },
            )
        }
    }
}