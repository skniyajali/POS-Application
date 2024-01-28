package com.niyaj.popos.features.order.presentation.details

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.Details
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreTime
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.RoomService
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material.icons.filled.Update
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.niyaj.popos.common.utils.toFormattedDateAndTime
import com.niyaj.popos.common.utils.toRupee
import com.niyaj.popos.features.address.domain.model.Address
import com.niyaj.popos.features.cart.domain.model.CartProductItem
import com.niyaj.popos.features.cart_order.domain.model.CartOrder
import com.niyaj.popos.features.cart_order.domain.util.CartOrderType
import com.niyaj.popos.features.charges.domain.model.Charges
import com.niyaj.popos.features.charges.presentation.ChargesViewModel
import com.niyaj.popos.features.common.ui.theme.ButtonSize
import com.niyaj.popos.features.common.ui.theme.LightColor6
import com.niyaj.popos.features.common.ui.theme.SpaceMedium
import com.niyaj.popos.features.common.ui.theme.SpaceMini
import com.niyaj.popos.features.common.ui.theme.SpaceSmall
import com.niyaj.popos.features.components.ItemNotAvailable
import com.niyaj.popos.features.components.StandardExpandable
import com.niyaj.popos.features.components.StandardOutlinedChip
import com.niyaj.popos.features.components.StandardScaffold
import com.niyaj.popos.features.components.TextWithIcon
import com.niyaj.popos.features.customer.domain.model.Customer
import com.niyaj.popos.features.destinations.AddressDetailsScreenDestination
import com.niyaj.popos.features.destinations.CustomerDetailsScreenDestination
import com.niyaj.popos.features.order.presentation.components.TextDivider
import com.niyaj.popos.features.order.presentation.components.ThreeGridTexts
import com.niyaj.popos.features.order.presentation.components.TwoGridTexts
import com.niyaj.popos.features.order.presentation.print_order.OrderPrintViewModel
import com.niyaj.popos.features.order.presentation.print_order.PrintEvent
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.navigate
import io.sentry.compose.SentryTraced
import timber.log.Timber

/**
 * [OrderDetailsScreen] is the screen that displays the details of the order
 * @param cartOrderId is the id of the order
 * @param navController is the navController that handles the navigation
 * @param orderDetailsViewModel is the viewModel that handles the business logic of the screen
 * @param chargesViewModel is the viewModel that handles the business logic of the screen
 * @param orderPrintViewModel is the viewModel that handles the business logic of the screen
 * @author Sk Niyaj Ali
 * @see OrderDetailsViewModel
 * @see ChargesViewModel
 * @see OrderPrintViewModel
 */
@OptIn(ExperimentalPermissionsApi::class, ExperimentalComposeUiApi::class)
@Destination
@Composable
fun OrderDetailsScreen(
    cartOrderId: String = "",
    navController: NavController,
    orderDetailsViewModel: OrderDetailsViewModel = hiltViewModel(),
    chargesViewModel: ChargesViewModel = hiltViewModel(),
    orderPrintViewModel: OrderPrintViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    val bluetoothPermissions =
        // Checks if the device has Android 12 or above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            rememberMultiplePermissionsState(
                permissions = listOf(
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_ADMIN,
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.BLUETOOTH_SCAN,
                )
            )
        } else {
            rememberMultiplePermissionsState(
                permissions = listOf(
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_ADMIN,
                )
            )
        }

    val enableBluetoothContract = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == Activity.RESULT_OK) {
            Timber.d("bluetoothLauncher", "Success")
        } else {
            Timber.w("bluetoothLauncher", "Failed")
        }
    }

    // This intent will open the enable bluetooth dialog
    val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)

    val bluetoothManager = remember {
        context.getSystemService(BluetoothManager::class.java)
    }

    val bluetoothAdapter: BluetoothAdapter? = remember {
        bluetoothManager.adapter
    }

    val printOrder: (String) -> Unit = {
        if (bluetoothPermissions.allPermissionsGranted) {
            if (bluetoothAdapter?.isEnabled == true) {
                // Bluetooth is on print the receipt
                orderPrintViewModel.onPrintEvent(PrintEvent.PrintOrder(it))
            } else {
                // Bluetooth is off, ask user to turn it on
                enableBluetoothContract.launch(enableBluetoothIntent)
                orderPrintViewModel.onPrintEvent(PrintEvent.PrintOrder(it))
            }
        } else {
            bluetoothPermissions.launchMultiplePermissionRequest()
        }
    }

    val scaffoldState = rememberScaffoldState()
    val orderDetails = orderDetailsViewModel.orderDetails.collectAsStateWithLifecycle().value.orderDetails
    val charges = chargesViewModel.state.collectAsStateWithLifecycle().value.chargesItem

    var cartOrderExpended by remember {
        mutableStateOf(true)
    }

    var customerExpended by remember {
        mutableStateOf(false)
    }

    var addressExpended by remember {
        mutableStateOf(false)
    }

    var cartExpended by remember {
        mutableStateOf(true)
    }

    SentryTraced(tag = "OrderDetailsScreen-$cartOrderId") {
        StandardScaffold(
            navController = navController,
            scaffoldState = scaffoldState,
            showBackArrow = true,
            title = {
                Text(text = "Order Details")
            },
            navActions = {
                IconButton(
                    onClick = {
                        if(orderDetails?.cartOrder?.cartOrderId != null) {
                            printOrder(orderDetails.cartOrder.cartOrderId)
                        }
                    },
                    enabled = orderDetails != null,
                ) {
                    Icon(
                        imageVector = Icons.Default.Print,
                        contentDescription = "Print Order",
                        tint = MaterialTheme.colors.onSecondary,
                    )
                }
            }
        ){
            if (orderDetails == null) {
                ItemNotAvailable(text = "Order Details not available")
            }else{
                LazyColumn {
                    item {
                        orderDetails.cartOrder?.let { cartOrder ->
                            CartOrderDetails(
                                cartOrder = cartOrder,
                                doesExpanded = cartOrderExpended,
                                onExpandChanged = {
                                    cartOrderExpended = !cartOrderExpended
                                }
                            )

                            cartOrder.customer?.let { customer ->
                                CustomerDetails(
                                    customer = customer,
                                    doesExpanded = customerExpended,
                                    onExpandChanged = {
                                        customerExpended = !customerExpended
                                    },
                                    onClickViewDetails = {
                                        navController.navigate(CustomerDetailsScreenDestination(it))
                                    }
                                )
                            }

                            cartOrder.address?.let { address ->
                                AddressDetails(
                                    address = address,
                                    doesExpanded = addressExpended,
                                    onExpandChanged = {
                                        addressExpended = !addressExpended
                                    },
                                    onClickViewDetails = {
                                        navController.navigate(AddressDetailsScreenDestination(addressId = it))
                                    }
                                )
                            }

                            if (orderDetails.orderedProducts.isNotEmpty()) {
                                CartItemDetails(
                                    cartOrder = cartOrder,
                                    cartProduct = orderDetails.orderedProducts,
                                    charges = charges,
                                    orderPrice = orderDetails.orderPrice,
                                    doesExpanded = cartExpended,
                                    onExpandChanged = {
                                        cartExpended = !cartExpended
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


/**
 * This composable displays the cart order details
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CartOrderDetails(
    cartOrder: CartOrder,
    doesExpanded: Boolean,
    onExpandChanged: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(SpaceSmall)
            .clickable {
                onExpandChanged()
            },
        shape = RoundedCornerShape(4.dp),
    ) {
        StandardExpandable(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceSmall),
            expanded = doesExpanded,
            onExpandChanged = {
                onExpandChanged()
            },
            title = {
                TextWithIcon(
                    text = "Order Details",
                    icon = Icons.Default.Inventory,
                    isTitle = true
                )
            },
            trailing = {
                StandardOutlinedChip(
                    text = cartOrder.cartOrderStatus,
                    isSelected = false,
                    isToggleable = false,
                    onClick = {}
                )
            },
            rowClickable = true,
            expand = { modifier: Modifier ->
                IconButton(
                    modifier = modifier,
                    onClick = {
                        onExpandChanged()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowDown,
                        contentDescription = "Expand More",
                        tint = MaterialTheme.colors.secondary
                    )
                }
            },
            content = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(SpaceSmall)
                ) {

                    TextWithIcon(
                        text = cartOrder.orderId,
                        icon = Icons.Default.Tag
                    )

                    Spacer(modifier = Modifier.height(SpaceSmall))

                    TextWithIcon(
                        text = "Order Type : ${cartOrder.orderType}",
                        icon = if(cartOrder.orderType == CartOrderType.DineIn.orderType)
                            Icons.Default.RoomService else Icons.Default.DeliveryDining
                    )

                    Spacer(modifier = Modifier.height(SpaceSmall))

                    TextWithIcon(
                        text = "Created At : ${cartOrder.createdAt.toFormattedDateAndTime}",
                        icon = Icons.Default.MoreTime
                    )

                    cartOrder.updatedAt?.let {
                        Spacer(modifier = Modifier.height(SpaceSmall))

                        TextWithIcon(
                            text = "Updated At : ${it.toFormattedDateAndTime}",
                            icon = Icons.Default.Update
                        )
                    }
                }
            },
        )
    }
}

/**
 * This composable displays the customer details
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CustomerDetails(
    customer: Customer,
    doesExpanded : Boolean,
    onExpandChanged : () -> Unit,
    onClickViewDetails : (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(SpaceSmall)
            .clickable {
                onExpandChanged()
            },
        shape = RoundedCornerShape(4.dp),
    ) {
        StandardExpandable(
            onExpandChanged = {
                onExpandChanged()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceSmall),
            expanded = doesExpanded,
            title = {
                TextWithIcon(
                    text = "Customer Details",
                    icon = Icons.Default.Person,
                    isTitle = true
                )
            },
            rowClickable = true,
            expand = {  modifier: Modifier ->
                IconButton(
                    onClick = {
                        onClickViewDetails(customer.customerId)
                    }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                        contentDescription = "View Address Details",
                        tint = MaterialTheme.colors.secondaryVariant
                    )
                }
                IconButton(
                    modifier = modifier,
                    onClick = {
                        onExpandChanged()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowDown,
                        contentDescription = "Expand More",
                        tint = MaterialTheme.colors.secondary
                    )
                }
            },
            content = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(SpaceSmall),
                ) {
                    customer.customerName?.let {
                        TextWithIcon(
                            text = "Name: $it",
                            icon = Icons.Default.Person,
                        )
                        Spacer(modifier = Modifier.height(SpaceSmall))
                    }

                    TextWithIcon(
                        text = "Phone: ${customer.customerPhone}",
                        icon = Icons.Default.PhoneAndroid,
                    )

                    customer.customerEmail?.let {
                        Spacer(modifier = Modifier.height(SpaceSmall))

                        TextWithIcon(
                            text = "Name: $it",
                            icon = Icons.Default.AlternateEmail,
                        )
                    }

                    Spacer(modifier = Modifier.height(SpaceSmall))

                    TextWithIcon(
                        text = "Created At : ${customer.createdAt.toFormattedDateAndTime}",
                        icon = Icons.Default.MoreTime
                    )

                    customer.updatedAt?.let {
                        Spacer(modifier = Modifier.height(SpaceSmall))

                        TextWithIcon(
                            text = "Updated At : ${it.toFormattedDateAndTime}",
                            icon = Icons.Default.Update
                        )
                    }

                    Spacer(modifier = Modifier.height(SpaceMedium))

                    Button(
                        onClick = {
                            onClickViewDetails(customer.customerId)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(ButtonSize),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = LightColor6,
                            contentColor = Color.Black
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.OpenInBrowser,
                            contentDescription = "Customer Details"
                        )
                        Spacer(modifier = Modifier.width(SpaceMini))
                        Text(
                            text = "View Customer Details".uppercase(),
                            style = MaterialTheme.typography.button,
                        )
                    }
                }
            },
        )
    }
}

/**
 * This composable displays the address details
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AddressDetails(
    address: Address,
    doesExpanded : Boolean,
    onExpandChanged : () -> Unit,
    onClickViewDetails: (String) -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(SpaceSmall)
            .clickable {
                onExpandChanged()
            },
        shape = RoundedCornerShape(4.dp),
    ) {
        StandardExpandable(
            onExpandChanged = {
                onExpandChanged()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceSmall),
            expanded = doesExpanded,
            title = {
                TextWithIcon(
                    text = "Address Details",
                    icon = Icons.Default.LocationOn,
                    isTitle = true
                )
            },
            rowClickable = true,
            expand = {  modifier: Modifier ->
                IconButton(
                    onClick = {
                        onClickViewDetails(address.addressId)
                    }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                        contentDescription = "View Address Details",
                        tint = MaterialTheme.colors.secondaryVariant
                    )
                }

                IconButton(
                    modifier = modifier,
                    onClick = {
                        onExpandChanged()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowDown,
                        contentDescription = "Expand More",
                        tint = MaterialTheme.colors.secondary
                    )
                }
            },
            content = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(SpaceSmall),
                ) {
                    TextWithIcon(
                        text = "Short Name: ${address.shortName}",
                        icon = Icons.Default.Business,
                    )
                    Spacer(modifier = Modifier.height(SpaceSmall))

                    TextWithIcon(
                        text = "Name: ${address.addressName}",
                        icon = Icons.Default.Home,
                    )

                    Spacer(modifier = Modifier.height(SpaceSmall))

                    TextWithIcon(
                        text = "Created At : ${address.createdAt.toFormattedDateAndTime}",
                        icon = Icons.Default.MoreTime
                    )

                    address.updatedAt?.let {
                        Spacer(modifier = Modifier.height(SpaceSmall))

                        TextWithIcon(
                            text = "Updated At : ${it.toFormattedDateAndTime}",
                            icon = Icons.Default.Update
                        )
                    }

                    Spacer(modifier = Modifier.height(SpaceMedium))

                    Button(
                        onClick = {
                            onClickViewDetails(address.addressId)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(ButtonSize),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = MaterialTheme.colors.secondaryVariant,
                        )
                    ) {
                        Icon(imageVector = Icons.Default.Details, contentDescription = "Address Details" )
                        Spacer(modifier = Modifier.width(SpaceMini))
                        Text(
                            text = "View Address Details".uppercase(),
                            style = MaterialTheme.typography.button,
                        )
                    }
                }
            },
        )
    }
}


/**
 * This composable displays the cart items
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CartItemDetails(
    cartOrder : CartOrder,
    cartProduct: List<CartProductItem>,
    charges: List<Charges>,
    orderPrice: Pair<Int, Int>,
    doesExpanded : Boolean,
    onExpandChanged : () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(SpaceSmall)
            .clickable {
                onExpandChanged()
            },
        shape = RoundedCornerShape(4.dp),
    ){
        StandardExpandable(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceSmall),
            expanded = doesExpanded,
            onExpandChanged = {
                onExpandChanged()
            },
            title = {
                TextWithIcon(
                    text = "Cart Items",
                    icon = Icons.Default.ShoppingBag,
                    isTitle = true
                )
            },
            trailing = {
                StandardOutlinedChip(
                    text = "${cartProduct.size} Items",
                    isToggleable = false,
                    isSelected = false,
                    onClick = {}
                )
            },
            rowClickable = true,
            expand = { modifier: Modifier ->
                IconButton(
                    modifier = modifier,
                    onClick = {
                        onExpandChanged()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowDown,
                        contentDescription = "Expand More",
                        tint = MaterialTheme.colors.secondary
                    )
                }
            },
            content = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(SpaceSmall)
                ) {
                    ThreeGridTexts(
                        textOne = "Name",
                        textTwo = "Price",
                        textThree = "Qty",
                        isTitle = true
                    )
                    Spacer(modifier = Modifier.height(SpaceSmall))

                    Divider(modifier = Modifier.fillMaxWidth())

                    Spacer(modifier = Modifier.height(SpaceSmall))

                    cartProduct.forEach { product ->
                        ThreeGridTexts(
                            textOne = product.productName,
                            textTwo = product.productPrice.toString().toRupee,
                            textThree = product.productQuantity.toString(),
                        )
                        Spacer(modifier = Modifier.height(SpaceSmall))
                    }

                    if(cartOrder.addOnItems.isNotEmpty()){
                        Spacer(modifier = Modifier.height(SpaceSmall))

                        TextDivider(
                            text = "Add On Items"
                        )

                        Spacer(modifier = Modifier.height(SpaceSmall))

                        for(addOnItem in cartOrder.addOnItems){
                            TwoGridTexts(
                                textOne = addOnItem.itemName,
                                textTwo = addOnItem.itemPrice.toString().toRupee,
                            )
                            Spacer(modifier = Modifier.height(SpaceSmall))
                        }
                    }

                    if(cartOrder.doesChargesIncluded && cartOrder.orderType != CartOrderType.DineIn.orderType){
                        Spacer(modifier = Modifier.height(SpaceSmall))

                        TextDivider(
                            text = "Charges"
                        )

                        Spacer(modifier = Modifier.height(SpaceSmall))

                        for(charge in charges){
                            if(charge.isApplicable){
                                TwoGridTexts(
                                    textOne = charge.chargesName,
                                    textTwo = charge.chargesPrice.toString().toRupee,
                                )
                                Spacer(modifier = Modifier.height(SpaceSmall))
                            }
                        }

                    }

                    Spacer(modifier = Modifier.height(SpaceSmall))

                    Divider(modifier = Modifier.fillMaxWidth())

                    Spacer(modifier = Modifier.height(SpaceSmall))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "Sub Total",
                            style = MaterialTheme.typography.body1,
                        )

                        Text(
                            text = orderPrice.first.toString().toRupee,
                            style = MaterialTheme.typography.body1,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(SpaceSmall))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "Discount",
                            style = MaterialTheme.typography.body1
                        )

                        Text(
                            text = orderPrice.second.toString().toRupee,
                            style = MaterialTheme.typography.body1,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(SpaceSmall))

                    Divider(modifier = Modifier.fillMaxWidth(), thickness = 2.dp)

                    Spacer(modifier = Modifier.height(SpaceSmall))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "Total",
                            style = MaterialTheme.typography.body1,
                            fontWeight = FontWeight.Bold,
                        )

                        Text(
                            text = (orderPrice.first.minus(orderPrice.second)).toString().toRupee,
                            style = MaterialTheme.typography.body1,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(SpaceSmall))
                }
            },
        )
    }
}