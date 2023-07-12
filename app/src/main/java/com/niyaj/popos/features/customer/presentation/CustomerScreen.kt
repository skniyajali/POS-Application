package com.niyaj.popos.features.customer.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.FabPosition
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.niyaj.popos.R
import com.niyaj.popos.features.common.ui.theme.SpaceSmall
import com.niyaj.popos.features.common.util.UiEvent
import com.niyaj.popos.features.components.ItemNotAvailable
import com.niyaj.popos.features.components.ScaffoldNavActions
import com.niyaj.popos.features.components.StandardFabButton
import com.niyaj.popos.features.components.StandardScaffold
import com.niyaj.popos.features.customer.presentation.components.CustomerCard
import com.niyaj.popos.features.destinations.AddEditCustomerScreenDestination
import com.niyaj.popos.features.destinations.CustomerDetailsScreenDestination
import com.niyaj.popos.features.destinations.CustomerSettingsScreenDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.message
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import com.vanpra.composematerialdialogs.title
import io.sentry.compose.SentryTraced
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
@OptIn(ExperimentalComposeUiApi::class)
@Destination
@Composable
fun CustomerScreen(
    navController: NavController,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    resultRecipient: ResultRecipient<AddEditCustomerScreenDestination, String>,
    viewModel: CustomerViewModel = hiltViewModel(),
) {
    val lazyListState = rememberLazyGridState()
    val scope = rememberCoroutineScope()
    val deleteCustomerState = rememberMaterialDialogState()

    val customers = viewModel.customers.collectAsStateWithLifecycle().value.customers
    val isLoading: Boolean = viewModel.customers.collectAsStateWithLifecycle().value.isLoading
    val error = viewModel.customers.collectAsStateWithLifecycle().value.error

    val selectedCustomers = viewModel.selectedCustomer.toList()

    // Remember a SystemUiController
    val systemUiController = rememberSystemUiController()

    val transition = updateTransition(selectedCustomers.isNotEmpty(), label = "isContextual")

    val statusBarColor by transition.animateColor(label = "statusBar") { isContextualMode ->
        if(isContextualMode) { MaterialTheme.colors.secondary } else { MaterialTheme.colors.primary }
    }
    val backgroundColor by transition.animateColor(label = "actionBar") { isContextualMode ->
        if(isContextualMode) { MaterialTheme.colors.secondary } else { MaterialTheme.colors.primary }
    }

    val showSearchBar = viewModel.toggledSearchBar.collectAsStateWithLifecycle().value
    val searchText = viewModel.searchText.collectAsStateWithLifecycle().value

    SideEffect {
        systemUiController.setStatusBarColor(
            color = statusBarColor,
            darkIcons = false
        )
    }

    val showScrollToTop = remember {
        derivedStateOf {
            lazyListState.firstVisibleItemIndex > 0
        }
    }

    resultRecipient.onNavResult { result ->
        when(result) {
            is NavResult.Canceled -> {
                if (selectedCustomers.isNotEmpty()){
                    viewModel.onCustomerEvent(CustomerEvent.DeselectAllCustomer)
                }
            }
            is NavResult.Value -> {
                if (selectedCustomers.isNotEmpty()){
                    viewModel.onCustomerEvent(CustomerEvent.DeselectAllCustomer)
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

                is UiEvent.IsLoading -> {}
            }
        }
    }

    BackHandler(true) {
        if (showSearchBar){
            viewModel.onSearchBarCloseAndClearClick()
        } else if(selectedCustomers.isNotEmpty()) {
            viewModel.onCustomerEvent(CustomerEvent.DeselectAllCustomer)
        }else{
            navController.navigateUp()
        }
    }

    SentryTraced(tag = "CustomerScreen") {
        StandardScaffold(
            navController = navController,
            scaffoldState = scaffoldState,
            showBackArrow = selectedCustomers.isEmpty(),
            onBackButtonClick = {
                if (showSearchBar){
                    viewModel.onSearchBarCloseAndClearClick()
                }else{
                    navController.navigateUp()
                }
            },
            title = {
                if (selectedCustomers.isEmpty()) {
                    Text(text = "Customers")
                } else if(selectedCustomers.size > 1){
                    Text(text = "${selectedCustomers.size} Selected")
                }
            },
            isFloatingActionButtonDocked = customers.isNotEmpty(),
            floatingActionButton = {
                StandardFabButton(
                    text = stringResource(id = R.string.create_customer).uppercase(),
                    showScrollToTop = showScrollToTop.value,
                    visible = customers.isNotEmpty() && selectedCustomers.isEmpty() && !showSearchBar,
                    onScrollToTopClick = {
                        scope.launch {
                            lazyListState.animateScrollToItem(index = 0)
                        }
                    },
                    onClick = {
                        navController.navigate(AddEditCustomerScreenDestination())
                    },
                )
            },
            floatingActionButtonPosition = if(showScrollToTop.value) FabPosition.End else FabPosition.Center,
            navActions = {
                ScaffoldNavActions(
                    multiSelect = true,
                    allItemsIsEmpty = customers.isEmpty(),
                    selectedItems = selectedCustomers,
                    onClickEdit = {
                        navController.navigate(AddEditCustomerScreenDestination(selectedCustomers.first()))
                    },
                    onClickDelete = {
                        deleteCustomerState.show()
                    },
                    onClickSelectAll = {
                        viewModel.onCustomerEvent(CustomerEvent.SelectAllCustomer)
                    },
                    showSearchBar = showSearchBar,
                    searchText = searchText,
                    onSearchTextChanged = {
                        viewModel.onCustomerEvent(CustomerEvent.OnSearchCustomer(it))
                    },
                    onClearClick = {
                        viewModel.onSearchTextClearClick()
                    },
                    onClickSearch = {
                        viewModel.onCustomerEvent(CustomerEvent.ToggleSearchBar)
                    },
                    showSettingsIcon = true,
                    onClickSetting = {
                        navController.navigate(CustomerSettingsScreenDestination)
                    }
                )
            },
            navigationIcon = {
                if(selectedCustomers.isNotEmpty()) {
                    IconButton(
                        onClick = {
                            viewModel.onCustomerEvent(CustomerEvent.DeselectAllCustomer)
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
                dialogState = deleteCustomerState,
                buttons = {
                    positiveButton(
                        text = "Delete",
                        onClick = {
                            viewModel.onCustomerEvent(
                                CustomerEvent.DeleteCustomer(
                                    selectedCustomers
                                )
                            )
                        }
                    )
                    negativeButton(
                        text = "Cancel",
                        onClick = {
                            deleteCustomerState.hide()
                            viewModel.onCustomerEvent(CustomerEvent.DeselectAllCustomer)
                        },
                    )
                }
            ) {
                title(text = "Delete ${selectedCustomers.size} Customer?")
                message(res = R.string.delete_customer_message)
            }

            SwipeRefresh(
                state = rememberSwipeRefreshState(isRefreshing = isLoading),
                onRefresh = {
                    viewModel.onCustomerEvent(CustomerEvent.RefreshCustomer)
                }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(SpaceSmall),
                ) {
                    if(isLoading){
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ){
                            CircularProgressIndicator()
                        }
                    } else if (customers.isEmpty() || error != null) {
                        ItemNotAvailable(
                            text = error ?: if(showSearchBar) stringResource(id = R.string.search_item_not_found) else stringResource(id = R.string.no_items_in_customers),
                            buttonText = stringResource(id = R.string.create_customer).uppercase(),
                            onClick = {
                                navController.navigate(AddEditCustomerScreenDestination())
                            }
                        )
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            state = lazyListState,
                        ){
                            items(
                                items = customers,
                                key = {
                                    it.customerId
                                }
                            ){customer ->
                                CustomerCard(
                                    customer = customer,
                                    doesSelected = {
                                        selectedCustomers.contains(it)
                                    },
                                    doesAnySelected = selectedCustomers.isNotEmpty(),
                                    onSelectContact = {
                                        viewModel.onCustomerEvent(CustomerEvent.SelectCustomer(it))
                                    },
                                    onClickViewDetails = {
                                        navController.navigate(CustomerDetailsScreenDestination(it))
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