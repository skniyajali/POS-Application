package com.niyaj.popos.features.cart_order.presentation.add_edit

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.DinnerDining
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
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
import com.niyaj.popos.features.common.ui.theme.Olive
import com.niyaj.popos.features.common.ui.theme.SpaceMedium
import com.niyaj.popos.features.common.ui.theme.SpaceSmall
import com.niyaj.popos.features.common.ui.theme.TextGray
import com.niyaj.popos.features.common.util.UiEvent
import com.niyaj.popos.features.components.StandardButton
import com.niyaj.popos.features.components.StandardOutlinedTextField
import com.niyaj.popos.features.components.TextWithIcon
import com.niyaj.popos.features.components.util.BottomSheetWithCloseDialog
import com.niyaj.popos.features.destinations.AddEditCartOrderScreenDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.ramcosta.composedestinations.spec.DestinationStyle
import io.sentry.compose.SentryTraced

/**
 * Add Edit Cart Order Screen
 * @author Sk Niyaj Ali
 * @param cartOrderId
 * @param navController
 * @param viewModel
 * @param resultNavigator
 * @see AddEditCartOrderViewModel
 */
@OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)
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
    val events = viewModel.eventFlow.collectAsStateWithLifecycle(initialValue = null).value

    var addressDropdownToggled by remember { mutableStateOf(false) }

    var textFieldSize by remember { mutableStateOf(Size.Zero) }

    LaunchedEffect(key1 = cartOrderId) {
        if (cartOrderId.isNullOrEmpty()) {
            viewModel.onEvent(AddEditCartOrderEvent.ResetFields)
            viewModel.onEvent(AddEditCartOrderEvent.GetAndSetCartOrderId)
        }
    }

    LaunchedEffect(key1 = events) {
        events?.let {event ->
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
    
    SentryTraced(tag = AddEditCartOrderScreenDestination.route) {
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
                        viewModel.onEvent(
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
                        viewModel.onEvent(
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
                            viewModel.onEvent(AddEditCartOrderEvent.CustomerPhoneChanged(it))
                        },
                        trailingIcon = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                PhoneNoCountBox(
                                    count = viewModel.state.customer?.customerPhone?.length ?: 0
                                )
                                Spacer(modifier = Modifier.width(1.dp))
                                if (!viewModel.state.customer?.customerPhone.isNullOrEmpty()) {
                                    IconButton(
                                        onClick = {
                                            viewModel.onEvent(AddEditCartOrderEvent.OnClearCustomer)
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Clear Text",
                                        )
                                    }
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
                                viewModel.onEvent(AddEditCartOrderEvent.CustomerAddressChanged(it))
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
                                            viewModel.onEvent(AddEditCartOrderEvent.OnClearAddress)
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Clear Text",
                                        )
                                    }
                                } else {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = addressDropdownToggled)
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
                                            viewModel.onEvent(
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

                StandardButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = if (!cartOrderId.isNullOrEmpty()) stringResource(id = R.string.edit_cart_order)
                        else stringResource(id = R.string.create_new_order),
                    onClick = {
                        if (!cartOrderId.isNullOrEmpty()) {
                            viewModel.onEvent(
                                AddEditCartOrderEvent.UpdateCartOrder(cartOrderId)
                            )
                        } else {
                            viewModel.onEvent(AddEditCartOrderEvent.CreateNewCartOrder)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun PhoneNoCountBox(
    modifier : Modifier = Modifier,
    count: Int = 0,
    totalCount: Int = 10,
    backgroundColor: Color = Color.Transparent,
    color: Color = TextGray,
    errorColor: Color = Olive,
) {
    val countColor = if (count <= 10) color else errorColor
    val textColor = if (count >= 10) color else errorColor

    AnimatedVisibility(
        visible = count != 0,
        enter = fadeIn(),
        exit = fadeOut(),
        label = "Phone No Count Box",
    ) {
        Card(
            modifier = modifier.background(backgroundColor),
            shape = RoundedCornerShape(2.dp),
            elevation = 0.dp,
        ) {
            Row(
                modifier = Modifier.padding(2.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(
                    text = count.toString(),
                    style = MaterialTheme.typography.caption,
                    color = countColor,
                )
                Text(
                    text = "/",
                    fontFamily = FontFamily.Cursive,
                    color = color,
                )
                Text(
                    text = totalCount.toString(),
                    style = MaterialTheme.typography.caption,
                    color = textColor,
                )
            }
        }
    }
}