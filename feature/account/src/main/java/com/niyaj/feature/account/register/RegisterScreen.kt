package com.niyaj.feature.account.register

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.tween
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.niyaj.common.utils.ImageStorageManager
import com.niyaj.common.utils.toBitmap
import com.niyaj.feature.account.destinations.RegistrationResultScreenDestination
import com.niyaj.feature.account.register.components.RegistrationScaffold
import com.niyaj.feature.account.register.components.basic_info.BasicInfo
import com.niyaj.feature.account.register.components.basic_info.BasicInfoEvent
import com.niyaj.feature.account.register.components.login_info.LoginInfo
import com.niyaj.feature.account.register.components.login_info.LoginInfoEvent
import com.niyaj.feature.account.register.components.registration_result.RegistrationResult
import com.niyaj.feature.account.register.utils.RegisterScreenPage
import com.niyaj.model.RESTAURANT_LOGO_NAME
import com.niyaj.model.RESTAURANT_PRINT_LOGO_NAME
import com.niyaj.ui.event.UiEvent
import com.niyaj.ui.util.Screens
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.navigate
import kotlinx.coroutines.launch

private const val CONTENT_ANIMATION_DURATION = 300


@Destination(route = Screens.REGISTER_SCREEN)
@Composable
fun RegisterScreen(
    navController: NavController,
    scaffoldState: ScaffoldState,
    viewModel: RegisterViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val lazyListState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    val screenData = viewModel.registerScreenState

    val emailError = viewModel.emailError.collectAsStateWithLifecycle().value
    val passwordError = viewModel.passwordError.collectAsStateWithLifecycle().value
    val phoneError = viewModel.phoneError.collectAsStateWithLifecycle().value

    val nameError = viewModel.nameError.collectAsStateWithLifecycle().value
    val secondaryPhoneError = viewModel.secondaryPhoneError.collectAsStateWithLifecycle().value
    val taglineError = viewModel.taglineError.collectAsStateWithLifecycle().value

    val addressError = viewModel.addressError.collectAsStateWithLifecycle().value
    val descError = viewModel.descError.collectAsStateWithLifecycle().value
    val paymentQrCodeError = viewModel.paymentQrCodeError.collectAsStateWithLifecycle().value

    val scannedBitmap = viewModel.scannedBitmap.collectAsStateWithLifecycle().value
    val resLogo = viewModel.resLogo.collectAsStateWithLifecycle().value
    val printLogo = viewModel.printLogo.collectAsStateWithLifecycle().value

    val event = viewModel.eventFlow.collectAsStateWithLifecycle(null).value

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
                    viewModel.onLoginInfoEvent(LoginInfoEvent.LogoChanged(RESTAURANT_LOGO_NAME))
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
                    viewModel.onBasicInfoEvent(
                        BasicInfoEvent.PrintLogoChanged(RESTAURANT_PRINT_LOGO_NAME)
                    )

                } else {
                    scaffoldState.snackbarHostState.showSnackbar("Unable save print image into storage.")
                }
            }
        }
    }

    LaunchedEffect(key1 = event) {
        event?.let {
            when (it) {
                is UiEvent.Success -> {
                    navController.navigate(
                        RegistrationResultScreenDestination(
                            result = RegistrationResult.Success,
                            message = it.successMessage
                        )
                    )
                }

                is UiEvent.Error -> {
                    navController.navigate(
                        RegistrationResultScreenDestination(
                            result = RegistrationResult.Failure,
                            message = it.errorMessage
                        )
                    )
                }
            }
        }
    }

    BackHandler {
        if (!viewModel.onBackPressed()) {
            navController.navigateUp()
        }
    }

    RegistrationScaffold(
        scaffoldState = scaffoldState,
        screenData = screenData,
        isNextEnabled = viewModel.isNextEnabled,
        onClosePressed = {
            navController.navigateUp()
        },
        onPreviousPressed = viewModel::onPreviousPressed,
        onNextPressed = viewModel::onNextPressed,
        onDonePressed = viewModel::onDonePressed,
    ) { paddingValues ->
        val modifier = Modifier.padding(paddingValues)

        AnimatedContent(
            targetState = screenData,
            transitionSpec = {
                val animationSpec: TweenSpec<IntOffset> =
                    tween(CONTENT_ANIMATION_DURATION)
                val direction = getTransitionDirection(
                    initialIndex = initialState.pageIndex,
                    targetIndex = targetState.pageIndex,
                )
                slideIntoContainer(
                    towards = direction,
                    animationSpec = animationSpec,
                ) togetherWith slideOutOfContainer(
                    towards = direction,
                    animationSpec = animationSpec
                )
            }, label = ""
        ) { targetState ->
            when (targetState.screenPage) {
                RegisterScreenPage.LOGIN_INFO -> {
                    LoginInfo(
                        modifier = modifier,
                        lazyListState = lazyListState,
                        name = viewModel.loginInfoState.name,
                        nameError = nameError,
                        secondaryPhone = viewModel.loginInfoState.secondaryPhone,
                        secondaryPhoneError = secondaryPhoneError,
                        email = viewModel.loginInfoState.email,
                        phone = viewModel.loginInfoState.phone,
                        password = viewModel.loginInfoState.password,
                        emailError = emailError,
                        phoneError = phoneError,
                        passwordError = passwordError,
                        resLogo = resLogo,
                        onChangeName = viewModel::onLoginInfoEvent,
                        onChangePhone = viewModel::onLoginInfoEvent,
                        onChangeEmail = viewModel::onLoginInfoEvent,
                        onChangeSecondaryPhone = viewModel::onLoginInfoEvent,
                        onChangePassword = viewModel::onLoginInfoEvent,
                        onChangeLogo = {
                            resLogoLauncher.launch(
                                PickVisualMediaRequest(
                                    ActivityResultContracts.PickVisualMedia.ImageOnly
                                )
                            )
                        }
                    )
                }

                RegisterScreenPage.BASIC_INFO -> {
                    BasicInfo(
                        modifier = modifier,
                        lazyListState = lazyListState,
                        tagline = viewModel.basicInfoState.tagline,
                        taglineError = taglineError,
                        address = viewModel.basicInfoState.address,
                        addressError = addressError,
                        description = viewModel.basicInfoState.description,
                        descriptionError = descError,
                        paymentQRCode = viewModel.basicInfoState.paymentQrCode,
                        paymentQRCodeError = paymentQrCodeError,
                        scannedBitmap = scannedBitmap,
                        printLogo = printLogo,
                        onChangeAddress = viewModel::onBasicInfoEvent,
                        onChangeDescription = viewModel::onBasicInfoEvent,
                        onChangePaymentQRCode = viewModel::onBasicInfoEvent,
                        onClickScanCode = viewModel::onBasicInfoEvent,
                        onChangeTagline = viewModel::onBasicInfoEvent,
                        onChangeLogo = {
                            printLogoLauncher.launch(
                                PickVisualMediaRequest(
                                    ActivityResultContracts.PickVisualMedia.ImageOnly
                                )
                            )
                        }
                    )
                }
            }
        }
    }
}


private fun getTransitionDirection(
    initialIndex: Int,
    targetIndex: Int
): AnimatedContentTransitionScope.SlideDirection {
    return if (targetIndex > initialIndex) {
        // Going forwards in the survey: Set the initial offset to start
        // at the size of the content so it slides in from right to left, and
        // slides out from the left of the screen to -fullWidth
        AnimatedContentTransitionScope.SlideDirection.Left
    } else {
        // Going back to the previous question in the set, we do the same
        // transition as above, but with different offsets - the inverse of
        // above, negative fullWidth to enter, and fullWidth to exit.
        AnimatedContentTransitionScope.SlideDirection.Right
    }
}
