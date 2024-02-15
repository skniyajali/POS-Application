package com.niyaj.feature.customer

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.FabPosition
import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.niyaj.common.tags.CustomerTestTags
import com.niyaj.common.tags.CustomerTestTags.CREATE_NEW_CUSTOMER
import com.niyaj.common.tags.CustomerTestTags.CUSTOMER_NOT_AVAILABLE
import com.niyaj.common.tags.CustomerTestTags.CUSTOMER_SCREEN_TITLE
import com.niyaj.common.tags.CustomerTestTags.DELETE_CUSTOMER_MESSAGE
import com.niyaj.common.tags.CustomerTestTags.DELETE_CUSTOMER_TITLE
import com.niyaj.common.tags.CustomerTestTags.NO_ITEMS_IN_CUSTOMER
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.feature.customer.components.CustomerData
import com.niyaj.feature.customer.destinations.AddEditCustomerScreenDestination
import com.niyaj.feature.customer.destinations.CustomerDetailsScreenDestination
import com.niyaj.feature.customer.destinations.CustomerSettingsScreenDestination
import com.niyaj.model.Customer
import com.niyaj.ui.components.ItemNotAvailable
import com.niyaj.ui.components.LoadingIndicator
import com.niyaj.ui.components.ScaffoldNavActions
import com.niyaj.ui.components.StandardFAB
import com.niyaj.ui.components.StandardScaffoldNew
import com.niyaj.ui.event.UiEvent
import com.niyaj.ui.event.UiState
import com.niyaj.ui.util.Screens
import com.niyaj.ui.util.isScrolled
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.message
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import com.vanpra.composematerialdialogs.title
import kotlinx.coroutines.launch

/**
 * Customer Screen to show all the customers in a grid view with a search bar,
 * and a fab button to add a new customer to the database,
 * and a contextual action bar to delete the selected customers from the database.
 * This screen also has a swipe to refresh functionality to refresh the list of customers.
 * @param navController: NavController to navigate to other screens in the app.
 * @param scaffoldState: ScaffoldState to show a snack-bar when a customer is deleted.
 * @param resultRecipient: ResultRecipient to receive the result from the AddEditCustomerScreen.
 * @param viewModel: CustomerViewModel to get the list of customers from the database.
 *
 */
@RootNavGraph(start = true)
@Destination(route = Screens.CUSTOMER_SCREEN)
@Composable
fun CustomerScreen(
    navController: NavController,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    viewModel: CustomerViewModel = hiltViewModel(),
    resultRecipient: ResultRecipient<AddEditCustomerScreenDestination, String>,
) {
    val lazyListState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val deleteCustomerState = rememberMaterialDialogState()

    val uiState = viewModel.customers.collectAsStateWithLifecycle().value

    val selectedItems = viewModel.selectedItems.toList()

    val showFab = viewModel.totalItems.isNotEmpty()

    val showSearchBar = viewModel.showSearchBar.collectAsStateWithLifecycle().value
    val searchText = viewModel.searchText.value

    resultRecipient.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {
                if (selectedItems.isNotEmpty()) {
                    viewModel.deselectItems()
                }
            }

            is NavResult.Value -> {
                if (selectedItems.isNotEmpty()) {
                    viewModel.deselectItems()
                }

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

    BackHandler(true) {
        if (showSearchBar) {
            viewModel.closeSearchBar()
        } else if (selectedItems.isNotEmpty()) {
            viewModel.deselectItems()
        } else {
            navController.navigateUp()
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
        title = if (selectedItems.isEmpty()) CUSTOMER_SCREEN_TITLE else "${selectedItems.size} Selected",
        showFab = showFab,
        floatingActionButton = {
            StandardFAB(
                showScrollToTop = lazyListState.isScrolled,
                fabText = CREATE_NEW_CUSTOMER,
                fabVisible = (showFab && selectedItems.isEmpty() && !showSearchBar),
                onFabClick = {
                    navController.navigate(AddEditCustomerScreenDestination())
                },
                onClickScroll = {
                    scope.launch {
                        lazyListState.animateScrollToItem(0)
                    }
                }
            )
        },
        fabPosition = if (lazyListState.isScrolled) FabPosition.End else FabPosition.Center,
        navActions = {
            ScaffoldNavActions(
                placeholderText = CustomerTestTags.CUSTOMER_SEARCH_PLACEHOLDER,
                showSettingsIcon = true,
                selectionCount = selectedItems.size,
                showSearchIcon = showFab,
                showSearchBar = showSearchBar,
                searchText = searchText,
                onEditClick = {
                    navController.navigate(AddEditCustomerScreenDestination(selectedItems.first()))
                },
                onDeleteClick = {
                    deleteCustomerState.show()
                },
                onSettingsClick = {
                    navController.navigate(CustomerSettingsScreenDestination)
                },
                onSelectAllClick = viewModel::selectAllItems,
                onClearClick = viewModel::clearSearchText,
                onSearchClick = viewModel::openSearchBar,
                onSearchTextChanged = viewModel::searchTextChanged
            )
        },
        onDeselect = viewModel::deselectItems
    ) {
        Crossfade(
            targetState = uiState,
            label = "Customer::State"
        ) { state ->
            when (state) {
                is UiState.Loading -> LoadingIndicator()

                is UiState.Empty -> {
                    ItemNotAvailable(
                        text = if (searchText.isEmpty()) CUSTOMER_NOT_AVAILABLE else NO_ITEMS_IN_CUSTOMER,
                        buttonText = CREATE_NEW_CUSTOMER,
                        onClick = {
                            navController.navigate(AddEditCustomerScreenDestination())
                        }
                    )
                }

                is UiState.Success -> {
                    LazyColumn(
                        modifier = Modifier
                            .padding(SpaceSmall),
                        state = lazyListState
                    ) {
                        items(
                            items = state.data,
                            key = { it.customerId }
                        ) { item: Customer ->
                            CustomerData(
                                item = item,
                                doesSelected = {
                                    selectedItems.contains(it)
                                },
                                onClick = {
                                    if (selectedItems.isNotEmpty()) {
                                        viewModel.selectItem(it)
                                    } else {
                                        navController.navigate(CustomerDetailsScreenDestination(it))
                                    }
                                },
                                onLongClick = viewModel::selectItem
                            )
                        }
                    }
                }
            }
        }
    }

    MaterialDialog(
        dialogState = deleteCustomerState,
        buttons = {
            positiveButton(
                text = "Delete",
                onClick = viewModel::deleteItems
            )
            negativeButton(
                text = "Cancel",
                onClick = {
                    deleteCustomerState.hide()
                    viewModel.deselectItems()
                },
            )
        }
    ) {
        title(text = DELETE_CUSTOMER_TITLE)
        message(text = DELETE_CUSTOMER_MESSAGE)
    }
}