package com.niyaj.popos.features.addon_item.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.ExperimentalMaterialApi
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
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Rule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
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
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.niyaj.popos.R
import com.niyaj.popos.features.addon_item.domain.util.AddOnConstants.ADDON_SCREEN_TITLE
import com.niyaj.popos.features.addon_item.domain.util.AddOnConstants.CREATE_NEW_ADD_ON
import com.niyaj.popos.features.addon_item.domain.util.AddOnConstants.NO_ITEMS_IN_ADDON
import com.niyaj.popos.features.common.ui.theme.SpaceSmall
import com.niyaj.popos.features.common.util.BottomSheetScreen
import com.niyaj.popos.features.common.util.UiEvent
import com.niyaj.popos.features.components.ExtendedFabButton
import com.niyaj.popos.features.components.FlexRowBox
import com.niyaj.popos.features.components.ItemNotAvailable
import com.niyaj.popos.features.components.StandardScaffold
import com.niyaj.popos.features.components.StandardSearchBar
import com.niyaj.popos.features.destinations.AddEditAddOnItemScreenDestination
import com.niyaj.popos.util.Constants.ADDON_NOT_AVAIlABLE
import com.niyaj.popos.util.Constants.ADDON_SCREEN
import com.niyaj.popos.util.Constants.CREATE_NEW_ADDON_BTN
import com.niyaj.popos.util.Constants.SEARCH_ITEM_NOT_FOUND
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

@OptIn(ExperimentalLifecycleComposeApi::class, ExperimentalMaterialApi::class)
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

    val addOnItems = addOnItemViewModel.state.collectAsStateWithLifecycle().value.addOnItems
    val selectedAddOnItems = addOnItemViewModel.selectedAddOnItems
    val isLoading: Boolean = addOnItemViewModel.state.collectAsStateWithLifecycle().value.isLoading
    val hasError = addOnItemViewModel.state.collectAsStateWithLifecycle().value.error

    val filterAddOnItem = addOnItemViewModel.state.collectAsStateWithLifecycle().value.filterAddOnItem

    // Remember a SystemUiController
    val systemUiController = rememberSystemUiController()

    val transition = updateTransition(selectedAddOnItems.isNotEmpty(), label = "isContextual")

    val statusBarColor by transition.animateColor(label = "statusBarContextual") { isContextualMode ->
        if(isContextualMode) { MaterialTheme.colors.secondary } else { MaterialTheme.colors.primary }
    }
    val backgroundColor by transition.animateColor(label = "actionBarContextual") { isContextualMode ->
        if(isContextualMode) { MaterialTheme.colors.secondary } else { MaterialTheme.colors.primary }
    }

    val showSearchBar = addOnItemViewModel.toggledSearchBar.collectAsStateWithLifecycle().value

    val pullRefreshState = rememberPullRefreshState(isLoading, { addOnItemViewModel.onAddOnItemsEvent(AddOnItemEvent.RefreshAddOnItem) })


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
        modifier = Modifier.testTag(ADDON_SCREEN),
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
                Text(text = ADDON_SCREEN_TITLE)
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

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .pullRefresh(pullRefreshState)
        ) {
            if (addOnItems.isEmpty() || hasError != null) {
                ItemNotAvailable(
                    modifier = Modifier.testTag(ADDON_NOT_AVAIlABLE),
                    btnModifier = Modifier.testTag(CREATE_NEW_ADDON_BTN),
                    text = hasError ?: if(showSearchBar) SEARCH_ITEM_NOT_FOUND else NO_ITEMS_IN_ADDON,
                    buttonText = CREATE_NEW_ADD_ON.uppercase(),
                    onClick = {
                        navController.navigate(AddEditAddOnItemScreenDestination())
                    }
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    state = lazyGridState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(SpaceSmall)
                ){
                    itemsIndexed(addOnItems){ _, addOnItem ->
                        FlexRowBox(
                            modifier = Modifier.testTag(addOnItem.itemName),
                            title = addOnItem.itemName,
                            secondaryText = addOnItem.itemPrice.toString().toRupee,
                            icon = Icons.Default.Link ,
                            doesSelected = selectedAddOnItems.contains(addOnItem.addOnItemId),
                            onClick = {
                                addOnItemViewModel.onAddOnItemsEvent(
                                    AddOnItemEvent.SelectAddOnItem(addOnItem.addOnItemId)
                                )
                            }
                        )
                    }
                }
            }
            
            PullRefreshIndicator(
                refreshing = isLoading,
                state = pullRefreshState,
                modifier = Modifier.align(
                    Alignment.TopCenter
                )
            )
        }
    }
}
