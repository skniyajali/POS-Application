package com.niyaj.popos.realm.addon_item.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.FabPosition
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
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
import com.niyaj.popos.destinations.AddEditAddOnItemScreenDestination
import com.niyaj.popos.presentation.ui.theme.SpaceMini
import com.niyaj.popos.presentation.ui.theme.SpaceSmall
import com.niyaj.popos.util.toRupee
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

@Destination
@Composable
fun AddOnItemScreen(
    onOpenSheet: (BottomSheetScreen) -> Unit = {},
    navController: NavController = rememberNavController(),
    scaffoldState: ScaffoldState,
    addOnItemViewModel: AddOnItemViewModel = hiltViewModel(),
    resultRecipient: ResultRecipient<AddEditAddOnItemScreenDestination, String>,
) {
    val lazyGridState = rememberLazyGridState()
    val deleteAddOnItemState = rememberMaterialDialogState()
    val scope = rememberCoroutineScope()

    val addOnItems = addOnItemViewModel.state.collectAsState().value.addOnItems
    val selectedAddOnItems = addOnItemViewModel.selectedAddOnItems
    val isLoading: Boolean = addOnItemViewModel.state.collectAsState().value.isLoading
    val hasError = addOnItemViewModel.state.collectAsState().value.error

    val filterAddOnItem = addOnItemViewModel.state.collectAsState().value.filterAddOnItem

    // Remember a SystemUiController
    val systemUiController = rememberSystemUiController()

    val transition = updateTransition(selectedAddOnItems.isNotEmpty(), label = "isContextual")

    val statusBarColor by transition.animateColor(label = "statusBarContextual") { isContextualMode ->
        if(isContextualMode) { MaterialTheme.colors.secondary } else { MaterialTheme.colors.primary }
    }
    val backgroundColor by transition.animateColor(label = "actionBarContextual") { isContextualMode ->
        if(isContextualMode) { MaterialTheme.colors.secondary } else { MaterialTheme.colors.primary }
    }

    val showSearchBar = addOnItemViewModel.toggledSearchBar.collectAsState().value

    val showScrollToTop = remember {
        derivedStateOf {
            lazyGridState.firstVisibleItemIndex > 0
        }
    }

    LaunchedEffect(key1 = true) {
        addOnItemViewModel.eventFlow.collect { event ->
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
            addOnItemViewModel.onSearchBarCloseAndClearClick()
        } else if(selectedAddOnItems.isNotEmpty()) {
            addOnItemViewModel.onAddOnItemsEvent(AddOnItemEvent.DeselectAddOnItem)
        }else{
            navController.navigateUp()
        }
    }

    resultRecipient.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {
                // `GoToProfileConfirmationDestination` was shown but it was canceled
                // and no value was set (example: dialog/bottom sheet dismissed)
                if(selectedAddOnItems.isNotEmpty()){
                    addOnItemViewModel.onAddOnItemsEvent(AddOnItemEvent.DeselectAddOnItem)
                }
            }
            is NavResult.Value -> {
                if(selectedAddOnItems.isNotEmpty()){
                    addOnItemViewModel.onAddOnItemsEvent(AddOnItemEvent.DeselectAddOnItem)
                }

                scope.launch {
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = result.value
                    )
                }
            }
        }
    }

    StandardScaffold(
        navController = navController,
        scaffoldState = scaffoldState,
        showBackArrow = selectedAddOnItems.isEmpty(),
        onBackButtonClick = {
            if (showSearchBar){
                addOnItemViewModel.onSearchBarCloseAndClearClick()
            }else{
                navController.navigateUp()
            }
        },
        title = {
            if (selectedAddOnItems.isEmpty()) {
                Text(text = "AddOn Items")
            } else if (selectedAddOnItems.size > 1){
                Text(text = "${selectedAddOnItems.size} Selected")
            }
        },
        isFloatingActionButtonDocked = addOnItems.isNotEmpty(),
        floatingActionButton = {
            ExtendedFabButton(
                text = stringResource(id = R.string.create_new_add_on).uppercase(),
                showScrollToTop = showScrollToTop.value,
                visible = addOnItems.isNotEmpty() && selectedAddOnItems.isEmpty(),
                onScrollToTopClick = {
                    scope.launch {
                        lazyGridState.animateScrollToItem(index = 0)
                    }
                },
                onClick = {
                    navController.navigate(AddEditAddOnItemScreenDestination())
                },
            )
        },
        floatingActionButtonPosition = if(showScrollToTop.value) FabPosition.End else FabPosition.Center,
        navActions = {
            if(selectedAddOnItems.isNotEmpty()) {
                if(selectedAddOnItems.size == 1){
                    IconButton(
                        onClick = {
                            navController.navigate(AddEditAddOnItemScreenDestination(addOnItemId = selectedAddOnItems.first()))
                        },
                    ){
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit AddOn Item",
                            tint = MaterialTheme.colors.onPrimary,
                        )
                    }
                }

                IconButton(
                    onClick = {
                        deleteAddOnItemState.show()
                    },
                    enabled = selectedAddOnItems.isNotEmpty()
                ){
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete AddOn Item",
                        tint = MaterialTheme.colors.onPrimary,
                    )
                }

                IconButton(
                    onClick = {
                        addOnItemViewModel.onAddOnItemsEvent(AddOnItemEvent.SelectAllAddOnItem)
                    },
                    enabled = selectedAddOnItems.isNotEmpty()
                ){
                    Icon(
                        imageVector = Icons.Default.Rule,
                        contentDescription = "Select All AddOn Item",
                        tint = MaterialTheme.colors.onPrimary,
                    )
                }
            }
            else if(showSearchBar){
                StandardSearchBar(
                    searchText = addOnItemViewModel.searchText.collectAsState().value,
                    placeholderText = "Search for AddOn Items...",
                    onSearchTextChanged = {
                        addOnItemViewModel.onAddOnItemsEvent(AddOnItemEvent.OnSearchAddOnItem(it))
                    },
                    onClearClick = {
                        addOnItemViewModel.onSearchTextClearClick()
                    },
                )
            }
            else {
                if (addOnItems.isNotEmpty()){
                    IconButton(
                        onClick = {
                            addOnItemViewModel.onAddOnItemsEvent(AddOnItemEvent.ToggleSearchBar)
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
                                BottomSheetScreen.FilterAddOnItemScreen(
                                    filterAddOnItem = filterAddOnItem,
                                    onFilterChanged = {
                                        addOnItemViewModel.onAddOnItemsEvent(
                                            AddOnItemEvent.OnFilterAddOnItem(
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
                            contentDescription = stringResource(id = R.string.filter_add_on_item),
                            tint = MaterialTheme.colors.onPrimary,
                        )
                    }
                }
            }
        },
        navigationIcon = {
            if(selectedAddOnItems.isNotEmpty()) {
                IconButton(
                    onClick = {
                        addOnItemViewModel.onAddOnItemsEvent(AddOnItemEvent.DeselectAddOnItem)
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
            dialogState = deleteAddOnItemState,
            buttons = {
                positiveButton(
                    text = "Delete",
                    onClick = {
                        addOnItemViewModel.onAddOnItemsEvent(
                            AddOnItemEvent.DeleteAddOnItem(
                                selectedAddOnItems.toList()
                            )
                        )
                    }
                )
                negativeButton(
                    text = "Cancel",
                    onClick = {
                        deleteAddOnItemState.hide()
                    },
                )
            }
        ) {
            title(text = "Delete ${selectedAddOnItems.size} AddOn Item?")
            message(res = R.string.delete_add_on_item_message)
        }

        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing = isLoading),
            onRefresh = {
                addOnItemViewModel.onAddOnItemsEvent(AddOnItemEvent.RefreshAddOnItem)
            }
        ) {
            if (addOnItems.isEmpty() || hasError != null) {
                ItemNotAvailable(
                    text = hasError ?: if(showSearchBar) stringResource(id = R.string.search_item_not_found) else stringResource(id = R.string.no_items_in_addon),
                    buttonText = stringResource(id = R.string.create_new_add_on).uppercase(),
                    onClick = {
                        navController.navigate(AddEditAddOnItemScreenDestination())
                    }
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    state = lazyGridState,
                    modifier = Modifier.fillMaxSize()
                ){
                    itemsIndexed(addOnItems){ _, addOnItem ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(SpaceSmall)
                                .clickable {
                                    addOnItemViewModel.onAddOnItemsEvent(
                                        AddOnItemEvent.SelectAddOnItem(addOnItem.addOnItemId)
                                    )
                                },
                            shape = RoundedCornerShape(4.dp),
                            backgroundColor = MaterialTheme.colors.surface,
                            contentColor = MaterialTheme.colors.onSurface,
                            border = if(selectedAddOnItems.contains(addOnItem.addOnItemId))
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
                                    text = addOnItem.itemName,
                                    style = MaterialTheme.typography.body1,
                                    textAlign = TextAlign.Center,
                                    fontWeight = FontWeight.SemiBold,
                                    overflow = TextOverflow.Ellipsis,
                                )
                                Spacer(modifier = Modifier.height(SpaceMini))
                                Text(
                                    text = addOnItem.itemPrice.toString().toRupee,
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