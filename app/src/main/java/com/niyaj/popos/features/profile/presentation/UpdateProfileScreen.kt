package com.niyaj.popos.features.profile.presentation

import android.Manifest
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.StarHalf
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.niyaj.popos.R
import com.niyaj.popos.features.common.ui.theme.SpaceSmall
import com.niyaj.popos.features.common.util.UiEvent
import com.niyaj.popos.features.components.StandardButtonFW
import com.niyaj.popos.features.components.StandardOutlinedTextField
import com.niyaj.popos.features.components.util.BottomSheetWithCloseDialog
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.ramcosta.composedestinations.spec.DestinationStyle

@OptIn(ExperimentalPermissionsApi::class)
@Destination(style = DestinationStyle.BottomSheet::class)
@Composable
fun UpdateProfileScreen(
    navController : NavController = rememberNavController(),
    profileViewModel : ProfileViewModel = hiltViewModel(),
    resultBackNavigator : ResultBackNavigator<String>
) {
    val scannedBitmap = profileViewModel.scannedBitmap.collectAsStateWithLifecycle().value

    val hasCameraPermission = rememberPermissionState(
        permission = Manifest.permission.CAMERA
    )

    fun requestCameraPermission() {
        if (!hasCameraPermission.status.isGranted) {
            hasCameraPermission.launchPermissionRequest()
        }
    }

    LaunchedEffect(key1 = true) {
        profileViewModel.eventFlow.collect { event ->
            when (event) {
                is UiEvent.Success -> {
                    resultBackNavigator.navigateBack(event.successMessage)
                }

                is UiEvent.Error -> {
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
                    leadingIcon = Icons.Default.Restaurant,
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
                    hint = "Restaurant Email",
                    leadingIcon = Icons.Default.Email,
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
                    leadingIcon = Icons.Default.PhoneAndroid,
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
                    leadingIcon = Icons.Default.Phone,
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
                    text = profileViewModel.updateState.tagline,
                    hint = "Restaurant Tagline",
                    leadingIcon = Icons.Default.StarHalf,
                    error = profileViewModel.updateState.taglineError,
                    onValueChange = {
                        profileViewModel.onEvent(ProfileEvent.TaglineChanged(it))
                    },
                )

                Spacer(modifier = Modifier.height(SpaceSmall))
            }

            item {
                StandardOutlinedTextField(
                    modifier = Modifier,
                    text = profileViewModel.updateState.address,
                    hint = "Restaurant Address",
                    leadingIcon = Icons.Default.LocationOn,
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
                    text = profileViewModel.updateState.description,
                    hint = "Restaurant Description",
                    singleLine = false,
                    maxLines = 2,
                    leadingIcon = Icons.Default.Notes,
                    error = null,
                    onValueChange = {
                        profileViewModel.onEvent(ProfileEvent.DescriptionChanged(it))
                    },
                )

                Spacer(modifier = Modifier.height(SpaceSmall))
            }

            item {
                StandardOutlinedTextField(
                    modifier = Modifier,
                    text = profileViewModel.updateState.paymentQrCode,
                    hint = "Restaurant Payment QR Code",
                    leadingIcon = Icons.Default.QrCode,
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                requestCameraPermission()
                                profileViewModel.onEvent(ProfileEvent.StartScanning)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.QrCodeScanner,
                                contentDescription = "Scan QR Code"
                            )
                        }
                    },
                    error = profileViewModel.updateState.paymentQrCodeError,
                    singleLine = false,
                    maxLines = 2,
                    onValueChange = {
                        profileViewModel.onEvent(ProfileEvent.PaymentQrCodeChanged(it))
                    },
                )

                Spacer(modifier = Modifier.height(SpaceSmall))
            }

            item {
                if (scannedBitmap != null) {
                    Spacer(modifier = Modifier.height(SpaceSmall))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Image(
                            bitmap = scannedBitmap.asImageBitmap(),
                            contentDescription = "QR Code",
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.Center)
                        )
                    }

                    Spacer(modifier = Modifier.height(SpaceSmall))
                }
            }

            item {
                Spacer(modifier = Modifier.height(SpaceSmall))

                StandardButtonFW(
                    text = stringResource(id = R.string.update_restaurant_info),
                    icon = Icons.Default.Edit,
                    onClick = {
                        profileViewModel.onEvent(ProfileEvent.UpdateProfile)
                    }
                )

                Spacer(modifier = Modifier.height(SpaceSmall))
            }
        }
    }
}