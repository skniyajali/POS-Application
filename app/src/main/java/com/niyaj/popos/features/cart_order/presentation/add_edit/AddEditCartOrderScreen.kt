package com.niyaj.popos.features.cart_order.presentation.add_edit

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
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
import com.niyaj.popos.features.components.util.BottomSheetWithCloseDialog
import com.niyaj.popos.features.main_feed.presentation.utils.collectAsStateLifecycleAware
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
    val addresses = addEditCartOrderViewModel.addresses.collectAsStateLifecycleAware().value.addresses
    val addressesIsLoading = addEditCartOrderViewModel.addresses.collectAsStateLifecycleAware().value.isLoading

    val customers = addEditCartOrderViewModel.customers.collectAsStateLifecycleAware().value.customers
    val customerIsLoading = addEditCartOrderViewModel.customers.collectAsState().value.isLoading

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

            MultiSelector(
                options = orderTypes,
                selectedOption = addEditCartOrderViewModel.addEditCartOrderState.orderType,
                onOptionSelect = { option ->
                    addEditCartOrderViewModel.onAddEditCartOrderEvent(
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
            
            AnimatedVisibility(
                visible = addEditCartOrderViewModel.addEditCartOrderState.orderType != CartOrderType.DineIn.orderType,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                Spacer(modifier = Modifier.height(SpaceSmall))

                ExposedDropdownMenuBox(
                    expanded = phoneDropdownToggled,
                    onExpandedChange = {
                        phoneDropdownToggled = !phoneDropdownToggled
                    }
                ) {
                    StandardOutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth(),
                        text = addEditCartOrderViewModel.addEditCartOrderState.customer?.customerPhone ?: "",
                        hint = "Customer Phone",
                        error = addEditCartOrderViewModel.addEditCartOrderState.customerError,
                        keyboardType = KeyboardType.Phone,
                        onValueChange = {
                            addEditCartOrderViewModel.onAddEditCartOrderEvent(
                                AddEditCartOrderEvent.CustomerPhoneChanged(it)
                            )
                        },
                        trailingIcon = {
                            if(customerIsLoading){
                                CircularProgressIndicator()
                            }

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

                    if (customers.isNotEmpty()){
                        ExposedDropdownMenu(
                            expanded = phoneDropdownToggled,
                            onDismissRequest = {
                                phoneDropdownToggled = false
                            },
                        ) {
                            if(customerIsLoading){
                                CircularProgressIndicator()
                            }

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
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
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

                }
            }

            AnimatedVisibility(
                visible = addEditCartOrderViewModel.addEditCartOrderState.orderType != CartOrderType.DineIn.orderType,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                Spacer(modifier = Modifier.height(SpaceSmall))

                ExposedDropdownMenuBox(
                    expanded = addressDropdownToggled,
                    onExpandedChange = {
                        addressDropdownToggled = !addressDropdownToggled
                    }
                ){
                    StandardOutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth(),
                        text = addEditCartOrderViewModel.addEditCartOrderState.address?.addressName ?: "",
                        hint = "Customer Address",
                        error = addEditCartOrderViewModel.addEditCartOrderState.addressError,
                        onValueChange = {
                            addEditCartOrderViewModel.onAddEditCartOrderEvent(
                                AddEditCartOrderEvent.CustomerAddressChanged(it)
                            )
                        },
                        trailingIcon = {
                            if (addressesIsLoading) {
                                CircularProgressIndicator()
                            }
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

                    if (addresses.isNotEmpty()){
                        ExposedDropdownMenu(
                            expanded = addressDropdownToggled,
                            onDismissRequest = {
                                addressDropdownToggled = false
                            },
                        ) {
                            addresses.forEachIndexed { index, address ->
                                DropdownMenuItem(
                                    modifier = Modifier
                                        .fillMaxWidth(),
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