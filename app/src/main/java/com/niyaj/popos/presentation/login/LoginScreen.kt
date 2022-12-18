package com.niyaj.popos.presentation.login

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.niyaj.popos.R
import com.niyaj.popos.presentation.components.StandardOutlinedTextField
import com.niyaj.popos.presentation.ui.theme.SpaceLarge
import com.niyaj.popos.presentation.ui.theme.SpaceMedium
import com.niyaj.popos.presentation.util.Screen
import com.ramcosta.composedestinations.annotation.Destination

@Destination
@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel = hiltViewModel()
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                start = SpaceLarge,
                end = SpaceLarge,
                top = SpaceLarge,
                bottom = 50.dp
            ),
        contentAlignment = Alignment.Center
    ){
        Column(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center),
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = stringResource(id = R.string.login),
                style = MaterialTheme.typography.h5,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(SpaceMedium))
            StandardOutlinedTextField(
                text = viewModel.emailText.value,
                hint = stringResource(id = R.string.login_hint),
                error = viewModel.emailError.value,
                keyboardType = KeyboardType.Email,
                onValueChange = {
                    viewModel.setEmail(it)
                },
            )
            Spacer(modifier = Modifier.height(SpaceMedium))
            StandardOutlinedTextField(
                text = viewModel.passwordText.value,
                hint = stringResource(id = R.string.password_hint),
                error = viewModel.passwordError.value,
                keyboardType = KeyboardType.Password,
                isPasswordVisible = viewModel.passwordToggle.value,
                onPasswordToggleClick = {
                    viewModel.togglePassword(it)
                },
                onValueChange = {
                    viewModel.setPassword(it)
                },
            )
            Spacer(modifier = Modifier.height(SpaceMedium))
            Button(
                onClick = {
                    navController.navigate(
                        Screen.MainFeedScreen.route
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()

            ) {
                Text(
                    text = stringResource(id = R.string.login),
                    style = MaterialTheme.typography.button,
                    color = MaterialTheme.colors.onPrimary
                )
            }
        }
    }
}