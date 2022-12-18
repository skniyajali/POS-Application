package com.niyaj.popos.presentation.util

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomSheetScaffoldState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.plusAssign
import com.google.accompanist.navigation.material.BottomSheetNavigator
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.niyaj.popos.domain.util.BottomSheetScreen
import com.niyaj.popos.presentation.NavGraphs
import com.niyaj.popos.presentation.add_on_items.AddOnItemScreen
import com.niyaj.popos.presentation.address.AddressScreen
import com.niyaj.popos.presentation.category.CategoryScreen
import com.niyaj.popos.presentation.charges.ChargesScreen
import com.niyaj.popos.presentation.customer.CustomerScreen
import com.niyaj.popos.presentation.delivery_partner.PartnerScreen
import com.niyaj.popos.presentation.destinations.AddOnItemScreenDestination
import com.niyaj.popos.presentation.destinations.AddressScreenDestination
import com.niyaj.popos.presentation.destinations.CategoryScreenDestination
import com.niyaj.popos.presentation.destinations.ChargesScreenDestination
import com.niyaj.popos.presentation.destinations.CustomerScreenDestination
import com.niyaj.popos.presentation.destinations.EmployeeScreenDestination
import com.niyaj.popos.presentation.destinations.ExpensesCategoryScreenDestination
import com.niyaj.popos.presentation.destinations.ExpensesScreenDestination
import com.niyaj.popos.presentation.destinations.MainFeedScreenDestination
import com.niyaj.popos.presentation.destinations.OrderScreenDestination
import com.niyaj.popos.presentation.destinations.PartnerScreenDestination
import com.niyaj.popos.presentation.destinations.ProductScreenDestination
import com.niyaj.popos.presentation.employee.EmployeeScreen
import com.niyaj.popos.presentation.expenses.ExpensesScreen
import com.niyaj.popos.presentation.expenses_category.ExpensesCategoryScreen
import com.niyaj.popos.presentation.main_feed.MainFeedScreen
import com.niyaj.popos.presentation.order.OrderScreen
import com.niyaj.popos.presentation.product.ProductScreen
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.animations.defaults.NestedNavGraphDefaultAnimations
import com.ramcosta.composedestinations.animations.defaults.RootNavGraphDefaultAnimations
import com.ramcosta.composedestinations.animations.rememberAnimatedNavHostEngine
import com.ramcosta.composedestinations.manualcomposablecalls.composable
import com.ramcosta.composedestinations.navigation.dependency
import com.ramcosta.composedestinations.scope.resultRecipient

@OptIn(ExperimentalMaterialNavigationApi::class, ExperimentalAnimationApi::class, ExperimentalMaterialApi::class)
@Composable
fun NewNavigation(
    modifier: Modifier = Modifier,
    onOpenSheet: (BottomSheetScreen) -> Unit = {},
    bottomSheetScaffoldState: BottomSheetScaffoldState,
    scaffoldState: ScaffoldState,
    navController: NavHostController,
    bottomSheetNavigator: BottomSheetNavigator
) {
    navController.navigatorProvider += bottomSheetNavigator

    val navHostEngine = rememberAnimatedNavHostEngine(
        navHostContentAlignment = Alignment.TopCenter,
        rootDefaultAnimations = RootNavGraphDefaultAnimations.ACCOMPANIST_FADING, //default `rootDefaultAnimations` means no animations
        defaultAnimationsForNestedNavGraph = mapOf(
            NavGraphs.root to NestedNavGraphDefaultAnimations(
                enterTransition = { fadeIn(animationSpec = tween(2000)) },
                exitTransition = { fadeOut(animationSpec = tween(2000)) }
            )
        ) // all other nav graphs not specified in this map, will get their animations from the `rootDefaultAnimations` above.
    )

    ModalBottomSheetLayout(
        bottomSheetNavigator = bottomSheetNavigator,
        sheetShape = RoundedCornerShape(4.dp),
        modifier = modifier,
    ) {
        DestinationsNavHost(
            navGraph = NavGraphs.root,
            navController = navController,
            engine = navHostEngine,
            dependenciesContainerBuilder = {
                dependency(bottomSheetScaffoldState)
                dependency(scaffoldState)
            },
        ){
            composable(MainFeedScreenDestination){
                MainFeedScreen(
                    onOpenSheet = onOpenSheet,
                    scaffoldState = scaffoldState,
                    navController = navController
                )
            }

            composable(CategoryScreenDestination){
                CategoryScreen(
                    onOpenSheet = onOpenSheet,
                    scaffoldState = scaffoldState,
                    navController = navController,
                    resultRecipient = resultRecipient()
                )
            }

            composable(AddOnItemScreenDestination){
                AddOnItemScreen(
                    onOpenSheet = onOpenSheet,
                    scaffoldState = scaffoldState,
                    resultRecipient = resultRecipient(),
                    navController = navController
                )
            }

            composable(ChargesScreenDestination){
                ChargesScreen(
                    onOpenSheet = onOpenSheet,
                    scaffoldState = scaffoldState,
                    navController = navController,
                    resultRecipient = resultRecipient()
                )
            }

            composable(AddressScreenDestination){
                AddressScreen(
                    onOpenSheet = onOpenSheet,
                    scaffoldState = scaffoldState,
                    resultRecipient = resultRecipient(),
                    navController = navController
                )
            }

            composable(CustomerScreenDestination){
                CustomerScreen(
                    onOpenSheet = onOpenSheet,
                    scaffoldState = scaffoldState,
                    resultRecipient = resultRecipient(),
                    navController = navController
                )
            }

            composable(PartnerScreenDestination){
                PartnerScreen(
                    onOpenSheet = onOpenSheet,
                    scaffoldState = scaffoldState,
                    resultRecipient = resultRecipient(),
                    navController = navController
                )
            }

            composable(EmployeeScreenDestination){
                EmployeeScreen(
                    onOpenSheet = onOpenSheet,
                    scaffoldState = scaffoldState,
                    navController = navController,
                    resultRecipient = resultRecipient(),
                    addEditSalaryRecipient = resultRecipient(),
                    addEditAbsentRecipient = resultRecipient(),
                )
            }

            composable(ExpensesScreenDestination){
                ExpensesScreen(
                    onOpenSheet = onOpenSheet,
                    scaffoldState = scaffoldState,
                    navController = navController,
                    resultRecipient = resultRecipient(),
                    settingRecipient = resultRecipient(),
                )
            }

            composable(ExpensesCategoryScreenDestination){
                ExpensesCategoryScreen(
                    onOpenSheet = onOpenSheet,
                    scaffoldState = scaffoldState,
                    resultRecipient = resultRecipient(),
                    navController = navController
                )
            }

            composable(ProductScreenDestination){
                ProductScreen(
                    onOpenSheet = onOpenSheet,
                    scaffoldState = scaffoldState,
                    resultRecipient = resultRecipient(),
                    navController = navController
                )
            }

            composable(OrderScreenDestination){
                OrderScreen(
                    onOpenSheet = onOpenSheet,
                    scaffoldState = scaffoldState,
                    navController = navController
                )
            }
        }
    }
}