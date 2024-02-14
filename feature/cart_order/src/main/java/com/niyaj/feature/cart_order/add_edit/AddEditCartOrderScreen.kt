package com.niyaj.feature.cart_order.add_edit

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.DinnerDining
import androidx.compose.material.icons.filled.Phone
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.window.PopupProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.niyaj.common.tags.CartOrderTestTags.ADDRESS_NAME_ERROR_FIELD
import com.niyaj.common.tags.CartOrderTestTags.ADDRESS_NAME_FIELD
import com.niyaj.common.tags.CartOrderTestTags.ADD_EDIT_CART_ORDER_BTN
import com.niyaj.common.tags.CartOrderTestTags.ADD_EDIT_CART_ORDER_SCREEN
import com.niyaj.common.tags.CartOrderTestTags.CREATE_NEW_CART_ORDER
import com.niyaj.common.tags.CartOrderTestTags.CUSTOMER_PHONE_ERROR_FIELD
import com.niyaj.common.tags.CartOrderTestTags.CUSTOMER_PHONE_FIELD
import com.niyaj.common.tags.CartOrderTestTags.EDIT_CART_ORDER
import com.niyaj.common.tags.CartOrderTestTags.ORDER_ID_FIELD
import com.niyaj.common.tags.CartOrderTestTags.ORDER_TYPE_FIELD
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.OrderType
import com.niyaj.ui.components.MultiSelector
import com.niyaj.ui.components.PhoneNoCountBox
import com.niyaj.ui.components.StandardButtonFW
import com.niyaj.ui.components.StandardOutlinedTextField
import com.niyaj.ui.components.TextWithIcon
import com.niyaj.ui.event.UiEvent
import com.niyaj.ui.util.BottomSheetWithCloseDialog
import com.niyaj.ui.util.Screens
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.ramcosta.composedestinations.spec.DestinationStyleBottomSheet

/**
 * Add Edit Cart Order Screen
 * @author Sk Niyaj Ali
 * @param cartOrderId
 * @param navController
 * @param viewModel
 * @param resultNavigator
 * @see AddEditCartOrderViewModel
 */
@OptIn(ExperimentalMaterialApi::class)
@Destination(route = Screens.ADD_EDIT_CART_ORDER_SCREEN, style = DestinationStyleBottomSheet::class)
@Composable
fun AddEditCartOrderScreen(
    cartOrderId: String? = "",
    navController: NavController = rememberNavController(),
    viewModel: AddEditCartOrderViewModel = hiltViewModel(),
    resultNavigator: ResultBackNavigator<String>
) {
    val customers = viewModel.customers.collectAsStateWithLifecycle().value
    val addresses = viewModel.addresses.collectAsStateWithLifecycle().value

    val newAddress = viewModel.newAddress.collectAsStateWithLifecycle().value
    val newCustomer = viewModel.newCustomer.collectAsStateWithLifecycle().value

    val addressError = viewModel.addressError.collectAsStateWithLifecycle().value
    val customerError = viewModel.customerError.collectAsStateWithLifecycle().value
    val orderIdError = viewModel.orderIdError.collectAsStateWithLifecycle().value

    val orderId = viewModel.orderId.collectAsStateWithLifecycle().value

    val enableBtn = listOf(
        orderIdError,
        addressError,
        customerError,
    ).all { it == null }

    val event = viewModel.eventFlow.collectAsStateWithLifecycle(initialValue = null).value

    LaunchedEffect(key1 = event) {
        event?.let { data ->
            when (data) {
                is UiEvent.Error -> {
                    resultNavigator.navigateBack(data.errorMessage)
                }

                is UiEvent.Success -> {
                    resultNavigator.navigateBack(data.successMessage)
                }
            }
        }
    }

    var addressToggled by remember { mutableStateOf(false) }
    var customerToggled by remember { mutableStateOf(false) }

    var textFieldSize by remember { mutableStateOf(Size.Zero) }

    val title = if (cartOrderId.isNullOrEmpty()) CREATE_NEW_CART_ORDER else EDIT_CART_ORDER

    BottomSheetWithCloseDialog(
        modifier = Modifier
            .fillMaxWidth()
            .testTag(ADD_EDIT_CART_ORDER_SCREEN),
        text = title,
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
            val orderTypes = listOf(
                OrderType.DineIn.name, OrderType.DineOut.name
            )

            val icons = listOf(
                Icons.Default.DinnerDining, Icons.Default.DeliveryDining
            )

            MultiSelector(
                options = orderTypes,
                icons = icons,
                selectedOption = viewModel.state.orderType.name,
                onOptionSelect = { option ->
                    viewModel.onEvent(
                        AddEditCartOrderEvent.OrderTypeChanged(OrderType.valueOf(option))
                    )
                },
                modifier = Modifier
                    .height(48.dp)
                    .fillMaxWidth()
                    .testTag(ORDER_TYPE_FIELD)
            )

            Spacer(modifier = Modifier.height(SpaceMedium))

            StandardOutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(ORDER_ID_FIELD),
                text = orderId,
                label = ORDER_ID_FIELD,
                error = orderIdError,
                onValueChange = {},
                readOnly = true
            )

            AnimatedVisibility(
                visible = viewModel.state.orderType != OrderType.DineIn,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                Spacer(modifier = Modifier.height(SpaceSmall))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    StandardOutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag(CUSTOMER_PHONE_FIELD)
                            .onGloballyPositioned { coordinates ->
                                textFieldSize = coordinates.size.toSize()
                            },
                        text = newCustomer?.customerPhone ?: "",
                        label = CUSTOMER_PHONE_FIELD,
                        error = customerError,
                        errorTag = CUSTOMER_PHONE_ERROR_FIELD,
                        keyboardType = KeyboardType.Phone,
                        onValueChange = {
                            viewModel.onEvent(AddEditCartOrderEvent.CustomerPhoneChanged(it))
                            customerToggled = true

                        },
                        trailingIcon = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(SpaceMini)
                            ) {
                                PhoneNoCountBox(count = newCustomer?.customerPhone?.length ?: 0)
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = customerToggled)
                            }
                        },
                    )

                    if (customers.isNotEmpty()) {
                        DropdownMenu(
                            expanded = customerToggled,
                            onDismissRequest = {
                                customerToggled = false
                            },
                            properties = PopupProperties(
                                focusable = false,
                                dismissOnBackPress = true,
                                dismissOnClickOutside = true,
                                excludeFromSystemGesture = true,
                                clippingEnabled = true,
                            ),
                            modifier = Modifier
                                .heightIn(max = 200.dp)
                                .width(with(LocalDensity.current) { textFieldSize.width.toDp() }),
                        ) {
                            customers.forEachIndexed { index, customer ->
                                DropdownMenuItem(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    onClick = {
                                        viewModel.onEvent(
                                            AddEditCartOrderEvent.CustomerChanged(
                                                customer
                                            )
                                        )
                                        customerToggled = false
                                    }
                                ) {
                                    TextWithIcon(
                                        text = customer.customerPhone,
                                        isTitle = true,
                                        icon = Icons.Default.Phone
                                    )
                                }

                                if (index != customers.size - 1) {
                                    Divider(
                                        modifier = Modifier.fillMaxWidth(),
                                        color = Color.Gray,
                                        thickness = 0.8.dp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            AnimatedVisibility(
                visible = viewModel.state.orderType != OrderType.DineIn,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                Spacer(modifier = Modifier.height(SpaceSmall))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    StandardOutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag(ADDRESS_NAME_FIELD)
                            .onGloballyPositioned { coordinates ->
                                textFieldSize = coordinates.size.toSize()
                            },
                        text = newAddress?.addressName ?: "",
                        label = ADDRESS_NAME_FIELD,
                        error = addressError,
                        errorTag = ADDRESS_NAME_ERROR_FIELD,
                        onValueChange = {
                            viewModel.onEvent(AddEditCartOrderEvent.AddressNameChanged(it))
                            addressToggled = true
                        },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = addressToggled)
                        },
                    )

                    if (addresses.isNotEmpty()) {
                        DropdownMenu(
                            expanded = addressToggled,
                            onDismissRequest = {
                                addressToggled = false
                            },
                            properties = PopupProperties(
                                focusable = false,
                                dismissOnBackPress = true,
                                dismissOnClickOutside = true,
                                excludeFromSystemGesture = true,
                                clippingEnabled = true,
                            ),
                            modifier = Modifier
                                .heightIn(max = 200.dp)
                                .width(with(LocalDensity.current) { textFieldSize.width.toDp() }),
                        ) {
                            addresses.forEachIndexed { index, address ->
                                DropdownMenuItem(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    onClick = {
                                        viewModel.onEvent(
                                            AddEditCartOrderEvent.AddressChanged(
                                                address
                                            )
                                        )
                                        addressToggled = false
                                    }
                                ) {
                                    TextWithIcon(
                                        text = address.addressName,
                                        isTitle = true,
                                        icon = Icons.Default.Business
                                    )
                                }

                                if (index != addresses.size - 1) {
                                    Divider(
                                        modifier = Modifier.fillMaxWidth(),
                                        color = Color.Gray,
                                        thickness = 0.8.dp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(SpaceMedium))

            StandardButtonFW(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(ADD_EDIT_CART_ORDER_BTN),
                text = title,
                enabled = enableBtn,
                onClick = {
                    viewModel.onEvent(AddEditCartOrderEvent.CreateOrUpdateCartOrder)
                }
            )
        }
    }
}