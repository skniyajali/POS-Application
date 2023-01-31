package com.niyaj.popos.features.cart_order.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FabPosition
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.niyaj.popos.R
import com.niyaj.popos.features.cart_order.domain.util.OrderStatus
import com.niyaj.popos.features.common.ui.theme.SpaceMini
import com.niyaj.popos.features.common.ui.theme.SpaceSmall
import com.niyaj.popos.features.common.ui.theme.TextGray
import com.niyaj.popos.features.common.util.UiEvent
import com.niyaj.popos.features.components.ExtendedFabButton
import com.niyaj.popos.features.components.ItemNotAvailable
import com.niyaj.popos.features.components.StandardChip
import com.niyaj.popos.features.components.StandardScaffold
import com.niyaj.popos.features.components.StandardSearchBar
import com.niyaj.popos.features.components.TextWithCount
import com.niyaj.popos.features.components.TextWithIcon
import com.niyaj.popos.features.components.header
import com.niyaj.popos.features.destinations.AddEditCartOrderScreenDestination
import com.niyaj.popos.features.destinations.CartOrderSettingScreenDestination
import com.niyaj.popos.features.destinations.OrderDetailsScreenDestination
import com.niyaj.popos.util.toBarDate
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.message
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import com.vanpra.composematerialdialogs.title
import kotlinx.coroutines.launch
import timber.log.Timber

@OptIn(ExperimentalMaterialApi::class, ExperimentalLifecycleComposeApi::class)
@Destination
@Composable
fun CartOrderScreen(
    navController: NavController,
    cartOrderViewModel: CartOrderViewModel = hiltViewModel(),
    scaffoldState: ScaffoldState,
    resultRecipient: ResultRecipient<AddEditCartOrderScreenDestination, String>,
    settingRecipient: ResultRecipient<CartOrderSettingScreenDestination, String>,
) {
    val lazyListState = rememberLazyGridState()
    val scope = rememberCoroutineScope()
    val showAlert = rememberMaterialDialogState()
    val systemUiController = rememberSystemUiController()

    val cartOrders = cartOrderViewModel.cartOrders.collectAsStateWithLifecycle().value.cartOrders
    val groupedByDate = cartOrders.groupBy { it.createdAt.toBarDate }
    val isLoading = cartOrderViewModel.cartOrders.collectAsStateWithLifecycle().value.isLoading
    val hasError = cartOrderViewModel.cartOrders.collectAsStateWithLifecycle().value.error

    val selectedCartOrder = cartOrderViewModel.selectedCartOrder.collectAsStateWithLifecycle().value
    val selectedOrder = cartOrderViewModel.selectedOrder.collectAsStateWithLifecycle().value

    val transition = updateTransition(selectedOrder.isNotEmpty(), label = "isContextual")

    val statusBarColor by transition.animateColor(label = "statusBarContextual") { isContextualMode ->
        if (isContextualMode) {
            MaterialTheme.colors.secondary
        } else {
            MaterialTheme.colors.primary
        }
    }

    val backgroundColor by transition.animateColor(label = "actionBarContextual") { isContextualMode ->
        if (isContextualMode) {
            MaterialTheme.colors.secondary
        } else {
            MaterialTheme.colors.primary
        }
    }

    val showSearchBar = cartOrderViewModel.toggledSearchBar.collectAsStateWithLifecycle().value

    var showMenu by remember { mutableStateOf(false) }

    val showScrollToTop = remember {
        derivedStateOf {
            lazyListState.firstVisibleItemIndex > 0
        }
    }

    SideEffect {
        systemUiController.setStatusBarColor(
            color = statusBarColor,
            darkIcons = false
        )
    }

    BackHandler(true) {
        if (showSearchBar) {
            cartOrderViewModel.onSearchBarCloseAndClearClick()
        } else if (selectedOrder.isNotEmpty()) {
            cartOrderViewModel.onCartOrderEvent(CartOrderEvent.SelectCartOrder(selectedOrder))
        } else {
            navController.navigateUp()
        }
    }

    resultRecipient.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {
                if (selectedOrder.isNotEmpty()) {
                    cartOrderViewModel.onCartOrderEvent(CartOrderEvent.SelectCartOrder(selectedOrder))
                }
            }

            is NavResult.Value -> {
                scope.launch {
                    scaffoldState.snackbarHostState.showSnackbar(result.value)
                }
            }
        }
    }

    settingRecipient.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {
                if (selectedOrder.isNotEmpty()) {
                    cartOrderViewModel.onCartOrderEvent(CartOrderEvent.SelectCartOrder(selectedOrder))
                }
            }

            is NavResult.Value -> {
                scope.launch {
                    scaffoldState.snackbarHostState.showSnackbar(result.value)
                }
            }
        }
    }

    LaunchedEffect(key1 = true) {
        cartOrderViewModel.eventFlow.collect { event ->
            when (event) {
                is UiEvent.OnSuccess -> {
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = event.successMessage
                    )
                }

                is UiEvent.OnError -> {
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = event.errorMessage
                    )
                }

                is UiEvent.IsLoading -> {
                    Timber.d("Loading.. ${event.isLoading.toString()}")
                }
            }
        }
    }

    StandardScaffold(
        navController = navController,
        scaffoldState = scaffoldState,
        showBackArrow = selectedOrder.isEmpty(),
        onBackButtonClick = {
            if (showSearchBar) {
                cartOrderViewModel.onSearchBarCloseAndClearClick()
            } else {
                navController.navigateUp()
            }
        },
        title = {
            if (selectedOrder.isEmpty()) {
                Text(text = "Cart Orders")
            }
        },
        isFloatingActionButtonDocked = cartOrders.isNotEmpty(),
        floatingActionButtonPosition = if (showScrollToTop.value) FabPosition.End else FabPosition.Center,
        floatingActionButton = {
            ExtendedFabButton(
                text = stringResource(id = R.string.create_new_order).uppercase(),
                showScrollToTop = showScrollToTop.value,
                visible = cartOrders.isNotEmpty() && selectedOrder.isEmpty() && !showSearchBar,
                onScrollToTopClick = {
                    scope.launch {
                        lazyListState.animateScrollToItem(index = 0)
                    }
                },
                onClick = {
                    navController.navigate(AddEditCartOrderScreenDestination())
                },
            )
        },
        navActions = {
            if (selectedOrder.isNotEmpty()) {
                IconButton(
                    onClick = {
                        cartOrderViewModel.onCartOrderEvent(CartOrderEvent.SelectCartOrderEvent(selectedOrder))
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.TaskAlt,
                        contentDescription = stringResource(id = R.string.select_cart_order),
                        tint = MaterialTheme.colors.onPrimary,
                    )
                }

                IconButton(
                    onClick = {
                        navController.navigate(AddEditCartOrderScreenDestination(cartOrderId = selectedOrder))
                    },
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Address",
                        tint = MaterialTheme.colors.onPrimary,
                    )
                }

                IconButton(
                    onClick = {
                        showAlert.show()
                    },
                    enabled = selectedOrder.isNotEmpty()
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Address",
                        tint = MaterialTheme.colors.onPrimary,
                    )
                }

                IconButton(
                    onClick = {
                        cartOrderViewModel.onCartOrderEvent(
                            CartOrderEvent.SelectCartOrder(
                                selectedOrder
                            )
                        )
                        navController.navigate(OrderDetailsScreenDestination(cartOrderId = selectedOrder))
                    },
                    enabled = selectedOrder.isNotEmpty()
                ) {
                    Icon(
                        imageVector = Icons.Default.OpenInNew,
                        contentDescription = "View Details",
                        tint = MaterialTheme.colors.onPrimary,
                    )
                }
            } else if (showSearchBar) {
                StandardSearchBar(
                    searchText = cartOrderViewModel.searchText.collectAsStateWithLifecycle().value,
                    placeholderText = "Search for cart orders...",
                    onSearchTextChanged = {
                        cartOrderViewModel.onCartOrderEvent(CartOrderEvent.OnSearchCartOrder(it))
                    },
                    onClearClick = {
                        cartOrderViewModel.onSearchTextClearClick()
                    },
                )
            } else {
                if (cartOrders.isNotEmpty()) {
                    IconButton(
                        onClick = {
                            cartOrderViewModel.onCartOrderEvent(CartOrderEvent.ToggleSearchBar)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = stringResource(id = R.string.search_icon),
                            tint = MaterialTheme.colors.onPrimary,
                        )
                    }
                }

                IconButton(
                    onClick = {
                        navController.navigate(CartOrderSettingScreenDestination())
                    },
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Open Settings",
                        tint = MaterialTheme.colors.onPrimary,
                    )
                }
                Box {
                    IconButton(
                        onClick = {
                            showMenu = !showMenu
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "View More Settings",
                            tint = MaterialTheme.colors.onPrimary,
                        )
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu  = false },
                    ) {
                        DropdownMenuItem(
                            onClick = {
                                cartOrderViewModel.onCartOrderEvent(CartOrderEvent.ViewAllOrders)
                                showMenu = false
                            }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Visibility,
                                    contentDescription = "View All",
                                    tint = MaterialTheme.colors.secondaryVariant
                                )
                                Spacer(modifier = Modifier.width(SpaceSmall))
                                Text(
                                    text = "View All",
                                    style = MaterialTheme.typography.body1,
                                )
                            }
                        }
                    }
                }
            }
        },
        navigationIcon = {
            if (selectedOrder.isNotEmpty()) {
                IconButton(
                    onClick = {
                        cartOrderViewModel.onCartOrderEvent(
                            CartOrderEvent.SelectCartOrder(
                                selectedOrder
                            )
                        )
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(id = R.string.close_icon),
                        tint = MaterialTheme.colors.onPrimary,
                    )
                }
            }
        },
        topAppBarBackgroundColor = backgroundColor,
    ) {
        MaterialDialog(
            dialogState = showAlert,
            buttons = {
                positiveButton(
                    text = "Delete",
                    onClick = {
                        cartOrderViewModel.onCartOrderEvent(
                            CartOrderEvent.DeleteCartOrder(
                                selectedOrder
                            )
                        )
                    }
                )
                negativeButton(
                    text = "Cancel",
                    onClick = {
                        showAlert.hide()
                    },
                )
            }
        ) {
            title(text = "Are you sure to delete this order?")
            message(res = R.string.delete_order_msg)
        }

        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing = isLoading),
            onRefresh = {
                cartOrderViewModel.onCartOrderEvent(CartOrderEvent.RefreshCartOrder)
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(SpaceSmall),
            ) {
                if (isLoading) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        CircularProgressIndicator()
                    }
                } else if (cartOrders.isEmpty() || hasError != null) {
                    ItemNotAvailable(
                        text = hasError
                            ?: if (showSearchBar) stringResource(id = R.string.search_item_not_found) else stringResource(
                                id = R.string.cart_order_is_empty
                            ),
                        buttonText = stringResource(id = R.string.create_new_order).uppercase(),
                        onClick = {
                            navController.navigate(AddEditCartOrderScreenDestination())
                        }
                    )
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        state = lazyListState,
                    ) {
                        groupedByDate.forEach { (date, orders) ->
                            header {
                                TextWithCount(
                                    modifier = Modifier
                                        .background(
                                            if (showScrollToTop.value)
                                                MaterialTheme.colors.onPrimary
                                            else Color.Transparent
                                        )
                                        .clip(
                                            RoundedCornerShape(if (showScrollToTop.value) 4.dp else 0.dp)
                                        ),
                                    text = date,
                                    leadingIcon = Icons.Default.CalendarMonth,
                                    count = orders.count(),
                                    onClick = {}
                                )
                            }

                            itemsIndexed(orders) { _, cartOrder ->
                                Card(
                                    onClick = {
                                        cartOrderViewModel.onCartOrderEvent(
                                            CartOrderEvent.SelectCartOrder(cartOrder.cartOrderId)
                                        )
                                    },
                                    enabled = cartOrder.cartOrderStatus == OrderStatus.Processing.orderStatus,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(SpaceSmall),
                                    shape = RoundedCornerShape(4.dp),
                                    backgroundColor = MaterialTheme.colors.surface,
                                    border = if (selectedOrder == cartOrder.cartOrderId)
                                        BorderStroke(1.dp, MaterialTheme.colors.primary)
                                    else if (cartOrder.cartOrderId == selectedCartOrder.cartOrderId)
                                        BorderStroke(1.dp, MaterialTheme.colors.secondaryVariant)
                                    else null,
                                    elevation = 2.dp,
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .padding(SpaceSmall)
                                            .fillMaxWidth(),
                                        verticalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            TextWithIcon(
                                                text = cartOrder.orderId,
                                                isTitle = true,
                                                icon = Icons.Default.Tag
                                            )

                                            if (cartOrder.cartOrderStatus != OrderStatus.Processing.orderStatus){
                                                StandardChip(
                                                    text = cartOrder.cartOrderStatus,
                                                    isPrimary = cartOrder.cartOrderStatus == OrderStatus.Processing.orderStatus
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(SpaceMini))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = cartOrder.orderType,
                                                style = MaterialTheme.typography.body1,
                                            )

                                            Text(
                                                text = cartOrder.createdAt.toBarDate,
                                                style = MaterialTheme.typography.overline,
                                                color = TextGray
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
