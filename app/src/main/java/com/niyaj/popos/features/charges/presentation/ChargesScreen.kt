package com.niyaj.popos.features.charges.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.CircularProgressIndicator
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.niyaj.popos.R
import com.niyaj.popos.common.utils.toRupee
import com.niyaj.popos.features.charges.domain.model.Charges
import com.niyaj.popos.features.common.ui.theme.SpaceSmall
import com.niyaj.popos.features.common.util.UiEvent
import com.niyaj.popos.features.components.ItemNotAvailable
import com.niyaj.popos.features.components.ScaffoldNavActions
import com.niyaj.popos.features.components.StandardFabButton
import com.niyaj.popos.features.components.StandardOutlinedChip
import com.niyaj.popos.features.components.StandardScaffold
import com.niyaj.popos.features.destinations.AddEditChargesScreenDestination
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
 * Charges Screen
 * @author Sk Niyaj Ali
 * @param navController
 * @param scaffoldState
 * @param chargesViewModel
 * @param resultRecipient
 * @see ChargesViewModel
 */
@OptIn(ExperimentalComposeUiApi::class)
@Destination
@Composable
fun ChargesScreen(
    navController: NavController,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    chargesViewModel : ChargesViewModel = hiltViewModel (),
    resultRecipient: ResultRecipient<AddEditChargesScreenDestination, String>,
) {
    val scope = rememberCoroutineScope()
    val lazyListState = rememberLazyGridState()
    val deleteChargesState = rememberMaterialDialogState()

    val chargesItems = chargesViewModel.state.collectAsStateWithLifecycle().value.chargesItem
    val isLoading = chargesViewModel.state.collectAsStateWithLifecycle().value.isLoading
    val hasError = chargesViewModel.state.collectAsStateWithLifecycle().value.error

    val selectedChargesItem = chargesViewModel.selectedCharges.collectAsStateWithLifecycle().value

    // Remember a SystemUiController
    val systemUiController = rememberSystemUiController()

    val transition = updateTransition(selectedChargesItem.isNotEmpty(), label = "isContextual")

    val statusBarColor by transition.animateColor(label = "statusBarContextual") { isContextualMode ->
        if(isContextualMode) { MaterialTheme.colors.secondary } else { MaterialTheme.colors.primary }
    }
    val backgroundColor by transition.animateColor(label = "actionBarContextual") { isContextualMode ->
        if(isContextualMode) { MaterialTheme.colors.secondary } else { MaterialTheme.colors.primary }
    }

    val showSearchBar = chargesViewModel.toggledSearchBar.collectAsStateWithLifecycle().value
    val searchText = chargesViewModel.searchText.collectAsStateWithLifecycle().value

    val showScrollToTop = remember {
        derivedStateOf {
            lazyListState.firstVisibleItemIndex > 0
        }
    }

    LaunchedEffect(key1 = true) {
        chargesViewModel.eventFlow.collect { event ->
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

                is UiEvent.IsLoading -> {
                    Timber.d("Loading.. ${event.isLoading.toString()}")
                }
            }
        }
    }

    resultRecipient.onNavResult { result ->
        when(result) {
            is NavResult.Canceled -> {
                if(selectedChargesItem.isNotEmpty()){
                    chargesViewModel.onChargesEvent(ChargesEvent.SelectCharges(selectedChargesItem))
                }
            }
            is NavResult.Value -> {
                if(selectedChargesItem.isNotEmpty()){
                    chargesViewModel.onChargesEvent(ChargesEvent.SelectCharges(selectedChargesItem))
                }

                scope.launch {
                    scaffoldState.snackbarHostState.showSnackbar(result.value)
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
            chargesViewModel.onSearchBarCloseAndClearClick()
        } else if(selectedChargesItem.isNotEmpty()) {
            chargesViewModel.onChargesEvent(ChargesEvent.SelectCharges(selectedChargesItem))
        }else{
            navController.navigateUp()
        }
    }

    SentryTraced(tag = "ChargesScreen") {
        StandardScaffold(
            navController = navController,
            scaffoldState = scaffoldState,
            showBackArrow = selectedChargesItem.isEmpty(),
            onBackButtonClick = {
                if (showSearchBar){
                    chargesViewModel.onSearchBarCloseAndClearClick()
                }else{
                    navController.navigateUp()
                }
            },
            title = {
                if (selectedChargesItem.isEmpty()) {
                    Text(text = "Charges Item")
                }
            },
            isFloatingActionButtonDocked = chargesItems.isNotEmpty(),
            floatingActionButton = {
                StandardFabButton(
                    text = stringResource(id = R.string.create_new_charges).uppercase(),
                    showScrollToTop = showScrollToTop.value,
                    visible = chargesItems.isNotEmpty() && selectedChargesItem.isEmpty() && !showSearchBar,
                    onScrollToTopClick = {
                        scope.launch {
                            lazyListState.animateScrollToItem(index = 0)
                        }
                    },
                    onClick = {
                        navController.navigate(AddEditChargesScreenDestination())
                    },
                )
            },
            floatingActionButtonPosition = if(showScrollToTop.value) FabPosition.End else FabPosition.Center,
            navActions = {
                ScaffoldNavActions(
                    multiSelect = false,
                    allItemsIsEmpty = chargesItems.isEmpty(),
                    selectedItem = selectedChargesItem,
                    onClickEdit = {
                        navController.navigate(AddEditChargesScreenDestination(chargesId = selectedChargesItem))
                    },
                    onClickDelete = {
                        deleteChargesState.show()
                    },
                    showSearchBar = showSearchBar,
                    searchText = searchText,
                    onSearchTextChanged = {
                        chargesViewModel.onChargesEvent(ChargesEvent.OnSearchCharges(it))
                    },
                    onClearClick = {
                        chargesViewModel.onSearchTextClearClick()
                    },
                    onClickSearch = {
                        chargesViewModel.onChargesEvent(ChargesEvent.ToggleSearchBar)
                    }
                )
            },
            navigationIcon = {
                if(selectedChargesItem.isNotEmpty()) {
                    IconButton(
                        onClick = {
                            chargesViewModel.onChargesEvent(
                                ChargesEvent.SelectCharges(
                                    selectedChargesItem
                                )
                            )
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(id = R.string.close_icon),
                            tint = MaterialTheme.colors.onPrimary,
                        )
                    }
                }
            },
            topAppBarBackgroundColor = backgroundColor,
        ) {
            MaterialDialog(
                dialogState = deleteChargesState,
                buttons = {
                    positiveButton(
                        text = "Delete",
                        onClick = {
                            chargesViewModel.onChargesEvent(
                                ChargesEvent.DeleteCharges(
                                    selectedChargesItem
                                )
                            )
                        }
                    )
                    negativeButton(
                        text = "Cancel",
                        onClick = {
                            deleteChargesState.hide()
                            chargesViewModel.onChargesEvent(ChargesEvent.SelectCharges(selectedChargesItem))
                        },
                    )
                }
            ) {
                title(text = "Delete Charges?")
                message(res = R.string.delete_charges_message)
            }

            SwipeRefresh(
                state = rememberSwipeRefreshState(isRefreshing = isLoading),
                onRefresh = {
                    chargesViewModel.onChargesEvent(ChargesEvent.RefreshCharges)
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
                    } else if (chargesItems.isEmpty()  || hasError != null) {
                        ItemNotAvailable(
                            text = hasError ?: if(showSearchBar) stringResource(id = R.string.search_item_not_found) else stringResource(id = R.string.no_items_in_charges),
                            buttonText = stringResource(id = R.string.create_new_charges).uppercase(),
                            onClick = {
                                navController.navigate(AddEditChargesScreenDestination())
                            }
                        )
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            state = lazyListState,
                        ){
                            itemsIndexed(chargesItems){ _, chargesItem ->
                                ChargesItem(
                                    chargesItem = chargesItem,
                                    doesSelected = selectedChargesItem == chargesItem.chargesId,
                                    onClickCharges = {
                                        chargesViewModel.onChargesEvent(
                                            ChargesEvent.SelectCharges(chargesItem.chargesId)
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


/**
 * Charges Item composable
 * @param chargesItem
 * @param doesSelected
 * @param onClickCharges
 */
@Composable
fun ChargesItem(
    chargesItem: Charges,
    doesSelected: Boolean = false,
    onClickCharges: (String) -> Unit,
) {
    Card(
        modifier = Modifier
            .testTag(chargesItem.chargesName)
            .fillMaxWidth()
            .padding(SpaceSmall)
            .clickable {
                onClickCharges(chargesItem.chargesId)
            },
        shape = RoundedCornerShape(4.dp),
        border = if(doesSelected)
            BorderStroke(1.dp, MaterialTheme.colors.primary)
        else null,
        elevation = 2.dp,
    ) {
        Column(
            modifier = Modifier
                .padding(SpaceSmall)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = chargesItem.chargesName,
                style = MaterialTheme.typography.body1,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(SpaceSmall))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = chargesItem.chargesPrice.toString().toRupee,
                    style = MaterialTheme.typography.body1,
                )

                StandardOutlinedChip(
                    text = if(chargesItem.isApplicable) "Applied" else "Not Applied",
                    onClick = {}
                )
            }
        }
    }
}