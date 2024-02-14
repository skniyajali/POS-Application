package com.niyaj.popos.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.plusAssign
import com.google.accompanist.navigation.material.BottomSheetNavigator
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.niyaj.cart_selected.SelectedCartOrderScreen
import com.niyaj.cart_selected.destinations.SelectedCartOrderScreenDestination
import com.niyaj.feature.address.destinations.AddressDetailsScreenDestination
import com.niyaj.feature.address.details.AddressDetailsScreen
import com.niyaj.feature.cart.CartScreen
import com.niyaj.feature.cart.destinations.CartScreenDestination
import com.niyaj.feature.cart_order.CartOrderScreen
import com.niyaj.feature.cart_order.destinations.AddEditCartOrderScreenDestination
import com.niyaj.feature.cart_order.destinations.CartOrderScreenDestination
import com.niyaj.feature.customer.destinations.CustomerDetailsScreenDestination
import com.niyaj.feature.customer.details.CustomerDetailsScreen
import com.niyaj.feature.employee.destinations.EmployeeDetailsScreenDestination
import com.niyaj.feature.employee.details.EmployeeDetailsScreen
import com.niyaj.feature.employee_attendance.destinations.AddEditAbsentScreenDestination
import com.niyaj.feature.employee_payment.destinations.AddEditPaymentScreenDestination
import com.niyaj.feature.order.OrderScreen
import com.niyaj.feature.order.destinations.OrderDetailsScreenDestination
import com.niyaj.feature.order.destinations.OrderScreenDestination
import com.niyaj.feature.order.details.OrderDetailsScreen
import com.niyaj.feature.product.destinations.ProductDetailsScreenDestination
import com.niyaj.feature.product.details.ProductDetailsScreen
import com.niyaj.feature.reports.ReportScreen
import com.niyaj.feature.reports.destinations.ReportScreenDestination
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.animations.defaults.NestedNavGraphDefaultAnimations
import com.ramcosta.composedestinations.animations.defaults.RootNavGraphDefaultAnimations
import com.ramcosta.composedestinations.animations.manualcomposablecalls.bottomSheetComposable
import com.ramcosta.composedestinations.animations.rememberAnimatedNavHostEngine
import com.ramcosta.composedestinations.manualcomposablecalls.composable
import com.ramcosta.composedestinations.navigation.dependency
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.scope.resultRecipient
import com.ramcosta.composedestinations.spec.Route

/**
 *  Navigation controller
 *  @author Sk Niyaj Ali
 *  @param modifier
 *  @param bottomSheetNavigator
 *  @param scaffoldState
 *  @param navController
 *  @param startRoute
 */
@OptIn(ExperimentalMaterialNavigationApi::class, ExperimentalAnimationApi::class,
    ExperimentalComposeUiApi::class
)
@Composable
fun PoposNavigation(
    modifier: Modifier = Modifier,
    scaffoldState: ScaffoldState,
    navController: NavHostController,
    bottomSheetNavigator: BottomSheetNavigator,
    startRoute: Route,
) {
    navController.navigatorProvider += bottomSheetNavigator

    val navHostEngine = rememberAnimatedNavHostEngine(
        navHostContentAlignment = Alignment.TopCenter,
        //default `rootDefaultAnimations` means no animations
        rootDefaultAnimations = RootNavGraphDefaultAnimations.ACCOMPANIST_FADING,
        // all other nav graphs not specified in this map, will get their animations from the `rootDefaultAnimations` above.
        defaultAnimationsForNestedNavGraph = mapOf(
            RootNavGraph to NestedNavGraphDefaultAnimations.ACCOMPANIST_FADING
        )
    )

    ModalBottomSheetLayout(
        bottomSheetNavigator = bottomSheetNavigator,
        sheetShape = RoundedCornerShape(4.dp),
        modifier = modifier,
    ) {
        DestinationsNavHost(
            navGraph = RootNavGraph,
            navController = navController,
            engine = navHostEngine,
            startRoute = startRoute,
            dependenciesContainerBuilder = {
                dependency(scaffoldState)
                dependency(bottomSheetNavigator)
            },
            manualComposableCallsBuilder = {
                composable(AddressDetailsScreenDestination){
                    AddressDetailsScreen(
                        addressId = navBackStackEntry.arguments?.getString("addressId") ?: "",
                        onClickOrder = {
                            navController.navigate(OrderDetailsScreenDestination(it))
                        },
                        navController = navController,
                        scaffoldState = scaffoldState,
                    )
                }

                composable(CartOrderScreenDestination) {
                    CartOrderScreen(
                        navController = navController,
                        scaffoldState = scaffoldState,
                        onClickOrderDetails = {
                            navController.navigate(OrderDetailsScreenDestination(it))
                        },
                        resultRecipient = resultRecipient(),
                        settingRecipient = resultRecipient()
                    )
                }

                composable(CartScreenDestination) {
                    CartScreen(
                        navController = navController,
                        scaffoldState = scaffoldState,
                        onClickEditOrder = {
                            navController.navigate(AddEditCartOrderScreenDestination(it))
                        },
                        onClickViewOrder = {
                            navController.navigate(OrderDetailsScreenDestination(it))
                        },
                    )
                }

                bottomSheetComposable(SelectedCartOrderScreenDestination) {
                    SelectedCartOrderScreen(
                        navController = navController,
                        onClickEditCartOrder = {
                            navController.navigate(AddEditCartOrderScreenDestination(it))
                        },
                        onClickCreateCartOrder = {
                            navController.navigate(AddEditCartOrderScreenDestination())
                        }
                    )
                }

                composable(CustomerDetailsScreenDestination) {
                    CustomerDetailsScreen(
                        customerId = navBackStackEntry.arguments?.getString("customerId") ?: "",
                        navController = navController,
                        onClickOrder = {
                            navController.navigate(OrderDetailsScreenDestination(it))
                        }
                    )
                }

                composable(EmployeeDetailsScreenDestination) {
                    EmployeeDetailsScreen(
                        employeeId = navBackStackEntry.arguments?.getString("employeeId") ?: "",
                        navController = navController,
                        onClickAddPayment = {
                            navController.navigate(AddEditPaymentScreenDestination(employeeId = it))
                        },
                        onClickAddAbsent = {
                            navController.navigate(AddEditAbsentScreenDestination(employeeId = it))
                        },
                        resultRecipient = resultRecipient(),
                        paymentRecipient = resultRecipient<AddEditPaymentScreenDestination, String>(),
                        absentRecipient = resultRecipient<AddEditPaymentScreenDestination, String>()
                    )
                }

                composable(OrderScreenDestination) {
                    OrderScreen(
                        navController = navController,
                        scaffoldState = scaffoldState,
                        onClickEditOrder = {
                            navController.navigate(AddEditCartOrderScreenDestination(it))
                        }
                    )
                }

                composable(OrderDetailsScreenDestination) {
                    OrderDetailsScreen(
                        cartOrderId = this.navBackStackEntry.arguments?.getString("cartOrderId") ?: "",
                        navController = navController,
                        onClickCustomer = {
                            navController.navigate(CustomerDetailsScreenDestination(it))
                        },
                        onClickAddress = {
                            navController.navigate(AddressDetailsScreenDestination(it))
                        }
                    )
                }

                composable(ProductDetailsScreenDestination) {
                    ProductDetailsScreen(
                        productId = navBackStackEntry.arguments?.getString("productId") ?: "",
                        navController = navController,
                        onClickOrder = {
                            navController.navigate(OrderDetailsScreenDestination(it))
                        }
                    )
                }

                composable(ReportScreenDestination) {
                    ReportScreen(
                        navController = navController,
                        scaffoldState = scaffoldState,
                        onClickAddress = {
                            navController.navigate(AddressDetailsScreenDestination(it))
                        },
                        onClickCustomer = {
                            navController.navigate(CustomerDetailsScreenDestination(it))
                        },
                        onClickProduct = {
                            navController.navigate(ProductDetailsScreenDestination(it))
                        }
                    )
                }


            }
        )
    }
}