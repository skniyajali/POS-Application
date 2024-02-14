package com.niyaj.feature.address.add_edit

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ShortText
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.DomainAdd
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.niyaj.common.tags.AddressTestTags.ADDRESS_FULL_NAME_ERROR
import com.niyaj.common.tags.AddressTestTags.ADDRESS_FULL_NAME_FIELD
import com.niyaj.common.tags.AddressTestTags.ADDRESS_SHORT_NAME_ERROR
import com.niyaj.common.tags.AddressTestTags.ADDRESS_SHORT_NAME_FIELD
import com.niyaj.common.tags.AddressTestTags.CREATE_ADDRESS_SCREEN
import com.niyaj.common.tags.AddressTestTags.CREATE_UPDATE_ADDRESS_BUTTON
import com.niyaj.common.tags.AddressTestTags.UPDATE_ADDRESS_SCREEN
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.ui.components.StandardButtonFW
import com.niyaj.ui.components.StandardOutlinedTextField
import com.niyaj.ui.event.UiEvent
import com.niyaj.ui.util.BottomSheetWithCloseDialog
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.ramcosta.composedestinations.spec.DestinationStyleBottomSheet

/**
 *  Add/Edit Address Screen
 *  @author Sk Niyaj Ali
 *  @param addressId[String]
 *  @param navController[NavController]
 *  @param viewModel
 *  @param resultNavigator[ResultBackNavigator]
 *  @see AddEditAddressViewModel
 */
@Destination(style = DestinationStyleBottomSheet::class)
@Composable
fun AddEditAddressScreen(
    addressId: String? = "",
    navController: NavController,
    viewModel: AddEditAddressViewModel = hiltViewModel(),
    resultNavigator: ResultBackNavigator<String>
) {

    val nameError = viewModel.nameError.collectAsStateWithLifecycle().value
    val shortNameError = viewModel.shortNameError.collectAsStateWithLifecycle().value

    val enableBtn = nameError == null && shortNameError == null

    val title = if (addressId.isNullOrEmpty()) CREATE_ADDRESS_SCREEN else UPDATE_ADDRESS_SCREEN

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is UiEvent.Success -> {
                    resultNavigator.navigateBack(event.successMessage)
                }

                is UiEvent.Error -> {
                    resultNavigator.navigateBack(event.errorMessage)
                }
            }
        }
    }

    BottomSheetWithCloseDialog(
        modifier = Modifier.fillMaxWidth(),
        text = title,
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
                text = viewModel.state.addressName,
                label = ADDRESS_FULL_NAME_FIELD,
                leadingIcon = Icons.Default.Business,
                error = nameError,
                errorTag = ADDRESS_FULL_NAME_ERROR,
                onValueChange = {
                    viewModel.onAddressEvent(AddEditAddressEvent.AddressNameChanged(it))
                },
            )

            Spacer(modifier = Modifier.height(SpaceSmall))

            StandardOutlinedTextField(
                modifier = Modifier.testTag(ADDRESS_SHORT_NAME_FIELD),
                text = viewModel.state.shortName,
                label = ADDRESS_SHORT_NAME_FIELD,
                leadingIcon = Icons.AutoMirrored.Filled.ShortText,
                error = shortNameError,
                errorTag = ADDRESS_SHORT_NAME_ERROR,
                onValueChange = {
                    viewModel.onAddressEvent(AddEditAddressEvent.ShortNameChanged(it))
                },
            )

            Spacer(modifier = Modifier.height(SpaceMedium))

            StandardButtonFW(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(CREATE_UPDATE_ADDRESS_BUTTON),
                text = title,
                enabled = enableBtn,
                icon = if (!addressId.isNullOrEmpty()) Icons.Default.Edit else Icons.Default.Add,
                onClick = {
                    viewModel.onAddressEvent(AddEditAddressEvent.CreateOrUpdateAddress)
                }
            )
        }
    }
}