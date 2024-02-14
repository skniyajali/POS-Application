package com.niyaj.feature.cart_order

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.FabPosition
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ScaffoldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.niyaj.common.tags.CartOrderTestTags.CART_ORDER_NOTES
import com.niyaj.common.tags.CartOrderTestTags.CART_ORDER_NOT_AVAILABLE
import com.niyaj.common.tags.CartOrderTestTags.CART_ORDER_SCREEN_TITLE
import com.niyaj.common.tags.CartOrderTestTags.CREATE_NEW_CART_ORDER
import com.niyaj.common.tags.CartOrderTestTags.DELETE_CART_ORDER_ITEM_MESSAGE
import com.niyaj.common.tags.CartOrderTestTags.DELETE_CART_ORDER_ITEM_TITLE
import com.niyaj.common.utils.Constants.SEARCH_ITEM_NOT_FOUND
import com.niyaj.core.ui.R
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.feature.cart_order.components.CartOrderScaffoldNavActions
import com.niyaj.feature.cart_order.components.CartOrdersBox
import com.niyaj.feature.cart_order.destinations.AddEditCartOrderScreenDestination
import com.niyaj.feature.cart_order.destinations.CartOrderSettingScreenDestination
import com.niyaj.ui.components.ItemNotAvailable
import com.niyaj.ui.components.LoadingIndicator
import com.niyaj.ui.components.NoteCard
import com.niyaj.ui.components.StandardFAB
import com.niyaj.ui.components.StandardScaffoldNew
import com.niyaj.ui.components.TextWithCount
import com.niyaj.ui.components.header
import com.niyaj.ui.event.UiEvent
import com.niyaj.ui.event.UiState
import com.niyaj.ui.util.Screens
import com.niyaj.ui.util.isScrolled
import com.niyaj.ui.util.isScrollingUp
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.message
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import com.vanpra.composematerialdialogs.title
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.minutes

/**
 *  CartOrderScreen
 *  @param navController
 *  @param viewModel
 *  @param scaffoldState
 *  @param resultRecipient
 *  @param settingRecipient
 *  @see CartOrderViewModel
 *  @see CartOrderSettingScreenDestination
 *  @see AddEditCartOrderScreenDestination
 */
@RootNavGraph(start = true)
@Destination(route = Screens.CART_ORDER_SCREEN)
@Composable
fun CartOrderScreen(
    onClickOrderDetails: (String) -> Unit,
    navController: NavController = rememberNavController(),
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    viewModel: CartOrderViewModel = hiltViewModel(),
    resultRecipient: ResultRecipient<AddEditCartOrderScreenDestination, String>,
    settingRecipient: ResultRecipient<CartOrderSettingScreenDestination, String>,
) {
    val lazyGridState = rememberLazyGridState()
    val scope = rememberCoroutineScope()
    val showAlert = rememberMaterialDialogState()

    val uiState = viewModel.cartOrders.collectAsStateWithLifecycle().value
    val selectedCartOrder = viewModel.selectedCartOrder.collectAsStateWithLifecycle().value

    val selectedItems = viewModel.selectedItems.toList()

    val showSearchBar = viewModel.showSearchBar.collectAsStateWithLifecycle().value
    val searchText = viewModel.searchText.value

    val showFab = viewModel.totalItems.isNotEmpty()

    val showNoteText = remember { mutableStateOf(true) }

    LaunchedEffect(key1 = Unit) {
        scope.launch {
            delay(1.minutes)
            showNoteText.value = false
        }
    }

    var showMenu by remember { mutableStateOf(false) }

    BackHandler(true) {
        if (showSearchBar) {
            viewModel.closeSearchBar()
        } else if (selectedItems.isNotEmpty()) {
            viewModel.deselectItems()
        } else {
            navController.navigateUp()
        }
    }

    resultRecipient.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {
                viewModel.deselectItems()
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
                viewModel.deselectItems()
            }

            is NavResult.Value -> {
                scope.launch {
                    scaffoldState.snackbarHostState.showSnackbar(result.value)
                }
            }
        }
    }

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collect { event ->
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
            }
        }
    }

    StandardScaffoldNew(
        navController = navController,
        scaffoldState = scaffoldState,
        selectionCount = selectedItems.size,
        showBackButton = selectedItems.isEmpty(),
        onBackClick = {
            if (showSearchBar) {
                viewModel.closeSearchBar()
            } else {
                navController.navigateUp()
            }
        },
        title = if (selectedItems.isEmpty()) CART_ORDER_SCREEN_TITLE else "${selectedItems.size} Selected",
        showFab = showFab,
        fabPosition = if (lazyGridState.isScrolled) FabPosition.End else FabPosition.Center,
        floatingActionButton = {
            StandardFAB(
                showScrollToTop = lazyGridState.isScrolled,
                fabText = CREATE_NEW_CART_ORDER,
                fabVisible = (showFab && selectedItems.isEmpty() && !showSearchBar),
                onFabClick = {
                    navController.navigate(AddEditCartOrderScreenDestination())
                },
                onClickScroll = {
                    scope.launch {
                        lazyGridState.animateScrollToItem(0)
                    }
                }
            )
        },
        navActions = {
            CartOrderScaffoldNavActions(
                selectionCount = selectedItems.size,
                showSearchIcon = showFab,
                showSearchBar = showSearchBar,
                searchText = searchText,
                showMenu = showMenu,
                onDeleteClick = {
                    showAlert.show()
                },
                onEditClick = {
                    navController.navigate(AddEditCartOrderScreenDestination(selectedItems.first()))
                },
                onToggleMenu = { showMenu = !showMenu },
                onDismissDropdown = { showMenu = false },
                onDropItemClick = {
                    showMenu = false
                    viewModel.onClickViewAllOrder()
                },
                onSearchTextChanged = viewModel::searchTextChanged,
                onClearClick = viewModel::clearSearchText,
                onSearchClick = viewModel::openSearchBar,
                onSelectAllClick = viewModel::selectAllItems,
                onSelectOrderClick = viewModel::selectCartOrder,
                onSettingsClick = {
                    navController.navigate(CartOrderSettingScreenDestination)
                },
            )
        },
        onDeselect = viewModel::deselectItems
    ) {
        Crossfade(
            targetState = uiState,
            label = "Cart Order::State"
        ) { state ->
            when (state) {
                is UiState.Loading -> LoadingIndicator()

                is UiState.Empty -> {
                    ItemNotAvailable(
                        text = if (showSearchBar) SEARCH_ITEM_NOT_FOUND else CART_ORDER_NOT_AVAILABLE,
                        buttonText = CREATE_NEW_CART_ORDER,
                        image = R.drawable.emptyorder,
                        onClick = {
                            navController.navigate(AddEditCartOrderScreenDestination())
                        }
                    )
                }

                is UiState.Success -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(SpaceSmall)
                            .padding(it)
                    ) {
                        AnimatedVisibility(
                            visible = showNoteText.value
                        ) {
                            NoteCard(text = CART_ORDER_NOTES)
                        }

                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            state = lazyGridState,
                        ) {
                            state.data.forEach { (date, orders) ->
                                header {
                                    TextWithCount(
                                        modifier = Modifier
                                            .background(
                                                if (lazyGridState.isScrollingUp())
                                                    MaterialTheme.colors.onPrimary
                                                else Color.Transparent
                                            )
                                            .clip(
                                                RoundedCornerShape(if (lazyGridState.isScrollingUp()) 4.dp else 0.dp)
                                            ),
                                        text = date,
                                        leadingIcon = Icons.Default.CalendarMonth,
                                        count = orders.count(),
                                        onClick = {}
                                    )
                                }

                                items(
                                    items = orders,
                                    key = { it.cartOrderId }
                                ) { cartOrder ->
                                    CartOrdersBox(
                                        item = cartOrder,
                                        orderSelected = {
                                            selectedCartOrder == it
                                        },
                                        doesSelected = {
                                            selectedItems.contains(it)
                                        },
                                        onClickItem = {
                                            if (selectedItems.isNotEmpty()) {
                                                viewModel.selectItem(it)
                                            } else {
                                                onClickOrderDetails(it)
                                            }
                                        },
                                        onLongClickItem = viewModel::selectItem
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    MaterialDialog(
        dialogState = showAlert,
        buttons = {
            positiveButton(
                text = "Delete",
                onClick = {
                    viewModel.deleteItems()
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
        title(text = DELETE_CART_ORDER_ITEM_TITLE)
        message(text = DELETE_CART_ORDER_ITEM_MESSAGE)
    }
}
