package com.niyaj.popos.features.address.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.FabPosition
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Rule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.niyaj.popos.R
import com.niyaj.popos.features.address.domain.util.AddressTestTags.ADDRESS_SEARCH_BAR
import com.niyaj.popos.features.common.ui.theme.SpaceSmall
import com.niyaj.popos.features.common.util.BottomSheetScreen
import com.niyaj.popos.features.common.util.UiEvent
import com.niyaj.popos.features.components.ExtendedFabButton
import com.niyaj.popos.features.components.FlexRowBox
import com.niyaj.popos.features.components.ItemNotAvailable
import com.niyaj.popos.features.components.StandardScaffold
import com.niyaj.popos.features.components.StandardSearchBar
import com.niyaj.popos.features.destinations.AddEditAddressScreenDestination
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

@OptIn(ExperimentalLifecycleComposeApi::class)
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

    val addresses = addressViewModel.addresses.collectAsStateWithLifecycle().value.addresses
    val filterAddress = addressViewModel.addresses.collectAsStateWithLifecycle().value.filterAddress
    val hasError = addressViewModel.addresses.collectAsStateWithLifecycle().value.error
    val isLoading = addressViewModel.addresses.collectAsStateWithLifecycle().value.isLoading

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

    val showSearchBar = addressViewModel.toggledSearchBar.collectAsStateWithLifecycle().value

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
                    modifier = Modifier.testTag(ADDRESS_SEARCH_BAR),
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
                            contentDescription = stringResource(id = R.string.filter_address),
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
                        contentDescription = "Deselect address",
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
                        addressViewModel.onAddressEvent(AddressEvent.DeselectAddress)
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
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(SpaceSmall),
                ){
                    itemsIndexed(addresses){ _, address ->
                        FlexRowBox(
                            modifier = Modifier.testTag(address.addressName),
                            title = address.addressName,
                            secondaryText = address.shortName,
                            icon = Icons.Default.Business,
                            doesSelected = selectedAddress.contains(address.addressId),
                            onClick = {
                                addressViewModel.onAddressEvent(
                                    AddressEvent.SelectAddress(address.addressId)
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}