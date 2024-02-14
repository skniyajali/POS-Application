package com.niyaj.feature.cart_order.settings

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
import androidx.compose.ui.platform.testTag
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.niyaj.common.tags.CartOrderTestTags.CART_ORDER_SETTINGS_SCREEN
import com.niyaj.common.tags.CartOrderTestTags.DELETE_ALL_CART_ORDERS
import com.niyaj.common.tags.CartOrderTestTags.DELETE_ALL_CART_ORDERS_MSG
import com.niyaj.common.tags.CartOrderTestTags.DELETE_ALL_CART_ORDERS_TITLE
import com.niyaj.common.tags.CartOrderTestTags.DELETE_PAST_CART_ORDERS
import com.niyaj.common.tags.CartOrderTestTags.DELETE_PAST_DATA
import com.niyaj.common.tags.CartOrderTestTags.DELETE_PAST_ORDERS_MSG
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.ui.components.SettingsCard
import com.niyaj.ui.event.UiEvent
import com.niyaj.ui.util.BottomSheetWithCloseDialog
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.ramcosta.composedestinations.spec.DestinationStyleBottomSheet
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.message
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import com.vanpra.composematerialdialogs.title

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
            }
        }
    }

    BottomSheetWithCloseDialog(
        modifier = Modifier
            .fillMaxWidth()
            .testTag(CART_ORDER_SETTINGS_SCREEN),
        text = CART_ORDER_SETTINGS_SCREEN,
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
            Spacer(modifier = Modifier.height(SpaceMini))

            SettingsCard(
                text = DELETE_PAST_DATA,
                icon = Icons.Default.Delete,
                onClick = {
                    deleteLastSevenDaysState.show()
                },
            )

            Spacer(modifier = Modifier.height(SpaceMedium))

            SettingsCard(
                text = DELETE_ALL_CART_ORDERS,
                icon = Icons.Default.DeleteForever,
                onClick = {
                    deleteAllOrdersState.show()
                },
            )

            Spacer(modifier = Modifier.height(SpaceMedium))
        }
    }

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
        title(text = DELETE_PAST_CART_ORDERS)
        message(text = DELETE_PAST_ORDERS_MSG)
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
        title(text = DELETE_ALL_CART_ORDERS_TITLE)
        message(text = DELETE_ALL_CART_ORDERS_MSG)
    }
}