package com.niyaj.feature.address

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.FabPosition
import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.niyaj.common.tags.AddressTestTags.ADDRESS_NOT_AVAILABLE
import com.niyaj.common.tags.AddressTestTags.ADDRESS_SCREEN_NOTE_TEXT
import com.niyaj.common.tags.AddressTestTags.ADDRESS_SCREEN_TITLE
import com.niyaj.common.tags.AddressTestTags.ADDRESS_SEARCH_PLACEHOLDER
import com.niyaj.common.tags.AddressTestTags.CREATE_NEW_ADDRESS
import com.niyaj.common.tags.AddressTestTags.DELETE_ADDRESS_ITEM_MESSAGE
import com.niyaj.common.utils.Constants.SEARCH_ITEM_NOT_FOUND
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.feature.address.components.AddressCard
import com.niyaj.feature.address.destinations.AddEditAddressScreenDestination
import com.niyaj.feature.address.destinations.AddressDetailsScreenDestination
import com.niyaj.feature.address.destinations.AddressSettingScreenDestination
import com.niyaj.ui.components.ItemNotAvailable
import com.niyaj.ui.components.LoadingIndicator
import com.niyaj.ui.components.NoteCard
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
 *  Address Screen for viewing and managing addresses
 *  @author Sk Niyaj Ali
 *  @param navController
 *  @param scaffoldState
 *  @param viewModel
 *  @param resultRecipient
 *  @see AddressViewModel
 */
@RootNavGraph(start = true)
@Destination(route = Screens.ADDRESS_SCREEN)
@Composable
fun AddressScreen(
    navController: NavController = rememberNavController(),
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    viewModel: AddressViewModel = hiltViewModel(),
    resultRecipient: ResultRecipient<AddEditAddressScreenDestination, String>,
) {
    val lazyListState = rememberLazyGridState()
    val scope = rememberCoroutineScope()
    val dialogState = rememberMaterialDialogState()

    val uiState = viewModel.addresses.collectAsStateWithLifecycle().value

    val showFab = viewModel.totalItems.isNotEmpty()
    val selectedAddress = viewModel.selectedItems.toList()

    val showSearchBar = viewModel.showSearchBar.collectAsStateWithLifecycle().value
    val searchText = viewModel.searchText.value

    resultRecipient.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {
                // `GoToProfileConfirmationDestination` was shown but it was canceled
                // and no value was set (example: dialog/bottom sheet dismissed)
                if (selectedAddress.isNotEmpty()) {
                    viewModel.deselectItems()
                }
            }

            is NavResult.Value -> {
                if (selectedAddress.isNotEmpty()) {
                    viewModel.deselectItems()
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
        } else if (selectedAddress.isNotEmpty()) {
            viewModel.deselectItems()
        } else {
            navController.navigateUp()
        }
    }

    StandardScaffoldNew(
        navController = navController,
        scaffoldState = scaffoldState,
        title = if (selectedAddress.isEmpty()) ADDRESS_SCREEN_TITLE else "${selectedAddress.size} Selected",
        floatingActionButton = {
            StandardFAB(
                modifier = Modifier.testTag(CREATE_NEW_ADDRESS),
                showScrollToTop = lazyListState.isScrolled,
                fabText = CREATE_NEW_ADDRESS,
                fabVisible = (showFab && selectedAddress.isEmpty() && !showSearchBar),
                onFabClick = {
                    navController.navigate(AddEditAddressScreenDestination())
                },
                onClickScroll = {
                    scope.launch {
                        lazyListState.animateScrollToItem(0)
                    }
                }
            )
        },
        navActions = {
            ScaffoldNavActions(
                placeholderText = ADDRESS_SEARCH_PLACEHOLDER,
                showSettingsIcon = true,
                selectionCount = selectedAddress.size,
                showSearchIcon = showFab,
                showSearchBar = showSearchBar,
                searchText = searchText,
                onEditClick = {
                    navController.navigate(AddEditAddressScreenDestination(selectedAddress.first()))
                },
                onDeleteClick = {
                    dialogState.show()
                },
                onSettingsClick = {
                    navController.navigate(AddressSettingScreenDestination)
                },
                onSelectAllClick = viewModel::selectAllItems,
                onClearClick = viewModel::clearSearchText,
                onSearchClick = viewModel::openSearchBar,
                onSearchTextChanged = viewModel::searchTextChanged
            )
        },
        fabPosition = if (lazyListState.isScrolled) FabPosition.End else FabPosition.Center,
        selectionCount = selectedAddress.size,
        showBackButton = selectedAddress.isEmpty(),
        onDeselect = viewModel::deselectItems,
        onBackClick = {
            if (showSearchBar) viewModel.closeSearchBar() else navController.navigateUp()
        },
    ) {
        Crossfade(
            targetState = uiState,
            label = "Address::State"
        ) { state ->
            when (state) {
                is UiState.Loading -> LoadingIndicator()

                is UiState.Empty -> {
                    ItemNotAvailable(
                        text = if (searchText.isEmpty()) ADDRESS_NOT_AVAILABLE else SEARCH_ITEM_NOT_FOUND,
                        buttonText = CREATE_NEW_ADDRESS,
                        onClick = {
                            navController.navigate(AddEditAddressScreenDestination())
                        }
                    )
                }

                is UiState.Success -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        state = lazyListState,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(SpaceSmall)
                            .padding(it),
                    ) {
                        item(
                            span = { GridItemSpan(2) }
                        ) {
                            NoteCard(
                                text = ADDRESS_SCREEN_NOTE_TEXT,
                                modifier = Modifier
                                    .padding(vertical = SpaceSmall, horizontal = SpaceMini)
                            )
                        }

                        items(
                            items = state.data,
                            key = { address ->
                                address.addressId
                            }
                        ) { address ->
                            AddressCard(
                                address = address,
                                doesSelected = { selectedAddress.contains(it) },
                                onClick = {
                                    if (selectedAddress.isNotEmpty()) {
                                        viewModel.selectItem(it)
                                    } else {
                                        navController.navigate(AddressDetailsScreenDestination(it))
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
        dialogState = dialogState,
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
                    dialogState.hide()
                    viewModel.deselectItems()
                },
            )
        }
    ) {
        title(text = "Delete ${selectedAddress.size} Address?")
        message(text = DELETE_ADDRESS_ITEM_MESSAGE)
    }
}