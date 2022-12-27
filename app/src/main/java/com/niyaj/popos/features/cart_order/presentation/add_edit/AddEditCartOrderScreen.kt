package com.niyaj.popos.features.cart_order.presentation.add_edit

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.ExposedDropdownMenuDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.window.PopupProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.niyaj.popos.R
import com.niyaj.popos.features.cart_order.domain.util.CartOrderType
import com.niyaj.popos.features.common.ui.theme.IconSizeMedium
import com.niyaj.popos.features.common.ui.theme.SpaceMedium
import com.niyaj.popos.features.common.ui.theme.SpaceSmall
import com.niyaj.popos.features.common.util.UiEvent
import com.niyaj.popos.features.components.StandardOutlinedTextField
import com.niyaj.popos.features.components.util.BottomSheetWithCloseDialog
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.ramcosta.composedestinations.spec.DestinationStyle

@OptIn(ExperimentalMaterialApi::class)
@Destination(style = DestinationStyle.BottomSheet::class)
@Composable
fun AddEditCartOrderScreen(
    cartOrderId: String? = "",
    navController: NavController = rememberNavController(),
    addEditCartOrderViewModel: AddEditCartOrderViewModel = hiltViewModel(),
    resultNavigator: ResultBackNavigator<String>
) {
    val addresses = addEditCartOrderViewModel.addresses.collectAsState().value.addresses
    val customers = addEditCartOrderViewModel.customers.collectAsState().value.customers

    var textFieldSize by remember { mutableStateOf(Size.Zero) }

    var orderTypeToggled by remember { mutableStateOf(false) }

    var phoneDropdownToggled by remember { mutableStateOf(false) }

    var addressDropdownToggled by remember { mutableStateOf(false) }


    LaunchedEffect(key1 = cartOrderId) {
        if (cartOrderId.isNullOrEmpty()) {
            addEditCartOrderViewModel.onAddEditCartOrderEvent(AddEditCartOrderEvent.ResetFields)
            addEditCartOrderViewModel.onAddEditCartOrderEvent(AddEditCartOrderEvent.GetAndSetCartOrderId)
        }
    }

    LaunchedEffect(key1 = true) {
        addEditCartOrderViewModel.eventFlow.collect { event ->
            when (event) {
                is UiEvent.IsLoading -> {}
                is UiEvent.OnSuccess -> {
                    resultNavigator.navigateBack(event.successMessage)
                }
                is UiEvent.OnError -> {
                    resultNavigator.navigateBack(event.errorMessage)
                }
            }
        }
    }

    BottomSheetWithCloseDialog(
        modifier = Modifier.fillMaxWidth(),
        text = if (!cartOrderId.isNullOrEmpty())
                stringResource(id = R.string.edit_cart_order)
            else
                stringResource(id = R.string.create_order),
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
            StandardOutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                text = addEditCartOrderViewModel.addEditCartOrderState.orderId,
                hint = "Order Id",
                error = addEditCartOrderViewModel.addEditCartOrderState.orderIdError,
                onValueChange = {
                    addEditCartOrderViewModel.onAddEditCartOrderEvent(
                        AddEditCartOrderEvent.OrderIdChanged(it)
                    )
                },
                readOnly = true
            )

            Spacer(modifier = Modifier.height(SpaceSmall))

            ExposedDropdownMenuBox(
                expanded = orderTypeToggled,
                onExpandedChange = {
                    orderTypeToggled = !orderTypeToggled
                }
            ) {
                StandardOutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .onGloballyPositioned { coordinates ->
                            textFieldSize = coordinates.size.toSize()
                        },
                    text = addEditCartOrderViewModel.addEditCartOrderState.orderType,
                    hint = "Order Type",
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = addEditCartOrderViewModel.expanded,
                            onIconClick = {
                                orderTypeToggled = !orderTypeToggled
                            }
                        )
                    },
                )

                DropdownMenu(
                    expanded = orderTypeToggled,
                    onDismissRequest = {
                        orderTypeToggled = false
                    },
                    properties = PopupProperties(
                        focusable = false,
                        dismissOnBackPress = true,
                        dismissOnClickOutside = true,
                    ),
                    modifier = Modifier
                        .width(with(LocalDensity.current){textFieldSize.width.toDp()}),
                ) {
                    DropdownMenuItem(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            addEditCartOrderViewModel.onAddEditCartOrderEvent(
                                AddEditCartOrderEvent.OrderTypeChanged(CartOrderType.DineIn.orderType)
                            )
                            orderTypeToggled = false
                        }
                    ) {
                        Text(
                            text = CartOrderType.DineIn.orderType,
                            style = MaterialTheme.typography.body1,
                        )
                    }

                    Divider(modifier = Modifier.fillMaxWidth(), color = Color.Gray, thickness = 0.8.dp)

                    DropdownMenuItem(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            addEditCartOrderViewModel.onAddEditCartOrderEvent(
                                AddEditCartOrderEvent.OrderTypeChanged(CartOrderType.DineOut.orderType)
                            )
                            orderTypeToggled = false
                        }
                    ) {
                        Text(
                            text = CartOrderType.DineOut.orderType,
                            style = MaterialTheme.typography.body1,
                        )
                    }
                }
            }

            if(addEditCartOrderViewModel.addEditCartOrderState.orderType != CartOrderType.DineIn.orderType) {
                Spacer(modifier = Modifier.height(SpaceSmall))

                Column {
                    StandardOutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .onGloballyPositioned { coordinates ->
                                textFieldSize = coordinates.size.toSize()
                            },
                        text = addEditCartOrderViewModel.addEditCartOrderState.customer?.customerPhone ?: "",
                        hint = "Customer Phone",
                        error = addEditCartOrderViewModel.addEditCartOrderState.customerError,
                        keyboardType = KeyboardType.Phone,
                        onValueChange = {
                            addEditCartOrderViewModel.onAddEditCartOrderEvent(
                                AddEditCartOrderEvent.CustomerPhoneChanged(it)
                            )
                            phoneDropdownToggled = true

                            addEditCartOrderViewModel.onAddEditCartOrderEvent(
                                AddEditCartOrderEvent.OnSearchCustomer(it)
                            )
                        },
                        trailingIcon = {
                            if (!addEditCartOrderViewModel.addEditCartOrderState.customer?.customerPhone.isNullOrEmpty()) {
                                IconButton(
                                    onClick = {
                                        addEditCartOrderViewModel.onAddEditCartOrderEvent(
                                            AddEditCartOrderEvent.OnClearCustomer
                                        )
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Clear Text",
                                        tint = Color.Blue,
                                        modifier = Modifier.size(IconSizeMedium)
                                    )
                                }
                            }
                        },
                    )

                    DropdownMenu(
                        expanded = phoneDropdownToggled,
                        onDismissRequest = {
                            phoneDropdownToggled = false
                        },
                        properties = PopupProperties(
                            focusable = false,
                            dismissOnBackPress = true,
                            dismissOnClickOutside = true,
                            clippingEnabled = true,
                            excludeFromSystemGesture = true,
                        ),
                        modifier = Modifier
                            .heightIn(max = 250.dp)
                            .width(with(LocalDensity.current) { textFieldSize.width.toDp() }),
                    ) {
                        customers.forEachIndexed { index, customer ->
                            DropdownMenuItem(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = {
                                    addEditCartOrderViewModel.onAddEditCartOrderEvent(
                                        AddEditCartOrderEvent.CustomerPhoneChanged(
                                            customerPhone = customer.customerPhone,
                                            customerId = customer.customerId
                                        )
                                    )
                                    phoneDropdownToggled = false
                                }
                            ) {
                                Text(
                                    text = buildAnnotatedString {
                                        append(customer.customerPhone)
                                        if (!customer.customerName.isNullOrEmpty()) {
                                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                                append(" | ")
                                                append(customer.customerName)
                                            }
                                        }
                                    },
                                    style = MaterialTheme.typography.body1,
                                )
                            }

                            if (index != customers.size - 1) {
                                Divider(modifier = Modifier.fillMaxWidth(),
                                    color = Color.Gray,
                                    thickness = 0.8.dp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(SpaceSmall))

                Column{
                    StandardOutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .onGloballyPositioned { coordinates ->
                                textFieldSize = coordinates.size.toSize()
                            },
                        text = addEditCartOrderViewModel.addEditCartOrderState.address?.addressName ?: "",
                        hint = "Customer Address",
                        error = addEditCartOrderViewModel.addEditCartOrderState.addressError,
                        onValueChange = {
                            addEditCartOrderViewModel.onAddEditCartOrderEvent(
                                AddEditCartOrderEvent.CustomerAddressChanged(it)
                            )
                            addressDropdownToggled = true

                            addEditCartOrderViewModel.onAddEditCartOrderEvent(
                                AddEditCartOrderEvent.OnSearchAddress(it)
                            )

                        },
                        trailingIcon = {
                            if (!addEditCartOrderViewModel.addEditCartOrderState.address?.addressName.isNullOrEmpty()) {
                                IconButton(
                                    onClick = {
                                        addEditCartOrderViewModel.onAddEditCartOrderEvent(
                                            AddEditCartOrderEvent.OnClearAddress
                                        )
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Clear Text",
                                    )
                                }
                            }
                        },
                    )

                    DropdownMenu(
                        expanded = addressDropdownToggled,
                        onDismissRequest = {
                            addressDropdownToggled = false
                        },
                        properties = PopupProperties(
                            focusable = false,
                            dismissOnBackPress = true,
                            dismissOnClickOutside = true,
                            clippingEnabled = true,
                            excludeFromSystemGesture = true,
                        ),
                        modifier = Modifier
                            .heightIn(max = 250.dp)
                            .width(with(LocalDensity.current) { textFieldSize.width.toDp() }),
                    ) {
                        addresses.forEachIndexed { index, address ->
                            DropdownMenuItem(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = {
                                    addEditCartOrderViewModel.onAddEditCartOrderEvent(
                                        AddEditCartOrderEvent.CustomerAddressChanged(
                                            address.addressName,
                                            address.addressId
                                        )
                                    )
                                    addressDropdownToggled = false
                                }
                            ) {
                                Text(
                                    text = address.addressName,
                                    style = MaterialTheme.typography.body1,
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

            Spacer(modifier = Modifier.height(SpaceMedium))

            Button(
                onClick = {
                    if (!cartOrderId.isNullOrEmpty()) {
                        addEditCartOrderViewModel.onAddEditCartOrderEvent(
                            AddEditCartOrderEvent.UpdateCartOrder(cartOrderId)
                        )
                    } else {
                        addEditCartOrderViewModel.onAddEditCartOrderEvent(AddEditCartOrderEvent.CreateNewCartOrder)
                    }
                },
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(44.dp),
            ) {
                Text(
                    text = if (!cartOrderId.isNullOrEmpty()) stringResource(id = R.string.edit_cart_order).uppercase()
                    else stringResource(id = R.string.create_order).uppercase(),
                    style = MaterialTheme.typography.button
                )
            }
        }
    }
}