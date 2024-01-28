package com.niyaj.popos.features.product.presentation.details

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FabPosition
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRightAlt
import androidx.compose.material.icons.automirrored.filled.Feed
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.AllInbox
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CollectionsBookmark
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.niyaj.popos.common.utils.isSameDay
import com.niyaj.popos.common.utils.isScrolled
import com.niyaj.popos.common.utils.toBarDate
import com.niyaj.popos.common.utils.toDate
import com.niyaj.popos.common.utils.toFormattedDateAndTime
import com.niyaj.popos.common.utils.toPrettyDate
import com.niyaj.popos.common.utils.toRupee
import com.niyaj.popos.common.utils.toTime
import com.niyaj.popos.features.common.ui.theme.Cream
import com.niyaj.popos.features.common.ui.theme.LightColor21
import com.niyaj.popos.features.common.ui.theme.LightColor6
import com.niyaj.popos.features.common.ui.theme.Pewter
import com.niyaj.popos.features.common.ui.theme.PoposPink300
import com.niyaj.popos.features.common.ui.theme.SpaceMedium
import com.niyaj.popos.features.common.ui.theme.SpaceMini
import com.niyaj.popos.features.common.ui.theme.SpaceSmall
import com.niyaj.popos.features.components.ItemNotAvailable
import com.niyaj.popos.features.components.LoadingIndicator
import com.niyaj.popos.features.components.StandardExpandable
import com.niyaj.popos.features.components.StandardFabButton
import com.niyaj.popos.features.components.StandardScaffold
import com.niyaj.popos.features.components.TextWithCount
import com.niyaj.popos.features.components.TextWithIcon
import com.niyaj.popos.features.destinations.AddEditProductScreenDestination
import com.niyaj.popos.features.destinations.OrderDetailsScreenDestination
import com.niyaj.popos.features.product.domain.model.Product
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.navigate
import io.sentry.compose.SentryTraced
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class)
@Destination
@Composable
fun ProductDetailsScreen(
    productId: String,
    navController: NavController = rememberNavController(),
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    viewModel: ProductDetailsViewModel = hiltViewModel()
) {
    val lazyListState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    val product = viewModel.product.collectAsStateWithLifecycle().value

    val orderDetails = viewModel.orderDetails.collectAsStateWithLifecycle().value

    val totalDetails = viewModel.totalOrders.collectAsStateWithLifecycle().value

    var productDetailsExpanded by rememberSaveable {
        mutableStateOf(false)
    }

    var orderDetailsExpanded by rememberSaveable {
        mutableStateOf(true)
    }

    SentryTraced(tag = "productDetails-$productId") {
        StandardScaffold(
            navController = navController,
            scaffoldState = scaffoldState,
            title = {
                Text(text = "Product Details")
            },
            showBackArrow = true,
            floatingActionButtonPosition = FabPosition.End,
            floatingActionButton = {
                StandardFabButton(
                    showScrollToTop = lazyListState.isScrolled,
                    onScrollToTopClick = {
                        scope.launch {
                            lazyListState.animateScrollToItem(0)
                        }
                    },
                    visible = false,
                )
            }
        ) {
            LazyColumn(
                state = lazyListState,
                modifier = Modifier.padding(SpaceSmall)
            ) {

                item("TotalOrderDetails") {
                    ProductTotalOrdersDetails(details = totalDetails)
                    Spacer(modifier = Modifier.height(SpaceSmall))
                }

                item("ProductDetails") {
                    ProductDetails(
                        product = product,
                        onExpanded = {
                            productDetailsExpanded = !productDetailsExpanded
                        },
                        doesExpanded = productDetailsExpanded,
                        onClickEdit = {
                            navController.navigate(AddEditProductScreenDestination(it))
                        }
                    )

                    Spacer(modifier = Modifier.height(SpaceSmall))
                }

                item("OrderDetails") {
                    ProductOrderDetails(
                        orderState = orderDetails,
                        onExpanded = {
                            orderDetailsExpanded = !orderDetailsExpanded
                        },
                        doesExpanded = orderDetailsExpanded,
                        onClickOrder = {
                            navController.navigate(OrderDetailsScreenDestination(it))
                        }
                    )

                    Spacer(modifier = Modifier.height(SpaceSmall))
                }
            }
        }
    }
}


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProductTotalOrdersDetails(
    details: ProductTotalOrderDetails,
    isLoading: Boolean = false,
) {
    Card(
        modifier = Modifier
            .testTag("CalculateSalary")
            .fillMaxWidth(),
        shape = RoundedCornerShape(4.dp),
        elevation = SpaceMini
    ) {
        if (isLoading) {
            LoadingIndicator()
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(SpaceMedium),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.Start,
            ) {
                Text(
                    text = "Total Orders",
                    style = MaterialTheme.typography.h6,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(SpaceSmall))
                Spacer(modifier = Modifier.height(SpaceSmall))
                Divider(modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(SpaceSmall))

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = details.totalAmount.toRupee,
                        style = MaterialTheme.typography.h5,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.testTag("ProductTotalAmount")
                    )

                    val startDate = details.datePeriod.first
                    val endDate = details.datePeriod.second

                    if (startDate.isNotEmpty()) {
                        Card(
                            backgroundColor = LightColor21,
                            modifier = Modifier.testTag("DatePeriod")
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(SpaceMini),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = startDate.toBarDate,
                                    style = MaterialTheme.typography.body2,
                                )

                                if (endDate.isNotEmpty()) {
                                    if (!details.datePeriod.isSameDay()) {
                                        Spacer(modifier = Modifier.width(SpaceMini))
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowRightAlt,
                                            contentDescription = "DatePeriod"
                                        )
                                        Spacer(modifier = Modifier.width(SpaceMini))
                                        Text(
                                            text = endDate.toBarDate,
                                            style = MaterialTheme.typography.body2,
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(SpaceSmall))
                Divider(modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(SpaceSmall))

                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalArrangement = Arrangement.Center
                ) {
                    Card(
                        backgroundColor = Cream,
                        modifier = Modifier
                            .weight(1.2f)
                            .testTag("DineInSales")
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(SpaceMini),
                            verticalArrangement = Arrangement.SpaceBetween,
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(
                                text = "DineIn Sales",
                                style = MaterialTheme.typography.body2,
                            )

                            Spacer(modifier = Modifier.height(SpaceMini))
                            Divider(modifier = Modifier.fillMaxWidth())
                            Spacer(modifier = Modifier.height(SpaceMini))

                            Text(
                                text = details.dineInAmount.toRupee,
                                style = MaterialTheme.typography.subtitle1,
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(SpaceSmall))

                    Card(
                        backgroundColor = PoposPink300,
                        modifier = Modifier
                            .weight(1.2f)
                            .testTag("DineOutSales")
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(SpaceMini),
                            verticalArrangement = Arrangement.SpaceBetween,
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(
                                text = "DineOut Sales",
                                style = MaterialTheme.typography.body2,
                            )

                            Spacer(modifier = Modifier.height(SpaceMini))
                            Divider(modifier = Modifier.fillMaxWidth())
                            Spacer(modifier = Modifier.height(SpaceMini))

                            Text(
                                text = details.dineOutAmount.toRupee,
                                style = MaterialTheme.typography.subtitle1,
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(SpaceSmall))
                Divider(modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(SpaceSmall))

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (details.mostOrderItemDate.isNotEmpty()) {
                        Card(
                            backgroundColor = LightColor6,
                            modifier = Modifier.testTag("MostOrders")
                        ) {
                            Text(
                                text = "Most Orders - ${details.mostOrderItemDate}",
                                style = MaterialTheme.typography.body2,
                                modifier = Modifier.padding(SpaceSmall)
                            )
                        }
                        Spacer(modifier = Modifier.width(SpaceSmall))
                    }

                    if (details.mostOrderQtyDate.isNotEmpty()) {
                        Card(
                            backgroundColor = LightColor6,
                            modifier = Modifier.testTag("MostSales")
                        ) {
                            Text(
                                text = "Most Sales Qty - ${details.mostOrderQtyDate}",
                                style = MaterialTheme.typography.body2,
                                modifier = Modifier.padding(SpaceSmall)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(SpaceSmall))
            }
        }
    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ProductDetails(
    product: Product,
    onExpanded: () -> Unit,
    doesExpanded: Boolean,
    onClickEdit: (String) -> Unit,
) {
    Card(
        onClick = onExpanded,
        modifier = Modifier
            .testTag("ProductDetails")
            .fillMaxWidth(),
        shape = RoundedCornerShape(4.dp),
        elevation = SpaceMini
    ) {
        StandardExpandable(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceSmall),
            expanded = doesExpanded,
            onExpandChanged = {
                onExpanded()
            },
            title = {
                TextWithIcon(
                    text = "Product Details",
                    icon = Icons.AutoMirrored.Filled.Feed,
                    isTitle = true
                )
            },
            trailing = {
                IconButton(
                    onClick = {
                        onClickEdit(product.productId)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Employee",
                        tint = MaterialTheme.colors.primary
                    )
                }
            },
            rowClickable = true,
            expand = { modifier: Modifier ->
                IconButton(
                    modifier = modifier,
                    onClick = onExpanded
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
                        modifier = Modifier.testTag(product.productName),
                        text = "Name - ${product.productName}",
                        icon = Icons.Default.CollectionsBookmark
                    )

                    Spacer(modifier = Modifier.height(SpaceSmall))

                    TextWithIcon(
                        modifier = Modifier.testTag(product.productPrice.toString()),
                        text = "Price - ${product.productPrice.toString().toRupee}",
                        icon = Icons.Default.CurrencyRupee
                    )

                    Spacer(modifier = Modifier.height(SpaceSmall))

                    TextWithIcon(
                        modifier = Modifier.testTag(product.category?.categoryName ?: "Category"),
                        text = "Category - ${product.category?.categoryName}",
                        icon = Icons.Default.Category
                    )

                    Spacer(modifier = Modifier.height(SpaceSmall))

                    TextWithIcon(
                        modifier = Modifier.testTag(product.productAvailability.toString()),
                        text = "Availability : ${product.productAvailability}",
                        icon = if (product.productAvailability)
                            Icons.Default.RadioButtonChecked
                        else Icons.Default.RadioButtonUnchecked
                    )

                    Spacer(modifier = Modifier.height(SpaceSmall))

                    TextWithIcon(
                        modifier = Modifier.testTag(product.createdAt.toDate),
                        text = "Created At : ${product.createdAt.toFormattedDateAndTime}",
                        icon = Icons.Default.CalendarToday
                    )

                    product.updatedAt?.let {
                        Spacer(modifier = Modifier.height(SpaceSmall))
                        TextWithIcon(
                            text = "Updated At : ${it.toFormattedDateAndTime}",
                            icon = Icons.AutoMirrored.Filled.Login
                        )
                    }
                }
            },
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ProductOrderDetails(
    orderState: ProductOrderState,
    onExpanded: () -> Unit,
    doesExpanded: Boolean,
    onClickOrder: (String) -> Unit,
) {
    Card(
        onClick = onExpanded,
        modifier = Modifier
            .testTag("EmployeeDetails")
            .fillMaxWidth(),
        shape = RoundedCornerShape(4.dp),
        elevation = SpaceMini
    ) {
        StandardExpandable(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceSmall),
            expanded = doesExpanded,
            onExpandChanged = {
                onExpanded()
            },
            title = {
                TextWithIcon(
                    text = "Recent Orders",
                    icon = Icons.Default.AllInbox,
                    isTitle = true
                )
            },
            rowClickable = true,
            expand = { modifier: Modifier ->
                IconButton(
                    modifier = modifier,
                    onClick = onExpanded
                ) {
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowDown,
                        contentDescription = "Expand More",
                        tint = MaterialTheme.colors.secondary
                    )
                }
            },
            content = {
                Crossfade(targetState = orderState, label = "") { state ->
                    when {
                        state.isLoading -> LoadingIndicator()

                        state.productOrders.isNotEmpty() && state.productPrice != 0 -> {
                            val productOrders = state.productOrders
                            val groupedByDate =
                                productOrders.groupBy { it.orderedDate.toPrettyDate() }

                            Column {
                                groupedByDate.forEach { (date, orders) ->
                                    val totalSales = orders
                                        .sumOf { it.quantity }
                                        .times(state.productPrice).toString()

                                    TextWithCount(
                                        modifier = Modifier
                                            .background(Color.Transparent),
                                        text = date,
                                        trailingText = totalSales.toRupee,
                                        count = orders.size,
                                    )

                                    val grpByOrderType = orders.groupBy { it.orderType }

                                    grpByOrderType.forEach { (orderType, grpOrders) ->
                                        val totalPrice = grpOrders
                                            .sumOf { it.quantity }
                                            .times(state.productPrice).toString()

                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(Pewter)
                                                .align(Alignment.CenterHorizontally)
                                        ) {
                                            Text(
                                                text = "$orderType - ${totalPrice.toRupee}",
                                                style = MaterialTheme.typography.caption,
                                                textAlign = TextAlign.Center,
                                                modifier = Modifier
                                                    .padding(SpaceMini)
                                                    .align(Alignment.Center)
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(SpaceMini))

                                        grpOrders.forEachIndexed { index, order ->
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clickable {
                                                        onClickOrder(order.cartOrderId)
                                                    }
                                                    .padding(SpaceSmall),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                TextWithIcon(
                                                    text = order.orderId,
                                                    icon = Icons.Default.Tag,
                                                    isTitle = true,
                                                )

                                                Column {
                                                    order.customerPhone?.let {
                                                        Spacer(modifier = Modifier.height(SpaceMini))
                                                        Text(text = it)
                                                    }
                                                    Spacer(modifier = Modifier.height(SpaceMini))

                                                    order.customerAddress?.let {
                                                        Spacer(modifier = Modifier.height(SpaceMini))
                                                        Text(text = it)
                                                    }
                                                }

                                                Text(
                                                    text = "${order.quantity} Qty",
                                                    textAlign = TextAlign.Start,
                                                    fontWeight = FontWeight.Bold
                                                )

                                                Text(
                                                    text = order.orderedDate.toTime,
                                                    textAlign = TextAlign.End
                                                )
                                            }

                                            if (index != productOrders.size - 1) {
                                                Spacer(modifier = Modifier.height(SpaceMini))
                                                Divider(modifier = Modifier.fillMaxWidth())
                                                Spacer(modifier = Modifier.height(SpaceMini))
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        else -> {
                            ItemNotAvailable(
                                text = state.hasError
                                    ?: "Have not placed any order on this product."
                            )
                        }
                    }
                }
            }
        )
    }
}