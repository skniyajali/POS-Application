package com.niyaj.popos.features.cart_order.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.FabPosition
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.OpenInNew
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
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.niyaj.popos.R
import com.niyaj.popos.features.cart_order.domain.model.CartOrder
import com.niyaj.popos.features.cart_order.domain.util.OrderStatus
import com.niyaj.popos.features.common.ui.theme.SpaceMini
import com.niyaj.popos.features.common.ui.theme.SpaceSmall
import com.niyaj.popos.features.common.ui.theme.TextGray
import com.niyaj.popos.features.common.util.UiEvent
import com.niyaj.popos.features.components.ItemNotAvailable
import com.niyaj.popos.features.components.LoadingIndicator
import com.niyaj.popos.features.components.NoteCard
import com.niyaj.popos.features.components.ScaffoldNavActions
import com.niyaj.popos.features.components.StandardChip
import com.niyaj.popos.features.components.StandardFabButton
import com.niyaj.popos.features.components.StandardScaffold
import com.niyaj.popos.features.components.TextWithCount
import com.niyaj.popos.features.components.TextWithIcon
import com.niyaj.popos.features.components.header
import com.niyaj.popos.features.destinations.AddEditCartOrderScreenDestination
import com.niyaj.popos.features.destinations.CartOrderScreenDestination
import com.niyaj.popos.features.destinations.CartOrderSettingScreenDestination
import com.niyaj.popos.features.destinations.OrderDetailsScreenDestination
import com.niyaj.popos.utils.toBarDate
import com.niyaj.popos.utils.toPrettyDate
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.message
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import com.vanpra.composematerialdialogs.title
import io.sentry.compose.SentryTraced
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.time.Duration.Companion.minutes
import kotlin.time.ExperimentalTime

/**
 *  CartOrderScreen
 *  @param navController
 *  @param cartOrderViewModel
 *  @param scaffoldState
 *  @param resultRecipient
 *  @param settingRecipient
 *  @see CartOrderViewModel
 *  @see CartOrderSettingScreenDestination
 *  @see AddEditCartOrderScreenDestination
 */
@OptIn(ExperimentalComposeUiApi::class, ExperimentalTime::class)
@Destination
@Composable
fun CartOrderScreen(
    navController : NavController,
    cartOrderViewModel : CartOrderViewModel = hiltViewModel(),
    scaffoldState : ScaffoldState,
    resultRecipient : ResultRecipient<AddEditCartOrderScreenDestination, String>,
    settingRecipient : ResultRecipient<CartOrderSettingScreenDestination, String>,
) {
    val lazyListState = rememberLazyGridState()
    val scope = rememberCoroutineScope()
    val showAlert = rememberMaterialDialogState()
    val systemUiController = rememberSystemUiController()

    val cartOrders = cartOrderViewModel.cartOrders.collectAsStateWithLifecycle().value.cartOrders
    val groupedByDate = cartOrders.groupBy { it.createdAt.toPrettyDate() }
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
    val searchText = cartOrderViewModel.searchText.collectAsStateWithLifecycle().value

    val showNoteText = remember {
        mutableStateOf(true)
    }

    LaunchedEffect(key1 = Unit) {
        scope.launch {
            delay(1.minutes)
            showNoteText.value = false
        }
    }

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
            cartOrderViewModel.onEvent(CartOrderEvent.SelectCartOrder(selectedOrder))
        } else {
            navController.navigateUp()
        }
    }

    resultRecipient.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {
                if (selectedOrder.isNotEmpty()) {
                    cartOrderViewModel.onEvent(CartOrderEvent.SelectCartOrder(selectedOrder))
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
                    cartOrderViewModel.onEvent(CartOrderEvent.SelectCartOrder(selectedOrder))
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
                is UiEvent.Success -> {
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = event.successMessage
                    )
                }

                is UiEvent.Error -> {
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

    SentryTraced(tag = CartOrderScreenDestination.route) {
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
                StandardFabButton(
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
                CartOrderScaffoldNavActions(
                    allItemsIsEmpty = cartOrders.isEmpty(),
                    selectedItem = selectedOrder,
                    onClickEdit = {
                        navController.navigate(AddEditCartOrderScreenDestination(cartOrderId = selectedOrder))
                    },
                    onClickDelete = { showAlert.show() },
                    showSearchBar = showSearchBar,
                    searchText = searchText,
                    showMenu = showMenu,
                    onToggleMenu = { showMenu = !showMenu },
                    onDismissDropdown = { showMenu = false },
                    onClickDropItem = {
                        cartOrderViewModel.onEvent(CartOrderEvent.ViewAllOrders)
                        showMenu = false
                    },
                    onSearchTextChanged = {
                        cartOrderViewModel.onEvent(CartOrderEvent.OnSearchCartOrder(it))
                    },
                    onClearClick = {
                        cartOrderViewModel.onSearchTextClearClick()
                    },
                    onClickSearch = {
                        cartOrderViewModel.onEvent(CartOrderEvent.ToggleSearchBar)
                    },
                    onClickSetting = {
                        navController.navigate(CartOrderSettingScreenDestination())
                    },
                    onClickSelectOrder = {
                        cartOrderViewModel.onEvent(CartOrderEvent.SelectCartOrderEvent(selectedOrder))
                    },
                    onClickViewDetails = {
                        cartOrderViewModel.onEvent(CartOrderEvent.SelectCartOrder(selectedOrder))
                        navController.navigate(OrderDetailsScreenDestination(cartOrderId = selectedOrder))
                    }
                )
            },
            navigationIcon = {
                if (selectedOrder.isNotEmpty()) {
                    IconButton(
                        onClick = {
                            cartOrderViewModel.onEvent(
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
                            cartOrderViewModel.onEvent(
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
                    cartOrderViewModel.onEvent(CartOrderEvent.RefreshCartOrder)
                }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                ) {
                    if (isLoading) {
                        LoadingIndicator()
                    } else if (cartOrders.isEmpty() || hasError != null) {
                        ItemNotAvailable(
                            text = hasError
                                ?: if (showSearchBar) stringResource(id = R.string.search_item_not_found)
                                else stringResource(
                                    id = R.string.cart_order_is_empty
                                ),
                            buttonText = stringResource(id = R.string.create_new_order).uppercase(),
                            image = painterResource(R.drawable.emptyorder),
                            onClick = {
                                navController.navigate(AddEditCartOrderScreenDestination())
                            }
                        )
                    } else {
                        AnimatedVisibility(
                            visible = showNoteText.value
                        ) {
                            NoteCard(text = stringResource(id = R.string.cartorder_message))
                        }

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
                                    CartOrdersBox(
                                        cartOrder = cartOrder,
                                        doesOrderSelected = cartOrder.cartOrderId == selectedCartOrder.cartOrderId,
                                        selectedItem = selectedOrder,
                                        onClickItem = {
                                            cartOrderViewModel.onEvent(
                                                CartOrderEvent.SelectCartOrder(
                                                    it
                                                )
                                            )
                                        },
                                        onNavigateItem = {
                                            navController.navigate(
                                                OrderDetailsScreenDestination(
                                                    cartOrderId = it
                                                )
                                            )
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
}


/**
 * [ScaffoldNavActions] for [CartOrderScreen]
 */
@Composable
fun CartOrderScaffoldNavActions(
    allItemsIsEmpty : Boolean,
    selectedItem : String,
    onClickEdit : () -> Unit,
    onClickDelete : () -> Unit,
    showSearchBar : Boolean,
    searchText : String,
    showMenu : Boolean,
    onToggleMenu : () -> Unit,
    onDismissDropdown : () -> Unit,
    onClickDropItem : () -> Unit,
    onSearchTextChanged : (String) -> Unit,
    onClearClick : () -> Unit,
    onClickSearch : () -> Unit,
    onClickSetting : () -> Unit,
    onClickSelectOrder : () -> Unit,
    onClickViewDetails : () -> Unit,
) {
    ScaffoldNavActions(
        multiSelect = false,
        allItemsIsEmpty = allItemsIsEmpty,
        selectedItem = selectedItem,
        selectedItems = listOf(),
        onClickEdit = onClickEdit,
        onClickDelete = onClickDelete,
        onClickSelectAll = {},
        showSearchBar = showSearchBar,
        searchText = searchText,
        onSearchTextChanged = onSearchTextChanged,
        onClearClick = onClearClick,
        onClickSearch = onClickSearch,
        showSettingsIcon = true,
        onClickSetting = onClickSetting,
        content = {
            Box {
                IconButton(
                    onClick = onToggleMenu,
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "View More Settings",
                        tint = MaterialTheme.colors.onPrimary,
                    )
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = onDismissDropdown,
                ) {
                    DropdownMenuItem(
                        onClick = onClickDropItem
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
        },
        preActionContent = {
            IconButton(
                onClick = {
                    onClickSelectOrder()
                }
            ) {
                Icon(
                    imageVector = Icons.Default.TaskAlt,
                    contentDescription = stringResource(id = R.string.select_cart_order),
                    tint = MaterialTheme.colors.onPrimary,
                )
            }
        },
        postActionContent = {
            IconButton(
                onClick = onClickViewDetails,
                enabled = selectedItem.isNotEmpty()
            ) {
                Icon(
                    imageVector = Icons.Default.OpenInNew,
                    contentDescription = "View Details",
                    tint = MaterialTheme.colors.onPrimary,
                )
            }
        }
    )
}


/**
 * [CartOrder] Box
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CartOrdersBox(
    cartOrder : CartOrder,
    selectedItem : String,
    doesOrderSelected : Boolean,
    onClickItem : (String) -> Unit,
    onNavigateItem : (String) -> Unit,
) {
//    val isCurrentDateOrder = DateUtils.isToday(cartOrder.updatedAt?.toLong() ?: cartOrder.createdAt.toLong())
//    val enabled = if (isCurrentDateOrder) true
//        else cartOrder.cartOrderStatus == OrderStatus.Processing.orderStatus
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(SpaceMini)
            .combinedClickable(
                enabled = true,
                onClick = {
                    if (selectedItem.isNotEmpty()) {
                        onClickItem(cartOrder.cartOrderId)
                    } else {
                        onNavigateItem(cartOrder.cartOrderId)
                    }
                },
                onLongClick = {
                    onClickItem(cartOrder.cartOrderId)
                }
            ),
        shape = RoundedCornerShape(4.dp),
        backgroundColor = MaterialTheme.colors.surface,
        border = if (selectedItem == cartOrder.cartOrderId)
            BorderStroke(1.dp, MaterialTheme.colors.primary)
        else if (doesOrderSelected)
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

                if (cartOrder.cartOrderStatus != OrderStatus.Processing.orderStatus) {
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