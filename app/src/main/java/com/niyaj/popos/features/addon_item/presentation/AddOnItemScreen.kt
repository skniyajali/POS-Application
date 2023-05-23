package com.niyaj.popos.features.addon_item.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.material.FabPosition
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Link
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.niyaj.popos.features.addon_item.domain.util.AddOnConstants
import com.niyaj.popos.features.addon_item.domain.util.AddOnConstants.ADDON_DESELECT_ALL_BUTTON
import com.niyaj.popos.features.addon_item.domain.util.AddOnConstants.ADDON_ITEM_REFRESH
import com.niyaj.popos.features.addon_item.domain.util.AddOnConstants.ADDON_ITEM_TAG
import com.niyaj.popos.features.addon_item.domain.util.AddOnConstants.ADDON_NOT_AVAIlABLE
import com.niyaj.popos.features.addon_item.domain.util.AddOnConstants.ADDON_SCREEN
import com.niyaj.popos.features.addon_item.domain.util.AddOnConstants.ADDON_SCREEN_TITLE
import com.niyaj.popos.features.addon_item.domain.util.AddOnConstants.DELETE_ADD_ON_ITEM_MESSAGE
import com.niyaj.popos.features.addon_item.domain.util.AddOnConstants.NO_ITEMS_IN_ADDON
import com.niyaj.popos.features.common.ui.theme.SpaceMini
import com.niyaj.popos.features.common.ui.theme.SpaceSmall
import com.niyaj.popos.features.common.util.UiEvent
import com.niyaj.popos.features.components.ItemNotAvailable
import com.niyaj.popos.features.components.LoadingIndicator
import com.niyaj.popos.features.components.ScaffoldNavActions
import com.niyaj.popos.features.components.StandardFabButton
import com.niyaj.popos.features.components.StandardIconButton
import com.niyaj.popos.features.components.StandardScaffold
import com.niyaj.popos.features.destinations.AddEditAddOnItemScreenDestination
import com.niyaj.popos.utils.Constants.SEARCH_ITEM_NOT_FOUND
import com.niyaj.popos.utils.toRupee
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
 * AddOnItem Screen
 * @author Sk Niyaj Ali
 *
 */
@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
@Destination
@Composable
fun AddOnItemScreen(
    navController: NavController = rememberNavController(),
    scaffoldState: ScaffoldState = rememberScaffoldState(),
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

    SentryTraced(tag = "AddOnItemScreen") {
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
                StandardFabButton(
                    text = AddOnConstants.CREATE_NEW_ADD_ON.uppercase(),
                    showScrollToTop = showScrollToTop.value,
                    visible = addOnItems.isNotEmpty() && selectedAddOnItems.isEmpty() && !showSearchBar,
                    onScrollToTopClick = {
                        scope.launch {
                            lazyGridState.animateScrollToItem(index = 0)
                        }
                    },
                    onClick = {
                        navController.navigate(AddEditAddOnItemScreenDestination())
                    },
                    modifier = Modifier.testTag(AddOnConstants.CREATE_NEW_ADD_ON),
                )
            },
            floatingActionButtonPosition = if(showScrollToTop.value) FabPosition.End else FabPosition.Center,
            navActions = {
                ScaffoldNavActions(
                    multiSelect = true,
                    allItemsIsEmpty = addOnItems.isEmpty(),
                    selectedItems = selectedAddOnItems,
                    onClickEdit = {
                        navController.navigate(AddEditAddOnItemScreenDestination(addOnItemId = selectedAddOnItems.first()))
                    },
                    onClickDelete = {
                        deleteAddOnItemState.show()
                    },
                    onClickSelectAll = {
                        addOnItemViewModel.onAddOnItemsEvent(AddOnItemEvent.SelectAllAddOnItem)
                    },
                    showSearchBar = showSearchBar,
                    searchText = addOnItemViewModel.searchText.collectAsStateWithLifecycle().value,
                    onSearchTextChanged = {
                        addOnItemViewModel.onAddOnItemsEvent(AddOnItemEvent.OnSearchAddOnItem(it))
                    },
                    onClearClick = {
                        addOnItemViewModel.onSearchTextClearClick()
                    },
                    onClickSearch = {
                        addOnItemViewModel.onAddOnItemsEvent(AddOnItemEvent.ToggleSearchBar)
                    },
                )
            },
            navigationIcon = {
                if(selectedAddOnItems.isNotEmpty()) {
                    StandardIconButton(
                        modifier = Modifier.testTag(ADDON_DESELECT_ALL_BUTTON),
                        onClick = {
                            addOnItemViewModel.onAddOnItemsEvent(AddOnItemEvent.DeselectAddOnItem)
                        },
                        imageVector = Icons.Default.Close,
                        contentDescription = ADDON_DESELECT_ALL_BUTTON,
                        tint = MaterialTheme.colors.onPrimary,
                    )
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
                            addOnItemViewModel.onAddOnItemsEvent(AddOnItemEvent.DeselectAddOnItem)
                        },
                    )
                }
            ) {
                title(text = "Delete ${selectedAddOnItems.size} AddOn Item?")
                message(DELETE_ADD_ON_ITEM_MESSAGE)
            }

            SwipeRefresh(
                state = rememberSwipeRefreshState(isRefreshing = isLoading),
                onRefresh = {
                    addOnItemViewModel.onAddOnItemsEvent(AddOnItemEvent.RefreshAddOnItem)
                },
                modifier = Modifier.testTag(ADDON_ITEM_REFRESH)
            ) {
                if (isLoading) {
                    LoadingIndicator()
                }else if (addOnItems.isEmpty() || hasError != null) {
                    ItemNotAvailable(
                        modifier = Modifier.testTag(ADDON_NOT_AVAIlABLE),
                        btnModifier = Modifier.testTag(AddOnConstants.CREATE_NEW_ADD_ON),
                        text = hasError ?: if(showSearchBar) SEARCH_ITEM_NOT_FOUND else NO_ITEMS_IN_ADDON,
                        buttonText = AddOnConstants.CREATE_NEW_ADD_ON.uppercase(),
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
                            Card(
                                modifier = Modifier
                                    .testTag(
                                        ADDON_ITEM_TAG
                                            .plus(addOnItem.itemName)
                                            .plus(addOnItem.itemPrice.toString())
                                    )
                                    .fillMaxWidth()
                                    .padding(SpaceMini)
                                    .combinedClickable(
                                        onClick = {
                                            if (selectedAddOnItems.isNotEmpty()) {
                                                addOnItemViewModel.onAddOnItemsEvent(
                                                    AddOnItemEvent.SelectAddOnItem(addOnItem.addOnItemId)
                                                )
                                            }
                                        },
                                        onLongClick = {
                                            addOnItemViewModel.onAddOnItemsEvent(
                                                AddOnItemEvent.SelectAddOnItem(addOnItem.addOnItemId)
                                            )
                                        },
                                    ),
                                elevation = 2.dp,
                                border = if(selectedAddOnItems.contains(addOnItem.addOnItemId))
                                    BorderStroke(1.dp, MaterialTheme.colors.primary) else null
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(SpaceSmall),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(
                                        verticalArrangement = Arrangement.SpaceBetween,
                                    ) {
                                        Text(
                                            text = addOnItem.itemName,
                                            style = MaterialTheme.typography.body1,
                                            fontWeight = FontWeight.SemiBold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                        )

                                        Spacer(modifier = Modifier.height(SpaceSmall))

                                        Text(
                                            text = addOnItem.itemPrice.toString().toRupee,
                                            style = MaterialTheme.typography.body2,
                                            fontWeight = FontWeight.Normal,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                        )
                                    }

                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colors.background),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Link,
                                            contentDescription = addOnItem.itemName,
                                            tint = MaterialTheme.colors.secondaryVariant,
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
