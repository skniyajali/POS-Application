package com.niyaj.popos.features.account.presentation.login

import android.Manifest
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.BackdropScaffoldDefaults
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Login
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.SettingsBackupRestore
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.niyaj.popos.R
import com.niyaj.popos.features.common.ui.theme.IconSizeLarge
import com.niyaj.popos.features.common.ui.theme.SpaceMedium
import com.niyaj.popos.features.common.ui.theme.SpaceMini
import com.niyaj.popos.features.common.ui.theme.SpaceSmall
import com.niyaj.popos.features.common.ui.theme.TextGray
import com.niyaj.popos.features.common.util.UiEvent
import com.niyaj.popos.features.components.NoteCard
import com.niyaj.popos.features.components.SettingsCard
import com.niyaj.popos.features.components.StandardButtonFW
import com.niyaj.popos.features.components.StandardOutlinedTextField
import com.niyaj.popos.features.destinations.MainFeedScreenDestination
import com.niyaj.popos.features.destinations.RegisterScreenDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.navigate

/**
 * A Composable function representing the login screen.
 * @author Sk Niyaj Ali
 * @param navController The NavController used for navigation within the app.
 * @param showRestoreButton A boolean indicating whether to show the restore button.
 * @param viewModel The LoginViewModel used for managing the login screen's state and logic.
 */
@OptIn(ExperimentalPermissionsApi::class)
@RootNavGraph(start = true)
@Destination
@Composable
fun LoginScreen(
    navController : NavController,
    showRestoreButton : Boolean = false,
    viewModel : LoginViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val lazyListState = rememberLazyListState()

    val event = viewModel.eventFlow.collectAsStateWithLifecycle(initialValue = null).value

    val checkLoggedIn = viewModel.isLoggedIn

    var error by remember {
        mutableStateOf<String?>(null)
    }

    var showPassword by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(key1 = checkLoggedIn, key2 = Unit) {
        if (checkLoggedIn) {
            navController.navigate(MainFeedScreenDestination()) {
                popUpTo(navController.graph.id) {
                    inclusive = true
                }
            }
        }
    }

    LaunchedEffect(key1 = event) {
        event?.let { result ->
            when (result) {
                is UiEvent.IsLoading -> {}
                is UiEvent.Success -> {
                    navController.navigate(MainFeedScreenDestination()) {
                        popUpTo(navController.graph.id) {
                            inclusive = true
                        }
                    }
                }

                is UiEvent.Error -> {
                    error = result.errorMessage
                }
            }
        }
    }

    val hasStoragePermission =
        rememberMultiplePermissionsState(
            permissions = listOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
            )
        )

    fun askForPermissions() {
        if (!hasStoragePermission.allPermissionsGranted) {
            hasStoragePermission.launchMultiplePermissionRequest()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(230.dp)
                .background(MaterialTheme.colors.primary),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(R.drawable.main),
                contentDescription = "",
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center)
                    .size(width = 300.dp, height = 230.dp)
                    .padding(bottom = 10.dp)
            )
        }

        Card(
            shape = BackdropScaffoldDefaults.frontLayerShape,
            elevation = 0.dp,
            modifier = Modifier.offset(y = -SpaceSmall)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(SpaceMedium),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                LoginForm(
                    modifier = Modifier,
                    lazyListState = lazyListState,
                    emailOrPhone = viewModel.state.emailOrPhone,
                    password = viewModel.state.password,
                    emailError = viewModel.state.emailError,
                    passwordError = viewModel.state.passwordError,
                    errorMessage = error,
                    showPassword = showPassword,
                    onTogglePassword = {
                        showPassword = !showPassword
                    },
                    onEmailOrPhoneChanged = {
                        viewModel.onEvent(LoginEvent.EmailOrPhoneChanged(it))
                    },
                    onPasswordChanged = {
                        viewModel.onEvent(LoginEvent.PasswordChanged(it))
                    },
                    onClickLogin = {
                        viewModel.onEvent(LoginEvent.OnClickLogin)
                    },
                    onClickRegister = {
                        navController.navigate(RegisterScreenDestination())
                    },
                )
                if (showRestoreButton) {
                    SettingsCard(
                        text = "Restore Database",
                        icon = Icons.Default.SettingsBackupRestore,
                        onClick = {
                            askForPermissions()
                            viewModel.restoreDatabase(context)
                        },
                    )
                }
            }
        }
    }
}

/**
 * A composable function that renders a login form UI.
 *
 * @author Sk Niyaj Ali
 * @param modifier Modifier for customizing the layout of the login form.
 * @param lazyListState State object for managing the scroll position of the LazyColumn.
 * @param emailOrPhone Current value of the email/phone field.
 * @param password Current value of the password field.
 * @param showPassword Flag indicating whether the password should be shown or hidden.
 * @param onTogglePassword Callback function invoked when the password visibility toggle is clicked.
 * @param emailError Error message for the email/phone field, if any.
 * @param passwordError Error message for the password field, if any.
 * @param errorMessage Error message to be displayed, if any.
 * @param onEmailOrPhoneChanged Callback function invoked when the email/phone field value changes.
 * @param onPasswordChanged Callback function invoked when the password field value changes.
 * @param onClickLogin Callback function invoked when the login button is clicked.
 * @param onClickRegister Callback function invoked when the register button is clicked.
 */
@Composable
fun LoginForm(
    modifier : Modifier = Modifier,
    lazyListState : LazyListState,
    emailOrPhone : String,
    password : String,
    showPassword : Boolean,
    onTogglePassword : (Boolean) -> Unit,
    emailError : String? = null,
    passwordError : String? = null,
    errorMessage : String? = null,
    onEmailOrPhoneChanged : (String) -> Unit,
    onPasswordChanged : (String) -> Unit,
    onClickLogin : () -> Unit,
    onClickRegister : () -> Unit,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(SpaceMedium),
        state = lazyListState,
    ) {
        item("Welcome_Text") {
            Spacer(modifier = Modifier.height(SpaceSmall))

            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(SpaceMini),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(
                        SpaceSmall,
                        Alignment.CenterHorizontally
                    ),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(R.string.welcome),
                        style = MaterialTheme.typography.h5,
                        fontWeight = FontWeight.Bold,
                    )

                    Image(
                        painter = painterResource(id = R.drawable.hi),
                        contentDescription = "Hi",
                        modifier = Modifier.size(IconSizeLarge),
                    )
                }

                Text(
                    text = stringResource(R.string.login_text),
                    style = MaterialTheme.typography.subtitle1,
                    fontWeight = FontWeight.Normal,
                    color = TextGray,
                )
            }

            Spacer(modifier = Modifier.height(SpaceSmall))
        }

        item("Email/Phone_Field") {
            StandardOutlinedTextField(
                text = emailOrPhone,
                leadingIcon = Icons.Default.AlternateEmail,
                error = emailError,
                label = stringResource(R.string.email_phone),
                onValueChange = onEmailOrPhoneChanged
            )
        }

        item("Password_Field") {
            StandardOutlinedTextField(
                text = password,
                leadingIcon = Icons.Default.Password,
                error = passwordError,
                isPasswordToggleDisplayed = true,
                isPasswordVisible = showPassword,
                onPasswordToggleClick = onTogglePassword,
                label = stringResource(R.string.password),
                onValueChange = onPasswordChanged
            )
        }

        errorMessage?.let {
            item("Message_Field") {
                NoteCard(text = it)
            }
        }

        item("Login_Button") {
            Spacer(modifier = Modifier.height(SpaceSmall))

            StandardButtonFW(
                text = stringResource(id = R.string.login),
                icon = Icons.AutoMirrored.Outlined.Login,
                onClick = onClickLogin
            )
        }

        item("Signup_Button") {
            Spacer(modifier = Modifier.height(SpaceSmall))

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(SpaceSmall, Alignment.CenterVertically)
            ) {
                Text(
                    text = stringResource(R.string.no_account),
                    style = MaterialTheme.typography.subtitle1,
                    fontWeight = FontWeight.Normal,
                    color = TextGray,
                )

                TextButton(
                    onClick = onClickRegister
                ) {
                    Text(text = stringResource(id = R.string.register))
                }
            }
        }
    }
}