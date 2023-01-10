package com.niyaj.popos.features.app_settings.presentation.data_deletion

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
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.niyaj.popos.R
import com.niyaj.popos.features.common.ui.theme.SpaceMedium
import com.niyaj.popos.features.common.ui.theme.SpaceMini
import com.niyaj.popos.features.common.ui.theme.SpaceSmall
import com.niyaj.popos.features.common.ui.theme.TextGray
import com.niyaj.popos.features.common.util.UiEvent
import com.niyaj.popos.features.components.StandardButton
import com.niyaj.popos.features.components.StandardOutlinedTextField
import com.niyaj.popos.features.components.util.BottomSheetWithCloseDialog
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.ramcosta.composedestinations.spec.DestinationStyle

@Destination(style = DestinationStyle.BottomSheet::class)
@Composable
fun DeletionSettings(
    navController: NavController = rememberNavController(),
    deletionSettingsViewModel: DeletionSettingsViewModel = hiltViewModel(),
    resultBackNavigator: ResultBackNavigator<String>
) {
    LaunchedEffect(key1 = true) {
        deletionSettingsViewModel.eventFlow.collect { event ->
            when (event) {
                is UiEvent.OnSuccess -> {
                    resultBackNavigator.navigateBack(event.successMessage)
                }

                is UiEvent.OnError -> {
                    resultBackNavigator.navigateBack(event.errorMessage)
                }

                is UiEvent.IsLoading -> {}
            }
        }
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
                text = deletionSettingsViewModel.state.expensesInterval,
                hint = "Expenses Deletion Interval",
                keyboardType = KeyboardType.Number,
                error = deletionSettingsViewModel.state.expensesIntervalError,
                onValueChange = {
                    deletionSettingsViewModel.onEvent(
                        DeletionSettingsEvent.ExpensesIntervalChanged(
                            it
                        )
                    )
                },
            )

            Spacer(modifier = Modifier.height(SpaceSmall))

            StandardOutlinedTextField(
                modifier = Modifier,
                text = deletionSettingsViewModel.state.reportsInterval,
                hint = "Reports Deletion Interval",
                keyboardType = KeyboardType.Number,
                error = deletionSettingsViewModel.state.reportsIntervalError,
                onValueChange = {
                    deletionSettingsViewModel.onEvent(
                        DeletionSettingsEvent.ReportsIntervalChanged(
                            it
                        )
                    )
                },
            )

            Spacer(modifier = Modifier.height(SpaceSmall))

            StandardOutlinedTextField(
                modifier = Modifier,
                text = deletionSettingsViewModel.state.cartInterval,
                hint = "Cart Deletion Interval",
                keyboardType = KeyboardType.Number,
                error = deletionSettingsViewModel.state.cartIntervalError,
                onValueChange = {
                    deletionSettingsViewModel.onEvent(DeletionSettingsEvent.CartIntervalChanged(it))
                },
            )

            Spacer(modifier = Modifier.height(SpaceSmall))

            StandardOutlinedTextField(
                modifier = Modifier,
                text = deletionSettingsViewModel.state.cartOrderInterval,
                hint = "CartOrder Deletion Interval",
                keyboardType = KeyboardType.Number,
                error = deletionSettingsViewModel.state.cartOrderIntervalError,
                onValueChange = {
                    deletionSettingsViewModel.onEvent(
                        DeletionSettingsEvent.CartOrderIntervalChanged(
                            it
                        )
                    )
                },
            )
            Spacer(modifier = Modifier.height(SpaceMini))
            Text(
                text = "All value consider as days, 0 means data will be deleted from today start time.",
                style = MaterialTheme.typography.caption,
                color = TextGray,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(SpaceMedium))

            StandardButton(
                text = stringResource(id = R.string.update_deletion_settings),
                icon = Icons.Default.Save,
                onClick = {
                    deletionSettingsViewModel.onEvent(DeletionSettingsEvent.UpdateSettings)
                },
            )
        }
    }
}