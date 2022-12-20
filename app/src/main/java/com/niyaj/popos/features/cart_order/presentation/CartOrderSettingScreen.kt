package com.niyaj.popos.features.cart_order.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.niyaj.popos.R
import com.niyaj.popos.features.common.ui.theme.SpaceSmall
import com.niyaj.popos.features.common.util.UiEvent
import com.niyaj.popos.features.components.StartIconButton
import com.niyaj.popos.features.components.util.BottomSheetWithCloseDialog
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.ramcosta.composedestinations.spec.DestinationStyle
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.message
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import com.vanpra.composematerialdialogs.title
import timber.log.Timber

@Destination(style = DestinationStyle.BottomSheet::class)
@Composable
fun CartOrderSettingScreen(
    navController: NavController = rememberNavController(),
    cartOrderViewModel: CartOrderViewModel = hiltViewModel(),
    resultBackNavigator: ResultBackNavigator<String>
) {

    val deleteLastSevenDaysState = rememberMaterialDialogState()
    val deleteAllOrdersState = rememberMaterialDialogState()

    LaunchedEffect(key1 = true) {
        cartOrderViewModel.eventFlow.collect { event ->
            when (event) {
                is UiEvent.OnSuccess -> {
                    resultBackNavigator.navigateBack(event.successMessage)
                }

                is UiEvent.OnError -> {
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
                            cartOrderViewModel.onCartOrderEvent(CartOrderEvent.DeletePastSevenDaysBeforeData)
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
                            cartOrderViewModel.onCartOrderEvent(CartOrderEvent.DeleteAllCartOrders)
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

            StartIconButton(
                modifier = Modifier
                    .fillMaxWidth(),
                onClick = {
                    deleteLastSevenDaysState.show()
                },
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Past Data",
                    tint = MaterialTheme.colors.secondaryVariant
                )
                Text(
                    text = "Delete Past Data",
                    style = MaterialTheme.typography.button,
                )
            }
            Spacer(modifier = Modifier.height(SpaceSmall))
            Divider(Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(SpaceSmall))
            StartIconButton(
                modifier = Modifier
                    .fillMaxWidth(),
                onClick = {
                    deleteAllOrdersState.show()
                },
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteForever,
                    contentDescription = "Delete All Data",
                    tint = MaterialTheme.colors.error
                )
                Text(
                    text = "Delete All Data",
                    style = MaterialTheme.typography.button,
                )
            }
        }
    }
}