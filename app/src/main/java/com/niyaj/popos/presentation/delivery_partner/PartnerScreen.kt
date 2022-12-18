package com.niyaj.popos.presentation.delivery_partner

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.niyaj.popos.R
import com.niyaj.popos.destinations.AddEditPartnerScreenDestination
import com.niyaj.popos.domain.model.PartnerStatus
import com.niyaj.popos.domain.util.BottomSheetScreen
import com.niyaj.popos.domain.util.UiEvent
import com.niyaj.popos.presentation.components.*
import com.niyaj.popos.presentation.ui.theme.*
import com.niyaj.popos.util.toFormattedDateAndTime
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


@OptIn(ExperimentalMaterialApi::class)
@Destination
@Composable
fun PartnerScreen(
    onOpenSheet: (BottomSheetScreen) -> Unit = {},
    navController: NavController,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    partnerViewModel: PartnerViewModel = hiltViewModel(),
    resultRecipient: ResultRecipient<AddEditPartnerScreenDestination, String>
) {
    val lazyListState = rememberLazyListState()
    val dialogState = rememberMaterialDialogState()
    val scope = rememberCoroutineScope()

    val partners by lazy { partnerViewModel.state.partners }
    val isLoading by lazy { partnerViewModel.state.isLoading }
    val hasError by lazy { partnerViewModel.state.error }

    val selectedPartnerItem = partnerViewModel.selectedPartner.collectAsState().value

    // Remember a SystemUiController
    val systemUiController = rememberSystemUiController()

    val transition = updateTransition(selectedPartnerItem.isNotEmpty(), label = "isContextual")

    val statusBarColor by transition.animateColor(label = "statusBarContextual") { isContextualMode ->
        if(isContextualMode) { MaterialTheme.colors.secondary } else { MaterialTheme.colors.primary }
    }
    val backgroundColor by transition.animateColor(label = "actionBarContextual") { isContextualMode ->
        if(isContextualMode) { MaterialTheme.colors.secondary } else { MaterialTheme.colors.primary }
    }

    val showSearchBar = partnerViewModel.toggledSearchBar.collectAsState().value

    LaunchedEffect(key1 = true) {
        partnerViewModel.eventFlow.collect { event ->
            when (event) {
                is UiEvent.OnSuccess -> {
                    scaffoldState.snackbarHostState.showSnackbar(event.successMessage)
                }

                is UiEvent.OnError -> {
                    scaffoldState.snackbarHostState.showSnackbar(event.errorMessage)
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

    val showScrollToTop = remember {
        derivedStateOf {
            lazyListState.firstVisibleItemIndex > 0
        }
    }

    resultRecipient.onNavResult { result ->
        when(result) {
            is NavResult.Canceled -> {
                if(selectedPartnerItem.isNotEmpty()) {
                    partnerViewModel.onPartnerEvent(PartnerEvent.SelectPartner(selectedPartnerItem))
                }
            }
            is NavResult.Value -> {
                if(selectedPartnerItem.isNotEmpty()) {
                    partnerViewModel.onPartnerEvent(PartnerEvent.SelectPartner(selectedPartnerItem))
                }
                scope.launch {
                    scaffoldState.snackbarHostState.showSnackbar(result.value)
                }
            }
        }
    }

    BackHandler(true) {
        if (showSearchBar){
            partnerViewModel.onSearchBarCloseAndClearClick()
        } else if(selectedPartnerItem.isNotEmpty()) {
            partnerViewModel.onPartnerEvent(PartnerEvent.SelectPartner(selectedPartnerItem))
        }else{
            navController.navigateUp()
        }
    }

    StandardScaffold(
        navController = navController,
        scaffoldState = scaffoldState,
        showBackArrow = selectedPartnerItem.isEmpty(),
        onBackButtonClick = {
            if (showSearchBar){
                partnerViewModel.onSearchBarCloseAndClearClick()
            }else{
                navController.navigateUp()
            }
        },
        title = {
            if (selectedPartnerItem.isEmpty()) {
                Text(text = "Delivery Partners")
            }
        },
        isFloatingActionButtonDocked = partners.isNotEmpty(),
        floatingActionButton = {
            ExtendedFabButton(
                text = stringResource(id = R.string.create_new_partner).uppercase(),
                showScrollToTop = showScrollToTop.value,
                visible = partners.isNotEmpty() && selectedPartnerItem.isEmpty() && !showSearchBar,
                onScrollToTopClick = {
                    scope.launch {
                        lazyListState.animateScrollToItem(index = 0)
                    }
                },
                onClick = {
                    navController.navigate(AddEditPartnerScreenDestination())
                },
            )
        },
        floatingActionButtonPosition = if(showScrollToTop.value) FabPosition.End else FabPosition.Center,
        navActions = {
            if(selectedPartnerItem.isNotEmpty()) {
                IconButton(
                    onClick = {
                        navController.navigate(AddEditPartnerScreenDestination(selectedPartnerItem))
                    },
                ){
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Partner Item",
                        tint = MaterialTheme.colors.onPrimary,
                    )
                }

                IconButton(
                    onClick = {
                        dialogState.show()
                    },
                    enabled = selectedPartnerItem.isNotEmpty()
                ){
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Partner",
                        tint = MaterialTheme.colors.onPrimary,
                    )
                }
            }
            else if(showSearchBar){
                StandardSearchBar(
                    searchText = partnerViewModel.searchText.collectAsState().value,
                    placeholderText = "Search for delivery partners...",
                    onSearchTextChanged = {
                        partnerViewModel.onPartnerEvent(PartnerEvent.OnSearchPartner(it))
                    },
                    onClearClick = {
                        partnerViewModel.onSearchTextClearClick()
                    },
                )
            }
            else {
                if (partners.isNotEmpty()){
                    IconButton(
                        onClick = {
                            partnerViewModel.onPartnerEvent(PartnerEvent.ToggleSearchBar)
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
                                BottomSheetScreen.FilterPartnerScreen(
                                    filterPartner = partnerViewModel.state.filterPartner,
                                    onFilterChanged = {
                                        partnerViewModel.onPartnerEvent(PartnerEvent.OnFilterPartner(it))
                                    },
                                )
                            )
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Sort,
                            contentDescription = stringResource(id = R.string.filter_delivery_partner),
                            tint = MaterialTheme.colors.onPrimary,
                        )
                    }
                }
            }
        },
        navigationIcon = {
            if(selectedPartnerItem.isNotEmpty()) {
                IconButton(
                    onClick = {
                        partnerViewModel.onPartnerEvent(PartnerEvent.SelectPartner(selectedPartnerItem))
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
            dialogState = dialogState,
            buttons = {
                positiveButton(
                    text = "Delete",
                    onClick = {
                        partnerViewModel.onPartnerEvent(PartnerEvent.DeletePartner(selectedPartnerItem))
                    }
                )
                negativeButton(
                    text = "Cancel",
                    onClick = {
                        dialogState.hide()
                    },
                )
            }
        ) {
            title(text = "Delete partner?")
            message(res = R.string.delete_partner_message)
        }

        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing = isLoading),
            onRefresh = {
                partnerViewModel.onPartnerEvent(PartnerEvent.RefreshPartner)
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
            } else if (partners.isEmpty() || hasError != null) {
                ItemNotAvailable(
                    text = hasError ?: if(showSearchBar) stringResource(id = R.string.search_item_not_found) else stringResource(id = R.string.no_items_in_partner),
                    buttonText = stringResource(id = R.string.create_new_partner).uppercase(),
                    onClick = {
                        navController.navigate(AddEditPartnerScreenDestination())
                    }
                )
            } else {
                LazyColumn(
                    state = lazyListState,
                    modifier = Modifier.fillMaxSize()
                ){
                    itemsIndexed(partners){ index, partner ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    partnerViewModel.onPartnerEvent(
                                        PartnerEvent.SelectPartner(partner.deliveryPartnerId)
                                    )
                                },
                            shape = RoundedCornerShape(4.dp),
                            border = if(selectedPartnerItem == partner.deliveryPartnerId)
                                BorderStroke(1.dp, MaterialTheme.colors.primary)
                            else null,
                            elevation = 2.dp,
                        ) {
                            StandardExpandable(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(SpaceSmall),
                                onExpandChanged = {
                                    partnerViewModel.onPartnerEvent(
                                        PartnerEvent.SelectPartner(partner.deliveryPartnerId)
                                    )
                                },
                                expanded = selectedPartnerItem == partner.deliveryPartnerId,
                                leading = {},
                                title = {
                                    Column(
                                        verticalArrangement = Arrangement.SpaceBetween,
                                        horizontalAlignment = Alignment.Start
                                    ) {
                                        Text(
                                            text = partner.deliveryPartnerName,
                                            style = MaterialTheme.typography.body1,
                                            textAlign = TextAlign.Center,
                                            fontWeight = FontWeight.Bold,
                                            overflow = TextOverflow.Ellipsis,
                                        )
                                        Spacer(modifier = Modifier.height(SpaceMini))

                                        Text(
                                            text = partner.deliveryPartnerPhone,
                                            style = MaterialTheme.typography.body1,
                                            textAlign = TextAlign.Center,
                                        )

                                    }
                                },
                                trailing = {
                                    Card(
                                        modifier = Modifier
                                            .wrapContentSize(),
                                        backgroundColor = when (partner.deliveryPartnerStatus) {
                                            PartnerStatus.InActive.partnerStatus -> MaterialTheme.colors.secondary
                                            PartnerStatus.Suspended.partnerStatus -> MaterialTheme.colors.error
                                            else -> MaterialTheme.colors.primary
                                        },
                                        shape = RoundedCornerShape(2.dp),
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .wrapContentWidth()
                                                .padding(SpaceMini),
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center,
                                        ) {
                                            Text(
                                                text = partner.deliveryPartnerStatus.uppercase(),
                                                style = MaterialTheme.typography.caption,
                                                textAlign = TextAlign.Center,
                                                color = MaterialTheme.colors.onPrimary,
                                            )
                                        }

                                    }
                                },
                                rowClickable = true,
                                expand = { modifier ->
                                    IconButton(
                                        modifier = modifier,
                                        onClick = {
                                            partnerViewModel.onPartnerEvent(
                                                PartnerEvent.SelectPartner(partner.deliveryPartnerId)
                                            )
                                        }
                                    ) {
                                        Icon(imageVector = Icons.Filled.KeyboardArrowDown,
                                            contentDescription = null)
                                    }
                                },
                                content = {
                                    Column(
                                        modifier = Modifier
                                            .padding(SpaceSmall)
                                    ) {
                                        TextWithIcon(
                                            text = "Username - ${partner.deliveryPartnerName}",
                                            icon = Icons.Default.Person
                                        )
                                        Spacer(modifier = Modifier.height(SpaceSmall))
                                        TextWithIcon(
                                            text = "Email - ${partner.deliveryPartnerEmail}",
                                            icon = Icons.Default.AlternateEmail
                                        )
                                        Spacer(modifier = Modifier.height(SpaceSmall))
                                        TextWithIcon(
                                            text = "Phone - ${partner.deliveryPartnerPhone}",
                                            icon = Icons.Default.PhoneAndroid
                                        )
                                        Spacer(modifier = Modifier.height(SpaceSmall))
                                        TextWithIcon(
                                            text = "Password - ${partner.deliveryPartnerPassword}",
                                            icon = Icons.Default.Password
                                        )
                                        Spacer(modifier = Modifier.height(SpaceSmall))
                                        TextWithIcon(
                                            text = "Status - ${partner.deliveryPartnerStatus}",
                                            icon = Icons.Default.ModeStandby
                                        )
                                        Spacer(modifier = Modifier.height(SpaceSmall))
                                        TextWithIcon(
                                            text = "Type - ${partner.deliveryPartnerType}",
                                            icon = Icons.Default.MergeType
                                        )
                                        Spacer(modifier = Modifier.height(SpaceSmall))
                                        TextWithIcon(
                                            text = "Created At : ${partner.createdAt?.toFormattedDateAndTime}",
                                            icon = Icons.Default.AccessTime
                                        )
                                        if(!partner.updatedAt.isNullOrEmpty()){
                                            Spacer(modifier = Modifier.height(SpaceSmall))
                                            TextWithIcon(
                                                text = "Last Login : ${partner.updatedAt.toFormattedDateAndTime}",
                                                icon = Icons.Default.Login
                                            )
                                        }
                                    }
                                },
                            )
                        }

                        Spacer(modifier = Modifier.height(SpaceSmall))

                        if(index == partners.size - 1) {
                            Spacer(modifier = Modifier.height(ProfilePictureSizeSmall))
                        }
                    }
                }
            }
        }
    }

}