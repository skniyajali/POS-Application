package com.niyaj.feature.profile

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Password
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.niyaj.common.tags.ProfileTestTags.PROFILE_SCREEN
import com.niyaj.designsystem.theme.LightColor6
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.feature.profile.components.AccountInfo
import com.niyaj.feature.profile.components.RestaurantCard
import com.niyaj.feature.profile.destinations.UpdateProfileScreenDestination
import com.niyaj.ui.components.SettingsCard
import com.niyaj.ui.event.UiEvent
import com.niyaj.ui.util.Screens
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import kotlinx.coroutines.launch

/**
 * Profile Screen Composable
 * @author Sk Niyaj Ali
 *
 */
@OptIn(ExperimentalPermissionsApi::class)
@RootNavGraph(start = true)
@Destination(route = Screens.PROFILE_SCREEN)
@Composable
fun ProfileScreen(
    navController: NavController,
    scaffoldState: ScaffoldState,
    viewModel: ProfileViewModel = hiltViewModel(),
    resultRecipient: ResultRecipient<UpdateProfileScreenDestination, String>
) {
    val lazyListState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    val info = viewModel.info.collectAsStateWithLifecycle().value
    val accountInfo = viewModel.accountInfo.collectAsStateWithLifecycle().value

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

    LaunchedEffect(key1 = Unit) {
        checkForMediaPermission()
    }

    var showPrintLogo by rememberSaveable {
        mutableStateOf(false)
    }

    val resLogoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let {
            viewModel.onEvent(ProfileEvent.LogoChanged(uri = it))
        }
    }

    val printLogoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let {
            viewModel.onEvent(ProfileEvent.PrintLogoChanged(uri = it))
        }
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

    resultRecipient.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {}
            is NavResult.Value -> {
                scope.launch {
                    scaffoldState.snackbarHostState.showSnackbar(result.value)
                }
            }
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = { Text(text = PROFILE_SCREEN) },
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
                actions = {
                    IconButton(
                        onClick = {
                            navController.navigate(UpdateProfileScreenDestination())
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Profile"
                        )
                    }
                },
                elevation = 0.dp
            )
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .background(LightColor6),
            state = lazyListState,
            verticalArrangement = Arrangement.spacedBy(SpaceSmall)
        ) {
            item("Restaurant Info") {
                RestaurantCard(
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

            item("Account Info") {
                AccountInfo(
                    modifier = Modifier.padding(SpaceSmall),
                    account = accountInfo
                )
            }

            item("Change Password") {
                SettingsCard(
                    modifier = Modifier.padding(SpaceSmall),
                    text = "Change Password",
                    icon = Icons.Default.Password
                ) {
                    navController.navigate(Screens.CHANGE_PASSWORD_SCREEN)
                }

                Spacer(modifier = Modifier.height(SpaceSmall))
            }
        }
    }
}