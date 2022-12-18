package com.niyaj.popos.presentation.add_on_items.add_edit

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.niyaj.popos.R
import com.niyaj.popos.domain.util.UiEvent
import com.niyaj.popos.domain.util.safeString
import com.niyaj.popos.presentation.components.StandardOutlinedTextField
import com.niyaj.popos.presentation.components.util.BottomSheetWithCloseDialog
import com.niyaj.popos.presentation.ui.theme.SpaceSmall
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.ramcosta.composedestinations.spec.DestinationStyle
import kotlinx.coroutines.flow.collectLatest
import timber.log.Timber

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
        modifier = Modifier.fillMaxWidth(),
        text = if (!addOnItemId.isNullOrEmpty())
            stringResource(id = R.string.edit_add_on_item)
        else
            stringResource(id = R.string.create_new_add_on),
        onClosePressed = {
            navController.navigateUp()
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            StandardOutlinedTextField(
                modifier = Modifier,
                text = addEditAddOnItemViewModel.addEditState.itemName,
                hint = "AddOn Name",
                error = addEditAddOnItemViewModel.addEditState.itemNameError,
                onValueChange = {
                    addEditAddOnItemViewModel.onEvent(AddEditAddOnItemEvent.ItemNameChanged(it))
                },
            )

            Spacer(modifier = Modifier.height(SpaceSmall))

            StandardOutlinedTextField(
                modifier = Modifier,
                text = addEditAddOnItemViewModel.addEditState.itemPrice,
                hint = "AddOn Price",
                keyboardType = KeyboardType.Number,
                error = addEditAddOnItemViewModel.addEditState.itemPriceError,
                onValueChange = {
                    addEditAddOnItemViewModel.onEvent(
                        AddEditAddOnItemEvent.ItemPriceChanged(safeString(it).toString())
                    )
                },
            )

            Spacer(modifier = Modifier.height(SpaceSmall))

            Button(
                onClick = {
                    if (!addOnItemId.isNullOrEmpty()) {
                        addEditAddOnItemViewModel.onEvent(
                            AddEditAddOnItemEvent.UpdateAddOnItem(addOnItemId)
                        )
                    } else {
                        addEditAddOnItemViewModel.onEvent(AddEditAddOnItemEvent.CreateNewAddOnItem)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(44.dp),
            ) {
                Text(
                    text =
                    if (!addOnItemId.isNullOrEmpty())
                        stringResource(id = R.string.edit_add_on_item).uppercase()
                    else
                        stringResource(id = R.string.create_new_add_on).uppercase(),
                    style = MaterialTheme.typography.button,
                )
            }
        }
    }
}