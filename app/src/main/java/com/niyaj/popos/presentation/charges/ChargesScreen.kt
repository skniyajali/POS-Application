package com.niyaj.popos.presentation.charges

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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.niyaj.popos.R
import com.niyaj.popos.domain.util.BottomSheetScreen
import com.niyaj.popos.domain.util.UiEvent
import com.niyaj.popos.presentation.components.*
import com.niyaj.popos.presentation.destinations.AddEditChargesScreenDestination
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
fun ChargesScreen(
    navController: NavController,
    onOpenSheet: (BottomSheetScreen) -> Unit = {},
    resultRecipient: ResultRecipient<AddEditChargesScreenDestination, String>,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    chargesViewModel : ChargesViewModel = hiltViewModel (),
) {
    val scope = rememberCoroutineScope()
    val lazyListState = rememberLazyGridState()
    val deleteChargesState = rememberMaterialDialogState()

    val chargesItems by lazy { chargesViewModel.state.chargesItem }
    val isLoading by lazy { chargesViewModel.state.isLoading }
    val hasError by lazy { chargesViewModel.state.error }

    val selectedChargesItem = chargesViewModel.selectedCharges.collectAsState().value

    // Remember a SystemUiController
    val systemUiController = rememberSystemUiController()

    val transition = updateTransition(selectedChargesItem.isNotEmpty(), label = "isContextual")

    val statusBarColor by transition.animateColor(label = "statusBarContextual") { isContextualMode ->
        if(isContextualMode) { MaterialTheme.colors.secondary } else { MaterialTheme.colors.primary }
    }
    val backgroundColor by transition.animateColor(label = "actionBarContextual") { isContextualMode ->
        if(isContextualMode) { MaterialTheme.colors.secondary } else { MaterialTheme.colors.primary }
    }

    val showSearchBar = chargesViewModel.toggledSearchBar.collectAsState().value

    val showScrollToTop = remember {
        derivedStateOf {
            lazyListState.firstVisibleItemIndex > 0
        }
    }

    LaunchedEffect(key1 = true) {
        chargesViewModel.eventFlow.collect { event ->
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
            ExtendedFabButton(
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
            if(selectedChargesItem.isNotEmpty()) {
                IconButton(
                    onClick = {
                        navController.navigate(AddEditChargesScreenDestination(chargesId = selectedChargesItem))
                    },
                ){
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Charges Item",
                        tint = MaterialTheme.colors.onPrimary,
                    )
                }

                IconButton(
                    onClick = {
                        deleteChargesState.show()
                    },
                    enabled = selectedChargesItem.isNotEmpty()
                ){
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Charges",
                        tint = MaterialTheme.colors.onPrimary,
                    )
                }
            }
            else if(showSearchBar){
                StandardSearchBar(
                    searchText = chargesViewModel.searchText.collectAsState().value,
                    placeholderText = "Search for charges items...",
                    onSearchTextChanged = {
                        chargesViewModel.onChargesEvent(ChargesEvent.OnSearchCharges(it))
                    },
                    onClearClick = {
                        chargesViewModel.onSearchTextClearClick()
                    },
                )
            }
            else {
                if (chargesItems.isNotEmpty()){
                    IconButton(
                        onClick = {
                            chargesViewModel.onChargesEvent(ChargesEvent.ToggleSearchBar)
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
                                BottomSheetScreen.FilterChargesScreen(
                                    filterCharges = chargesViewModel.state.filterCharges,
                                    onFilterChanged = {
                                        chargesViewModel.onChargesEvent(ChargesEvent.OnFilterCharges(it))
                                    },
                                )
                            )
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Sort,
                            contentDescription = stringResource(id = R.string.filter_charges_item),
                            tint = MaterialTheme.colors.onPrimary,
                        )
                    }
                }
            }
        },
        navigationIcon = {
            if(selectedChargesItem.isNotEmpty()) {
                IconButton(
                    onClick = {
                        chargesViewModel.onChargesEvent(ChargesEvent.SelectCharges(selectedChargesItem))
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
                        chargesViewModel.onChargesEvent(ChargesEvent.DeleteCharges(selectedChargesItem))
                    }
                )
                negativeButton(
                    text = "Cancel",
                    onClick = {
                        deleteChargesState.hide()
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
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(SpaceSmall)
                                    .clickable {
                                        chargesViewModel.onChargesEvent(
                                            ChargesEvent.SelectCharges(chargesItem.chargesId)
                                        )
                                    },
                                shape = RoundedCornerShape(4.dp),
                                border = if(selectedChargesItem == chargesItem.chargesId)
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

                                        StandardChip(
                                            text = if(chargesItem.isApplicable) "Applied" else "Not Applied",
                                            onClick = {}
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