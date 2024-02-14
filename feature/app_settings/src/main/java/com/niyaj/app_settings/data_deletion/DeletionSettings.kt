package com.niyaj.app_settings.data_deletion

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoDelete
import androidx.compose.material.icons.filled.Save
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.niyaj.core.ui.R
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.designsystem.theme.TextGray
import com.niyaj.ui.components.StandardButtonFW
import com.niyaj.ui.components.StandardOutlinedTextField
import com.niyaj.ui.event.UiEvent
import com.niyaj.ui.util.BottomSheetWithCloseDialog
import com.niyaj.ui.util.Screens.DELETION_SETTINGS_SCREEN
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.ramcosta.composedestinations.spec.DestinationStyleBottomSheet

/**
 * Deletion Settings Screen
 * @author Sk Niyaj Ali
 * @param navController
 * @param viewModel
 * @param resultBackNavigator
 * @see DeletionSettingsViewModel
 */
@Destination(
    route = DELETION_SETTINGS_SCREEN,
    style = DestinationStyleBottomSheet::class
)
@Composable
fun DeletionSettings(
    navController: NavController = rememberNavController(),
    viewModel: DeletionSettingsViewModel = hiltViewModel(),
    resultBackNavigator: ResultBackNavigator<String>
) {
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

    val expensesIntervalError = viewModel.expensesIntervalError.collectAsStateWithLifecycle().value
    val reportsIntervalError = viewModel.reportsIntervalError.collectAsStateWithLifecycle().value
    val cartIntervalError = viewModel.cartIntervalError.collectAsStateWithLifecycle().value
    val cartOrderIntervalError =
        viewModel.cartOrderIntervalError.collectAsStateWithLifecycle().value

    val enableBtn = listOf(
        expensesIntervalError,
        reportsIntervalError,
        cartIntervalError,
        cartOrderIntervalError
    ).all {
        it == null
    }


    BottomSheetWithCloseDialog(
        modifier = Modifier.fillMaxWidth(),
        text = stringResource(R.string.data_deletion_settings),
        icon = Icons.Default.AutoDelete,
        onClosePressed = {
            navController.navigateUp()
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            StandardOutlinedTextField(
                modifier = Modifier,
                text = viewModel.state.expensesInterval,
                label = "Expenses Deletion Interval",
                keyboardType = KeyboardType.Number,
                error = expensesIntervalError,
                onValueChange = {
                    viewModel.onEvent(DeletionSettingsEvent.ExpensesIntervalChanged(it))
                },
            )

            Spacer(modifier = Modifier.height(SpaceSmall))

            StandardOutlinedTextField(
                modifier = Modifier,
                text = viewModel.state.reportsInterval,
                label = "Reports Deletion Interval",
                keyboardType = KeyboardType.Number,
                error = reportsIntervalError,
                onValueChange = {
                    viewModel.onEvent(DeletionSettingsEvent.ReportsIntervalChanged(it))
                },
            )

            Spacer(modifier = Modifier.height(SpaceSmall))

            StandardOutlinedTextField(
                modifier = Modifier,
                text = viewModel.state.cartInterval,
                label = "Cart Deletion Interval",
                keyboardType = KeyboardType.Number,
                error = cartIntervalError,
                onValueChange = {
                    viewModel.onEvent(DeletionSettingsEvent.CartIntervalChanged(it))
                },
            )

            Spacer(modifier = Modifier.height(SpaceSmall))

            StandardOutlinedTextField(
                modifier = Modifier,
                text = viewModel.state.cartOrderInterval,
                label = "CartOrder Deletion Interval",
                keyboardType = KeyboardType.Number,
                error = cartOrderIntervalError,
                onValueChange = {
                    viewModel.onEvent(DeletionSettingsEvent.CartOrderIntervalChanged(it))
                },
            )
            Spacer(modifier = Modifier.height(SpaceMini))
            Text(
                text = stringResource(id = R.string.delete_settings_msg),
                style = MaterialTheme.typography.caption,
                color = TextGray,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(SpaceMedium))

            StandardButtonFW(
                modifier = Modifier.fillMaxWidth(),
                enabled = enableBtn,
                text = stringResource(id = R.string.update_deletion_settings),
                icon = Icons.Default.Save,
                onClick = {
                    viewModel.onEvent(DeletionSettingsEvent.UpdateSettings)
                },
            )
        }
    }
}