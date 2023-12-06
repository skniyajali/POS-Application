package com.niyaj.popos.features.cart_order.presentation.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.niyaj.popos.R
import com.niyaj.popos.features.common.ui.theme.SpaceMedium
import com.niyaj.popos.features.common.ui.theme.SpaceMini
import com.niyaj.popos.features.common.ui.theme.SpaceSmall
import com.niyaj.popos.features.common.util.UiEvent
import com.niyaj.popos.features.components.SettingsCard
import com.niyaj.popos.features.components.util.BottomSheetWithCloseDialog
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.ramcosta.composedestinations.spec.DestinationStyleBottomSheet
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.message
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import com.vanpra.composematerialdialogs.title
import timber.log.Timber

@Destination(style = DestinationStyleBottomSheet::class)
@Composable
fun CartOrderSettingScreen(
    navController: NavController = rememberNavController(),
    viewModel: CartOrderSettingsViewModel = hiltViewModel(),
    resultBackNavigator: ResultBackNavigator<String>,
) {

    val deleteLastSevenDaysState = rememberMaterialDialogState()
    val deleteAllOrdersState = rememberMaterialDialogState()

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is UiEvent.Success -> {
                    resultBackNavigator.navigateBack(event.successMessage)
                }

                is UiEvent.Error -> {
                    resultBackNavigator.navigateBack(event.errorMessage)
                }

                is UiEvent.IsLoading -> {
                    Timber.d("Loading.. ${event.isLoading.toString()}")
                }
            }
        }
    }

    BottomSheetWithCloseDialog(
        modifier = Modifier.fillMaxWidth(),
        text = "Cart Order Settings",
        icon = Icons.Default.Settings,
        onClosePressed = {
            navController.navigateUp()
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceSmall),
            horizontalAlignment = Alignment.Start
        ) {
            MaterialDialog(
                dialogState = deleteLastSevenDaysState,
                buttons = {
                    positiveButton(
                        text = "Delete",
                        onClick = {
                            viewModel.onEvent(CartOrderSettingsEvent.DeletePastSevenDaysBeforeData)
                        }
                    )
                    negativeButton(
                        text = "Cancel",
                        onClick = {
                            deleteLastSevenDaysState.hide()
                        },
                    )
                }
            ) {
                title(text = "Delete Past CartOrders?")
                message(res = R.string.delete_last_seven_days_cart_order_message)
            }

            MaterialDialog(
                dialogState = deleteAllOrdersState,
                buttons = {
                    positiveButton(
                        text = "Delete",
                        onClick = {
                            viewModel.onEvent(CartOrderSettingsEvent.DeleteAllCartOrders)
                        }
                    )
                    negativeButton(
                        text = "Cancel",
                        onClick = {
                            deleteAllOrdersState.hide()
                        },
                    )
                }
            ) {
                title(text = "Delete All CartOrders?")
                message(res = R.string.delete_all_cart_orders)
            }

            Spacer(modifier = Modifier.height(SpaceMini))

            SettingsCard(
                text = "Delete Past Data",
                icon = Icons.Default.Delete,
                onClick = {
                    deleteLastSevenDaysState.show()
                },
            )

            Spacer(modifier = Modifier.height(SpaceMedium))

            SettingsCard(
                text = "Delete All Cart Orders",
                icon = Icons.Default.DeleteForever,
                onClick = {
                    deleteAllOrdersState.show()
                },
            )

            Spacer(modifier = Modifier.height(SpaceMedium))
        }
    }
}