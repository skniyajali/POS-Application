package com.niyaj.popos.presentation.customer

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
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
import com.niyaj.popos.domain.util.BottomSheetScreen
import com.niyaj.popos.domain.util.UiEvent
import com.niyaj.popos.presentation.components.ExtendedFabButton
import com.niyaj.popos.presentation.components.ItemNotAvailable
import com.niyaj.popos.presentation.components.StandardScaffold
import com.niyaj.popos.presentation.components.StandardSearchBar
import com.niyaj.popos.destinations.AddEditCustomerScreenDestination
import com.niyaj.popos.presentation.ui.theme.SpaceMini
import com.niyaj.popos.presentation.ui.theme.SpaceSmall
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.message
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import com.vanpra.composematerialdialogs.title
import kotlinx.coroutines.launch

@Destination
@Composable
fun CustomerScreen(
    onOpenSheet: (BottomSheetScreen) -> Unit = {},
    navController: NavController,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    resultRecipient: ResultRecipient<AddEditCustomerScreenDestination, String>,
    customerViewModel: CustomerViewModel = hiltViewModel(),
) {
    val lazyListState = rememberLazyGridState()
    val scope = rememberCoroutineScope()
    val deleteCustomerState = rememberMaterialDialogState()

    val customers = customerViewModel.customers.collectAsState().value.customers
    val isLoading: Boolean = customerViewModel.customers.collectAsState().value.isLoading
    val error = customerViewModel.customers.collectAsState().value.error

    val selectedCustomers = customerViewModel.selectedCustomer.toList()

    // Remember a SystemUiController
    val systemUiController = rememberSystemUiController()

    val transition = updateTransition(selectedCustomers.isNotEmpty(), label = "isContextual")

    val statusBarColor by transition.animateColor(label = "statusBarContextual") { isContextualMode ->
        if(isContextualMode) { MaterialTheme.colors.secondary } else { MaterialTheme.colors.primary }
    }
    val backgroundColor by transition.animateColor(label = "actionBarContextual") { isContextualMode ->
        if(isContextualMode) { MaterialTheme.colors.secondary } else { MaterialTheme.colors.primary }
    }

    val showSearchBar = customerViewModel.toggledSearchBar.collectAsState().value

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
                    customerViewModel.onCustomerEvent(CustomerEvent.DeselectAllCustomer)
                }
            }
            is NavResult.Value -> {
                if (selectedCustomers.isNotEmpty()){
                    customerViewModel.onCustomerEvent(CustomerEvent.DeselectAllCustomer)
                }

                scope.launch {
                    scaffoldState.snackbarHostState.showSnackbar(result.value)
                }
            }
        }
    }

    LaunchedEffect(key1 = true) {
        customerViewModel.eventFlow.collect { event ->
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

                is UiEvent.IsLoading -> {}
            }
        }
    }

    BackHandler(true) {
        if (showSearchBar){
            customerViewModel.onSearchBarCloseAndClearClick()
        } else if(selectedCustomers.isNotEmpty()) {
            customerViewModel.onCustomerEvent(CustomerEvent.DeselectAllCustomer)
        }else{
            navController.navigateUp()
        }
    }

    StandardScaffold(
        navController = navController,
        scaffoldState = scaffoldState,
        showBackArrow = selectedCustomers.isEmpty(),
        onBackButtonClick = {
            if (showSearchBar){
                customerViewModel.onSearchBarCloseAndClearClick()
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
            ExtendedFabButton(
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
            if(selectedCustomers.isNotEmpty()) {
                if(selectedCustomers.size == 1){
                    IconButton(
                        onClick = {
                            navController.navigate(AddEditCustomerScreenDestination(customerId = selectedCustomers.first()))
                        },
                    ){
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Customer",
                            tint = MaterialTheme.colors.onPrimary,
                        )
                    }
                }

                IconButton(
                    onClick = {
                        deleteCustomerState.show()
                    },
                    enabled = selectedCustomers.isNotEmpty()
                ){
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Customer",
                        tint = MaterialTheme.colors.onPrimary,
                    )
                }

                IconButton(
                    onClick = {
                        customerViewModel.onCustomerEvent(CustomerEvent.SelectAllCustomer)
                    },
                    enabled = selectedCustomers.isNotEmpty()
                ){
                    Icon(
                        imageVector = Icons.Default.Rule,
                        contentDescription = "Select All Customers",
                        tint = MaterialTheme.colors.onPrimary,
                    )
                }
            }
            else if(showSearchBar){
                StandardSearchBar(
                    searchText = customerViewModel.searchText.collectAsState().value,
                    placeholderText = "Search for addresses...",
                    onSearchTextChanged = {
                        customerViewModel.onCustomerEvent(CustomerEvent.OnSearchCustomer(it))
                    },
                    onClearClick = {
                        customerViewModel.onSearchTextClearClick()
                    },
                )
            }
            else {
                if (customers.isNotEmpty()){
                    IconButton(
                        onClick = {
                            customerViewModel.onCustomerEvent(CustomerEvent.ToggleSearchBar)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = stringResource(id = R.string.search_icon),
                            tint = MaterialTheme.colors.onPrimary,
                        )
                    }
                    IconButton(
                        onClick = {
                            onOpenSheet(
                                BottomSheetScreen.FilterCustomerScreen(
                                    filterCustomer = customerViewModel.customers.value.filterCustomer,
                                    onFilterChanged = {
                                        customerViewModel.onCustomerEvent(CustomerEvent.OnFilterCustomer(it))
                                    },
                                )
                            )
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Sort,
                            contentDescription = stringResource(id = R.string.filter_product),
                            tint = MaterialTheme.colors.onPrimary,
                        )
                    }
                }
            }
        },
        navigationIcon = {
            if(selectedCustomers.isNotEmpty()) {
                IconButton(
                    onClick = {
                        customerViewModel.onCustomerEvent(CustomerEvent.DeselectAllCustomer)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(id = R.string.filter_product),
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
                        customerViewModel.onCustomerEvent(CustomerEvent.DeleteCustomer(selectedCustomers))
                    }
                )
                negativeButton(
                    text = "Cancel",
                    onClick = {
                        deleteCustomerState.hide()
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
                customerViewModel.onCustomerEvent(CustomerEvent.RefreshCustomer)
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
                        modifier = Modifier.padding(SpaceMini),
                        columns = GridCells.Fixed(2),
                        state = lazyListState,
                    ){
                        itemsIndexed(customers){ _, customer ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(SpaceMini)
                                    .clickable {
                                        customerViewModel.onCustomerEvent(
                                            CustomerEvent.SelectCustomer(customer.customerId)
                                        )
                                    },
                                shape = RoundedCornerShape(4.dp),
                                border = if(selectedCustomers.contains(customer.customerId))
                                    BorderStroke(1.dp, MaterialTheme.colors.primary)
                                else null,
                                elevation = 2.dp,
                            ) {
                                Column(
                                    modifier = Modifier
                                        .padding(SpaceSmall)
                                        .fillMaxWidth(),
                                    verticalArrangement = Arrangement.SpaceBetween,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = customer.customerPhone,
                                        style = MaterialTheme.typography.body1,
                                        textAlign = TextAlign.Center,
                                        fontWeight = FontWeight.Bold,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                    if(!customer.customerName.isNullOrEmpty()){
                                        Spacer(modifier = Modifier.height(SpaceMini))
                                        Text(
                                            text = customer.customerName,
                                            style = MaterialTheme.typography.body1,
                                            textAlign = TextAlign.Center,
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