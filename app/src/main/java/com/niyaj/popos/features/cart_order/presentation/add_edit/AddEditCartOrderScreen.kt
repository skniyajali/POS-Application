package com.niyaj.popos.features.cart_order.presentation.add_edit

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.DinnerDining
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.window.PopupProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.niyaj.popos.R
import com.niyaj.popos.features.cart_order.domain.util.CartOrderType
import com.niyaj.popos.features.cart_order.presentation.add_edit.components.MultiSelector
import com.niyaj.popos.features.common.ui.theme.ButtonSize
import com.niyaj.popos.features.common.ui.theme.IconSizeMedium
import com.niyaj.popos.features.common.ui.theme.SpaceMedium
import com.niyaj.popos.features.common.ui.theme.SpaceSmall
import com.niyaj.popos.features.common.util.UiEvent
import com.niyaj.popos.features.components.StandardOutlinedTextField
import com.niyaj.popos.features.components.TextWithIcon
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
    viewModel: AddEditCartOrderViewModel = hiltViewModel(),
    resultNavigator: ResultBackNavigator<String>
) {
    val addresses = viewModel.addresses.collectAsStateWithLifecycle().value.addresses
    val addressesIsLoading = viewModel.addresses.collectAsStateWithLifecycle().value.isLoading

    val customerAddress = viewModel.state.address?.addressName

    val filteredAddress by remember(customerAddress) {
        derivedStateOf {
            addresses.filter {
                if (!customerAddress.isNullOrEmpty()){
                    it.addressName.contains(customerAddress, true)
                }else{
                    true
                }
            }
        }
    }

    var addressDropdownToggled by remember { mutableStateOf(false) }

    var textFieldSize by remember { mutableStateOf(Size.Zero) }

    LaunchedEffect(key1 = cartOrderId) {
        if (cartOrderId.isNullOrEmpty()) {
            viewModel.onAddEditCartOrderEvent(AddEditCartOrderEvent.ResetFields)
            viewModel.onAddEditCartOrderEvent(AddEditCartOrderEvent.GetAndSetCartOrderId)
        }
    }

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collect { event ->
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
                stringResource(id = R.string.create_new_order),
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
                CartOrderType.DineIn.orderType, CartOrderType.DineOut.orderType
            )
            val icons = listOf(
                Icons.Default.DinnerDining, Icons.Default.DeliveryDining
            )

            MultiSelector(
                options = orderTypes,
                icons = icons,
                selectedOption = viewModel.state.orderType,
                onOptionSelect = { option ->
                    viewModel.onAddEditCartOrderEvent(
                        AddEditCartOrderEvent.OrderTypeChanged(option)
                    )
                },
                modifier = Modifier
                    .height(48.dp)
                    .fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(SpaceMedium))

            StandardOutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                text = viewModel.state.orderId,
                hint = "Order Id",
                error = viewModel.state.orderIdError,
                onValueChange = {
                    viewModel.onAddEditCartOrderEvent(
                        AddEditCartOrderEvent.OrderIdChanged(it)
                    )
                },
                readOnly = true
            )
            
            AnimatedVisibility(
                visible = viewModel.state.orderType != CartOrderType.DineIn.orderType,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                Spacer(modifier = Modifier.height(SpaceSmall))

                StandardOutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .onGloballyPositioned { coordinates ->
                            textFieldSize = coordinates.size.toSize()
                        },
                    text = viewModel.state.customer?.customerPhone ?: "",
                    hint = "Customer Phone",
                    error = viewModel.state.customerError,
                    keyboardType = KeyboardType.Phone,
                    onValueChange = {
                        viewModel.onAddEditCartOrderEvent(
                            AddEditCartOrderEvent.CustomerPhoneChanged(it)
                        )
                    },
                    trailingIcon = {
                        if (!viewModel.state.customer?.customerPhone.isNullOrEmpty()) {
                            IconButton(
                                onClick = {
                                    viewModel.onAddEditCartOrderEvent(
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
            }

            AnimatedVisibility(
                visible = viewModel.state.orderType != CartOrderType.DineIn.orderType,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                Spacer(modifier = Modifier.height(SpaceSmall))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                ){
                    StandardOutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .onGloballyPositioned { coordinates ->
                                textFieldSize = coordinates.size.toSize()
                            },
                        text = viewModel.state.address?.addressName ?: "",
                        hint = "Customer Address",
                        error = viewModel.state.addressError,
                        onValueChange = {
                            viewModel.onAddEditCartOrderEvent(
                                AddEditCartOrderEvent.CustomerAddressChanged(it)
                            )
                            addressDropdownToggled = true
                        },
                        trailingIcon = {
                            if (addressesIsLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .align(Alignment.CenterHorizontally)
                                )
                            }else if (!viewModel.state.address?.addressName.isNullOrEmpty()) {
                                IconButton(
                                    onClick = {
                                        viewModel.onAddEditCartOrderEvent(
                                            AddEditCartOrderEvent.OnClearAddress
                                        )
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Clear Text",
                                    )
                                }
                            } else {
                                ExposedDropdownMenuDefaults.TrailingIcon(
                                    expanded = addressDropdownToggled
                                )
                            }
                        },
                    )

                    if (filteredAddress.isNotEmpty()){
                        DropdownMenu(
                            expanded = addressDropdownToggled,
                            onDismissRequest = {
                                addressDropdownToggled = false
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
                            filteredAddress.forEachIndexed { index, address ->
                                DropdownMenuItem(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    onClick = {
                                        viewModel.onAddEditCartOrderEvent(
                                            AddEditCartOrderEvent.CustomerAddressChanged(
                                                address.addressName,
                                                address.addressId
                                            )
                                        )

                                        addressDropdownToggled = false
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

            Button(
                onClick = {
                    if (!cartOrderId.isNullOrEmpty()) {
                        viewModel.onAddEditCartOrderEvent(
                            AddEditCartOrderEvent.UpdateCartOrder(cartOrderId)
                        )
                    } else {
                        viewModel.onAddEditCartOrderEvent(AddEditCartOrderEvent.CreateNewCartOrder)
                    }
                },
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(ButtonSize),
            ) {
                Text(
                    text = if (!cartOrderId.isNullOrEmpty()) stringResource(id = R.string.edit_cart_order).uppercase()
                    else stringResource(id = R.string.create_new_order).uppercase(),
                    style = MaterialTheme.typography.button
                )
            }
        }
    }
}