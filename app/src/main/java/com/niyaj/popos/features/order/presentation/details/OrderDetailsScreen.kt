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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreTime
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.niyaj.popos.features.cart_order.domain.util.CartOrderType
import com.niyaj.popos.features.charges.presentation.ChargesViewModel
import com.niyaj.popos.features.common.ui.theme.SpaceSmall
import com.niyaj.popos.features.components.ItemNotAvailable
import com.niyaj.popos.features.components.StandardExpandable
import com.niyaj.popos.features.components.StandardOutlinedChip
import com.niyaj.popos.features.components.StandardScaffold
import com.niyaj.popos.features.components.TextWithIcon
import com.niyaj.popos.features.order.presentation.components.TextDivider
import com.niyaj.popos.features.order.presentation.components.ThreeGridTexts
import com.niyaj.popos.features.order.presentation.components.TwoGridTexts
import com.niyaj.popos.features.order.presentation.print_order.PrintEvent
import com.niyaj.popos.features.order.presentation.print_order.PrintViewModel
import com.niyaj.popos.util.toFormattedDateAndTime
import com.niyaj.popos.util.toRupee
import com.ramcosta.composedestinations.annotation.Destination
import timber.log.Timber

@OptIn(ExperimentalMaterialApi::class, ExperimentalPermissionsApi::class)
@Destination
@Composable
fun OrderDetailsScreen(
    cartOrderId: String = "",
    navController: NavController,
    orderDetailsViewModel: OrderDetailsViewModel = hiltViewModel(),
    chargesViewModel: ChargesViewModel = hiltViewModel(),
    printViewModel: PrintViewModel = hiltViewModel()
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
                printViewModel.onPrintEvent(PrintEvent.PrintOrder(it))
            } else {
                // Bluetooth is off, ask user to turn it on
                enableBluetoothContract.launch(enableBluetoothIntent)
                printViewModel.onPrintEvent(PrintEvent.PrintOrder(it))
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
        mutableStateOf(false)
    }


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
            ItemNotAvailable(
                text = "Order Details not available",
            )
        }else{
            LazyColumn {
                item {
                    if(orderDetails.cartOrder != null){
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(SpaceSmall)
                                .clickable {
                                    cartOrderExpended = !cartOrderExpended
                                },
                            shape = RoundedCornerShape(4.dp),
                        ) {
                            StandardExpandable(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(SpaceSmall),
                                expanded = cartOrderExpended,
                                onExpandChanged = {
                                    cartOrderExpended = !cartOrderExpended
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
                                        text = orderDetails.cartOrder.cartOrderStatus,
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
                                            cartOrderExpended = !cartOrderExpended
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
                                            text = orderDetails.cartOrder.orderId,
                                            icon = Icons.Default.Tag
                                        )

                                        Spacer(modifier = Modifier.height(SpaceSmall))

                                        TextWithIcon(
                                            text = "Order Type : ${orderDetails.cartOrder.orderType}",
                                            icon = if(orderDetails.cartOrder.orderType == CartOrderType.DineIn.orderType)
                                                Icons.Default.RoomService else Icons.Default.DeliveryDining
                                        )

                                        Spacer(modifier = Modifier.height(SpaceSmall))

                                        TextWithIcon(
                                            text = "Created At : ${orderDetails.cartOrder.createdAt.toFormattedDateAndTime}",
                                            icon = Icons.Default.MoreTime
                                        )

                                        if(orderDetails.cartOrder.updatedAt != null){
                                            Spacer(modifier = Modifier.height(SpaceSmall))

                                            TextWithIcon(
                                                text = "Updated At : ${orderDetails.cartOrder.updatedAt!!.toFormattedDateAndTime}",
                                                icon = Icons.Default.Update
                                            )
                                        }
                                    }
                                },
                            )

                        }

                        if(orderDetails.cartOrder.customer != null){
                            val customer = orderDetails.cartOrder.customer
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(SpaceSmall)
                                    .clickable {
                                        customerExpended = !customerExpended
                                    },
                                shape = RoundedCornerShape(4.dp),
                            ) {
                                StandardExpandable(
                                    onExpandChanged = {
                                        customerExpended = !customerExpended
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(SpaceSmall),
                                    expanded = customerExpended,
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
                                            modifier = modifier,
                                            onClick = {
                                                customerExpended = !customerExpended
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
                                            if (customer != null) {
                                                if(customer.customerName != null){
                                                    TextWithIcon(
                                                        text = "Name: ${customer.customerName}",
                                                        icon = Icons.Default.Person,
                                                    )
                                                    Spacer(modifier = Modifier.height(SpaceSmall))
                                                }

                                                TextWithIcon(
                                                    text = "Phone: ${customer.customerPhone}",
                                                    icon = Icons.Default.PhoneAndroid,
                                                )
                                                if(customer.customerEmail != null){
                                                    Spacer(modifier = Modifier.height(SpaceSmall))

                                                    TextWithIcon(
                                                        text = "Name: ${customer.customerEmail}",
                                                        icon = Icons.Default.AlternateEmail,
                                                    )
                                                }

                                                Spacer(modifier = Modifier.height(SpaceSmall))

                                                TextWithIcon(
                                                    text = "Created At : ${customer.createdAt.toFormattedDateAndTime}",
                                                    icon = Icons.Default.MoreTime
                                                )

                                                if(customer.updatedAt != null){
                                                    Spacer(modifier = Modifier.height(SpaceSmall))

                                                    TextWithIcon(
                                                        text = "Updated At : ${customer.updatedAt!!.toFormattedDateAndTime}",
                                                        icon = Icons.Default.Update
                                                    )
                                                }
                                            }
                                        }
                                    },
                                )
                            }
                        }

                        if(orderDetails.cartOrder.address != null){
                            val address = orderDetails.cartOrder.address
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(SpaceSmall)
                                    .clickable {
                                        addressExpended = !addressExpended
                                    },
                                shape = RoundedCornerShape(4.dp),
                            ) {
                                StandardExpandable(
                                    onExpandChanged = {
                                        addressExpended = !addressExpended
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(SpaceSmall),
                                    expanded = addressExpended,
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
                                            modifier = modifier,
                                            onClick = {
                                                addressExpended = !addressExpended
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

                                            if (address != null) {
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

                                                if(address.updatedAt != null){
                                                    Spacer(modifier = Modifier.height(SpaceSmall))

                                                    TextWithIcon(
                                                        text = "Updated At : ${address.updatedAt!!.toFormattedDateAndTime}",
                                                        icon = Icons.Default.Update
                                                    )
                                                }
                                            }
                                        }
                                    },
                                )
                            }
                        }

                        if(orderDetails.cartProducts.isNotEmpty()){
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(SpaceSmall)
                                    .clickable {
                                        cartExpended = !cartExpended
                                    },
                                shape = RoundedCornerShape(4.dp),
                            ){
                                StandardExpandable(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(SpaceSmall),
                                    expanded = cartExpended,
                                    onExpandChanged = {
                                        cartExpended = !cartExpended
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
                                            text = "${orderDetails.cartProducts.size} Items",
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
                                                cartExpended = !cartExpended
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

                                            for (product in orderDetails.cartProducts){
                                                if(product.product != null){
                                                    ThreeGridTexts(
                                                        textOne = product.product.productName,
                                                        textTwo = product.product.productPrice.toString().toRupee,
                                                        textThree = product.quantity.toString(),
                                                    )
                                                    Spacer(modifier = Modifier.height(SpaceSmall))
                                                }
                                            }

                                            if(orderDetails.cartOrder.addOnItems.isNotEmpty()){
                                                Spacer(modifier = Modifier.height(SpaceSmall))

                                                TextDivider(
                                                    text = "Add On Items"
                                                )

                                                Spacer(modifier = Modifier.height(SpaceSmall))

                                                for(addOnItem in orderDetails.cartOrder.addOnItems){
                                                    TwoGridTexts(
                                                        textOne = addOnItem.itemName,
                                                        textTwo = addOnItem.itemPrice.toString().toRupee,
                                                    )
                                                    Spacer(modifier = Modifier.height(SpaceSmall))
                                                }
                                            }

                                            if(orderDetails.cartOrder.doesChargesIncluded && orderDetails.cartOrder.orderType != CartOrderType.DineIn.orderType){
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
                                                    text = orderDetails.orderPrice.first.toString().toRupee,
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
                                                    text = orderDetails.orderPrice.second.toString().toRupee,
                                                    style = MaterialTheme.typography.body1,
                                                    fontWeight = FontWeight.SemiBold
                                                )
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
                                                    text = "Total",
                                                    style = MaterialTheme.typography.body1,
                                                    fontWeight = FontWeight.Bold,
                                                )

                                                Text(
                                                    text = (orderDetails.orderPrice.first.minus(orderDetails.orderPrice.second)).toString().toRupee,
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
                    }
                }
            }
        }
    }

}