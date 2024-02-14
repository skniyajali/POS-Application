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
import androidx.compose.material.FabPosition
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ScaffoldState
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.niyaj.common.tags.ProfileTestTags.PROFILE_SCREEN
import com.niyaj.common.utils.ImageStorageManager
import com.niyaj.common.utils.toBitmap
import com.niyaj.designsystem.theme.LightColor6
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.feature.profile.components.AccountInfo
import com.niyaj.feature.profile.components.RestaurantCard
import com.niyaj.feature.profile.destinations.UpdateProfileScreenDestination
import com.niyaj.model.RESTAURANT_LOGO_NAME
import com.niyaj.model.RESTAURANT_PRINT_LOGO_NAME
import com.niyaj.ui.components.ScrollToTop
import com.niyaj.ui.components.SettingsCard
import com.niyaj.ui.components.StandardScaffoldNew
import com.niyaj.ui.event.UiEvent
import com.niyaj.ui.util.Screens
import com.niyaj.ui.util.isScrolled
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
    val context = LocalContext.current
    val lazyListState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    val info = viewModel.info.collectAsStateWithLifecycle().value
    val accountInfo = viewModel.accountInfo.collectAsStateWithLifecycle().value

    val resLogo = info.getRestaurantLogo(context)
    val printLogo = info.getRestaurantPrintLogo(context)

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
            val result = ImageStorageManager.saveToInternalStorage(
                context,
                uri.toBitmap(context),
                RESTAURANT_LOGO_NAME
            )

            scope.launch {
                if (result) {
                    scaffoldState.snackbarHostState.showSnackbar("Profile image saved successfully.")
                    viewModel.onEvent(ProfileEvent.LogoChanged)
                } else {
                    scaffoldState.snackbarHostState.showSnackbar("Unable save image into storage.")
                }
            }
        }
    }

    val printLogoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let {
            val result = ImageStorageManager.saveToInternalStorage(
                context,
                uri.toBitmap(context),
                RESTAURANT_PRINT_LOGO_NAME
            )

            scope.launch {
                if (result) {
                    scaffoldState.snackbarHostState.showSnackbar("Print Image saved successfully.")
                    viewModel.onEvent(ProfileEvent.PrintLogoChanged)
                } else {
                    scaffoldState.snackbarHostState.showSnackbar("Unable save print image into storage.")
                }
            }
        }
    }


    StandardScaffoldNew(
        navController = navController,
        scaffoldState = scaffoldState,
        showBackButton = true,
        selectionCount = 0,
        title = PROFILE_SCREEN,
        navActions = {
            IconButton(
                onClick = {
                    navController.navigate(UpdateProfileScreenDestination())
                }
            ) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit Profile")
            }
        },
        showFab = true,
        floatingActionButton = {
            ScrollToTop(
                visible = lazyListState.isScrolled,
                onClick = {
                    scope.launch {
                        lazyListState.animateScrollToItem(index = 0)
                    }
                },
            )
        },
        fabPosition = FabPosition.End,
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(SpaceSmall)
                .background(LightColor6),
            state = lazyListState,
            verticalArrangement = Arrangement.spacedBy(SpaceSmall)
        ) {
            item("Restaurant Info") {
                RestaurantCard(
                    info = info,
                    resLogo = resLogo,
                    printLogo = printLogo,
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
                AccountInfo(account = accountInfo)
            }

            item("Change Password") {
                SettingsCard(
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


