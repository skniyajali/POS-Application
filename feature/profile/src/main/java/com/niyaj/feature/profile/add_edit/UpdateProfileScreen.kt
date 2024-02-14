package com.niyaj.feature.profile.add_edit

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
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.automirrored.filled.StarHalf
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.testTag
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.niyaj.common.tags.ProfileTestTags.ADDRESS_ERROR_FIELD
import com.niyaj.common.tags.ProfileTestTags.ADDRESS_FIELD
import com.niyaj.common.tags.ProfileTestTags.ADD_EDIT_PROFILE_BTN
import com.niyaj.common.tags.ProfileTestTags.DESC_ERROR_FIELD
import com.niyaj.common.tags.ProfileTestTags.DESC_FIELD
import com.niyaj.common.tags.ProfileTestTags.EMAIL_ERROR_FIELD
import com.niyaj.common.tags.ProfileTestTags.EMAIL_FIELD
import com.niyaj.common.tags.ProfileTestTags.NAME_ERROR_FIELD
import com.niyaj.common.tags.ProfileTestTags.NAME_FIELD
import com.niyaj.common.tags.ProfileTestTags.P_PHONE_ERROR_FIELD
import com.niyaj.common.tags.ProfileTestTags.P_PHONE_FIELD
import com.niyaj.common.tags.ProfileTestTags.QR_CODE_ERROR
import com.niyaj.common.tags.ProfileTestTags.QR_CODE_FIELD
import com.niyaj.common.tags.ProfileTestTags.S_PHONE_ERROR_FIELD
import com.niyaj.common.tags.ProfileTestTags.S_PHONE_FIELD
import com.niyaj.common.tags.ProfileTestTags.TAG_ERROR_FIELD
import com.niyaj.common.tags.ProfileTestTags.TAG_FIELD
import com.niyaj.common.tags.ProfileTestTags.UPDATE_PROFILE
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.feature.profile.ProfileEvent
import com.niyaj.feature.profile.ProfileViewModel
import com.niyaj.ui.components.StandardButtonFW
import com.niyaj.ui.components.StandardOutlinedTextField
import com.niyaj.ui.event.UiEvent
import com.niyaj.ui.util.BottomSheetWithCloseDialog
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.ramcosta.composedestinations.spec.DestinationStyleBottomSheet

@Destination(style = DestinationStyleBottomSheet::class)
@Composable
fun UpdateProfileScreen(
    navController: NavController = rememberNavController(),
    profileViewModel: ProfileViewModel = hiltViewModel(),
    resultBackNavigator: ResultBackNavigator<String>
) {
    val scannedBitmap = profileViewModel.scannedBitmap.collectAsStateWithLifecycle().value

    LaunchedEffect(key1 = true) {
        profileViewModel.eventFlow.collect { event ->
            when (event) {
                is UiEvent.Success -> {
                    resultBackNavigator.navigateBack(event.successMessage)
                }

                is UiEvent.Error -> {
                    resultBackNavigator.navigateBack(event.errorMessage)
                }
            }
        }
    }

    LaunchedEffect(key1 = true) {
        profileViewModel.onEvent(ProfileEvent.SetProfileInfo)
    }

    BottomSheetWithCloseDialog(
        modifier = Modifier.fillMaxSize(),
        text = UPDATE_PROFILE,
        onClosePressed = {
            navController.navigateUp()
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            item(NAME_FIELD) {
                StandardOutlinedTextField(
                    modifier = Modifier.testTag(NAME_FIELD),
                    text = profileViewModel.updateState.name,
                    label = NAME_FIELD,
                    leadingIcon = Icons.Default.Restaurant,
                    error = profileViewModel.updateState.nameError,
                    errorTag = NAME_ERROR_FIELD,
                    onValueChange = {
                        profileViewModel.onEvent(ProfileEvent.NameChanged(it))
                    },
                )

                Spacer(modifier = Modifier.height(SpaceSmall))
            }

            item(EMAIL_FIELD) {
                StandardOutlinedTextField(
                    modifier = Modifier.testTag(EMAIL_FIELD),
                    text = profileViewModel.updateState.email,
                    label = EMAIL_FIELD,
                    leadingIcon = Icons.Default.Email,
                    errorTag = EMAIL_ERROR_FIELD,
                    error = profileViewModel.updateState.emailError,
                    onValueChange = {
                        profileViewModel.onEvent(ProfileEvent.NameChanged(it))
                    },
                )

                Spacer(modifier = Modifier.height(SpaceSmall))
            }

            item(P_PHONE_FIELD) {
                StandardOutlinedTextField(
                    modifier = Modifier.testTag(P_PHONE_FIELD),
                    text = profileViewModel.updateState.primaryPhone,
                    label = P_PHONE_FIELD,
                    leadingIcon = Icons.Default.PhoneAndroid,
                    errorTag = P_PHONE_ERROR_FIELD,
                    error = profileViewModel.updateState.primaryPhoneError,
                    onValueChange = {
                        profileViewModel.onEvent(ProfileEvent.PrimaryPhoneChanged(it))
                    },
                )

                Spacer(modifier = Modifier.height(SpaceSmall))
            }

            item(S_PHONE_FIELD) {
                StandardOutlinedTextField(
                    modifier = Modifier.testTag(S_PHONE_FIELD),
                    text = profileViewModel.updateState.secondaryPhone,
                    label = S_PHONE_FIELD,
                    leadingIcon = Icons.Default.Phone,
                    errorTag = S_PHONE_ERROR_FIELD,
                    error = profileViewModel.updateState.secondaryPhoneError,
                    onValueChange = {
                        profileViewModel.onEvent(ProfileEvent.SecondaryPhoneChanged(it))
                    },
                )

                Spacer(modifier = Modifier.height(SpaceSmall))
            }

            item(TAG_FIELD) {
                StandardOutlinedTextField(
                    modifier = Modifier.testTag(TAG_FIELD),
                    text = profileViewModel.updateState.tagline,
                    label = TAG_FIELD,
                    errorTag = TAG_ERROR_FIELD,
                    leadingIcon = Icons.AutoMirrored.Filled.StarHalf,
                    error = profileViewModel.updateState.taglineError,
                    onValueChange = {
                        profileViewModel.onEvent(ProfileEvent.TaglineChanged(it))
                    },
                )

                Spacer(modifier = Modifier.height(SpaceSmall))
            }

            item(ADDRESS_FIELD) {
                StandardOutlinedTextField(
                    modifier = Modifier.testTag(ADDRESS_FIELD),
                    text = profileViewModel.updateState.address,
                    label = ADDRESS_FIELD,
                    maxLines = 2,
                    leadingIcon = Icons.Default.LocationOn,
                    errorTag = ADDRESS_ERROR_FIELD,
                    error = profileViewModel.updateState.addressError,
                    onValueChange = {
                        profileViewModel.onEvent(ProfileEvent.AddressChanged(it))
                    },
                )

                Spacer(modifier = Modifier.height(SpaceSmall))
            }

            item(DESC_FIELD) {
                StandardOutlinedTextField(
                    modifier = Modifier.testTag(DESC_FIELD),
                    text = profileViewModel.updateState.description,
                    label = DESC_FIELD,
                    singleLine = false,
                    maxLines = 2,
                    leadingIcon = Icons.AutoMirrored.Filled.Notes,
                    error = profileViewModel.updateState.descriptionError,
                    errorTag = DESC_ERROR_FIELD,
                    onValueChange = {
                        profileViewModel.onEvent(ProfileEvent.DescriptionChanged(it))
                    },
                )

                Spacer(modifier = Modifier.height(SpaceSmall))
            }

            item(QR_CODE_FIELD) {
                StandardOutlinedTextField(
                    modifier = Modifier.testTag(QR_CODE_FIELD),
                    text = profileViewModel.updateState.paymentQrCode,
                    label = QR_CODE_FIELD,
                    leadingIcon = Icons.Default.QrCode,
                    trailingIcon = {
                        IconButton(
                            onClick = {
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
                    errorTag = QR_CODE_ERROR,
                    singleLine = false,
                    maxLines = 4,
                    onValueChange = {
                        profileViewModel.onEvent(ProfileEvent.PaymentQrCodeChanged(it))
                    },
                )

                Spacer(modifier = Modifier.height(SpaceSmall))
            }

            item("Scanned Bitmap") {
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

            item(ADD_EDIT_PROFILE_BTN) {
                Spacer(modifier = Modifier.height(SpaceSmall))

                StandardButtonFW(
                    modifier = Modifier.testTag(ADD_EDIT_PROFILE_BTN),
                    text = UPDATE_PROFILE,
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