package com.niyaj.feature.customer.details

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.FabPosition
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.niyaj.common.tags.CustomerTestTags.CUSTOMER_DETAILS
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.feature.customer.components.CustomerDetailsCard
import com.niyaj.feature.customer.components.RecentOrders
import com.niyaj.feature.customer.components.TotalOrderDetailsCard
import com.niyaj.feature.customer.destinations.AddEditCustomerScreenDestination
import com.niyaj.ui.components.ScrollToTop
import com.niyaj.ui.components.StandardScaffold
import com.niyaj.ui.util.isScrolled
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.navigate
import kotlinx.coroutines.launch

@Destination
@Composable
fun CustomerDetailsScreen(
    customerId: String,
    onClickOrder: (String) -> Unit,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    navController: NavController = rememberNavController(),
    viewModel: CustomerDetailsViewModel = hiltViewModel(),
) {
    val currentId = navController.currentBackStackEntryAsState()
        .value?.arguments?.getString("customerId") ?: customerId

    val scope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()

    val customer = viewModel.customerDetails.collectAsStateWithLifecycle().value

    val orderDetails = viewModel.orderDetails.collectAsStateWithLifecycle().value

    val totalOrders = viewModel.totalOrders.collectAsStateWithLifecycle().value

    var detailsExpanded by remember {
        mutableStateOf(true)
    }
    var orderExpanded by remember {
        mutableStateOf(true)
    }

    StandardScaffold(
        navController = navController,
        scaffoldState = scaffoldState,
        showBackArrow = true,
        navActions = {},
        title = {
            Text(text = CUSTOMER_DETAILS)
        },
        isFloatingActionButtonDocked = true,
        floatingActionButton = {
            ScrollToTop(
                visible = lazyListState.isScrolled,
                onClick = {
                    scope.launch {
                        lazyListState.animateScrollToItem(index = 0)
                    }
                }
            )
        },
        floatingActionButtonPosition = FabPosition.End
    ) {
        LazyColumn(
            state = lazyListState,
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceSmall)
        ) {
            item(key = "TotalOrder Details") {
                TotalOrderDetailsCard(details = totalOrders)
            }

            item(key = "Address Details") {
                Spacer(modifier = Modifier.height(SpaceMedium))

                CustomerDetailsCard(
                    customerState = customer,
                    onExpanded = {
                        detailsExpanded = !detailsExpanded
                    },
                    doesExpanded = detailsExpanded,
                    onClickEdit = {
                        navController.navigate(AddEditCustomerScreenDestination(currentId))
                    }
                )
            }

            item(key = "OrderDetails") {
                Spacer(modifier = Modifier.height(SpaceMedium))

                RecentOrders(
                    uiState = orderDetails,
                    doesExpanded = orderExpanded,
                    onExpanded = {
                        orderExpanded = !orderExpanded
                    },
                    onClickOrder = onClickOrder
                )

                Spacer(modifier = Modifier.height(SpaceMedium))
            }
        }
    }

}