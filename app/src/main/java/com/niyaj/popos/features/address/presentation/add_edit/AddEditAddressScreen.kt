package com.niyaj.popos.features.address.presentation.add_edit

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.DomainAdd
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ShortText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.niyaj.popos.R
import com.niyaj.popos.features.address.domain.util.AddressTestTags.ADDRESS_FULL_NAME_ERROR
import com.niyaj.popos.features.address.domain.util.AddressTestTags.ADDRESS_FULL_NAME_FIELD
import com.niyaj.popos.features.address.domain.util.AddressTestTags.ADDRESS_SHORT_NAME_ERROR
import com.niyaj.popos.features.address.domain.util.AddressTestTags.ADDRESS_SHORT_NAME_FIELD
import com.niyaj.popos.features.address.domain.util.AddressTestTags.CREATE_UPDATE_ADDRESS_BUTTON
import com.niyaj.popos.features.common.ui.theme.SpaceMedium
import com.niyaj.popos.features.common.ui.theme.SpaceSmall
import com.niyaj.popos.features.common.util.UiEvent
import com.niyaj.popos.features.components.StandardButtonFW
import com.niyaj.popos.features.components.StandardOutlinedTextField
import com.niyaj.popos.features.components.util.BottomSheetWithCloseDialog
import com.niyaj.popos.features.destinations.AddEditAddressScreenDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.ramcosta.composedestinations.spec.DestinationStyle
import io.sentry.compose.SentryTraced

/**
 *  Add/Edit Address Screen
 *  @author Sk Niyaj Ali
 *  @param addressId[String]
 *  @param navController[NavController]
 *  @param addEditAddressViewModel
 *  @param resultNavigator[ResultBackNavigator]
 *  @see AddEditAddressViewModel
 */
@OptIn(ExperimentalComposeUiApi::class)
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
                is UiEvent.Success -> {
                    resultNavigator.navigateBack(event.successMessage)
                }

                is UiEvent.Error -> {
                    resultNavigator.navigateBack(event.errorMessage)
                }

                is UiEvent.IsLoading -> {}
            }
        }
    }

    SentryTraced(tag = AddEditAddressScreenDestination.route) {
        BottomSheetWithCloseDialog(
            modifier = Modifier.fillMaxWidth(),
            text = if (!addressId.isNullOrEmpty())
                stringResource(id = R.string.edit_address)
            else
                stringResource(id = R.string.create_address),
            icon = Icons.Default.DomainAdd,
            onClosePressed = {
                navController.navigateUp()
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                StandardOutlinedTextField(
                    modifier = Modifier.testTag(ADDRESS_FULL_NAME_FIELD),
                    text = addEditAddressViewModel.addEditAddressState.address,
                    hint = ADDRESS_FULL_NAME_FIELD,
                    leadingIcon = Icons.Default.Business,
                    error = addEditAddressViewModel.addEditAddressState.addressError,
                    errorTag = ADDRESS_FULL_NAME_ERROR,
                    onValueChange = {
                        addEditAddressViewModel.onAddressEvent(AddEditAddressEvent.AddressNameChanged(it))
                    },
                )

                Spacer(modifier = Modifier.height(SpaceSmall))

                StandardOutlinedTextField(
                    modifier = Modifier.testTag(ADDRESS_SHORT_NAME_FIELD),
                    text = addEditAddressViewModel.addEditAddressState.shortName,
                    hint = ADDRESS_SHORT_NAME_FIELD,
                    leadingIcon = Icons.Default.ShortText,
                    error = addEditAddressViewModel.addEditAddressState.shortNameError,
                    errorTag = ADDRESS_SHORT_NAME_ERROR,
                    onValueChange = {
                        addEditAddressViewModel.onAddressEvent(AddEditAddressEvent.ShortNameChanged(it))
                    },
                )

                Spacer(modifier = Modifier.height(SpaceMedium))

                StandardButtonFW(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag(CREATE_UPDATE_ADDRESS_BUTTON),
                    text = if (!addressId.isNullOrEmpty()) stringResource(id = R.string.edit_address)
                    else stringResource(id = R.string.create_address),
                    icon = if (!addressId.isNullOrEmpty()) Icons.Default.Edit else Icons.Default.Add,
                    onClick = {
                        if (!addressId.isNullOrEmpty()) {
                            addEditAddressViewModel.onAddressEvent(
                                AddEditAddressEvent.UpdateAddress(
                                    addressId
                                )
                            )
                        } else {
                            addEditAddressViewModel.onAddressEvent(AddEditAddressEvent.CreateNewAddress)
                        }
                    }
                )
            }
        }
    }
}