package com.niyaj.feature.addonitem

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
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
import com.niyaj.common.tags.AddOnConstants.ADDON_NOT_AVAILABLE
import com.niyaj.common.tags.AddOnConstants.ADDON_SCREEN_TITLE
import com.niyaj.common.tags.AddOnConstants.ADDON_SEARCH_PLACEHOLDER
import com.niyaj.common.tags.AddOnConstants.CREATE_NEW_ADD_ON
import com.niyaj.common.tags.AddOnConstants.DELETE_ADD_ON_ITEM_MESSAGE
import com.niyaj.common.tags.AddOnConstants.NO_ITEMS_IN_ADDON
import com.niyaj.common.utils.Constants.SEARCH_ITEM_NOT_FOUND
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.feature.addonitem.components.AddOnItemData
import com.niyaj.feature.addonitem.destinations.AddEditAddOnItemScreenDestination
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
 * AddOnItem Screen
 * @author Sk Niyaj Ali
 *
 */
@RootNavGraph(start = true)
@Destination(route = Screens.ADDON_ITEM_SCREEN)
@Composable
fun AddOnItemScreen(
    navController: NavController = rememberNavController(),
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    viewModel: AddOnItemViewModel = hiltViewModel(),
    resultRecipient: ResultRecipient<AddEditAddOnItemScreenDestination, String>,
) {
    val scope = rememberCoroutineScope()
    val lazyGridState = rememberLazyGridState()
    val dialogState = rememberMaterialDialogState()

    val uiState = viewModel.addOnItems.collectAsStateWithLifecycle().value

    val showFab = viewModel.totalItems.isNotEmpty()
    val selectedItems = viewModel.selectedItems.toList()

    val showSearchBar = viewModel.showSearchBar.collectAsStateWithLifecycle().value
    val searchText = viewModel.searchText.value

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

    BackHandler {
        if (selectedItems.isNotEmpty()) {
            viewModel.deselectItems()
        } else if (showSearchBar) {
            viewModel.closeSearchBar()
        }else {
            navController.navigateUp()
        }
    }

    resultRecipient.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {
                // `GoToProfileConfirmationDestination` was shown but it was canceled
                // and no value was set (example: dialog/bottom sheet dismissed)
                viewModel.deselectItems()
            }

            is NavResult.Value -> {
                viewModel.deselectItems()

                scope.launch {
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = result.value
                    )
                }
            }
        }
    }

    StandardScaffoldNew(
        navController = navController,
        scaffoldState = scaffoldState,
        title = if (selectedItems.isEmpty()) ADDON_SCREEN_TITLE else "${selectedItems.size} Selected",
        floatingActionButton = {
            StandardFAB(
                modifier = Modifier.testTag(CREATE_NEW_ADD_ON),
                showScrollToTop = lazyGridState.isScrolled,
                fabText = CREATE_NEW_ADD_ON,
                fabVisible = (showFab && selectedItems.isEmpty() && !showSearchBar),
                onFabClick = {
                    navController.navigate(AddEditAddOnItemScreenDestination())
                },
                onClickScroll = {
                    scope.launch {
                        lazyGridState.animateScrollToItem(0)
                    }
                }
            )
        },
        navActions = {
            ScaffoldNavActions(
                placeholderText = ADDON_SEARCH_PLACEHOLDER,
                selectionCount = selectedItems.size,
                showSearchIcon = showFab,
                showSearchBar = showSearchBar,
                searchText = searchText,
                onEditClick = {
                    navController.navigate(AddEditAddOnItemScreenDestination(selectedItems.first()))
                },
                onDeleteClick = {
                    dialogState.show()
                },
                onSelectAllClick = viewModel::selectAllItems,
                onClearClick = viewModel::clearSearchText,
                onSearchClick = viewModel::openSearchBar,
                onSearchTextChanged = viewModel::searchTextChanged
            )
        },
        fabPosition = if (lazyGridState.isScrolled) FabPosition.End else FabPosition.Center,
        selectionCount = selectedItems.size,
        showBackButton = selectedItems.isEmpty(),
        onDeselect = viewModel::deselectItems,
        onBackClick = {
            if (showSearchBar) viewModel.closeSearchBar() else navController.navigateUp()
        },
    ) {
        Crossfade(
            targetState = uiState,
            label = "AddOn::State"
        ) { state ->
            when (state) {
                is UiState.Loading -> LoadingIndicator()

                is UiState.Empty -> {
                    ItemNotAvailable(
                        modifier = Modifier.testTag(ADDON_NOT_AVAILABLE),
                        btnModifier = Modifier.testTag(CREATE_NEW_ADD_ON),
                        text = if (showSearchBar) SEARCH_ITEM_NOT_FOUND else NO_ITEMS_IN_ADDON,
                        buttonText = CREATE_NEW_ADD_ON.uppercase(),
                        onClick = {
                            navController.navigate(AddEditAddOnItemScreenDestination())
                        }
                    )
                }

                is UiState.Success -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        state = lazyGridState,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(SpaceSmall)
                    ) {
                        items(
                            items = state.data,
                            key = { it.addOnItemId }
                        ) { addOnItem ->
                            AddOnItemData(
                                item = addOnItem,
                                doesSelected = {
                                    selectedItems.contains(it)
                                },
                                onClick = {
                                    if (selectedItems.isNotEmpty()) {
                                        viewModel.selectItem(it)
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
                onClick = viewModel::deleteItems
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
        title(text = "Delete ${selectedItems.size} AddOn Item?")
        message(DELETE_ADD_ON_ITEM_MESSAGE)
    }
}
