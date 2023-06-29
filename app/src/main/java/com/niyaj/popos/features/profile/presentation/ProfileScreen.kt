package com.niyaj.popos.features.profile.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.FabPosition
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.InsertLink
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PeopleAlt
import androidx.compose.material.icons.filled.Sell
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.flowlayout.FlowCrossAxisAlignment
import com.google.accompanist.flowlayout.FlowMainAxisAlignment
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.flowlayout.SizeMode
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.niyaj.popos.BuildConfig
import com.niyaj.popos.features.common.ui.theme.ProfilePictureSizeLarge
import com.niyaj.popos.features.common.ui.theme.ProfilePictureSizeMedium
import com.niyaj.popos.features.common.ui.theme.SpaceMini
import com.niyaj.popos.features.common.ui.theme.SpaceSmall
import com.niyaj.popos.features.common.util.UiEvent
import com.niyaj.popos.features.components.StandardFabButton
import com.niyaj.popos.features.components.StandardScaffold
import com.niyaj.popos.features.destinations.AddOnItemScreenDestination
import com.niyaj.popos.features.destinations.AddressScreenDestination
import com.niyaj.popos.features.destinations.AttendanceScreenDestination
import com.niyaj.popos.features.destinations.CartScreenDestination
import com.niyaj.popos.features.destinations.ChargesScreenDestination
import com.niyaj.popos.features.destinations.CustomerScreenDestination
import com.niyaj.popos.features.destinations.EmployeeScreenDestination
import com.niyaj.popos.features.destinations.ExpensesScreenDestination
import com.niyaj.popos.features.destinations.OrderScreenDestination
import com.niyaj.popos.features.destinations.ProductScreenDestination
import com.niyaj.popos.features.destinations.ReminderScreenDestination
import com.niyaj.popos.features.destinations.ReportScreenDestination
import com.niyaj.popos.features.destinations.UpdateProfileScreenDestination
import com.niyaj.popos.features.main_feed.presentation.components.IconBox
import com.niyaj.popos.features.order.presentation.components.TwoGridText
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import io.sentry.compose.SentryTraced
import kotlinx.coroutines.launch

/**
 * Profile Screen Composable
 * @author Sk Niyaj Ali
 *
 */
@OptIn(ExperimentalComposeUiApi::class, ExperimentalLayoutApi::class)
@Destination
@Composable
fun ProfileScreen(
    navController : NavController,
    scaffoldState : ScaffoldState,
    profileViewModel : ProfileViewModel = hiltViewModel(),
    resultRecipient : ResultRecipient<UpdateProfileScreenDestination, String>
) {

    val lazyListState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    val info = profileViewModel.info.collectAsState().value
    val isLoading = profileViewModel.isLoading

    val showScrollToTop = remember {
        derivedStateOf {
            lazyListState.firstVisibleItemIndex > 0
        }
    }

    LaunchedEffect(key1 = true) {
        profileViewModel.eventFlow.collect { event ->
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

                is UiEvent.IsLoading -> {}
            }
        }
    }

    resultRecipient.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {}
            is NavResult.Value -> {
                profileViewModel.onEvent(ProfileEvent.RefreshEvent)

                scope.launch {
                    scaffoldState.snackbarHostState.showSnackbar(result.value)
                }
            }
        }
    }

    SentryTraced(tag = "ProfileScreen") {
        StandardScaffold(
            navController = navController,
            scaffoldState = scaffoldState,
            showBackArrow = true,
            title = {
                Text(text = "Restaurant Details")
            },
            navActions = {
                IconButton(
                    onClick = {
                        navController.navigate(UpdateProfileScreenDestination)
                    }
                ) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit Profile")
                }
            },
            isFloatingActionButtonDocked = true,
            floatingActionButton = {
                StandardFabButton(
                    text = "",
                    showScrollToTop = showScrollToTop.value,
                    visible = false,
                    onScrollToTopClick = {
                        scope.launch {
                            lazyListState.animateScrollToItem(index = 0)
                        }
                    },
                    onClick = {},
                )
            },
            floatingActionButtonPosition = if (showScrollToTop.value) FabPosition.End else FabPosition.Center,
        ) {
            SwipeRefresh(
                state = rememberSwipeRefreshState(isRefreshing = isLoading),
                onRefresh = {
                    profileViewModel.onEvent(ProfileEvent.RefreshEvent)
                }
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(SpaceSmall),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    item("RestaurantDetails") {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(SpaceSmall),
                            elevation = 4.dp,
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(SpaceSmall),
                                verticalArrangement = Arrangement.SpaceBetween,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Image(
                                    painter = painterResource(id = info.logo.toInt()),
                                    contentDescription = "logo",
                                    modifier = Modifier
                                        .size(
                                            width = ProfilePictureSizeLarge,
                                            height = ProfilePictureSizeMedium
                                        )
                                        .align(Alignment.CenterHorizontally)
                                )
                                Spacer(modifier = Modifier.height(SpaceMini))

                                Text(
                                    text = info.name,
                                    style = MaterialTheme.typography.h5,
                                    fontWeight = FontWeight.Bold,
                                )

                                Spacer(modifier = Modifier.height(SpaceMini))

                                Text(
                                    text = info.tagline,
                                    style = MaterialTheme.typography.body1,
                                    fontWeight = FontWeight.Medium,
                                )

                                Spacer(modifier = Modifier.height(SpaceSmall))
                                Divider(modifier = Modifier.fillMaxWidth())
                                Spacer(modifier = Modifier.height(SpaceSmall))

                                Text(
                                    text = info.description,
                                    style = MaterialTheme.typography.body1,
                                    fontWeight = FontWeight.Medium,
                                )

                                Spacer(modifier = Modifier.height(SpaceSmall))
                                Divider(modifier = Modifier.fillMaxWidth())
                                Spacer(modifier = Modifier.height(SpaceSmall))

                                Text(
                                    text = info.address,
                                    style = MaterialTheme.typography.body1,
                                    fontWeight = FontWeight.Medium,
                                    textAlign = TextAlign.Center,
                                    minLines = 2,
                                )
                                Spacer(modifier = Modifier.height(SpaceSmall))
                                Divider(modifier = Modifier.fillMaxWidth())
                                Spacer(modifier = Modifier.height(SpaceSmall))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = info.primaryPhone,
                                        style = MaterialTheme.typography.body1,
                                        fontWeight = FontWeight.Medium,
                                        textAlign = TextAlign.Center,
                                    )
                                    Text(text = " / ")
                                    Text(
                                        text = info.secondaryPhone,
                                        style = MaterialTheme.typography.body1,
                                        fontWeight = FontWeight.Medium,
                                        textAlign = TextAlign.Center,
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(SpaceSmall))

                        FlowRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(SpaceSmall),
                            mainAxisSize = SizeMode.Expand,
                            mainAxisAlignment = FlowMainAxisAlignment.SpaceBetween,
                            crossAxisAlignment = FlowCrossAxisAlignment.Center,
                            mainAxisSpacing = SpaceSmall,
                            crossAxisSpacing = SpaceSmall
                        ) {
                            QuickLink(
                                text = "Cart",
                                icon = Icons.Default.ShoppingCart,
                                onClick = {
                                    navController.navigate(CartScreenDestination())
                                }
                            )
                            QuickLink(
                                text = "Orders",
                                icon = Icons.Default.Inventory,
                                onClick = {
                                    navController.navigate(OrderScreenDestination())
                                }
                            )
                            QuickLink(
                                text = "Expenses",
                                icon = Icons.Default.Money,
                                onClick = {
                                    navController.navigate(ExpensesScreenDestination())
                                }
                            )
                            QuickLink(
                                text = "Reports",
                                icon = Icons.Default.Assessment,
                                onClick = {
                                    navController.navigate(ReportScreenDestination())
                                }
                            )
                            QuickLink(
                                text = "Employee",
                                icon = Icons.Default.People,
                                onClick = {
                                    navController.navigate(EmployeeScreenDestination())
                                }
                            )
                            QuickLink(
                                text = "Attendance",
                                icon = Icons.Default.CalendarMonth,
                                onClick = {
                                    navController.navigate(AttendanceScreenDestination())
                                }
                            )
                            QuickLink(
                                text = "Payments",
                                icon = Icons.Default.Money,
                                onClick = {
                                    navController.navigate(AttendanceScreenDestination())
                                }
                            )
                            QuickLink(
                                text = "Category",
                                icon = Icons.Default.Dns,
                                onClick = {
                                    navController.navigate(ProductScreenDestination())
                                }
                            )
                            QuickLink(
                                text = "Products",
                                icon = Icons.Default.Dns,
                                onClick = {
                                    navController.navigate(ProductScreenDestination())
                                }
                            )
                            QuickLink(
                                text = "AddOn",
                                icon = Icons.Default.InsertLink,
                                onClick = {
                                    navController.navigate(AddOnItemScreenDestination())
                                }
                            )

                            QuickLink(
                                text = "Charges",
                                icon = Icons.Default.Sell,
                                onClick = {
                                    navController.navigate(ChargesScreenDestination())
                                }
                            )
                            QuickLink(
                                text = "Address",
                                icon = Icons.Default.Business,
                                onClick = {
                                    navController.navigate(AddressScreenDestination())
                                }
                            )


                            QuickLink(
                                text = "Customer",
                                icon = Icons.Default.PeopleAlt,
                                onClick = {
                                    navController.navigate(CustomerScreenDestination())
                                }
                            )
                            QuickLink(
                                text = "Reminder",
                                icon = Icons.Default.Notifications,
                                onClick = {
                                    navController.navigate(ReminderScreenDestination())
                                }
                            )
                        }
                    }

                    item("App Details") {
                        Spacer(modifier = Modifier.height(SpaceSmall))

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(SpaceSmall)
                        ) {
                            Spacer(modifier = Modifier.height(SpaceSmall))
                            Divider(modifier = Modifier.fillMaxWidth())
                            Spacer(modifier = Modifier.height(SpaceSmall))

                            TwoGridText(
                                textOne = "Application ID",
                                textTwo = BuildConfig.APPLICATION_ID
                            )

                            Spacer(modifier = Modifier.height(SpaceMini))
                            Divider(modifier = Modifier.fillMaxWidth())
                            Spacer(modifier = Modifier.height(SpaceMini))

                            TwoGridText(
                                textOne = "Version Name",
                                textTwo = BuildConfig.VERSION_NAME
                            )

                            Spacer(modifier = Modifier.height(SpaceMini))
                            Divider(modifier = Modifier.fillMaxWidth())
                            Spacer(modifier = Modifier.height(SpaceMini))

                            TwoGridText(
                                textOne = "Version Code",
                                textTwo = BuildConfig.VERSION_CODE.toString()
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun QuickLink(
    text : String,
    icon : ImageVector,
    onClick : () -> Unit,
    width : Dp = 164.dp,
    elevation : Dp = 2.dp,
    backgroundColor : Color = MaterialTheme.colors.onPrimary,
    iconColor : Color = MaterialTheme.colors.secondaryVariant,
    textColor : Color = MaterialTheme.colors.onBackground,
) {
    IconBox(
        modifier = Modifier.width(width),
        iconName = icon,
        text = text,
        elevation = elevation,
        backgroundColor = backgroundColor,
        iconColor = iconColor,
        textColor = textColor,
        onClick = onClick
    )
}