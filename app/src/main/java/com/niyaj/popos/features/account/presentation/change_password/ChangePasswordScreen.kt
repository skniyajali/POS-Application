package com.niyaj.popos.features.account.presentation.change_password

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.niyaj.popos.features.common.ui.theme.SpaceSmall
import com.niyaj.popos.features.common.util.UiEvent
import com.niyaj.popos.features.components.NoteCard
import com.niyaj.popos.features.components.StandardButtonFW
import com.niyaj.popos.features.components.StandardOutlinedTextField
import com.niyaj.popos.features.components.util.BottomSheetWithCloseDialog
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.ramcosta.composedestinations.spec.DestinationStyle
import kotlinx.coroutines.flow.collectLatest

@Destination(style = DestinationStyle.BottomSheet::class)
@Composable
fun ChangePasswordScreen(
    navController : NavController,
    viewModel : ChangePasswordViewModel = hiltViewModel(),
    resultBackNavigator : ResultBackNavigator<String>
) {
    val passwordError = viewModel.passwordError.collectAsStateWithLifecycle().value
    val confirmError = viewModel.confirmPasswordError.collectAsStateWithLifecycle().value
    val error = viewModel.error.collectAsStateWithLifecycle().value

    val hasError = listOf(
        passwordError,
        confirmError,
    ).all { it == null }

    var showCurrent by remember {
        mutableStateOf(false)
    }

    var showNew by remember {
        mutableStateOf(false)
    }

    var showConfirm by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest {
            when(it) {
                is UiEvent.Success -> {
                    resultBackNavigator.navigateBack(it.successMessage)
                }
                else -> {}
            }
        }
    }

    BottomSheetWithCloseDialog(
        text = "Change Password",
        onClosePressed = {
            navController.navigateUp()
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceSmall),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(SpaceSmall)
        ) {
            StandardOutlinedTextField(
                label = "Current Password",
                text = viewModel.state.currentPassword,
                keyboardType = KeyboardType.Password,
                isPasswordVisible = showCurrent,
                onPasswordToggleClick = {
                    showCurrent = !showCurrent
                },
                onValueChange = {
                    viewModel.onEvent(ChangePasswordEvent.CurrentPasswordChanged(it))
                }
            )
            StandardOutlinedTextField(
                label = "New Password",
                text = viewModel.state.newPassword,
                error = passwordError,
                keyboardType = KeyboardType.Password,
                isPasswordVisible = showNew,
                onPasswordToggleClick = {
                    showNew = !showNew
                },
                onValueChange = {
                    viewModel.onEvent(ChangePasswordEvent.NewPasswordChanged(it))
                }
            )
            StandardOutlinedTextField(
                label = "Confirm Password",
                text = viewModel.state.confirmPassword,
                error = confirmError,
                keyboardType = KeyboardType.Password,
                isPasswordVisible = showConfirm,
                onPasswordToggleClick = {
                    showConfirm = !showConfirm
                },
                onValueChange = {
                    viewModel.onEvent(ChangePasswordEvent.ConfirmPasswordChanged(it))
                }
            )
            
            error?.let {
                Spacer(modifier = Modifier.height(SpaceSmall))
                NoteCard(text = it)
            }

            Spacer(modifier = Modifier.height(SpaceSmall))

            StandardButtonFW(
                text = "Change Password",
                enabled = hasError,
                onClick = {
                    viewModel.onEvent(ChangePasswordEvent.ChangePassword)
                }
            )
        }
    }
}