package com.niyaj.feature.profile.add_edit

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.offset
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
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
import com.niyaj.feature.profile.components.UpdatedRestaurantCard
import com.niyaj.ui.components.StandardButtonFW
import com.niyaj.ui.components.StandardOutlinedTextField
import com.niyaj.ui.event.UiEvent
import com.niyaj.ui.util.Screens
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.result.ResultBackNavigator
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class)
@Destination(route = Screens.UPDATE_PROFILE_SCREEN)
@Composable
fun UpdateProfileScreen(
    navController: NavController = rememberNavController(),
    viewModel: ProfileViewModel = hiltViewModel(),
    resultBackNavigator: ResultBackNavigator<String>
) {
    val scaffoldState = rememberScaffoldState()
    val lazyListState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    val info = viewModel.info.collectAsStateWithLifecycle().value

    val scannedBitmap = viewModel.scannedBitmap.collectAsStateWithLifecycle().value

    val permissionState = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        rememberMultiplePermissionsState(
            permissions = listOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.READ_MEDIA_IMAGES
            )
        )
    } else {
        rememberMultiplePermissionsState(
            permissions = listOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
            )
        )
    }

    fun checkForMediaPermission() {
        if (!permissionState.allPermissionsGranted) {
            permissionState.launchMultiplePermissionRequest()
        }
    }

    var showPrintLogo by rememberSaveable {
        mutableStateOf(false)
    }

    val resLogoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let {
            scope.launch {
                viewModel.onEvent(ProfileEvent.LogoChanged(uri = it))
            }
        }
    }

    val printLogoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let {
            scope.launch {
                viewModel.onEvent(ProfileEvent.PrintLogoChanged(uri = it))
            }
        }
    }

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collect { event ->
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
        viewModel.onEvent(ProfileEvent.SetProfileInfo)
    }

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is UiEvent.Success -> {
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = event.successMessage
                    )
                }

                is UiEvent.Error -> {
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = event.errorMessage
                    )
                }

            }
        }
    }

    Scaffold(
        scaffoldState = scaffoldState,
        modifier = Modifier
            .fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(text = UPDATE_PROFILE) },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.navigateUp() }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Navigate Back"
                        )
                    }
                },
                elevation = 0.dp
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues),
            state = lazyListState,
            contentPadding = PaddingValues(SpaceSmall),
            verticalArrangement = Arrangement.spacedBy(SpaceSmall)
        ) {
            val sidePadding = (-8).dp

            item("UpdatedRestaurantCard") {
                UpdatedRestaurantCard(
                    modifier = Modifier
                        .layout { measurable, constraints ->
                            // Measure the composable adding the side padding*2 (left+right)
                            val placeable =
                                measurable.measure(
                                    constraints.offset(
                                        horizontal = -sidePadding.roundToPx() * 2,
                                        vertical = -sidePadding.roundToPx()
                                    )
                                )

                            //increase the width adding the side padding*2
                            layout(
                                placeable.width + sidePadding.roundToPx() * 2,
                                placeable.height
                            ) {
                                // Where the composable gets placed
                                placeable.place(+sidePadding.roundToPx(), +sidePadding.roundToPx())
                            }

                        },
                    info = info,
                    showPrintLogo = showPrintLogo,
                    onClickEdit = {
                        checkForMediaPermission()
                        resLogoLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    onClickChangePrintLogo = {
                        checkForMediaPermission()
                        printLogoLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    onClickViewPrintLogo = {
                        showPrintLogo = !showPrintLogo
                    }
                )
            }

            item(NAME_FIELD) {
                StandardOutlinedTextField(
                    modifier = Modifier
                        .padding(horizontal = SpaceSmall)
                        .testTag(NAME_FIELD),
                    text = viewModel.updateState.name,
                    label = NAME_FIELD,
                    leadingIcon = Icons.Default.Restaurant,
                    error = viewModel.updateState.nameError,
                    errorTag = NAME_ERROR_FIELD,
                    onValueChange = {
                        viewModel.onEvent(ProfileEvent.NameChanged(it))
                    },
                )
            }

            item(EMAIL_FIELD) {
                StandardOutlinedTextField(
                    modifier = Modifier
                        .padding(horizontal = SpaceSmall)
                        .testTag(EMAIL_FIELD),
                    text = viewModel.updateState.email,
                    label = EMAIL_FIELD,
                    leadingIcon = Icons.Default.Email,
                    errorTag = EMAIL_ERROR_FIELD,
                    error = viewModel.updateState.emailError,
                    onValueChange = {
                        viewModel.onEvent(ProfileEvent.NameChanged(it))
                    },
                )
            }

            item(P_PHONE_FIELD) {
                StandardOutlinedTextField(
                    modifier = Modifier
                        .padding(horizontal = SpaceSmall)
                        .testTag(P_PHONE_FIELD),
                    text = viewModel.updateState.primaryPhone,
                    label = P_PHONE_FIELD,
                    leadingIcon = Icons.Default.PhoneAndroid,
                    errorTag = P_PHONE_ERROR_FIELD,
                    error = viewModel.updateState.primaryPhoneError,
                    onValueChange = {
                        viewModel.onEvent(ProfileEvent.PrimaryPhoneChanged(it))
                    },
                )
            }

            item(S_PHONE_FIELD) {
                StandardOutlinedTextField(
                    modifier = Modifier
                        .padding(horizontal = SpaceSmall)
                        .testTag(S_PHONE_FIELD),
                    text = viewModel.updateState.secondaryPhone,
                    label = S_PHONE_FIELD,
                    leadingIcon = Icons.Default.Phone,
                    errorTag = S_PHONE_ERROR_FIELD,
                    error = viewModel.updateState.secondaryPhoneError,
                    onValueChange = {
                        viewModel.onEvent(ProfileEvent.SecondaryPhoneChanged(it))
                    },
                )
            }

            item(TAG_FIELD) {
                StandardOutlinedTextField(
                    modifier = Modifier
                        .padding(horizontal = SpaceSmall)
                        .testTag(TAG_FIELD),
                    text = viewModel.updateState.tagline,
                    label = TAG_FIELD,
                    errorTag = TAG_ERROR_FIELD,
                    leadingIcon = Icons.AutoMirrored.Filled.StarHalf,
                    error = viewModel.updateState.taglineError,
                    onValueChange = {
                        viewModel.onEvent(ProfileEvent.TaglineChanged(it))
                    },
                )
            }

            item(ADDRESS_FIELD) {
                StandardOutlinedTextField(
                    modifier = Modifier
                        .padding(horizontal = SpaceSmall)
                        .testTag(ADDRESS_FIELD),
                    text = viewModel.updateState.address,
                    label = ADDRESS_FIELD,
                    maxLines = 2,
                    leadingIcon = Icons.Default.LocationOn,
                    errorTag = ADDRESS_ERROR_FIELD,
                    error = viewModel.updateState.addressError,
                    onValueChange = {
                        viewModel.onEvent(ProfileEvent.AddressChanged(it))
                    },
                )
            }

            item(DESC_FIELD) {
                StandardOutlinedTextField(
                    modifier = Modifier
                        .padding(horizontal = SpaceSmall)
                        .testTag(DESC_FIELD),
                    text = viewModel.updateState.description,
                    label = DESC_FIELD,
                    singleLine = false,
                    maxLines = 2,
                    leadingIcon = Icons.AutoMirrored.Filled.Notes,
                    error = viewModel.updateState.descriptionError,
                    errorTag = DESC_ERROR_FIELD,
                    onValueChange = {
                        viewModel.onEvent(ProfileEvent.DescriptionChanged(it))
                    },
                )
            }

            item(QR_CODE_FIELD) {
                StandardOutlinedTextField(
                    modifier = Modifier
                        .padding(horizontal = SpaceSmall)
                        .testTag(QR_CODE_FIELD),
                    text = viewModel.updateState.paymentQrCode,
                    label = QR_CODE_FIELD,
                    leadingIcon = Icons.Default.QrCode,
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                viewModel.onEvent(ProfileEvent.StartScanning)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.QrCodeScanner,
                                contentDescription = "Scan QR Code"
                            )
                        }
                    },
                    error = viewModel.updateState.paymentQrCodeError,
                    errorTag = QR_CODE_ERROR,
                    singleLine = false,
                    maxLines = 4,
                    onValueChange = {
                        viewModel.onEvent(ProfileEvent.PaymentQrCodeChanged(it))
                    },
                )
            }

            item("Scanned Bitmap") {
                if (scannedBitmap != null) {

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
                    modifier = Modifier
                        .padding(horizontal = SpaceSmall)
                        .testTag(ADD_EDIT_PROFILE_BTN),
                    text = UPDATE_PROFILE,
                    icon = Icons.Default.Edit,
                    onClick = {
                        viewModel.onEvent(ProfileEvent.UpdateProfile)
                    }
                )

                Spacer(modifier = Modifier.height(SpaceSmall))
            }
        }
    }
}