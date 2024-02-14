package com.niyaj.feature.product.details

import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.feature.product.components.ProductDetails
import com.niyaj.feature.product.components.ProductOrderDetails
import com.niyaj.feature.product.components.ProductTotalOrdersDetails
import com.niyaj.feature.product.destinations.AddEditProductScreenDestination
import com.niyaj.ui.components.StandardFabButton
import com.niyaj.ui.components.StandardScaffold
import com.niyaj.ui.util.isScrolled
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.navigate
import kotlinx.coroutines.launch

@Destination
@Composable
fun ProductDetailsScreen(
    productId: String,
    onClickOrder: (String) -> Unit,
    navController: NavController = rememberNavController(),
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    viewModel: ProductDetailsViewModel = hiltViewModel()
) {
    val lazyListState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    val product = viewModel.product.collectAsStateWithLifecycle().value

    val orderDetails = viewModel.orderDetails.collectAsStateWithLifecycle().value

    val totalDetails = viewModel.totalOrders.collectAsStateWithLifecycle().value

    var productDetailsExpanded by rememberSaveable {
        mutableStateOf(false)
    }

    var orderDetailsExpanded by rememberSaveable {
        mutableStateOf(true)
    }

    StandardScaffold(
        navController = navController,
        scaffoldState = scaffoldState,
        showBackArrow = true,
        title = {
            Text(text = "Product Details")
        },
        floatingActionButton = {
            StandardFabButton(
                showScrollToTop = lazyListState.isScrolled,
                onScrollToTopClick = {
                    scope.launch {
                        lazyListState.animateScrollToItem(0)
                    }
                },
                visible = false,
            )
        },
        floatingActionButtonPosition = FabPosition.End
    ) {
        LazyColumn(
            state = lazyListState,
            modifier = Modifier.padding(SpaceSmall)
        ) {

            item("TotalOrderDetails") {
                ProductTotalOrdersDetails(details = totalDetails)
                Spacer(modifier = Modifier.height(SpaceSmall))
            }

            item("ProductDetails") {
                ProductDetails(
                    product = product,
                    onExpanded = {
                        productDetailsExpanded = !productDetailsExpanded
                    },
                    doesExpanded = productDetailsExpanded,
                    onClickEdit = {
                        navController.navigate(AddEditProductScreenDestination(productId))
                    }
                )

                Spacer(modifier = Modifier.height(SpaceSmall))
            }

            item("OrderDetails") {
                ProductOrderDetails(
                    orderState = orderDetails,
                    onExpanded = {
                        orderDetailsExpanded = !orderDetailsExpanded
                    },
                    productPrice = product.productPrice,
                    doesExpanded = orderDetailsExpanded,
                    onClickOrder = onClickOrder
                )

                Spacer(modifier = Modifier.height(SpaceSmall))
            }
        }
    }

}