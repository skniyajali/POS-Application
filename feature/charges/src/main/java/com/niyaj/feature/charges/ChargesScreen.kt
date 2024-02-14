package com.niyaj.feature.charges

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.niyaj.common.tags.ChargesTestTags
import com.niyaj.common.tags.ChargesTestTags.CHARGES_NOT_AVAILABLE
import com.niyaj.common.tags.ChargesTestTags.CHARGES_SCREEN_TITLE
import com.niyaj.common.tags.ChargesTestTags.CREATE_NEW_CHARGES
import com.niyaj.common.tags.ChargesTestTags.DELETE_CHARGES_MESSAGE
import com.niyaj.common.tags.ChargesTestTags.DELETE_CHARGES_TITLE
import com.niyaj.common.tags.ChargesTestTags.NO_ITEMS_IN_CHARGES
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.feature.charges.components.ChargesData
import com.niyaj.feature.charges.destinations.AddEditChargesScreenDestination
import com.niyaj.model.Charges
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
 * Charges Screen
 * @author Sk Niyaj Ali
 * @param navController
 * @param scaffoldState
 * @param viewModel
 * @param resultRecipient
 * @see ChargesViewModel
 */
@RootNavGraph(start = true)
@Destination(route = Screens.CHARGES_SCREEN)
@Composable
fun ChargesScreen(
    navController: NavController,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    viewModel: ChargesViewModel = hiltViewModel(),
    resultRecipient: ResultRecipient<AddEditChargesScreenDestination, String>,
) {
    val scope = rememberCoroutineScope()
    val lazyListState = rememberLazyGridState()
    val deleteChargesState = rememberMaterialDialogState()

    val uiState = viewModel.charges.collectAsStateWithLifecycle().value

    val selectedItems = viewModel.selectedItems.toList()

    val showFab = viewModel.totalItems.isNotEmpty()

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
        title = if (selectedItems.isEmpty()) CHARGES_SCREEN_TITLE else "${selectedItems.size} Selected",
        showFab = showFab,
        floatingActionButton = {
            StandardFAB(
                fabText = CREATE_NEW_CHARGES.uppercase(),
                showScrollToTop = lazyListState.isScrolled,
                fabVisible = showFab && selectedItems.isEmpty() && !showSearchBar,
                onClickScroll = {
                    scope.launch {
                        lazyListState.animateScrollToItem(index = 0)
                    }
                },
                onFabClick = {
                    navController.navigate(AddEditChargesScreenDestination())
                },
            )
        },
        fabPosition = if (lazyListState.isScrolled) FabPosition.End else FabPosition.Center,
        navActions = {
            ScaffoldNavActions(
                placeholderText = ChargesTestTags.CHARGES_SEARCH_PLACEHOLDER,
                selectionCount = selectedItems.size,
                showSearchIcon = showFab,
                showSearchBar = showSearchBar,
                searchText = searchText,
                onEditClick = {
                    navController.navigate(AddEditChargesScreenDestination(selectedItems.first()))
                },
                onDeleteClick = {
                    deleteChargesState.show()
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
            label = "Charges::State"
        ) { state ->
            when (state) {
                is UiState.Loading -> LoadingIndicator()

                is UiState.Empty -> {
                    ItemNotAvailable(
                        text = if (showSearchBar) NO_ITEMS_IN_CHARGES else CHARGES_NOT_AVAILABLE,
                        buttonText = CREATE_NEW_CHARGES.uppercase(),
                        onClick = {
                            navController.navigate(AddEditChargesScreenDestination())
                        }
                    )
                }

                is UiState.Success -> {
                    LazyVerticalGrid(
                        modifier = Modifier
                            .padding(SpaceSmall),
                        columns = GridCells.Fixed(2),
                        state = lazyListState,
                    ) {
                        items(
                            items = state.data,
                            key = { it.chargesId }
                        ) { item: Charges ->
                            ChargesData(
                                item = item,
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
        dialogState = deleteChargesState,
        buttons = {
            positiveButton(
                text = "Delete",
                onClick = viewModel::deleteItems
            )
            negativeButton(
                text = "Cancel",
                onClick = {
                    deleteChargesState.hide()
                    viewModel.deselectItems()
                },
            )
        }
    ) {
        title(text = DELETE_CHARGES_TITLE)
        message(text = DELETE_CHARGES_MESSAGE)
    }

}