package com.niyaj.popos.presentation.order.details

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.niyaj.popos.domain.util.CartOrderType
import com.niyaj.popos.presentation.charges.ChargesViewModel
import com.niyaj.popos.presentation.components.*
import com.niyaj.popos.presentation.order.components.TextDivider
import com.niyaj.popos.presentation.order.components.ThreeGridTexts
import com.niyaj.popos.presentation.order.components.TwoGridTexts
import com.niyaj.popos.presentation.print_order.PrintEvent
import com.niyaj.popos.presentation.print_order.PrintViewModel
import com.niyaj.popos.presentation.ui.theme.SpaceSmall
import com.niyaj.popos.util.toFormattedDateAndTime
import com.niyaj.popos.util.toRupee
import com.ramcosta.composedestinations.annotation.Destination

@OptIn(ExperimentalMaterialApi::class)
@Destination
@Composable
fun OrderDetailsScreen(
    cartOrderId: String = "",
    navController: NavController,
    orderDetailsViewModel: OrderDetailsViewModel = hiltViewModel(),
    chargesViewModel: ChargesViewModel = hiltViewModel(),
    printViewModel: PrintViewModel = hiltViewModel()
) {

    val scaffoldState = rememberScaffoldState()
    val orderDetails = orderDetailsViewModel.orderDetails.collectAsState().value.orderDetails
    val charges by lazy {
        chargesViewModel.state.chargesItem
    }

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
                        printViewModel.onPrintEvent(PrintEvent.PrintOrder(orderDetails.cartOrder.cartOrderId))
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
                                    StandardChip(
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
                                            text = "Order Type : ${orderDetails.cartOrder.cartOrderType}",
                                            icon = if(orderDetails.cartOrder.cartOrderType == CartOrderType.DineIn.orderType)
                                                Icons.Default.RoomService else Icons.Default.DeliveryDining
                                        )

                                        Spacer(modifier = Modifier.height(SpaceSmall))

                                        TextWithIcon(
                                            text = "Created At : ${orderDetails.cartOrder.created_at!!.toFormattedDateAndTime}",
                                            icon = Icons.Default.MoreTime
                                        )

                                        if(orderDetails.cartOrder.updated_at != null){
                                            Spacer(modifier = Modifier.height(SpaceSmall))

                                            TextWithIcon(
                                                text = "Updated At : ${orderDetails.cartOrder.updated_at.toFormattedDateAndTime}",
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
                                                text = "Created At : ${customer.created_at!!.toFormattedDateAndTime}",
                                                icon = Icons.Default.MoreTime
                                            )

                                            if(customer.updated_at != null){
                                                Spacer(modifier = Modifier.height(SpaceSmall))

                                                TextWithIcon(
                                                    text = "Updated At : ${customer.updated_at.toFormattedDateAndTime}",
                                                    icon = Icons.Default.Update
                                                )
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
                                                text = "Created At : ${address.created_at!!.toFormattedDateAndTime}",
                                                icon = Icons.Default.MoreTime
                                            )

                                            if(address.updated_at != null){
                                                Spacer(modifier = Modifier.height(SpaceSmall))

                                                TextWithIcon(
                                                    text = "Updated At : ${address.updated_at.toFormattedDateAndTime}",
                                                    icon = Icons.Default.Update
                                                )
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
                                        StandardChip(
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

                                            if(orderDetails.cartOrder.doesChargesIncluded && orderDetails.cartOrder.cartOrderType != CartOrderType.DineIn.orderType){
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