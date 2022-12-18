package com.niyaj.popos.presentation.cart_order

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.FabPosition
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.niyaj.popos.R
import com.niyaj.popos.domain.util.UiEvent
import com.niyaj.popos.presentation.components.ExtendedFabButton
import com.niyaj.popos.presentation.components.ItemNotAvailable
import com.niyaj.popos.presentation.components.StandardScaffold
import com.niyaj.popos.presentation.components.StandardSearchBar
import com.niyaj.popos.presentation.destinations.AddEditCartOrderScreenDestination
import com.niyaj.popos.presentation.destinations.CartOrderSettingScreenDestination
import com.niyaj.popos.presentation.destinations.OrderDetailsScreenDestination
import com.niyaj.popos.presentation.ui.theme.SpaceMini
import com.niyaj.popos.presentation.ui.theme.SpaceSmall
import com.niyaj.popos.presentation.ui.theme.TextGray
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

    val cartOrders = cartOrderViewModel.cartOrders.collectAsState().value.cartOrders
    val isLoading = cartOrderViewModel.cartOrders.collectAsState().value.isLoading
    val hasError = cartOrderViewModel.cartOrders.collectAsState().value.error

    val selectedCartOrder = cartOrderViewModel.selectedCartOrder.collectAsState().value
    val selectedOrder = cartOrderViewModel.selectedOrder.collectAsState().value

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

    val showSearchBar = cartOrderViewModel.toggledSearchBar.collectAsState().value

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
                    searchText = cartOrderViewModel.searchText.collectAsState().value,
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
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Open Settings",
                        tint = MaterialTheme.colors.onPrimary,
                    )
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
                        itemsIndexed(cartOrders) { _, cartOrder ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(SpaceSmall)
                                    .clickable {
                                        cartOrderViewModel.onCartOrderEvent(
                                            CartOrderEvent.SelectCartOrder(cartOrder.cartOrderId)
                                        )
                                    },
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
                                    Text(
                                        text = cartOrder.orderId,
                                        style = MaterialTheme.typography.body1,
                                        textAlign = TextAlign.Center,
                                        fontWeight = FontWeight.Bold,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                    Spacer(modifier = Modifier.height(SpaceMini))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = cartOrder.cartOrderType,
                                            style = MaterialTheme.typography.body1,
                                        )

                                        cartOrder.created_at?.toBarDate?.let {
                                            Text(
                                                text = it,
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