package com.niyaj.popos.realm.address.presentation

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
import com.niyaj.popos.destinations.AddEditAddressScreenDestination
import com.niyaj.popos.domain.util.BottomSheetScreen
import com.niyaj.popos.domain.util.UiEvent
import com.niyaj.popos.presentation.components.ExtendedFabButton
import com.niyaj.popos.presentation.components.ItemNotAvailable
import com.niyaj.popos.presentation.components.StandardScaffold
import com.niyaj.popos.presentation.components.StandardSearchBar
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
import timber.log.Timber

@OptIn(ExperimentalMaterialApi::class)
@Destination
@Composable
fun AddressScreen(
    navController: NavController,
    onOpenSheet: (BottomSheetScreen) -> Unit = {},
    scaffoldState: ScaffoldState,
    resultRecipient: ResultRecipient<AddEditAddressScreenDestination, String>,
    addressViewModel: AddressViewModel = hiltViewModel(),
) {
    val lazyListState = rememberLazyGridState()
    val scope = rememberCoroutineScope()
    val deleteAddressState = rememberMaterialDialogState()

    val addresses = addressViewModel.addresses.collectAsState().value.addresses
    val filterAddress = addressViewModel.addresses.collectAsState().value.filterAddress
    val hasError = addressViewModel.addresses.collectAsState().value.error
    val isLoading = addressViewModel.addresses.collectAsState().value.isLoading

    val selectedAddress = addressViewModel.selectedAddresses.toList()

    // Remember a SystemUiController
    val systemUiController = rememberSystemUiController()

    val transition = updateTransition(selectedAddress.isNotEmpty(), label = "isContextual")

    val statusBarColor by transition.animateColor(label = "statusBarContextual") { isContextualMode ->
        if(isContextualMode) { MaterialTheme.colors.secondary } else { MaterialTheme.colors.primary }
    }
    val backgroundColor by transition.animateColor(label = "actionBarContextual") { isContextualMode ->
        if(isContextualMode) { MaterialTheme.colors.secondary } else { MaterialTheme.colors.primary }
    }

    val showSearchBar = addressViewModel.toggledSearchBar.collectAsState().value

    val showScrollToTop = remember {
        derivedStateOf {
            lazyListState.firstVisibleItemIndex > 0
        }
    }

    resultRecipient.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {
                // `GoToProfileConfirmationDestination` was shown but it was canceled
                // and no value was set (example: dialog/bottom sheet dismissed)
                if(selectedAddress.isNotEmpty()){
                    addressViewModel.onAddressEvent(AddressEvent.DeselectAddress)
                }
            }
            is NavResult.Value -> {
                if(selectedAddress.isNotEmpty()){
                    addressViewModel.onAddressEvent(AddressEvent.DeselectAddress)
                }

                scope.launch {
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = result.value
                    )
                }
            }
        }
    }

    LaunchedEffect(key1 = true) {
        addressViewModel.eventFlow.collect { event ->
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

    SideEffect {
        systemUiController.setStatusBarColor(
            color = statusBarColor,
            darkIcons = false
        )
    }


    BackHandler(true) {
        if (showSearchBar){
            addressViewModel.onSearchBarCloseAndClearClick()
        } else if(selectedAddress.isNotEmpty()) {
            addressViewModel.onAddressEvent(AddressEvent.DeselectAddress)
        }else{
            navController.navigateUp()
        }
    }

    StandardScaffold(
        navController = navController,
        scaffoldState = scaffoldState,
        showBackArrow = selectedAddress.isEmpty(),
        onBackButtonClick = {
            if (showSearchBar){
                addressViewModel.onSearchBarCloseAndClearClick()
            }else{
                navController.navigateUp()
            }
        },
        title = {
            if (selectedAddress.isEmpty()) {
                Text(text = "Addresses")
            } else if(selectedAddress.size > 1){
                Text(text = "${selectedAddress.size} Selected")
            }
        },
        isFloatingActionButtonDocked = addresses.isNotEmpty(),
        floatingActionButton = {
            ExtendedFabButton(
                text = stringResource(id = R.string.create_address).uppercase(),
                showScrollToTop = showScrollToTop.value,
                visible = addresses.isNotEmpty() && selectedAddress.isEmpty() && !showSearchBar,
                onScrollToTopClick = {
                    scope.launch {
                        lazyListState.animateScrollToItem(index = 0)
                    }
                },
                onClick = {
                    navController.navigate(AddEditAddressScreenDestination())
                },
            )
        },
        floatingActionButtonPosition = if(showScrollToTop.value) FabPosition.End else FabPosition.Center,
        navActions = {
            if(selectedAddress.isNotEmpty()) {
                if(selectedAddress.size == 1){
                    IconButton(
                        onClick = {
                            navController.navigate(AddEditAddressScreenDestination(addressId = selectedAddress.first()))
                        },
                    ){
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Address",
                            tint = MaterialTheme.colors.onPrimary,
                        )
                    }
                }

                IconButton(
                    onClick = {
                        deleteAddressState.show()
                    },
                    enabled = selectedAddress.isNotEmpty()
                ){
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Address",
                        tint = MaterialTheme.colors.onPrimary,
                    )
                }

                IconButton(
                    onClick = {
                        addressViewModel.onAddressEvent(AddressEvent.SelectAllAddress)
                    },
                    enabled = selectedAddress.isNotEmpty()
                ){
                    Icon(
                        imageVector = Icons.Default.Rule,
                        contentDescription = "Select All Address",
                        tint = MaterialTheme.colors.onPrimary,
                    )
                }
            }
            else if(showSearchBar){
                StandardSearchBar(
                    searchText = addressViewModel.searchText.collectAsState().value,
                    placeholderText = "Search for addresses...",
                    onSearchTextChanged = {
                        addressViewModel.onAddressEvent(AddressEvent.OnSearchAddress(it))
                    },
                    onClearClick = {
                        addressViewModel.onSearchTextClearClick()
                    },
                )
            }
            else {
                if (addresses.isNotEmpty()){
                    IconButton(
                        onClick = {
                            addressViewModel.onAddressEvent(AddressEvent.ToggleSearchBar)
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
                                BottomSheetScreen.FilterAddressScreen(
                                    filterAddress = filterAddress,
                                    onFilterChanged = {
                                        addressViewModel.onAddressEvent(
                                            AddressEvent.OnFilterAddress(
                                                it
                                            )
                                        )
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
            if(selectedAddress.isNotEmpty()) {
                IconButton(
                    onClick = {
                        addressViewModel.onAddressEvent(AddressEvent.DeselectAddress)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(id = R.string.filter_address),
                        tint = MaterialTheme.colors.onPrimary,
                    )
                }
            }
        },
        topAppBarBackgroundColor = backgroundColor,
    ) {
        MaterialDialog(
            dialogState = deleteAddressState,
            buttons = {
                positiveButton(
                    text = "Delete",
                    onClick = {
                        addressViewModel.onAddressEvent(AddressEvent.DeleteAddress(selectedAddress))
                    }
                )
                negativeButton(
                    text = "Cancel",
                    onClick = {
                        deleteAddressState.hide()
                    },
                )
            }
        ) {
            title(text = "Delete ${selectedAddress.size} Address?")
            message(res = R.string.delete_address_message)
        }

        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing = isLoading),
            onRefresh = {
                addressViewModel.onAddressEvent(AddressEvent.RefreshAddress)
            }
        ) {
            if(isLoading){
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ){
                    CircularProgressIndicator()
                }
            } else if (addresses.isEmpty() || hasError != null) {
                ItemNotAvailable(
                    text = hasError ?: if(showSearchBar) stringResource(id = R.string.search_item_not_found) else stringResource(id = R.string.no_items_in_address),
                    buttonText = stringResource(id = R.string.create_address).uppercase(),
                    onClick = {
                        navController.navigate(AddEditAddressScreenDestination())
                    }
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    state = lazyListState,
                    modifier = Modifier.fillMaxSize()
                ){
                    itemsIndexed(addresses){ _, address ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(SpaceSmall)
                                .clickable {
                                    addressViewModel.onAddressEvent(
                                        AddressEvent.SelectAddress(address.addressId)
                                    )
                                },
                            shape = RoundedCornerShape(4.dp),
                            border = if(selectedAddress.contains(address.addressId))
                                BorderStroke(1.dp, MaterialTheme.colors.primary)
                            else null,
                            elevation = 2.dp,
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(SpaceSmall)
                                    .fillMaxWidth(),
                                verticalArrangement = Arrangement.SpaceBetween,
                                horizontalAlignment = Alignment.Start
                            ) {
                                Text(
                                    text = address.addressName,
                                    style = MaterialTheme.typography.body1,
                                    textAlign = TextAlign.Center,
                                    fontWeight = FontWeight.SemiBold,
                                    overflow = TextOverflow.Ellipsis,
                                )
                                Spacer(modifier = Modifier.height(SpaceMini))
                                Text(
                                    text = address.shortName,
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