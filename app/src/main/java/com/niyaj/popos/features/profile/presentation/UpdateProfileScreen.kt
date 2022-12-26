package com.niyaj.popos.features.profile.presentation

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.niyaj.popos.R
import com.niyaj.popos.features.common.ui.theme.ButtonSize
import com.niyaj.popos.features.common.ui.theme.SpaceSmall
import com.niyaj.popos.features.common.util.UiEvent
import com.niyaj.popos.features.components.StandardOutlinedTextField
import com.niyaj.popos.features.components.util.BottomSheetWithCloseDialog
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.ramcosta.composedestinations.spec.DestinationStyle

@Destination(style = DestinationStyle.BottomSheet::class)
@Composable
fun UpdateProfileScreen(
    navController: NavController = rememberNavController(),
    profileViewModel: ProfileViewModel = hiltViewModel(),
    resultBackNavigator: ResultBackNavigator<String>
) {

    LaunchedEffect(key1 = true) {
        profileViewModel.eventFlow.collect { event ->
            when (event) {
                is UiEvent.OnSuccess -> {
                    resultBackNavigator.navigateBack(event.successMessage)
                }

                is UiEvent.OnError -> {
                    resultBackNavigator.navigateBack(event.errorMessage)
                }

                is UiEvent.IsLoading -> {}
            }
        }
    }

    LaunchedEffect(key1 = true) {
       profileViewModel.onEvent(ProfileEvent.SetProfileInfo)
    }

    BottomSheetWithCloseDialog(
        modifier = Modifier.fillMaxSize(),
        text = stringResource(id = R.string.update_restaurant_info),
        onClosePressed = {
            navController.navigateUp()
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            item {
                StandardOutlinedTextField(
                    modifier = Modifier,
                    text = profileViewModel.updateState.name,
                    hint = "Restaurant Name",
                    error = profileViewModel.updateState.nameError,
                    onValueChange = {
                        profileViewModel.onEvent(ProfileEvent.NameChanged(it))
                    },
                )

                Spacer(modifier = Modifier.height(SpaceSmall))
            }

            item {
                StandardOutlinedTextField(
                    modifier = Modifier,
                    text = profileViewModel.updateState.email,
                    hint = "Restaurant Name",
                    error = profileViewModel.updateState.emailError,
                    onValueChange = {
                        profileViewModel.onEvent(ProfileEvent.NameChanged(it))
                    },
                )

                Spacer(modifier = Modifier.height(SpaceSmall))
            }
            item {
                StandardOutlinedTextField(
                    modifier = Modifier,
                    text = profileViewModel.updateState.primaryPhone,
                    hint = "Restaurant Primary Phone",
                    error = profileViewModel.updateState.primaryPhoneError,
                    onValueChange = {
                        profileViewModel.onEvent(ProfileEvent.PrimaryPhoneChanged(it))
                    },
                )

                Spacer(modifier = Modifier.height(SpaceSmall))
            }

            item {
                StandardOutlinedTextField(
                    modifier = Modifier,
                    text = profileViewModel.updateState.secondaryPhone,
                    hint = "Restaurant Secondary Phone",
                    error = profileViewModel.updateState.secondaryPhoneError,
                    onValueChange = {
                        profileViewModel.onEvent(ProfileEvent.SecondaryPhoneChanged(it))
                    },
                )

                Spacer(modifier = Modifier.height(SpaceSmall))
            }

            item {
                StandardOutlinedTextField(
                    modifier = Modifier,
                    text = profileViewModel.updateState.email,
                    hint = "Restaurant Email",
                    error = profileViewModel.updateState.emailError,
                    onValueChange = {
                        profileViewModel.onEvent(ProfileEvent.EmailChanged(it))
                    },
                )

                Spacer(modifier = Modifier.height(SpaceSmall))
            }

            item {
                StandardOutlinedTextField(
                    modifier = Modifier,
                    text = profileViewModel.updateState.address,
                    hint = "Restaurant Address",
                    error = profileViewModel.updateState.addressError,
                    onValueChange = {
                        profileViewModel.onEvent(ProfileEvent.AddressChanged(it))
                    },
                )

                Spacer(modifier = Modifier.height(SpaceSmall))
            }

            item {
                StandardOutlinedTextField(
                    modifier = Modifier,
                    text = profileViewModel.updateState.paymentQrCode,
                    hint = "Restaurant Payment QR Code",
                    error = profileViewModel.updateState.paymentQrCodeError,
                    maxLines = 2,
                    onValueChange = {
                        profileViewModel.onEvent(ProfileEvent.PaymentQrCodeChanged(it))
                    },
                )

                Spacer(modifier = Modifier.height(SpaceSmall))
            }

            item {
                StandardOutlinedTextField(
                    modifier = Modifier,
                    text = profileViewModel.updateState.description,
                    hint = "Restaurant Description",
                    error = null,
                    onValueChange = {
                        profileViewModel.onEvent(ProfileEvent.DescriptionChanged(it))
                    },
                )

                Spacer(modifier = Modifier.height(SpaceSmall))
            }

            item {
                Button(
                    onClick = {
                        profileViewModel.onEvent(ProfileEvent.UpdateProfile)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(ButtonSize),
                ) {
                    Text(
                        text = stringResource(id = R.string.update_restaurant_info).uppercase(),
                        style = MaterialTheme.typography.button,
                    )
                }

                Spacer(modifier = Modifier.height(SpaceSmall))
            }
        }
    }
}