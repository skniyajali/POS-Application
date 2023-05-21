package com.niyaj.popos.features.address.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
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
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.niyaj.popos.R
import com.niyaj.popos.features.address.presentation.components.AddressCard
import com.niyaj.popos.features.common.ui.theme.SpaceSmall
import com.niyaj.popos.features.common.util.UiEvent
import com.niyaj.popos.features.components.ItemNotAvailable
import com.niyaj.popos.features.components.LoadingIndicator
import com.niyaj.popos.features.components.ScaffoldNavActions
import com.niyaj.popos.features.components.StandardFabButton
import com.niyaj.popos.features.components.StandardScaffold
import com.niyaj.popos.features.destinations.AddEditAddressScreenDestination
import com.niyaj.popos.features.destinations.AddressDetailsScreenDestination
import com.niyaj.popos.features.destinations.AddressScreenDestination
import com.niyaj.popos.features.destinations.AddressSettingScreenDestination
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
import timber.log.Timber

/**
 *  Address Screen for viewing and managing addresses
 *  @author Sk Niyaj Ali
 *  @param navController
 *  @param scaffoldState
 *  @param viewModel
 *  @param resultRecipient
 *  @see AddressViewModel
 */
@OptIn(ExperimentalComposeUiApi::class)
@Destination
@Composable
fun AddressScreen(
    navController: NavController = rememberNavController(),
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    viewModel: AddressViewModel = hiltViewModel(),
    resultRecipient: ResultRecipient<AddEditAddressScreenDestination, String>,
) {
    val lazyListState = rememberLazyGridState()
    val scope = rememberCoroutineScope()
    val deleteAddressState = rememberMaterialDialogState()

    val addresses = viewModel.addresses.collectAsStateWithLifecycle().value.addresses
    val hasError = viewModel.addresses.collectAsStateWithLifecycle().value.error
    val isLoading = viewModel.addresses.collectAsStateWithLifecycle().value.isLoading

    val selectedAddress = viewModel.selectedAddresses.toList()

    // Remember a SystemUiController
    val systemUiController = rememberSystemUiController()

    val transition = updateTransition(selectedAddress.isNotEmpty(), label = "isContextual")

    val statusBarColor by transition.animateColor(label = "statusBarContextual") { isContextualMode ->
        if(isContextualMode) { MaterialTheme.colors.secondary } else { MaterialTheme.colors.primary }
    }
    val backgroundColor by transition.animateColor(label = "actionBarContextual") { isContextualMode ->
        if(isContextualMode) { MaterialTheme.colors.secondary } else { MaterialTheme.colors.primary }
    }

    val showSearchBar = viewModel.toggledSearchBar.collectAsStateWithLifecycle().value
    val searchText = viewModel.searchText.collectAsStateWithLifecycle().value

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
                    viewModel.onAddressEvent(AddressEvent.DeselectAddress)
                }
            }
            is NavResult.Value -> {
                if(selectedAddress.isNotEmpty()){
                    viewModel.onAddressEvent(AddressEvent.DeselectAddress)
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
        viewModel.eventFlow.collect { event ->
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
            viewModel.onSearchBarCloseAndClearClick()
        } else if(selectedAddress.isNotEmpty()) {
            viewModel.onAddressEvent(AddressEvent.DeselectAddress)
        }else{
            navController.navigateUp()
        }
    }

    SentryTraced(tag = AddressScreenDestination.route) {
        StandardScaffold(
            navController = navController,
            scaffoldState = scaffoldState,
            showBackArrow = selectedAddress.isEmpty(),
            onBackButtonClick = {
                if (showSearchBar){
                    viewModel.onSearchBarCloseAndClearClick()
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
                StandardFabButton(
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
                ScaffoldNavActions(
                    multiSelect = true,
                    allItemsIsEmpty = addresses.isEmpty(),
                    selectedItems = selectedAddress,
                    onClickEdit = {
                        navController.navigate(AddEditAddressScreenDestination(addressId = selectedAddress.first()))
                    },
                    onClickDelete = {
                        deleteAddressState.show()
                    },
                    onClickSelectAll = {
                        viewModel.onAddressEvent(AddressEvent.SelectAllAddress)
                    },
                    showSearchBar = showSearchBar,
                    searchText = searchText,
                    onSearchTextChanged = {
                        viewModel.onAddressEvent(AddressEvent.OnSearchAddress(it))
                    },
                    onClearClick = {
                        viewModel.onSearchTextClearClick()
                    },
                    onClickSearch = {
                        viewModel.onAddressEvent(AddressEvent.ToggleSearchBar)
                    },
                    showSettingsIcon = true,
                    onClickSetting = {
                        navController.navigate(AddressSettingScreenDestination)
                    }
                )
            },
            navigationIcon = {
                if(selectedAddress.isNotEmpty()) {
                    IconButton(
                        onClick = {
                            viewModel.onAddressEvent(AddressEvent.DeselectAddress)
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
                            viewModel.onAddressEvent(AddressEvent.DeleteAddress(selectedAddress))
                        }
                    )
                    negativeButton(
                        text = "Cancel",
                        onClick = {
                            deleteAddressState.hide()
                            viewModel.onAddressEvent(AddressEvent.DeselectAddress)
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
                    viewModel.onAddressEvent(AddressEvent.RefreshAddress)
                }
            ) {
                if(isLoading){
                    LoadingIndicator()
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
                        items(
                            items = addresses,
                            key = { address ->
                                address.addressId
                            }
                        ){ address ->
                            AddressCard(
                                address = address,
                                doesSelected = { selectedAddress.contains(it) },
                                doesAnySelected = true,
                                onSelectAddress = {
                                    viewModel.onAddressEvent(AddressEvent.SelectAddress(it))
                                },
                                onClickAddress = {
                                    navController.navigate(AddressDetailsScreenDestination(it))
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}