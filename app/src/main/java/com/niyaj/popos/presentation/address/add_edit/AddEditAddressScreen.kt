package com.niyaj.popos.presentation.address.add_edit

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.niyaj.popos.R
import com.niyaj.popos.domain.util.UiEvent
import com.niyaj.popos.presentation.components.StandardOutlinedTextField
import com.niyaj.popos.presentation.components.util.BottomSheetWithCloseDialog
import com.niyaj.popos.presentation.ui.theme.SpaceSmall
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.ramcosta.composedestinations.spec.DestinationStyle

@Destination(style = DestinationStyle.BottomSheet::class)
@Composable
fun AddEditAddressScreen(
    addressId: String? = "",
    navController: NavController,
    addEditAddressViewModel: AddEditAddressViewModel = hiltViewModel(),
    resultNavigator: ResultBackNavigator<String>
) {

    LaunchedEffect(key1 = true) {
        addEditAddressViewModel.eventFlow.collect { event ->
            when (event) {
                is UiEvent.OnSuccess -> {
                    resultNavigator.navigateBack(event.successMessage)
                }

                is UiEvent.OnError -> {
                    resultNavigator.navigateBack(event.errorMessage)
                }

                is UiEvent.IsLoading -> {}
            }
        }
    }
    
    BottomSheetWithCloseDialog(
        modifier = Modifier.fillMaxWidth(),
        text = if (!addressId.isNullOrEmpty())
            stringResource(id = R.string.edit_address)
        else
            stringResource(id = R.string.create_address),
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
                text = addEditAddressViewModel.addEditAddressState.address,
                hint = "Full Address",
                error = addEditAddressViewModel.addEditAddressState.addressError,
                onValueChange = {
                    addEditAddressViewModel.onAddressEvent(AddEditAddressEvent.AddressNameChanged(it))
                },
            )

            Spacer(modifier = Modifier.height(SpaceSmall))

            StandardOutlinedTextField(
                modifier = Modifier,
                text = addEditAddressViewModel.addEditAddressState.shortName,
                hint = "Short Name",
                error = addEditAddressViewModel.addEditAddressState.shortNameError,
                onValueChange = {
                    addEditAddressViewModel.onAddressEvent(AddEditAddressEvent.ShortNameChanged(it))
                },
            )

            Spacer(modifier = Modifier.height(SpaceSmall))


            Button(
                onClick = {
                    if (!addressId.isNullOrEmpty()) {
                        addEditAddressViewModel.onAddressEvent(AddEditAddressEvent.UpdateAddress(addressId))
                    } else {
                        addEditAddressViewModel.onAddressEvent(AddEditAddressEvent.CreateNewAddress)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(44.dp),
            ) {
                Text(
                    text =
                    if (!addressId.isNullOrEmpty())
                        stringResource(id = R.string.edit_address).uppercase()
                    else
                        stringResource(id = R.string.create_address).uppercase(),
                    style = MaterialTheme.typography.button,
                )
            }
        }
    }
}